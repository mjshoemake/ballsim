package baseball.domain

import baseball.processing.ScheduleLoader
import org.apache.log4j.Logger

class ScheduledGame extends Comparable {

    /**
     * The Log4J logger used by this object.
     */
    protected Logger log = Logger.getLogger("Debug");
    //def highlightsLog = Logger.getLogger('highlights')
    //def seasonStatsLog = Logger.getLogger('seasonStats')

    int roundNum = 1
    int gameNum = 1
    String homeTeam, awayTeam

    ScheduledGame() {
    }

    ScheduledGame(Map map) {
        this.homeTeam = map.homeTeam
        this.awayTeam = map.awayTeam
        this.roundNum = map.roundNum
        this.gameNum = map.gameNum
    }

    boolean equals(ScheduledGame target) {
        boolean result = true
        def m = "${C}.equals() - "
        if (! compareString("homeTeam", homeTeam, target.homeTeam)) { result = false }
        if (! compareString("awayTeam", awayTeam, target.awayTeam)) { result = false }
        if (! compareInt("roundNum", roundNum, target.roundNum)) { result = false }
        if (! compareInt("gameNum", gameNum, target.gameNum)) { result = false }
        return result
    }

    boolean equals(ScheduledGame target, List builder) {
        boolean result = true
        def m = "${C}.equals() - "
        if (! compareString("homeTeam", homeTeam, target.homeTeam, builder)) { result = false }
        if (! compareString("awayTeam", awayTeam, target.awayTeam, builder)) { result = false }
        if (! compareInt("roundNum", roundNum, target.roundNum, builder)) { result = false }
        if (! compareInt("gameNum", gameNum, target.gameNum, builder)) { result = false }
        if (result) {
            builder << "$m ScheduledGames match?  OK"
        } else {
            builder << "$m ScheduledGames match?  NO MATCH"
        }
        return result
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
