package baseball


import baseball.mongo.MongoManager
import mjs.common.utils.LogUtils
import org.junit.After
import org.junit.Before
import org.junit.Test

class CleanMongoTests {

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
    void testCleanMongo() {
        String TABLE_TEAMS_FOR_SEASON = "teamsForSeason"
        String TABLE_TEAM_MAP_FOR_SEASON = "teamMapForSeason"
        String TABLE_TEAM_ROSTER_FOR_SEASON = "teamRosterForSeason"
        String TABLE_TEST = "flintstones"

        println "Opening Mongo connection..."
        mongoManager.open("ballsim")
        println "Opening Mongo connection... Done!"

        mongoManager.deleteAll(TABLE_TEAM_ROSTER_FOR_SEASON)
        mongoManager.deleteAll(TABLE_TEAMS_FOR_SEASON)
        mongoManager.deleteAll(TABLE_TEAM_MAP_FOR_SEASON)
        mongoManager.deleteAll(TABLE_TEST)

        println "Mongo cleaned!! No more data!"
    }
}
