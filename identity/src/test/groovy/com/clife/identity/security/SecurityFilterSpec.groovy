package com.clife.identity.security

import com.clife.identity.dao.UserDao
import com.clife.identity.domain.User
import com.google.appengine.api.users.UserService
import spock.lang.Specification

import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse

class SecurityFilterSpec extends Specification {

    UserService userService
    UserDao userDao
    FilterChain filterChain
    SecurityFilter securityFilter
    ServletRequest servletRequest
    ServletResponse servletResponse
    com.google.appengine.api.users.User googleUser

    def setup() {
        securityFilter = new SecurityFilter()
        userService = Mock(UserService)
        userDao = Mock(UserDao)
        securityFilter.userService = userService
        securityFilter.userDao = userDao

        filterChain = Mock(FilterChain)
        servletRequest = Mock(ServletRequest)
        servletResponse = Mock(ServletResponse)
        googleUser = new com.google.appengine.api.users.User("test@email.com", "")

        SecurityContext.clearCurrentUser()
    }

    def cleanup() {
        SecurityContext.clearCurrentUser()
    }

    def "test no google user"() {
        setup:
        User capturedUser

        when:
        securityFilter.doFilter(servletRequest, servletResponse, filterChain)

        then:
        1 * filterChain.doFilter(servletRequest, servletResponse) >> {
            capturedUser = SecurityContext.getCurrentUser()
        }
        0 * userDao.findUser("test@email.com")
        capturedUser == null
        SecurityContext.getCurrentUser() == null
    }

    def "test existing user"() {
        setup:
        User capturedUser
        User existingUser = new User()

        when:
        securityFilter.doFilter(servletRequest, servletResponse, filterChain)

        then:
        1 * userService.getCurrentUser() >> googleUser
        1 * userDao.findUser("test@email.com") >> existingUser
        1 * filterChain.doFilter(servletRequest, servletResponse) >> {
            capturedUser = SecurityContext.getCurrentUser()
        }
        capturedUser == existingUser
        SecurityContext.getCurrentUser() == null
    }

    def "test new user"() {
        setup:
        User capturedUser
        User newUser = new User()

        when:
        securityFilter.doFilter(servletRequest, servletResponse, filterChain)

        then:
        1 * userService.getCurrentUser() >> googleUser
        1 * userDao.findUser("test@email.com") >> null
        1 * userDao.createUser("test@email.com", "test") >> newUser
        1 * filterChain.doFilter(servletRequest, servletResponse) >> {
            capturedUser = SecurityContext.getCurrentUser()
        }
        capturedUser == newUser
        SecurityContext.getCurrentUser() == null
    }

}