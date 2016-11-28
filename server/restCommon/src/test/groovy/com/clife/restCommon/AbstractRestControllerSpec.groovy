package com.clife.restCommon

import com.googlecode.objectify.Key
import com.googlecode.objectify.LoadResult
import com.googlecode.objectify.Objectify
import com.googlecode.objectify.Result
import com.googlecode.objectify.cmd.Deleter
import com.googlecode.objectify.cmd.LoadType
import com.googlecode.objectify.cmd.Loader
import com.googlecode.objectify.cmd.Saver
import org.springframework.util.ReflectionUtils
import spock.lang.Specification

import java.lang.reflect.Method

class AbstractRestControllerSpec extends Specification {
    private Method method

    private class DumbyService {
        public Object actionService() {
            return null;
        }
    }

    private class DumbyAuthorizer implements Authorizer {
        public AuthorizeOutcome authorize(RestRequest request) {
            return new AuthorizeOutcome(true)
        }
    }

    private class ConcreteTestRestController extends AbstractRestController<DomainB> {
        public DumbyService actionService;
        public ConcreteTestRestController(Authorizer authorizer) {
            super(DomainB.class, authorizer);
        }

        public Object customAction(RestRequest restRequest) {
            return actionService.actionService();
        }

        public Object badAction(DomainB badParam) {
            return null;
        }
    }

    Authorizer buildMockAuthorizer() {
        return Mock(Authorizer.class)
    }
    def "test setSimpleActionMap"() {
        setup:
        ConcreteTestRestController restController = new ConcreteTestRestController(buildMockAuthorizer())
        restController.actionService = Mock(DumbyService.class)
        Map<String, Class<?>> simpleActionMap = ["customAction":DomainA.class];

        when:
        restController.setSimpleActionMap(simpleActionMap)

        then:
        restController.actionMap.size() == 2
        restController.actionMap.containsKey("customAction")
        restController.actionMap.get("customAction").clazz == DomainA.class
        restController.actionMap.get("customAction").method == restController.class.getMethod("customAction", RestRequest.class)
        restController.actionMap.containsKey("create")
        restController.actionMap.get("create").clazz == DomainB.class
        restController.actionMap.get("create").method == restController.class.getMethod("create", RestRequest.class)
    }

    def "test setSimpleActionMap override create"() {
        setup:
        ConcreteTestRestController restController = new ConcreteTestRestController(buildMockAuthorizer())
        restController.actionService = Mock(DumbyService.class)
        Map<String, Class<?>> simpleActionMap = ["create":DomainA.class];

        when:
        restController.setSimpleActionMap(simpleActionMap)

        then:
        restController.actionMap.size() == 1
        restController.actionMap.containsKey("create")
        restController.actionMap.get("create").clazz == DomainA.class
        restController.actionMap.get("create").method == restController.class.getMethod("create", RestRequest.class)
    }

    def "test setSimpleActionMap bad"() {
        setup:
        ConcreteTestRestController restController = new ConcreteTestRestController(buildMockAuthorizer())
        restController.actionService = Mock(DumbyService.class)
        Map<String, Class<?>> simpleActionMap = ["badAction":DomainB.class];

        when:
        restController.setSimpleActionMap(simpleActionMap)

        then:
        RuntimeException ex = thrown()
        ex.message == "badAction is not a valid action method name"
    }

    def "test getSimpleActionMap"() {
        setup:
        ConcreteTestRestController restController = new ConcreteTestRestController(buildMockAuthorizer())
        restController.actionService = Mock(DumbyService.class)
        Method method = restController.class.getMethod("customAction", RestRequest.class)
        Map<String, ActionConfig> actionMap = ["customAction":new ActionConfig(DomainB.class, method)]
        restController.actionMap = actionMap

        when:
        Map<String, Class<?>> result = restController.getSimpleActionMap()

        then:
        result.size() == 1
        result.containsKey("customAction")
        result.get("customAction") == DomainB.class
    }

    def "test getSimpleActionMap bad"() {
        setup:
        ConcreteTestRestController restController = new ConcreteTestRestController(buildMockAuthorizer())
        restController.actionService = Mock(DumbyService.class)
        Method method = restController.class.getMethod("customAction", RestRequest.class)
        Map<String, ActionConfig> actionMap = ["customAction2":new ActionConfig(DomainB.class, method)]
        restController.actionMap = actionMap

        when:
        restController.getSimpleActionMap()

        then:
        RuntimeException ex = thrown()
        ex.message == "customAction2 is not a simple action, because it maps to method customAction"
    }

    def "test list"() {
        setup:
        ObjectifySharedService objectifySharedService = Mock(ObjectifySharedService)
        List<DomainB> expectedResult = []

        ConcreteTestRestController restController = new ConcreteTestRestController(buildMockAuthorizer())
        restController.objectifySharedService = objectifySharedService
        RestRequest request = new RestRequest()

        when:
        List result = restController.list(request)

        then:
        1 * restController.authorizer.authorize(request) >> new AuthorizeOutcome(true, "Description", "CODE")
        objectifySharedService.list(DomainB.class) >> expectedResult
        result == expectedResult
    }

    def "test list not authorized"() {
        setup:
        ConcreteTestRestController<DomainB> restController = new ConcreteTestRestController(buildMockAuthorizer())
        RestRequest request = new RestRequest()

        when:
        restController.list(request)

        then:
        restController.authorizer.authorize(request) >> new AuthorizeOutcome(false, "Description", "CODE")
        SecurityException ex = thrown()
        ex.message == "Authorization failed Description : CODE"
    }

    def "test get long ID"() {
        setup:
        ObjectifySharedService objectifySharedService = Mock(ObjectifySharedService)
        DomainB expectedResult = new DomainB()

        ConcreteTestRestController<DomainB> restController = new ConcreteTestRestController(buildMockAuthorizer())
        restController.objectifySharedService = objectifySharedService
        RestRequest request = new RestRequest()
        request.objectId = "1"

        when:
        DomainB result = restController.get(request)

        then:
        1 * restController.authorizer.authorize(request) >> new AuthorizeOutcome(true)
        objectifySharedService.get(DomainB.class, 1l) >> expectedResult
        result == expectedResult
    }

    def "test get String ID"() {
        setup:
        ObjectifySharedService objectifySharedService = Mock(ObjectifySharedService)
        DomainB expectedResult = new DomainB()

        ConcreteTestRestController<DomainB> restController = new ConcreteTestRestController(buildMockAuthorizer())
        restController.objectifySharedService = objectifySharedService
        RestRequest request = new RestRequest()
        request.objectId = "a"

        when:
        DomainB result = restController.get(request)

        then:
        1 * restController.authorizer.authorize(request) >> new AuthorizeOutcome(true)
        objectifySharedService.get(DomainB.class, "a") >> expectedResult
        result == expectedResult
    }

    def "test get no id"() {
        setup:
        ConcreteTestRestController<DomainB> restController = new ConcreteTestRestController(buildMockAuthorizer())
        RestRequest request = new RestRequest()

        when:
        restController.get(request)

        then:
        1 * restController.authorizer.authorize(request) >> new AuthorizeOutcome(true)
        RuntimeException ex = thrown()
        ex.message == "ID Required"
    }

    def "test get not authorized"() {
        setup:
        ConcreteTestRestController<DomainB> restController = new ConcreteTestRestController(buildMockAuthorizer())
        RestRequest request = new RestRequest()

        when:
        restController.get(request)

        then:
        restController.authorizer.authorize(request) >> new AuthorizeOutcome(false, "Description", "CODE")
        SecurityException ex = thrown()
        ex.message == "Authorization failed Description : CODE"
    }

    def "test action"() {
        setup:
        ConcreteTestRestController restController = new ConcreteTestRestController(buildMockAuthorizer())
        restController.actionService = Mock(DumbyService.class)
        method = ReflectionUtils.findMethod(ConcreteTestRestController.class, "customAction", RestRequest.class)
        restController.actionMap = ["customAction": new ActionConfig(method: method, clazz: String.class)]

        RestRequest request = new RestRequest()
        ActionBody<String> actionBody = new ActionBody<>(actionName: "customAction", actionContent: "a")
        request.requestBody = actionBody

        when:
        String result = restController.action(request)

        then:
        1 * restController.authorizer.authorize(request) >> new AuthorizeOutcome(true)
        restController.actionService.actionService() >> "Result String"
        result == "Result String"
    }

    def "test action with exception"() {
        setup:
        ConcreteTestRestController restController = new ConcreteTestRestController(buildMockAuthorizer())
        restController.actionService = Mock(DumbyService.class)
        method = ReflectionUtils.findMethod(ConcreteTestRestController.class, "customAction", RestRequest.class)
        restController.actionMap = ["customAction": new ActionConfig(method: method, clazz: String.class)]

        RestRequest request = new RestRequest()
        ActionBody<String> actionBody = new ActionBody<>(actionName: "customAction", actionContent: "a")
        request.requestBody = actionBody

        when:
        restController.action(request)

        then:
        1 * restController.authorizer.authorize(request) >> new AuthorizeOutcome(true)
        restController.actionService.actionService() >> { throw new RuntimeException("Runtime Exception thrown") }
        RuntimeException ex = thrown()
        ex.message == "Failed executing request: {\"controllerName\":null,\"controller\":null,\"objectId\":null,\"type\":null,\"parentFilter\":null,\"queryParams\":null,\"requestBody\":{\"actionName\":\"customAction\",\"actionContent\":\"a\"},\"originalServletRequest\":null}"
    }

    def "test action not authorized"() {
        setup:
        ConcreteTestRestController<DomainB> restController = new ConcreteTestRestController(buildMockAuthorizer())
        RestRequest request = new RestRequest()

        when:
        restController.action(request)

        then:
        restController.authorizer.authorize(request) >> new AuthorizeOutcome(false, "Description", "CODE")
        SecurityException ex = thrown()
        ex.message == "Authorization failed Description : CODE"
    }

    def "test create"() {
        setup:
        ObjectifySharedService objectifySharedService = Mock(ObjectifySharedService)
        DomainB expectedResult = new DomainB()

        ConcreteTestRestController<DomainB> restController = new ConcreteTestRestController(buildMockAuthorizer())
        restController.objectifySharedService = objectifySharedService
        RestRequest request = new RestRequest()
        request.requestBody = expectedResult

        when:
        DomainB result = restController.create(request)

        then:
        0 * restController.authorizer.authorize(request)
        objectifySharedService.save(expectedResult) >> expectedResult
        result == expectedResult
    }

    def "test save"() {
        setup:
        ObjectifySharedService objectifySharedService = Mock(ObjectifySharedService)
        DomainB expectedResult = new DomainB()

        ConcreteTestRestController<DomainB> restController = new ConcreteTestRestController(buildMockAuthorizer())
        restController.objectifySharedService = objectifySharedService
        RestRequest request = new RestRequest()
        expectedResult.id = "id"
        request.requestBody = expectedResult
        request.objectId = "id"

        when:
        DomainB result = restController.save(request)

        then:
        1 * restController.authorizer.authorize(request) >> new AuthorizeOutcome(true)
        1 * objectifySharedService.save(expectedResult) >> expectedResult
        result == expectedResult
    }

    def "test save id mismatch"() {
        setup:
        ObjectifySharedService objectifySharedService = Mock(ObjectifySharedService)
        DomainB expectedResult = new DomainB()

        ConcreteTestRestController<DomainB> restController = new ConcreteTestRestController(buildMockAuthorizer())
        restController.objectifySharedService = objectifySharedService
        RestRequest request = new RestRequest()
        expectedResult.id = "id"
        request.requestBody = expectedResult
        request.objectId = "id2"

        when:
        DomainB result = restController.save(request)

        then:
        1 * restController.authorizer.authorize(request) >> new AuthorizeOutcome(true)
        RuntimeException ex = thrown()
        ex.message == "Request ID id2 didn't match body ID id"
    }

    def "test save no id"() {
        setup:
        ConcreteTestRestController<DomainB> restController = new ConcreteTestRestController(buildMockAuthorizer())
        RestRequest request = new RestRequest()
        request.objectId = null

        when:
        restController.save(request)

        then:
        1 * restController.authorizer.authorize(request) >> new AuthorizeOutcome(true)
        RuntimeException ex = thrown()
        ex.message == "ID Required"
    }

    def "test save not authorized"() {
        setup:
        ConcreteTestRestController<DomainB> restController = new ConcreteTestRestController(buildMockAuthorizer())
        RestRequest request = new RestRequest()

        when:
        restController.save(request)

        then:
        restController.authorizer.authorize(request) >> new AuthorizeOutcome(false, "Description", "CODE")
        SecurityException ex = thrown()
        ex.message == "Authorization failed Description : CODE"
    }

    def "test saveAll"() {
        setup:
        ObjectifySharedService objectifySharedService = Mock(ObjectifySharedService)
        DomainB domainB = new DomainB()
        List<DomainB> requestBody = [domainB]

        ConcreteTestRestController<DomainB> restController = new ConcreteTestRestController(buildMockAuthorizer())
        restController.objectifySharedService = objectifySharedService
        RestRequest request = new RestRequest()
        request.requestBody = requestBody

        when:
        List<DomainB> result = restController.saveAll(request)

        then:
        1 * restController.authorizer.authorize(request) >> new AuthorizeOutcome(true)
        objectifySharedService.saveAll(requestBody) >> requestBody
        result == requestBody
    }

    def "test saveAll not authorized"() {
        setup:
        ConcreteTestRestController<DomainB> restController = new ConcreteTestRestController(buildMockAuthorizer())
        RestRequest request = new RestRequest()

        when:
        restController.saveAll(request)

        then:
        restController.authorizer.authorize(request) >> new AuthorizeOutcome(false, "Description", "CODE")
        SecurityException ex = thrown()
        ex.message == "Authorization failed Description : CODE"
    }

    def "test delete number"() {
        setup:
        ObjectifySharedService objectifySharedService = Mock(ObjectifySharedService)

        ConcreteTestRestController<DomainB> restController = new ConcreteTestRestController(buildMockAuthorizer())
        restController.objectifySharedService = objectifySharedService
        RestRequest request = new RestRequest()
        request.objectId = "1"

        when:
        restController.delete(request)

        then:
        1 * restController.authorizer.authorize(request) >> new AuthorizeOutcome(true)
        1 * objectifySharedService.delete(DomainB.class, 1l)
    }

    def "test delete string"() {
        setup:
        ObjectifySharedService objectifySharedService = Mock(ObjectifySharedService)

        ConcreteTestRestController<DomainB> restController = new ConcreteTestRestController(buildMockAuthorizer())
        restController.objectifySharedService = objectifySharedService
        RestRequest request = new RestRequest()
        request.objectId = "id"

        when:
        restController.delete(request)

        then:
        1 * restController.authorizer.authorize(request) >> new AuthorizeOutcome(true)
        1 * objectifySharedService.delete(DomainB.class, "id")
    }

    def "test delete no id"() {
        setup:
        ConcreteTestRestController<DomainB> restController = new ConcreteTestRestController(buildMockAuthorizer())
        RestRequest request = new RestRequest()
        request.objectId = null

        when:
        restController.delete(request)

        then:
        1 * restController.authorizer.authorize(request) >> new AuthorizeOutcome(true)
        RuntimeException ex = thrown()
        ex.message == "ID Required"
    }

    def "test delete not authorized"() {
        setup:
        ConcreteTestRestController<DomainB> restController = new ConcreteTestRestController(buildMockAuthorizer())
        RestRequest request = new RestRequest()

        when:
        restController.delete(request)

        then:
        restController.authorizer.authorize(request) >> new AuthorizeOutcome(false, "Description", "CODE")
        SecurityException ex = thrown()
        ex.message == "Authorization failed Description : CODE"
    }

    def "test deleteAll"() {
        setup:
        ObjectifySharedService objectifySharedService = Mock(ObjectifySharedService)

        ConcreteTestRestController<DomainB> restController = new ConcreteTestRestController(buildMockAuthorizer())
        restController.objectifySharedService = objectifySharedService
        RestRequest request = new RestRequest()

        when:
        restController.deleteAll(request)

        then:
        1 * restController.authorizer.authorize(request) >> new AuthorizeOutcome(true)
        1 * objectifySharedService.deleteAll(DomainB.class)
    }

    def "test deleteAll not authorized"() {
        setup:
        ConcreteTestRestController<DomainB> restController = new ConcreteTestRestController(buildMockAuthorizer())
        RestRequest request = new RestRequest()

        when:
        restController.deleteAll(request)

        then:
        restController.authorizer.authorize(request) >> new AuthorizeOutcome(false, "Description", "CODE")
        SecurityException ex = thrown()
        ex.message == "Authorization failed Description : CODE"
    }


}


