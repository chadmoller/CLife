package com.clife.restCommon;

import java.lang.reflect.Method;

/**
 * Created by chadmoller on 11/3/16.
 */
public class ActionConfig<T> {
    public Class<T> clazz;
    public Method method;

    public ActionConfig(Class<T> clazz, Method method) {
        super();
        this.clazz = clazz;
        this.method = method;
    }

    public ActionConfig() {
        super();
    }
}
