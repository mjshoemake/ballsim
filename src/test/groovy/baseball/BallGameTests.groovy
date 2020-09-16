package baseball

import baseball.domain.SimTeam
import baseball.processing.DatabankTeamLoader
import baseball.processing.HttpHistoricalDataManager
import mjs.common.utils.ConfigFileLoader
import mjs.common.model.DatabaseDriver
import baseball.mongo.MongoManager
import org.junit.*
import baseball.domain.BallGame

class BallGameTests {

    def ballGame = null

    //MongoManager mongoDB = new MongoManager()
    //DatabaseDriver dbDriver = null

    @Before
    void setUp() {
        // MySQL
        //Properties dbProps = new ConfigFileLoader("/Users/Dad/Config/").loadPropertiesFile("mysql.properties")
        //dbDriver = new DatabaseDriver(dbProps)
        //mongoDB.open("ballsim")
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
        HttpHistoricalDataManager dataMgr = new HttpHistoricalDataManager()
        def homeYear = "1995"
        def awayYear = "2020"
        def teams
        if (homeYear == awayYear) {

        }
        teams = dataMgr.getTeamMapForSeason(homeYear)
        def home = teams["Braves"]
        teams = dataMgr.getTeamMapForSeason(awayYear)
        def away = teams["Braves"]

        def homeRoster = dataMgr.get40ManRoster(home.team_id, homeYear)
        def awayRoster = dataMgr.get40ManRoster(away.team_id, awayYear)

        ballGame = new BallGame()
        ballGame.with {
            awayTeam = new SimTeam(awayRoster, away.city, away.name_display_full, awayYear)
            homeTeam = new SimTeam(homeRoster, home.city, home.name_display_full, homeYear)
        }
        ballGame.start()
    }

}
