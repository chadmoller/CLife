package com.clife.restCommon;

public enum CommonActions {
    CREATE("create");

    public final String actionName;
    public final String methodName;

    CommonActions(String actionName) {
        this.actionName = actionName;
        this.methodName = actionName;
    }

    CommonActions(String actionName, String methodName) {
        this.actionName = actionName;
        this.methodName = methodName;
    }
}
