package com.clife.restCommon;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RestServlet extends HttpServlet {
    RestRequestFactory restRequestFactory;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(servletConfig.getServletContext());
        RestControllerMap restControllerMap = (RestControllerMap) ctx.getBean("restControllerMap");
        restRequestFactory = new RestRequestFactory(restControllerMap);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RestRequest restRequest = restRequestFactory.getRestRequest(req);

        Object result;
        if (restRequest.objectId == null) {
            restRequest.type = RestRequestType.LIST;
            result = restRequest.controller.list(restRequest);
        } else {
            restRequest.type = RestRequestType.GET;
            result = restRequest.controller.get(restRequest);
        }
        ServletUtility.writeJsonResponse(resp, result);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RestRequest restRequest = restRequestFactory.getRestRequest(req);

        restRequest.requestBody = restRequestFactory.getRequestActionBody(restRequest);
        requestBodyRequred(restRequest);
        restRequest.type = RestRequestType.ACTION;
        Object result = restRequest.controller.action(restRequest);
        ServletUtility.writeJsonResponse(resp, result);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RestRequest restRequest = restRequestFactory.getRestRequest(req);

        requestBodyRequred(restRequest);
        Object result;
        if (restRequest.objectId == null) {
            restRequest.requestBody = restRequestFactory.getRequestListBody(restRequest);
            restRequest.type = RestRequestType.SAVE_ALL;
            result = restRequest.controller.saveAll(restRequest);
        } else {
            restRequest.requestBody = restRequestFactory.getRequestBody(restRequest);
            restRequest.type = RestRequestType.SAVE;
            result = restRequest.controller.save(restRequest);
        }
        ServletUtility.writeJsonResponse(resp, result);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RestRequest restRequest = restRequestFactory.getRestRequest(req);

        if (restRequest.objectId == null) {
            restRequest.type = RestRequestType.DELETE_ALL;
            restRequest.controller.deleteAll(restRequest);
        } else {
            restRequest.type = RestRequestType.DELETE;
            restRequest.controller.delete(restRequest);
        }
        ServletUtility.writeJsonStringResponse(resp, "{'success':true}");
    }

    private void requestBodyRequred(RestRequest restRequest) {
        if (restRequest.requestBody == null) {
            throw new RuntimeException("Request Body Required");
        }
    }
}
