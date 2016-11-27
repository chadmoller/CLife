package com.clife.identity.security;

import com.clife.identity.domain.Application;
import com.clife.identity.domain.Authority;
import com.clife.identity.domain.User;
import com.google.appengine.api.users.UserService;

import javax.servlet.http.HttpServletRequest;

public class SecurityContext {
    public static final ThreadLocal<User> userThreadLocal = new ThreadLocal<User>();
    static UserService userService;

    public static final String getLoginUrl(HttpServletRequest request) {
        return userService.createLoginURL(request.getRequestURI());
    }

    public static final String getLogoutUrl(HttpServletRequest request) {
        return userService.createLogoutURL(request.getRequestURI());
    }

    public static final User getCurrentUser() {
        return userThreadLocal.get();
    }

    public static final void setCurrentUser(User user) {
        userThreadLocal.set(user);
    }

    public static final void clearCurrentUser() {
        userThreadLocal.set(null);
    }

    public static final boolean hasAuthority(Authority authority) {
        User user = getCurrentUser();
        if (user == null) {
            return false;
        }
        return user.authorities.contains(authority);
    };

    public static final Authority getApplicationAuthority(Application application) {
        User user = getCurrentUser();
        Authority authority = findAuthority(application, user);
        if (authority == null) {
            authority = findAuthority(Application.SYSTEM, user);
        }

        return authority;
    }

    private static Authority findAuthority(Application application, User user) {
        if (user == null) {
            return null;
        }
        for (Authority authority : user.authorities) {
            if (authority.application == application) {
                return authority;
            }
        }
        return null;
    }

    ;

}
