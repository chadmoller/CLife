package com.clife.restCommon;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

public class RestRequestFactory {
    RestControllerMap restControllerMap;

    public RestRequestFactory(RestControllerMap map) {
        this.restControllerMap = map;
    }

    public RestRequest getRestRequest(HttpServletRequest request) throws IOException {
        String pathInfo = request.getPathInfo();
        RestRequest restRequest = new RestRequest();
        restRequest.originalServletRequest = request;
        restRequest.queryParams = buildQueryParamMap(request);

        if (pathInfo == null) {
            throw new RuntimeException("Controller required");
        }
        if (pathInfo.startsWith("/")) {
            pathInfo = pathInfo.substring(1);
        }
        String[] pathParts = pathInfo.split("/");

        restRequest.parentFilter = getParentFilter(pathParts);
        int baseControllerIndex = restRequest.parentFilter.size() * 2;
        restRequest.controllerName = pathParts[baseControllerIndex];
        if (pathParts.length > (baseControllerIndex + 1)) {
            restRequest.objectId = pathParts[baseControllerIndex + 1];
        }

        restRequest.controller = getRestController(restRequest);
        restRequest.requestBody = getRequestBody(restRequest);
        return restRequest;
    }

    protected Map<String, String> getParentFilter(String[] pathParts) {
        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; (i+1)*2 < pathParts.length; i++) {
            map.put(pathParts[i*2], pathParts[(i*2) + 1]);
        }
        return map;
    }

    protected Object getRequestBody(RestRequest restRequest) throws IOException {
        Object requestObj = null;
        if (restRequest.originalServletRequest.getContentLength() > 0) {
            requestObj = ServletUtility.readJsonRequest(restRequest.originalServletRequest, restRequest.controller.getControllerClass());
        }
        return requestObj;
    }

    protected Object getRequestJsonBody(RestRequest restRequest) throws IOException {
        Object requestObj = null;
        if (restRequest.originalServletRequest.getContentLength() > 0) {
            requestObj = ServletUtility.readJsonRequest(restRequest.originalServletRequest, restRequest.controller.getControllerClass());
        }
        return requestObj;
    }

    protected Object getRequestListBody(RestRequest restRequest) throws IOException {
        Object requestObj = null;
        if (restRequest.originalServletRequest.getContentLength() > 0) {
            requestObj = ServletUtility.readListRequest(restRequest.originalServletRequest, restRequest.controller.getControllerClass());
        }
        return requestObj;
    }

    protected ActionBody getRequestActionBody(RestRequest restRequest) throws IOException {
        ActionBody requestObj = null;
        if (restRequest.originalServletRequest.getContentLength() > 0) {
            requestObj = ServletUtility.readActionRequest(restRequest.originalServletRequest, restRequest.controller.getActionMap());
        }
        return requestObj;
    }

    protected RestController getRestController(RestRequest restRequest) {
        RestController controller = restControllerMap.get(restRequest.controllerName);
        if (controller == null) {
            throw new RuntimeException("Unconfigured controller " + restRequest.controllerName);
        }
        return controller;
    }

    protected Map<String, List<String>> buildQueryParamMap(HttpServletRequest request) {
        Map<String, List<String>> map = new HashMap<>();
        if (request.getQueryString() == null) {
            return map;
        }

        List<NameValuePair> pairs = URLEncodedUtils.parse(request.getQueryString(), Charset.forName("UTF-8"));
        for (NameValuePair nvp : pairs) {
            List<String> list = map.get(nvp.getName());
            if (list == null) {
                list = new ArrayList<>();
                map.put(nvp.getName(), list);
            }
            list.add(nvp.getValue());
        }
        return map;
    }
}
