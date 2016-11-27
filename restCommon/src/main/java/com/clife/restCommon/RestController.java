package com.clife.restCommon;

import java.util.List;
import java.util.Map;

public interface RestController<T> {
    Class<T> getControllerClass();
    Map<String, ActionConfig<?>> getActionMap();

    List<T> list(RestRequest request);
    T get(RestRequest request);
    Object action(RestRequest request);
    T save(RestRequest request);
    List<T> saveAll(RestRequest request);
    void delete(RestRequest request);
    void deleteAll(RestRequest request);
}