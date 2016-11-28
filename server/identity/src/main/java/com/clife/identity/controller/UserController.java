package com.clife.identity.controller;

import com.clife.identity.domain.User;
import com.clife.identity.security.SecurityContext;
import com.clife.identity.security.UserAuthorizer;
import com.clife.restCommon.AbstractRestController;
import com.clife.restCommon.Authorizer;
import com.clife.restCommon.RestRequest;

public class UserController extends AbstractRestController<User> {

    public UserController() {
        super(User.class);
    }

    @Override
    public User get(RestRequest request) {
        if ("me".equals(request.objectId)) {
            return SecurityContext.getCurrentUser();
        }

        return super.get(request);
    }
}
