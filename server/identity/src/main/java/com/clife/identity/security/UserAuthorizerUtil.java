package com.clife.identity.security;

import com.clife.identity.domain.Application;
import com.clife.identity.domain.Authority;
import com.clife.identity.domain.AuthorityType;
import com.clife.restCommon.AuthorizeOutcome;

public class UserAuthorizerUtil {
    public static AuthorizeOutcome authorizeByAppAndType(Application application, AuthorityType authorityType) {
        Authority authority = SecurityContext.getApplicationAuthority(application);
        AuthorizeOutcome outcome;
        if (authority == null || authorityType.compareTo(authority.authorityType) < 0) {
            String authTypeString = (authority==null?"NONE":authority.authorityType.toString());
            outcome = new AuthorizeOutcome(false, "Authority " + authTypeString + " isn't sufficient when " + authorityType + " is required", "INSUFFICIENT_AUTHORITY");
        } else {
            outcome = new AuthorizeOutcome(true);
        }

        return outcome;
    }
}
