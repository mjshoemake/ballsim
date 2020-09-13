package baseball


import baseball.domain.SimTeam

import baseball.processing.HttpHistoricalDataManager
import org.junit.After
import org.junit.Before
import org.junit.Test

class ProcessTeamPlayersTests {

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
    void processTeamPlayers() {
        HttpHistoricalDataManager dataMgr = new HttpHistoricalDataManager()
        String year = "1999"
        def teams = dataMgr.getTeamMapForSeason(year)
        def team = teams["Braves"]
        def roster = dataMgr.get40ManRoster(team.team_id, year)
        def simTeam = new SimTeam(roster)
    }

}
