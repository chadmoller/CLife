package com.clife.restCommon

import org.springframework.mock.web.MockHttpServletRequest
import spock.lang.Specification
import spock.lang.Unroll

class RestRequestFactorySpec extends Specification {

    @Unroll("getRestRequest #pathInfo test")
    def "test getRestRequest"() {
        setup:
        MockHttpServletRequest request = new MockHttpServletRequest()
        request.setPathInfo(pathInfo)
        request.setContent(content?.bytes)
        RestRequestFactory restRequestFactory = new RestRequestFactory()
        RestControllerMap mockRestControllerMap = Mock(RestControllerMap)
        restRequestFactory.restControllerMap = mockRestControllerMap
        RestController mockRestController = Mock(RestController)

        when:
        RestRequest result = restRequestFactory.getRestRequest(request)

        then:
        mockRestControllerMap.get(controllerName) >> mockRestController
        mockRestController.getControllerClass() >> DomainA.class
        result.controllerName == controllerName
        result.objectId == idValue
        result.parentFilter == parentFilter
        result.requestBody == expectedBody

        where:
        pathInfo                                     | queryString | controllerName | idValue | parentFilter                             | queryParams  | content                               | expectedBody
        "/firstPart"                                 | "a=1"       | "firstPart"    | null    | [:]                                      | ["a": ["1"]] | '{"a":1, "b": {"c": 2, "d":"three"}}' | new DomainA(a: 1, b: new DomainB(c: 2, d: "three"))
        "/firstPart/id"                              | "a=1"       | "firstPart"    | "id"    | [:]                                      | ["a": ["1"]] | '{"a":1, "b": {"c": 2, "d":"junk"}}'  | new DomainA(a: 1, b: new DomainB(c: 2, d: "junk"))
        "/firstPart/id/secondPart"                   | "a=1"       | "secondPart"   | null    | ["firstPart": "id"]                      | ["a": ["1"]] | null                                  | null
        "/firstPart/id/secondPart/"                  | null        | "secondPart"   | null    | ["firstPart": "id"]                      | [:]          | null                                  | null
        "/firstPart/id/secondPart/id2/thirdPart/id3" | null        | "thirdPart"    | "id3"   | ["firstPart": "id", "secondPart": "id2"] | [:]          | null                                  | null
    }

    @Unroll("getRestRequest #pathInfo test")
    def "test getRestRequest exception"() {
        setup:
        MockHttpServletRequest request = new MockHttpServletRequest()
        request.setPathInfo(pathInfo)
        RestRequestFactory restRequestFactory = new RestRequestFactory()
        RestControllerMap mockRestControllerMap = Mock(RestControllerMap)
        restRequestFactory.restControllerMap = mockRestControllerMap

        when:
        restRequestFactory.getRestRequest(request)

        then:
        RuntimeException ex = thrown()
        ex.message == exceptionText

        where:
        pathInfo         | exceptionText
        null             | "Controller required"
        ""               | "Unconfigured controller "
        "/badController" | "Unconfigured controller badController"
    }

    @Unroll("buildQueryParamMap #queryString test")
    def "test buildQueryParamMap"() {
        setup:
        MockHttpServletRequest request = new MockHttpServletRequest()
        request.setQueryString(queryString)
        RestRequestFactory restRequestFactory = new RestRequestFactory()

        when:
        Map<String, List<String>> result = restRequestFactory.buildQueryParamMap(request)

        then:
        result == map

        where:
        queryString       | map
        null              | [:]
        "a=1"             | ["a": ["1"]]
        "a=1;b=2"         | ["a": ["1"], "b": ["2"]]
        "a=1;b=2;a=3"     | ["a": ["1", "3"], "b": ["2"]]
        "a=1;b=2;a=3;b=4" | ["a": ["1", "3"], "b": ["2", "4"]]
    }

    @Unroll("getRequestBody #queryString")
    def "test getRequestBody"() {
        setup:
        MockHttpServletRequest request = new MockHttpServletRequest()
        request.setContent(queryString?.bytes)
        RestRequestFactory restRequestFactory = new RestRequestFactory()
        RestRequest restRequest = new RestRequest()
        RestController mockRestController = Mock(RestController)
        restRequest.controller = mockRestController;
        restRequest.originalServletRequest = request;

        when:
        Object result = restRequestFactory.getRequestBody(restRequest)

        then:
        mockRestController.getControllerClass() >> DomainA.class;
        result == expectedResult

        where:
        queryString                           | expectedResult
        '{"a":1, "b": {"c": 2, "d":"three"}}' | new DomainA(a: 1, b: new DomainB(c: 2, d: "three"))
        null                                  | null
        ""                                    | null
    }

    @Unroll("getParentFilter #pathParts")
    def "test getParentFilter"() {
        setup:
        RestRequestFactory restRequestFactory = new RestRequestFactory()

        when:
        Map<String, String> result = restRequestFactory.getParentFilter(pathParts)

        then:
        result == expectedResult

        where:
        pathParts                                                                 | expectedResult
        ["controller1", "key1", "controller2", "key2", "controller3"] as String[] | ["controller1": "key1", "controller2": "key2"]
        ["controller1", "key1", "controller2", "key2"] as String[]                | ["controller1": "key1"]
        ["controller1", "key1"] as String[]                                       | [:]
        ["controller1"] as String[]                                               | [:]
        [] as String[]                                                            | [:]
    }

    @Unroll("getRequestBody exceptions")
    def "test getRequestBody exception"() {
        setup:
        MockHttpServletRequest request = new MockHttpServletRequest()
        request.setContent(content.bytes)
        RestRequestFactory restRequestFactory = new RestRequestFactory()
        RestRequest restRequest = new RestRequest()
        RestController mockRestController = Mock(RestController)
        restRequest.controller = mockRestController;
        restRequest.originalServletRequest = request;

        when:
        restRequestFactory.getRequestBody(restRequest)

        then:
        Exception ex = thrown()
        ex.getClass() == expectedClass
        ex.message == exceptionText

        where:
        content | expectedClass            | exceptionText
        ' '     | IllegalArgumentException | "Unrecognized Type: [null]"
    }
}


