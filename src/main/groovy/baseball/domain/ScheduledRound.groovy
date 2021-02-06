package baseball.domain

import baseball.processing.ScheduleLoader
import org.apache.log4j.Logger

class ScheduledRound extends Comparable {

    /**
     * The Log4J logger used by this object.
     */
    //protected Logger log = Logger.getLogger("Core");
    //def highlightsLog = Logger.getLogger('highlights')
    //def seasonStatsLog = Logger.getLogger('seasonStats')

    def C = "ScheduledRound"
    int roundNum = 0
    List games = []

    ScheduledRound(int roundNum) {
        this.roundNum = roundNum
    }

    ScheduledRound(Map map) {
        this.roundNum = map.roundNum
        map.games.each() { Map next ->
            games << new ScheduledGame(next)
        }
    }

    boolean equals(ScheduledRound target) {
        boolean result = true
        def m = "${C}.equals() - "
        if (! games.equals(target.games)) { result = false }
        else if (! compareInt("roundNum", roundNum, target.roundNum)) { result = false }
        return result
    }

    boolean equals(ScheduledRound target, List builder) {
        boolean result = true
        def m = "${C}.equals() - "
        if (! games.equals(target.games, builder)) { result = false }
        else if (! compareInt("roundNum", roundNum, target.roundNum, builder)) { result = false }
        if (result) {
            builder << "$m ScheduledRounds match?  OK"
        } else {
            builder << "$m ScheduledRounds match?  NO MATCH"
        }
        return result
    }

    void addGame(ScheduledGame game) {
        games << game
    }

    ScheduledRound copy() {
        ScheduledRound newRound = new ScheduledRound(this.roundNum + 1)
        //println "Copying RD: ${newRound.roundNum}  gameCount=${this.games.size()}"
        games.each { next ->
            def copy = next.copy()
            //println "   Copying GM: ${copy.roundNum} - ${copy.gameNum}"
            newRound.games << next.copy()
        }
        newRound
    }
}
