package baseball.domain

import baseball.processing.ScheduleLoader
import org.apache.log4j.Logger

class ScheduledRound {

    /**
     * The Log4J logger used by this object.
     */
    //protected Logger log = Logger.getLogger("Core");
    //def highlightsLog = Logger.getLogger('highlights')
    //def seasonStatsLog = Logger.getLogger('seasonStats')

    int roundNum = 0
    List games = []

    ScheduledRound(int roundNum) {
        this.roundNum = roundNum
    }

    void addGame(ScheduledGame game) {
        games << game
    }

    ScheduledRound copy() {
        ScheduledRound newRound = new ScheduledRound(this.roundNum + 1)
        println "Copying RD: ${newRound.roundNum}  gameCount=${this.games.size()}"
        games.each { next ->
            def copy = next.copy()
            println "   Copying GM: ${copy.roundNum} - ${copy.gameNum}"
            newRound.games << next.copy()
        }
        newRound
    }
}
