package com.clife.identity.security

import com.clife.identity.domain.Application
import com.clife.identity.domain.Authority
import com.clife.identity.domain.AuthorityType
import com.clife.identity.domain.User
import com.clife.restCommon.*
import com.googlecode.objectify.Objectify
import com.googlecode.objectify.ObjectifyService
import org.springframework.util.ReflectionUtils
import spock.lang.Specification

import java.lang.reflect.Method

class UserAuthorizerUtilSpec extends Specification {

    def cleanup() {
        SecurityContext.clearCurrentUser()
    }
    def "test authorizeByAppAndType exact authority"() {
        setup:
        User testUser = new User()
        testUser.authorities = [Authority.IDENTITY_ADMIN, Authority.BODY_TRACKING]
        SecurityContext.setCurrentUser(testUser)

        when:
        AuthorizeOutcome result = UserAuthorizerUtil.authorizeByAppAndType(Application.IDENTITY, AuthorityType.ADMIN)

        then:
        result.success == true
    }

    def "test authorizeByAppAndType excessive authority"() {
        setup:
        User testUser = new User()
        testUser.authorities = [Authority.IDENTITY_ADMIN, Authority.BODY_TRACKING]
        SecurityContext.setCurrentUser(testUser)

        when:
        AuthorizeOutcome result = UserAuthorizerUtil.authorizeByAppAndType(Application.IDENTITY, AuthorityType.USER)

        then:
        result.success == true
    }

    def "test authorizeByAppAndType insufficient authority"() {
        setup:
        User testUser = new User()
        testUser.authorities = [Authority.IDENTITY_ADMIN, Authority.BODY_TRACKING]
        SecurityContext.setCurrentUser(testUser)

        when:
        AuthorizeOutcome result = UserAuthorizerUtil.authorizeByAppAndType(Application.BODY_TRACKING, AuthorityType.ADMIN)

        then:
        result.success == false
        result.description == "Authority USER isn't sufficient when ADMIN is required"
        result.code == "INSUFFICIENT_AUTHORITY"
    }

    def "test authorizeByAppAndType nonexistent authority"() {
        setup:
        User testUser = new User()
        testUser.authorities = [Authority.IDENTITY_ADMIN, Authority.BODY_TRACKING]
        SecurityContext.setCurrentUser(testUser)

        when:
        AuthorizeOutcome result = UserAuthorizerUtil.authorizeByAppAndType(Application.CALENDAR, AuthorityType.ADMIN)

        then:
        result.success == false
        result.description == "Authority NONE isn't sufficient when ADMIN is required"
        result.code == "INSUFFICIENT_AUTHORITY"
    }

    def "test authorizeByAppAndType root"() {
        setup:
        User testUser = new User()
        testUser.authorities = [Authority.ROOT]
        SecurityContext.setCurrentUser(testUser)

        when:
        AuthorizeOutcome result = UserAuthorizerUtil.authorizeByAppAndType(Application.SYSTEM, AuthorityType.ROOT)

        then:
        result.success == true
    }

    def "test authorizeByAppAndType root for other apps"() {
        setup:
        User testUser = new User()
        testUser.authorities = [Authority.ROOT]
        SecurityContext.setCurrentUser(testUser)

        when:
        AuthorizeOutcome result = UserAuthorizerUtil.authorizeByAppAndType(Application.CALENDAR, AuthorityType.ADMIN)

        then:
        result.success == true
    }

}


