package com.clife.restCommon;

import java.util.HashMap;
import java.util.Map;

public class RestControllerMap extends HashMap<String, RestController> {

    public RestControllerMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public RestControllerMap(int initialCapacity) {
        super(initialCapacity);
    }

    public RestControllerMap() {
    }

    public RestControllerMap(Map<? extends String, ? extends RestController> m) {
        super(m);
    }
}
