
package baseball

import baseball.mongo.MongoManager
import baseball.processing.FileTeamLoader
import org.junit.After
import org.junit.Before
import org.junit.Test

class FileTeamLoaderTest {

    FileTeamLoader loader = null

    MongoManager mongoDB = new MongoManager()

    @Before
    void setUp() {
        mongoDB.open("ballsim")
        loader = new FileTeamLoader(mongoDB)
    }

    @After
    void tearDown() {
        mongoDB.close()
        loader = null
    }

    @Test
    void testDBAccess() {
        loader.loadTeamFromFile("Phillies", 2003)
    }
}