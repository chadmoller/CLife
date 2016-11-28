package com.clife.identity.security

import com.clife.identity.domain.Authority
import com.clife.identity.domain.User
import com.clife.restCommon.AuthorizeOutcome
import com.clife.restCommon.RestRequest
import com.clife.restCommon.RestRequestType
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class UserAuthorizerSpec extends Specification {

    @Shared
    User testRegularUser
    @Shared
    User testAdminUser
    @Shared
    User testRootUser

    UserAuthorizer userAuthorizer

    def setupSpec() {
        testRegularUser = new User()
        testRegularUser.authorities = [Authority.IDENTITY, Authority.BODY_TRACKING_ADMIN]
        testRegularUser.fullName = "Regular User"
        testRegularUser.id = 1l
        testAdminUser = new User()
        testAdminUser.authorities = [Authority.IDENTITY_ADMIN]
        testAdminUser.fullName = "Admin User"
        testAdminUser.id = 2l
        testRootUser = new User()
        testRootUser.authorities = [Authority.ROOT]
        testRootUser.fullName = "Root User"
        testRootUser.id = 3l
    }

    def setup() {
        userAuthorizer = new UserAuthorizer()
    }

    def cleanup() {
        SecurityContext.clearCurrentUser()
    }

    @Unroll("authorize #testUser.fullName #requestType #objId")
    def "test authorize"() {
        setup:
        SecurityContext.setCurrentUser(testUser)
        RestRequest restRequest = new RestRequest(type: requestType, objectId: objId)

        when:
        AuthorizeOutcome result = userAuthorizer.authorize(restRequest);

        then:
        result.success == expectedResult
        result.description == expectedDescription
        result.code == expectedCode

        where:
        testUser        | requestType                | objId | expectedResult | expectedDescription                                      | expectedCode
        testRegularUser | RestRequestType.LIST       | null  | false          | "Authority USER isn't sufficient when ADMIN is required" | "INSUFFICIENT_AUTHORITY"
        testRegularUser | RestRequestType.GET        | 1l    | true           | null                                                     | null
        testRegularUser | RestRequestType.GET        | 2l    | false          | "You can only perform this operation on yourself"        | "OPERATION_CURRENT_USER_MISMATCH"
        testRegularUser | RestRequestType.SAVE_ALL   | null  | false          | "Authority USER isn't sufficient when ROOT is required"  | "INSUFFICIENT_AUTHORITY"
        testRegularUser | RestRequestType.SAVE       | 1l    | true           | null                                                     | null
        testRegularUser | RestRequestType.SAVE       | 2l    | false          | "You can only perform this operation on yourself"        | "OPERATION_CURRENT_USER_MISMATCH"
        testRegularUser | RestRequestType.DELETE_ALL | null  | false          | "Authority USER isn't sufficient when ROOT is required"  | "INSUFFICIENT_AUTHORITY"
        testRegularUser | RestRequestType.DELETE     | 1l    | true           | null                                                     | null
        testRegularUser | RestRequestType.DELETE     | 2l    | false          | "You can only perform this operation on yourself"        | "OPERATION_CURRENT_USER_MISMATCH"
        testRegularUser | RestRequestType.ACTION     | null  | true           | null                                                     | null
        testAdminUser   | RestRequestType.LIST       | null  | true           | null                                                     | null
        testAdminUser   | RestRequestType.GET        | 1l    | true           | null                                                     | null
        testAdminUser   | RestRequestType.GET        | 2l    | true           | null                                                     | null
        testAdminUser   | RestRequestType.SAVE_ALL   | null  | false          | "Authority ADMIN isn't sufficient when ROOT is required" | "INSUFFICIENT_AUTHORITY"
        testAdminUser   | RestRequestType.SAVE       | 1l    | true           | null                                                     | null
        testAdminUser   | RestRequestType.SAVE       | 2l    | true           | null                                                     | null
        testAdminUser   | RestRequestType.DELETE_ALL | null  | false          | "Authority ADMIN isn't sufficient when ROOT is required" | "INSUFFICIENT_AUTHORITY"
        testAdminUser   | RestRequestType.DELETE     | 1l    | true           | null                                                     | null
        testAdminUser   | RestRequestType.DELETE     | 2l    | true           | null                                                     | null
        testAdminUser   | RestRequestType.ACTION     | null  | true           | null                                                     | null
        testRootUser    | RestRequestType.LIST       | null  | true           | null                                                     | null
        testRootUser    | RestRequestType.GET        | 3l    | true           | null                                                     | null
        testRootUser    | RestRequestType.GET        | 2l    | true           | null                                                     | null
        testRootUser    | RestRequestType.SAVE_ALL   | null  | true           | null                                                     | null
        testRootUser    | RestRequestType.SAVE       | 3l    | true           | null                                                     | null
        testRootUser    | RestRequestType.SAVE       | 2l    | true           | null                                                     | null
        testRootUser    | RestRequestType.DELETE_ALL | null  | true           | null                                                     | null
        testRootUser    | RestRequestType.DELETE     | 3l    | true           | null                                                     | null
        testRootUser    | RestRequestType.DELETE     | 2l    | true           | null                                                     | null
        testRootUser    | RestRequestType.ACTION     | null  | true           | null                                                     | null
    }


}


