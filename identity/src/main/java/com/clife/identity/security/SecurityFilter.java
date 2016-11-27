package com.clife.identity.security;

import com.clife.identity.dao.UserDao;
import com.clife.identity.domain.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import java.io.IOException;

public class SecurityFilter implements Filter {
    protected UserService userService;
    protected UserDao userDao;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(filterConfig.getServletContext());
        userService = UserServiceFactory.getUserService();
        userDao = (UserDao) ctx.getBean("userDao");
        SecurityContext.userService = userService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        com.google.appengine.api.users.User googleUser = userService.getCurrentUser();
        User systemUser = null;
        if (googleUser != null) {
            systemUser = userDao.findUser(googleUser.getEmail());
            if (systemUser == null) {
                systemUser = userDao.createUser(googleUser.getEmail(), googleUser.getNickname());
            }
        }
        SecurityContext.setCurrentUser(systemUser);

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            SecurityContext.clearCurrentUser();
        }
    }

    @Override
    public void destroy() {
    }
}
