package com.clife.identity.security

import com.clife.identity.domain.Application
import com.clife.identity.domain.Authority
import com.clife.identity.domain.AuthorityType
import com.clife.identity.domain.User
import com.clife.restCommon.AuthorizeOutcome
import spock.lang.Specification

class SecurityContextSpec extends Specification {

    def cleanup() {
        SecurityContext.clearCurrentUser()
    }
    def "test setCurrentUser"() {
        setup:
        User testUser = new User()
        SecurityContext.setCurrentUser(testUser)

        when:
        User result = SecurityContext.getCurrentUser()

        then:
        result == testUser
    }

    def "test clearCurrentUser"() {
        setup:
        User testUser = new User()
        SecurityContext.setCurrentUser(testUser)
        SecurityContext.clearCurrentUser()

        when:
        User result = SecurityContext.getCurrentUser()

        then:
        result == null
    }

    def "test hasAuthority no current user"() {
        setup:

        when:
        boolean result = SecurityContext.hasAuthority(Authority.BODY_TRACKING)

        then:
        result == false
    }

    def "test hasAuthority true"() {
        setup:
        User testUser = new User()
        testUser.authorities = [Authority.BODY_TRACKING]
        SecurityContext.setCurrentUser(testUser)

        when:
        boolean result = SecurityContext.hasAuthority(Authority.BODY_TRACKING)

        then:
        result == true
    }

    def "test hasAuthority false"() {
        setup:
        User testUser = new User()
        testUser.authorities = [Authority.BODY_TRACKING]
        SecurityContext.setCurrentUser(testUser)

        when:
        boolean result = SecurityContext.hasAuthority(Authority.BODY_TRACKING_ADMIN)

        then:
        result == false
    }

    def "test getApplicationAuthority no current user"() {
        setup:

        when:
        Authority result = SecurityContext.getApplicationAuthority(Application.BODY_TRACKING)

        then:
        result == null
    }

    def "test getApplicationAuthority has app"() {
        setup:
        User testUser = new User()
        testUser.authorities = [Authority.BODY_TRACKING]
        SecurityContext.setCurrentUser(testUser)

        when:
        Authority result = SecurityContext.getApplicationAuthority(Application.BODY_TRACKING)

        then:
        result == Authority.BODY_TRACKING
    }

    def "test getApplicationAuthority no app"() {
        setup:
        User testUser = new User()
        testUser.authorities = [Authority.BODY_TRACKING]
        SecurityContext.setCurrentUser(testUser)

        when:
        Authority result = SecurityContext.getApplicationAuthority(Application.IDENTITY)

        then:
        result == null
    }

}