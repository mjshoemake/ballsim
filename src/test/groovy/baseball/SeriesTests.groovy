package baseball

import baseball.domain.BallGame
import baseball.domain.SimTeam
import baseball.processing.HttpHistoricalDataManager
import org.apache.log4j.Logger
import org.junit.After
import org.junit.Before
import org.junit.Test

class SeriesTests {

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
        // 92 over 91, 92 over 93, 95 over 92, 96 over 95, 97 over 96...
        // 97 over 97-2000, 2001 over 97 and 2002, 2004 over 2001...
        // 2004 over 2005-2006, 2007 over 2001, 
        def homeYear = "2004"
        def awayYear = "2007"
        def teams
        def numGames = 7
        auditLog.info "# of Games in the Series: $numGames"

        teams = dataMgr.getTeamMapForSeason(homeYear)
        def home = teams["Braves"]
        teams = dataMgr.getTeamMapForSeason(awayYear)
        def away = teams["Braves"]

        def homeRoster = dataMgr.get40ManRoster(home.team_id, homeYear)
        def awayRoster = dataMgr.get40ManRoster(away.team_id, awayYear)
        if (numGames % 2 == 0) {
            Exception e = new Exception("The number of games in a series must be odd.")
            auditLog.error(e.getMessage(), e)
            throw e
        }
        def numWinsRequired = (((numGames - 1) / 2) + 1)
        auditLog.info "# of Wins Required: $numWinsRequired"
        def team1, team2

        ballGame = new BallGame()
        team1 = new SimTeam(awayRoster, away.city, away.name_display_full, awayYear)
        team2 = new SimTeam(homeRoster, home.city, home.name_display_full, homeYear)

        int gameCount = 0
        while (team1.wins < 4 && team2.wins < 4) {
            gameCount++
            ballGame = new BallGame()
            if (gameCount <= 2 || gameCount >= 6) {
                ballGame.awayTeam = team1
                ballGame.homeTeam = team2
            } else {
                ballGame.awayTeam = team2
                ballGame.homeTeam = team1
            }
            ballGame.start()
            if (team1.wins < 4 && team2.wins < 4) {
                highlightsLog.debug ""
                highlightsLog.debug "Series Score:"
                highlightsLog.debug "${ballGame.homeTeam.year} ${ballGame.homeTeam.teamName}  ${ballGame.homeTeam.wins}    ${ballGame.awayTeam.year} ${ballGame.awayTeam.teamName}  ${ballGame.awayTeam.wins}"
                highlightsLog.debug ""
            }
        }

        highlightsLog.debug ""
        highlightsLog.debug "Final Series Score:"
        highlightsLog.debug "${ballGame.homeTeam.year} ${ballGame.homeTeam.teamName}  ${ballGame.homeTeam.wins}    ${ballGame.awayTeam.year} ${ballGame.awayTeam.teamName}  ${ballGame.awayTeam.wins}"
    }

}
