package com.clife.identity.controller

import com.clife.identity.domain.User
import com.clife.identity.security.SecurityContext
import com.clife.restCommon.AuthorizeOutcome
import com.clife.restCommon.Authorizer
import com.clife.restCommon.ObjectifySharedService
import com.clife.restCommon.RestRequest
import spock.lang.Specification


class UserControllerSpec extends Specification {
    User testUser
    UserController userController

    Authorizer authorizer
    ObjectifySharedService objectifySharedService

    def setup() {
        testUser = new User()
        SecurityContext.setCurrentUser(testUser)

        authorizer = Mock(Authorizer)
        objectifySharedService = Mock(ObjectifySharedService)

        userController = new UserController()
        userController.setAuthorizer(authorizer)
        userController.setObjectifySharedService(objectifySharedService)
    }

    def cleanup() {
        SecurityContext.clearCurrentUser()
    }

    def "test get with ID"() {
        setup:
        RestRequest request = new RestRequest()
        request.objectId = "1"

        when:
        User result = userController.get(request)

        then:
        1 * authorizer.authorize(request) >> new AuthorizeOutcome(true)
        objectifySharedService.get(User.class, 1l) >> testUser
        result == testUser
    }

    def "test get with ME"() {
        setup:
        RestRequest request = new RestRequest()
        request.objectId = "me"

        when:
        User result = userController.get(request)

        then:
        result == testUser
    }
}