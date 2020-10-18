
package baseball

import baseball.mongo.MongoManager
import baseball.processing.FileTeamLoader
import baseball.processing.ScheduleLoader
import org.junit.After
import org.junit.Before
import org.junit.Test

class ScheduleLoaderTest {

    ScheduleLoader loader = null

    MongoManager mongoDB = new MongoManager()

    @Before
    void setUp() {
        mongoDB.open("ballsim")
        loader = new ScheduleLoader()
    }

    @After
    void tearDown() {
        mongoDB.close()
        loader = null
    }

    @Test
    void testLoadSchedule() {
        println "ScheduleLoaderTest.testLoadSchedule() START"
        loader.loadScheduleFromFile("AA7-AB7_BA6-BB6", 26)
        println "ScheduleLoaderTest.testLoadSchedule() DONE"
    }
}