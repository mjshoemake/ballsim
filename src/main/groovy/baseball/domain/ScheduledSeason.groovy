package baseball.domain

import baseball.processing.ScheduleLoader
import org.apache.log4j.Logger

class ScheduledSeason extends Comparable {

    /**
     * The Log4J logger used by this object.
     */
    protected Logger log = Logger.getLogger("Debug");
    //def highlightsLog = Logger.getLogger('highlights')
    //def seasonStatsLog = Logger.getLogger('seasonStats')

    def C = "ScheduledSeason"
    List rounds = []
    int roundCompleted = 0
    int gameCompleted = 0

    ScheduledSeason() {
    }

    ScheduledSeason(Map map) {
        this.roundCompleted = map.roundCompleted
        this.gameCompleted = map.gameCompleted
        map.rounds.each() { Map next ->
            rounds << new ScheduledRound(next)
        }
    }

    boolean equals(ScheduledSeason target) {
        boolean result = true
        def m = "${C}.equals() - "
        if (! rounds.equals(target.rounds)) { result = false }
        else if (! compareInt("roundCompleted", roundCompleted, target.roundCompleted)) { result = false }
        else if (! compareInt("roundNum", gameCompleted, target.gameCompleted)) { result = false }
        return result
    }

    boolean equals(ScheduledSeason target, List builder) {
        boolean result = true
        def m = "${C}.equals() - "
        if (! rounds.equals(target.rounds)) { result = false }
        else if (! compareInt("roundCompleted", roundCompleted, target.roundCompleted, builder)) { result = false }
        else if (! compareInt("roundNum", gameCompleted, target.gameCompleted, builder)) { result = false }
        if (result) {
            builder << "$m ScheduledSeasons match?  OK"
        } else {
            builder << "$m ScheduledSeasons match?  NO MATCH"
        }
        return result
    }

    void addRound(ScheduledRound round) {
        rounds << round
    }

}
