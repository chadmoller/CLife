package com.clife.restCommon;

public interface Authorizer {
    AuthorizeOutcome authorize(RestRequest request);
}
