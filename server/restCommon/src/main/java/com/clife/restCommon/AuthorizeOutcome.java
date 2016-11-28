package com.clife.restCommon;

public class AuthorizeOutcome {
    public boolean success;
    public String description;
    public String code;

    public AuthorizeOutcome(boolean success) {
        this(success, null, null);
    }

    public AuthorizeOutcome(boolean success, String description, String code) {
        this.success = success;
        this.description = description;
        this.code = code;
    }

}
