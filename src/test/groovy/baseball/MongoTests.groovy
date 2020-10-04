package baseball

import baseball.domain.*
import baseball.mongo.MongoManager
import mjs.common.utils.LogUtils
import org.junit.After
import org.junit.Before
import org.junit.Test

class MongoTests {

    MongoManager mongoManager

    @Before
    void setUp() {
        if (! LogUtils.isLoggingConfigured()) {
            LogUtils.initializeLogging()
        }
        mongoManager = new MongoManager()
    }

    @After
    void tearDown() {
        mongoManager = null
    }

    @Test
    void testConnectToMongo() {
        def collection = "flintstones"

        println "Opening Mongo connection..."
        mongoManager.open("ballsim")
        println "Opening Mongo connection... Done!"

        long deleteCount = mongoManager.deleteAll(collection)
        println "Deleted all items: count=$deleteCount"

        def object1 = [id: 1, 'name': 'Fred', 'bats': 'L', 'throws': 'R']
        def object2 = [id: 2, 'name': 'Wilma', 'bats': 'R', 'throws': 'R']
        def object3 = [id: 3, 'name': 'Barney', 'bats': 'L', 'throws': 'L']
        def object4 = [id: 4, 'name': 'Betty', 'bats': 'R', 'throws': 'L']

        println "Adding to collection..."
        mongoManager.addToCollection(collection, object1)
        mongoManager.addToCollection(collection, object2)
        mongoManager.addToCollection(collection, object3)
        mongoManager.addToCollection(collection, object4)

        def count = mongoManager.getCount(collection)
        println "Number of items: $count"

        println "FindAll:"
        def result = mongoManager.findAll(collection)
        LogUtils.println(result, "   ", true)
        println ""

        println "Find name='Wilma':"
        result = mongoManager.find(collection, ['name': 'Wilma'])
        LogUtils.println(result, "   ", true)
        println ""

        println "Find bats='L':"
        result = mongoManager.find(collection, ['bats': 'L'])
        LogUtils.println(result, "   ", true)
        println ""
    }
}
