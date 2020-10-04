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
        // 7 game series results:
        // 92 over 91, 92 over 93, 95 over 92, 96 over 95, 97 over 96...
        // 97 over 96/97/98/99/00, 01 over 97/02, 04 over 01/05/06...
        // 07 over 04/08, 09 over 07

        // 51 game series results:
        // 91 over 92,
        // 93 over 91/95/96,
        // 97 over 93/98/99,
        // 00 over 97,
        // 01 over 00,
        // 02 over 01,
        // 04 over 02/05...
        // 06 over 04/07,
        // 08 over 06/09/10/11/12...
        // 13 over 08/14/15/16/17/18/19

        // 161 game series results:
        // 91 over 92 (11), 93 (12)
        // 95 over 91 (9),  96 (8)
        // 97 over 95 (8),  98 (13)
        // 99 over 97 (15), 00 (16), 01 (22)
        // 02 over 99 (2),  04 (5),  05 (17), 06 (6), 07 (23), 08 (5)
        // 09 over 02 (11), 10 (14), 11 (23)
        // 12 over 09 (11)
        // 13 over 12 (17)

        // 1995
        // Wins: 02 (6), 18 (14), 96 (22), 09 (13), 04 (19), 08 (5), 06 (9), 13 (6), 99 (11), 98 (24), 10 (9), 20 (19)
        // Losses: 97 (3),  91 (2)

        // 2002
        // Wins: 87 (47), 89 (34), 90 (32), 91 (4), 92 (24), 93 (13), 98 (9), 99 (8), 00 (27), 01 (17), 04 (5),  05 (17), 06 (6), 07 (23), 08 (5), 10 (9), 11 (12), 12 (13), 13 (6), 14 (5), 15 (), 16 (37), 17 (18), 19 (21), 20 (9)
        // Losses: 95 (19), 96 (12), 97 (23), 09 (11), 18 (21)

        // 1991
        // Wins: 96 (8), 04 (14), 08 (27), 06 (18), 13 (16), 98 (11), 99 (1), 10 (3),
        // Losses: 02 (1),  97 (4),  18 (21), 95 (4),  09 (11), 20 (3)

        // 1997
        // Wins: 09 (13), 91 (14), 08 (9), 06 (6), 13 (19), 99 (9), 98 (35),
        // Losses: 02 (2),  18 (7),  95 (21), 96 (10), 04 (12), 10 (3),  20 (5)

        // 1996
        // Wins: 02 (12), 97 (10), 95 (5), 09 (4), 08 (18), 99 (6)
        // Losses: 18 (3),  91 (18), 04 (14), 06 (12), 13 (3),  98 (10), 10 (21), 20 (8)


        // Rank: 02, 97, 18, 95, 96, 09?, 91, 04, 08?, 06?, 13, 99, 98, 10, 20



        // 2003 skipped... could only find 8 batters for lineup.
        def homeYear = "1991"
        def awayYear = "2020"
        def teams
        def numGames = 161
        auditLog.info "# of Games in the Series: $numGames"

        teams = dataMgr.getTeamMapForSeason(homeYear)
        def home = teams["Braves"]
        teams = dataMgr.getTeamMapForSeason(awayYear)
        def away = teams["Braves"]

        auditLog.info "Home Team ID: $home.team_id  Away Team ID: $away.team_id"
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
        while (team1.wins < numWinsRequired && team2.wins < numWinsRequired) {
            gameCount++
            ballGame = new BallGame()
            if (gameCount % 2 == 1) {
                ballGame.awayTeam = team1
                ballGame.homeTeam = team2
            } else {
                ballGame.awayTeam = team2
                ballGame.homeTeam = team1
            }
            ballGame.start()
            if (team1.wins < numWinsRequired && team2.wins < numWinsRequired) {
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
