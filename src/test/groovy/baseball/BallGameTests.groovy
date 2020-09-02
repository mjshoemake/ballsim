package baseball

import baseball.processing.DatabankTeamLoader

import mjs.common.utils.ConfigFileLoader
import mjs.common.model.DatabaseDriver
import baseball.mongo.MongoManager
import org.junit.*
import baseball.domain.BallGame

class BallGameTests {

    def ballGame = null

    MongoManager mongoDB = new MongoManager()
    DatabaseDriver dbDriver = null

    @Before
    void setUp() {
        // MySQL
        Properties dbProps = new ConfigFileLoader("/Users/Dad/Config/").loadPropertiesFile("mysql.properties")
        dbDriver = new DatabaseDriver(dbProps)

        mongoDB.open("ballsim")
    }

    @After
    void tearDown() {
        ballGame == null
    }

/*
    @Test
    void testPlayGame() {
        ballGame = new BallGame()

        def bravesTeamLoader = new BravesTeamLoader(mongoDB)
        def fileTeamLoader = new FileTeamLoader(mongoDB)
        def braves2003 = bravesTeamLoader.loadBraves2003()
        def phillies2003 = fileTeamLoader.loadTeamFromFile("Phillies", 2003)

        ballGame.with {
            awayTeam = braves2003
            homeTeam = phillies2003
        }
        ballGame.start()
    }
*/

    @Test
    void testPlayGame2() {
        ballGame = new BallGame()

        def dbTeamLoader = new DatabankTeamLoader(mongoDB, dbDriver)
        def braves2003 = dbTeamLoader.loadTeamFromMysql("Braves", 2003)
        def phillies2003 = dbTeamLoader.loadTeamFromMysql("Braves", 1997)

        ballGame.with {
            awayTeam = braves2003
            homeTeam = phillies2003
        }
        ballGame.start()
    }

}
