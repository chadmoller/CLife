package com.clife.restCommon;

import org.springframework.http.HttpMethod;

public enum RestRequestType {
    GET(HttpMethod.GET),
    LIST(HttpMethod.GET),
    ACTION(HttpMethod.POST),
    SAVE(HttpMethod.PUT),
    SAVE_ALL(HttpMethod.PUT),
    DELETE(HttpMethod.DELETE),
    DELETE_ALL(HttpMethod.DELETE);

    public final HttpMethod method;
    RestRequestType(HttpMethod method) {
        this.method = method;
    }
}
