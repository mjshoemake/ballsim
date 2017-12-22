package baseball

import baseball.mongo.MongoManager
import baseball.processing.FileTeamLoader

import static org.junit.Assert.*
import org.junit.*

import baseball.processing.BravesTeamLoader

class BravesTeamLoaderTests {

    BravesTeamLoader loader = null

    MongoManager mongoDB = new MongoManager()

    @Before
    void setUp() {
        mongoDB.open("ballsim")
        loader = new BravesTeamLoader(mongoDB)
    }

    @After
    void tearDown() {
        mongoDB.close()
        loader = null
    }

    @Test
    void testDBAccess() {
        loader.loadBraves2003()
        def shef = loader.findBatter("2003", "Braves", "Gary Sheffield")
        println "Name: ${shef?.name}"
    }
}
