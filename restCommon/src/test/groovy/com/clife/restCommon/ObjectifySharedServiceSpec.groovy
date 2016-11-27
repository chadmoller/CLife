package com.clife.restCommon

import com.googlecode.objectify.LoadResult
import com.googlecode.objectify.Objectify
import com.googlecode.objectify.ObjectifyService
import com.googlecode.objectify.Result
import com.googlecode.objectify.cmd.LoadType
import com.googlecode.objectify.cmd.Loader
import com.googlecode.objectify.cmd.Saver
import spock.lang.Specification

class ObjectifySharedServiceSpec extends Specification {

    private Objectify mockObjectify
    private ObjectifySharedService objectifySharedService

    def setup() {
        mockObjectify = Mock(Objectify)
        ObjectifyService.push(mockObjectify)
        objectifySharedService = new ObjectifySharedService()
    }

    def "test list"() {
        setup:
        Loader loader = Mock(Loader)
        LoadType loadType = Mock(LoadType)
        List expectedResult = []

        when:
        List result = objectifySharedService.list(DomainB.class)

        then:
        mockObjectify.load() >> loader
        loader.type(DomainB.class) >> loadType
        loadType.list() >> expectedResult
        result == expectedResult
    }

    def "test get String"() {
        setup:
        Loader loader = Mock(Loader)
        LoadType loadType = Mock(LoadType)
        LoadResult loadResult = Mock(LoadResult)
        DomainB expectedResult = new DomainB()

        when:
        DomainB result = objectifySharedService.get(DomainB.class, "1")

        then:
        mockObjectify.load() >> loader
        loader.type(DomainB.class) >> loadType
        loadType.id("1") >> loadResult
        loadResult.now() >> expectedResult
        result == expectedResult
    }

    def "test get Number"() {
        setup:
        Loader loader = Mock(Loader)
        LoadType loadType = Mock(LoadType)
        LoadResult loadResult = Mock(LoadResult)
        DomainB expectedResult = new DomainB()

        when:
        DomainB result = objectifySharedService.get(DomainB.class, 1l)

        then:
        mockObjectify.load() >> loader
        loader.type(DomainB.class) >> loadType
        loadType.id(1l) >> loadResult
        loadResult.now() >> expectedResult
        result == expectedResult
    }

    def "test get with Null"() {
        setup:

        when:
        objectifySharedService.get(DomainB.class, null)

        then:
        RuntimeException ex = thrown()
        ex.message == "ID Can't be null"
    }

    def "test save"() {
        setup:
        Saver saver = Mock(Saver)
        Result saveResult = Mock(Result)
        DomainB expectedResult = new DomainB()

        when:
        DomainB result = objectifySharedService.save(expectedResult)

        then:
        mockObjectify.save() >> saver
        saver.entity(expectedResult) >> saveResult
        1 * saveResult.now()
        result == expectedResult
    }

    def "test save all"() {
        setup:
        Saver saver = Mock(Saver)
        Result saveResult = Mock(Result)
        DomainB domain = new DomainB()
        List<DomainB> expectedResult = [domain]

        when:
        List<DomainB> result = objectifySharedService.saveAll(expectedResult)

        then:
        mockObjectify.save() >> saver
        saver.entities(expectedResult) >> saveResult
        1 * saveResult.now()
        result == expectedResult
    }
}
