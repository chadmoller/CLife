package com.clife.restCommon;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractRestController<T> implements RestController {
    private final Class<T> controllerClass;
    private Map<String, ActionConfig<?>> actionMap;
    private ObjectifySharedService objectifySharedService;
    private Authorizer authorizer;

    public AbstractRestController(Class<T> controllerClass) {
        super();
        this.controllerClass = controllerClass;
        this.setSimpleActionMap(new HashMap());
    }

    public AbstractRestController(Class<T> controllerClass, Authorizer authorizer) {
        super();
        this.controllerClass = controllerClass;
        this.authorizer = authorizer;
        this.setSimpleActionMap(new HashMap());
    }

    public Class<T> getControllerClass() {
        return controllerClass;
    }

    public Authorizer getAuthorizer() { return authorizer; }
    public void setAuthorizer(Authorizer authorizer) { this.authorizer = authorizer; }

    public void setActionMap(Map<String, ActionConfig<?>> actionMap) {
        this.actionMap = actionMap;
    }

    public Map<String, ActionConfig<?>> getActionMap() {
        return actionMap;
    }

    public void setObjectifySharedService(ObjectifySharedService objectifySharedService) {
        this.objectifySharedService = objectifySharedService;
    }

    public ObjectifySharedService getObjectifySharedService() {
        return this.objectifySharedService;
    }

    public void setSimpleActionMap(Map<String, Class<?>> simpleActionMap) {
        this.actionMap = new HashMap<>(simpleActionMap.size() + 1, 1);

        ActionConfig config = new ActionConfig();
        config.method = ReflectionUtils.findMethod(this.getClass(), CommonActions.CREATE.methodName, RestRequest.class);
        config.clazz = controllerClass;
        this.actionMap.put(CommonActions.CREATE.actionName, config);

        for (String key : simpleActionMap.keySet()) {
            config = new ActionConfig();
            config.method = ReflectionUtils.findMethod(this.getClass(), key, RestRequest.class);
            if (config.method == null) {
                throw new RuntimeException(key + " is not a valid action method name");
            }
            config.clazz = simpleActionMap.get(key);
            this.actionMap.put(key, config);
        }
    }

    public Map<String, Class<?>> getSimpleActionMap() {
        Map<String, Class<?>> simpleActionMap= new HashMap<>(actionMap.size(), 1);
        for (String key : actionMap.keySet()) {
            simpleActionMap.put(key, actionMap.get(key).clazz);
            if (!actionMap.get(key).method.getName().equals(key)) {
                throw new RuntimeException(key + " is not a simple action, because it maps to method " + actionMap.get(key).method.getName());
            }
        }
        return simpleActionMap;
    }

    public List<T> list(RestRequest request) {
        performAuthorize(request);
        return objectifySharedService.list(controllerClass);
    }

    public T get(RestRequest request) {
        performAuthorize(request);
        Object id = getConvertedRequestId(request);
        return objectifySharedService.get(controllerClass, id);
    }

    public Object action(RestRequest request) {
        performAuthorize(request);
        ActionBody body = (ActionBody)request.requestBody;

        ActionConfig config = actionMap.get(body.actionName);
        try {
            return config.method.invoke(this, request);
        } catch (Exception ex) {
            throw new RuntimeException("Failed executing request: " + request.toString(), ex);
        }
    }

    public T create(RestRequest request) {
        T body = (T)request.requestBody;

        return objectifySharedService.save(body);
    }

    public T save(RestRequest request) {
        performAuthorize(request);
        T body = verifyUrlIdWithBody(request);

        return objectifySharedService.save(body);
    }

    public List<T> saveAll(RestRequest request){
        performAuthorize(request);
        List<T> body = (List<T>)request.requestBody;
        return objectifySharedService.saveAll(body);
    }

    public void delete(RestRequest request){
        performAuthorize(request);
        Object id = getConvertedRequestId(request);
        objectifySharedService.delete(controllerClass, id);
    }

    public void deleteAll(RestRequest request){
        performAuthorize(request);
        objectifySharedService.deleteAll(controllerClass);
    }

    private T verifyUrlIdWithBody(RestRequest request) {
        if (request.objectId == null) {
            throw new RuntimeException("ID Required");
        }
        T body = (T)request.requestBody;
        Object id = getId(body);
        Object requestId = getConvertedRequestId(request);
        if (!id.equals(requestId)) {
            throw new RuntimeException("Request ID " + requestId + " didn't match body ID " + id);
        }
        return body;
    }

    private Object getId(Object body) {
        Field f = ReflectionUtils.findField(controllerClass, "id");
        f.setAccessible(true);
        return ReflectionUtils.getField(f, body);
    }

    private Object getConvertedRequestId(RestRequest request) {
        if (request.objectId == null) {
            throw new RuntimeException("ID Required");
        }

        try {
            return NumberUtils.createLong(request.objectId);
        } catch(NumberFormatException ex) {
            return request.objectId.toString();
        }
    }


    private void performAuthorize(RestRequest request) {
        AuthorizeOutcome outcome = authorizer.authorize(request);
        if (!outcome.success) {
            throw new SecurityException("Authorization failed " + outcome.description + " : "  + outcome.code);
        }
    }
}
