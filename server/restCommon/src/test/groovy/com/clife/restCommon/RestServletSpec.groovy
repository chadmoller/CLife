package com.clife.restCommon

import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

import javax.servlet.ServletConfig
import javax.servlet.ServletContext

class RestServletSpec extends Specification {

    def "test init"() {
        setup:
        ServletConfig mockServletConfig = Mock(ServletConfig)
        ServletContext mockServletContext = Mock(ServletContext)
        WebApplicationContext mockWebAppContext = Mock(WebApplicationContext)
        RestControllerMap expectedMap = Mock(RestControllerMap)
        RestServlet restServlet = new RestServlet()

        when:
        restServlet.init(mockServletConfig);

        then:
        mockServletConfig.getServletContext() >> mockServletContext
        mockServletContext.getAttribute(WebApplicationContext.class.getName() + ".ROOT")  >> mockWebAppContext
        mockWebAppContext.getBean("restControllerMap") >> expectedMap
        restServlet.restRequestFactory != null
        restServlet.restRequestFactory.restControllerMap == expectedMap
    }

    def "test doGet list"() {
        setup:
        MockHttpServletRequest request = new MockHttpServletRequest()
        MockHttpServletResponse response = new MockHttpServletResponse()
        RestRequestFactory mockRestRequestFactory = Mock(RestRequestFactory)
        RestServlet restServlet = new RestServlet()
        restServlet.restRequestFactory = mockRestRequestFactory
        RestRequest restRequest = new RestRequest()
        RestController mockRestController = Mock(RestController)
        restRequest.controller = mockRestController
        List<String> testResults = ["string1", "string2"]

        when:
        restServlet.doGet(request, response)

        then:
        mockRestRequestFactory.getRestRequest(request) >> restRequest
        mockRestController.list(restRequest) >> testResults
        response.getContentAsString() == "[\"string1\",\"string2\"]"
        restRequest.type == RestRequestType.LIST
    }

    def "test doGet get"() {
        setup:
        MockHttpServletRequest request = new MockHttpServletRequest()
        MockHttpServletResponse response = new MockHttpServletResponse()
        RestRequestFactory mockRestRequestFactory = Mock(RestRequestFactory)
        RestServlet restServlet = new RestServlet()
        restServlet.restRequestFactory = mockRestRequestFactory
        RestRequest restRequest = new RestRequest()
        RestController mockRestController = Mock(RestController)
        restRequest.controller = mockRestController
        restRequest.objectId = "id"
        String testResults = "string1"

        when:
        restServlet.doGet(request, response)

        then:
        mockRestRequestFactory.getRestRequest(request) >> restRequest
        mockRestController.get(restRequest) >> testResults
        response.getContentAsString() == "\"string1\""
        restRequest.type == RestRequestType.GET
    }


    def "test doPut saveAll"() {
        setup:
        MockHttpServletRequest request = new MockHttpServletRequest()
        MockHttpServletResponse response = new MockHttpServletResponse()
        RestRequestFactory mockRestRequestFactory = Mock(RestRequestFactory)
        RestServlet restServlet = new RestServlet()
        restServlet.restRequestFactory = mockRestRequestFactory
        RestRequest restRequest = new RestRequest()
        RestController mockRestController = Mock(RestController)
        restRequest.controller = mockRestController
        restRequest.requestBody = "requestBody"
        List<String> testResults = ["string1", "string2"]

        when:
        restServlet.doPut(request, response)

        then:
        mockRestRequestFactory.getRestRequest(request) >> restRequest
        mockRestController.saveAll(restRequest) >> testResults
        response.getContentAsString() == "[\"string1\",\"string2\"]"
        restRequest.type == RestRequestType.SAVE_ALL
    }

    def "test doPut save"() {
        setup:
        MockHttpServletRequest request = new MockHttpServletRequest()
        MockHttpServletResponse response = new MockHttpServletResponse()
        RestRequestFactory mockRestRequestFactory = Mock(RestRequestFactory)
        RestServlet restServlet = new RestServlet()
        restServlet.restRequestFactory = mockRestRequestFactory
        RestRequest restRequest = new RestRequest()
        RestController mockRestController = Mock(RestController)
        restRequest.controller = mockRestController
        restRequest.objectId = "id"
        restRequest.requestBody = "requestBody"
        String testResults = "string1"

        when:
        restServlet.doPut(request, response)

        then:
        mockRestRequestFactory.getRestRequest(request) >> restRequest
        mockRestController.save(restRequest) >> testResults
        response.getContentAsString() == "\"string1\""
        restRequest.type == RestRequestType.SAVE
    }

    def "test doDelete deleteAll"() {
        setup:
        MockHttpServletRequest request = new MockHttpServletRequest()
        MockHttpServletResponse response = new MockHttpServletResponse()
        RestRequestFactory mockRestRequestFactory = Mock(RestRequestFactory)
        RestServlet restServlet = new RestServlet()
        restServlet.restRequestFactory = mockRestRequestFactory
        RestRequest restRequest = new RestRequest()
        RestController mockRestController = Mock(RestController)
        restRequest.controller = mockRestController

        when:
        restServlet.doDelete(request, response)

        then:
        mockRestRequestFactory.getRestRequest(request) >> restRequest
        1 * mockRestController.deleteAll(restRequest)
        response.getContentAsString() == "{'success':true}"
        restRequest.type == RestRequestType.DELETE_ALL
    }

    def "test doDelete delete"() {
        setup:
        MockHttpServletRequest request = new MockHttpServletRequest()
        MockHttpServletResponse response = new MockHttpServletResponse()
        RestRequestFactory mockRestRequestFactory = Mock(RestRequestFactory)
        RestServlet restServlet = new RestServlet()
        restServlet.restRequestFactory = mockRestRequestFactory
        RestRequest restRequest = new RestRequest()
        RestController mockRestController = Mock(RestController)
        restRequest.controller = mockRestController
        restRequest.objectId = "id"

        when:
        restServlet.doDelete(request, response)

        then:
        mockRestRequestFactory.getRestRequest(request) >> restRequest
        1 * mockRestController.delete(restRequest)
        response.getContentAsString() == "{'success':true}"
        restRequest.type == RestRequestType.DELETE
    }
}


