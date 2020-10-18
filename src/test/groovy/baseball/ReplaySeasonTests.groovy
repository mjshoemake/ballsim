package baseball

import baseball.domain.BallGame
import baseball.domain.Season
import baseball.domain.SimTeam
import baseball.processing.HttpHistoricalDataManager
import org.apache.log4j.Logger
import org.junit.After
import org.junit.Before
import org.junit.Test

class ReplaySeasonTests {

    def ballGame = null
    def auditLog = Logger.getLogger('Audit')
    def highlightsLog = Logger.getLogger('highlights')

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

    @Test
    void testSeries() {
        HttpHistoricalDataManager dataMgr = new HttpHistoricalDataManager()

        // 2003 skipped... could only find 8 batters for lineup.
        def year = "1991"
        def teamMap = dataMgr.getTeamMapForSeason(year)
        def season = new Season(teamMap)


        // Load schedule

        // Iterate through schedule

        // Load 40-Man-Rosters for each game
        //def homeRoster = dataMgr.get40ManRoster(home.team_id, homeYear)
        //def awayRoster = dataMgr.get40ManRoster(away.team_id, awayYear)
        //ballGame = new BallGame()
        //def team1 = new SimTeam(awayRoster, away.city, away.name_display_full, awayYear)
        //def team2 = new SimTeam(homeRoster, home.city, home.name_display_full, homeYear)

    }

}
