package com.clife.identity.security;

import com.clife.identity.domain.Application;
import com.clife.identity.domain.AuthorityType;
import com.clife.restCommon.AuthorizeOutcome;
import com.clife.restCommon.Authorizer;
import com.clife.restCommon.RestRequest;

public class UserAuthorizer implements Authorizer {
    public AuthorizeOutcome authorize(RestRequest restRequest) {
        switch (restRequest.type) {
            case SAVE_ALL: case DELETE_ALL:
                return listModifyAuthorize();
            case LIST:
                return listAuthorize();
            case SAVE: case GET: case DELETE:
                return singleUserAuthorize(restRequest);
            case ACTION:
                return createAuthorize();
            default:
                throw new RuntimeException("Unsupported rest request type " + restRequest.type);

        }
    }

    protected AuthorizeOutcome listModifyAuthorize() {
        return UserAuthorizerUtil.authorizeByAppAndType(Application.IDENTITY, AuthorityType.ROOT);
    }

    protected AuthorizeOutcome listAuthorize() {
        return UserAuthorizerUtil.authorizeByAppAndType(Application.IDENTITY, AuthorityType.ADMIN);
    }

    protected AuthorizeOutcome singleUserAuthorize(RestRequest restRequest) {
        AuthorizeOutcome outcome = UserAuthorizerUtil.authorizeByAppAndType(Application.IDENTITY, AuthorityType.ADMIN);
        if (outcome.success) {
            return outcome;
        }

        String currentUserId = SecurityContext.getCurrentUser().id.toString();
        String providedUserId = restRequest.objectId;

        if (currentUserId.equals(providedUserId)) {
            outcome = new AuthorizeOutcome(true);
        } else {
            outcome = new AuthorizeOutcome(false, "You can only perform this operation on yourself", "OPERATION_CURRENT_USER_MISMATCH");
        }
        return outcome;
    }

    protected AuthorizeOutcome createAuthorize() {
        return new AuthorizeOutcome(true);
    }
}
