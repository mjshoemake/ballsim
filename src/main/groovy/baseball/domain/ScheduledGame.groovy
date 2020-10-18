package baseball.domain

import baseball.processing.ScheduleLoader
import org.apache.log4j.Logger

class ScheduledGame {

    /**
     * The Log4J logger used by this object.
     */
    //protected Logger log = Logger.getLogger("Core");
    //def highlightsLog = Logger.getLogger('highlights')
    //def seasonStatsLog = Logger.getLogger('seasonStats')

    int roundNum = 1
    int gameNum = 1
    String homeTeam, awayTeam

    ScheduledGame() {
    }

    ScheduledGame copy() {
        ScheduledGame newGame = new ScheduledGame()
        newGame.roundNum = this.roundNum + 1
        newGame.gameNum = this.gameNum
        newGame.homeTeam = this.homeTeam
        newGame.awayTeam = this.awayTeam
        newGame
    }

}
