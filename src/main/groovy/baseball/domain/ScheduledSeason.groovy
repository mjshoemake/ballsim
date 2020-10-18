package baseball.domain

import baseball.processing.ScheduleLoader
import org.apache.log4j.Logger

class ScheduledSeason {

    /**
     * The Log4J logger used by this object.
     */
    //protected Logger log = Logger.getLogger("Core");
    //def highlightsLog = Logger.getLogger('highlights')
    //def seasonStatsLog = Logger.getLogger('seasonStats')

    List rounds = []
    int roundCompleted = 0
    int gameCompleted = 0

    ScheduledSeason() {
    }

    void addRound(ScheduledRound round) {
        rounds << round
    }

}
