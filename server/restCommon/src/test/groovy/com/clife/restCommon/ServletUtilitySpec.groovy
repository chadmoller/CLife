package com.clife.restCommon

import org.springframework.mock.web.MockHttpServletRequest
import spock.lang.Specification
import spock.lang.Unroll

class ServletUtilitySpec extends Specification {

    @Unroll("readJsonRequest #queryString")
    def "test read"() {
        setup:
        MockHttpServletRequest request = new MockHttpServletRequest()
        request.setContent(queryString?.bytes)

        when:
        Object result = ServletUtility.readJsonRequest(request, DomainA.class)

        then:
        result == expectedResult

        where:
        queryString                           | expectedResult
        '{"a":1, "b": {"c": 2, "d":"three"}}' | new DomainA(a: 1, b: new DomainB(c: 2, d: "three"))
    }

    @Unroll("readListRequest #queryString")
    def "test readListRequest"() {
        setup:
        MockHttpServletRequest request = new MockHttpServletRequest()
        request.setContent(queryString?.bytes)

        when:
        Object result = ServletUtility.readListRequest(request, DomainA.class)

        then:
        result == expectedResult

        where:
        queryString                                                                     | expectedResult
        '[]'                                                                            | [] as ArrayList
        '[{"a":1, "b": {"c": 2, "d":"three"}}]'                                         | [new DomainA(a: 1, b: new DomainB(c: 2, d: "three"))] as ArrayList
        '[{"a":1, "b": {"c": 2, "d":"three"}}, {"a":10, "b": {"c": 20, "d":"thirty"}}]' | [new DomainA(a: 1, b: new DomainB(c: 2, d: "three")), new DomainA(a: 10, b: new DomainB(c: 20, d: "thirty"))] as ArrayList
    }

    @Unroll("readActionRequest #queryString")
    def "test readActionRequest"() {
        setup:
        MockHttpServletRequest request = new MockHttpServletRequest()
        request.setContent(queryString?.bytes)
        Map<String, ActionConfig<?>> clazzMap = ["action": new ActionConfig(clazz:DomainB.class), "act2": new ActionConfig(clazz:DomainA.class)]

        when:
        ActionBody result = ServletUtility.readActionRequest(request, clazzMap)

        then:
        result == expectedResult

        where:
        queryString                                                               | expectedResult
        '{"actionName": "action", "actionContent": {"c":1, "d":"D"}}'             | new ActionBody(actionName: "action", actionContent: new DomainB(c: 1, d: "D"))
        '{"actionName": "act2", "actionContent": {"a":2, "b":{"c":1, "d":"D"}}}' | new ActionBody(actionName: "act2", actionContent: new DomainA(a: 2, b: new DomainB(c: 1, d: "D")))
    }
}


