package baseball.domain

import org.bson.types.ObjectId

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Transient;


//import javax.persistence.Column;
import javax.persistence.Table
import java.math.RoundingMode


//import org.apache.log4j.Logger
class PitcherStats {
    String name = ""
    int pitchingGames
    int pitchingGamesStarted
    int pitchingBattersRetired
    int pitchingOrderPos
    int pitchingWalks
    int pitchingRuns
    int pitchingEarnedRuns
    int pitchingStrikeouts
    int pitchingHits
    int pitchingHomers
    int pitchingWildPitch
    int pitchingHitBatter
    int pitchingBalks
    String pitchingEra
    double pitchingWhip
    SimPitcher simPitcher

    PitcherStats() {
    }

    public def getBattersFaced() {
        pitchingBattersRetired + pitchingWalks + pitchingHits + pitchingHitBatter
    }

    def getOppBattingAvg() {
        int oppAtBats = pitchingBattersRetired + pitchingHits
        if (oppAtBats == 0) {
            BigDecimal.valueOf(0)
        }  else {
            BigDecimal.valueOf(pitchingHits / oppAtBats)
        }
    }

    public def getRate(int num) {
        def rate = BigDecimal.valueOf(num / battersFaced)
        rate
    }

    public def getAvgBattersRetiredPerGame() {
        def avgOuts = BigDecimal.valueOf(pitchingBattersRetired / pitchingGames).intValue()
        avgOuts
    }

    public def getRate(int num, int divisor) {
        def rate = BigDecimal.valueOf(num / divisor)
        rate
    }

    public setPitchingWhip(String value) {
        try {
            BigDecimal result = new BigDecimal(value)
            pitchingWhip = result.toDouble().doubleValue()
        } catch (Exception e) {
            println "Failed to convert whip '$value' to a BigDecimal value."
        }
    }

/*
    def getEra() {
        BigDecimal games = new BigDecimal(pitchingBattersRetired)
        games = games.divide(27, 5, RoundingMode.HALF_UP)
        if (games.intValue() == 0) {
            return "0.00"
        }
        BigDecimal result = new BigDecimal(pitchingEarnedRuns)
        result = result.divide(games, 3, RoundingMode.HALF_UP)
        String padded = result.toString() + "000"
        if (result.intValue() >= 10) {
            format(padded, 5)
        } else {
            format(padded, 4) + ' '
        }
        result
    }
*/

    def getEra() {
        pitchingEra
    }

    private def format(def text, int length) {
        text = text + ''
        if (text.length() < length) {
            int remainder = length - text.length()
            text + (" " * remainder)
        } else {
            text.substring(0, length)
        }
    }

}
