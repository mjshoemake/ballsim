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
class PitcherStats extends Comparable {
    def C = "PitcherStats"
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
    double pitchingWhip
    String pitchingEra
    SimPitcher simPitcher

    PitcherStats() {
    }

    PitcherStats(Map source) {
        //println "Converting Map to PitcherStats..."
        this.name = source.name
        this.pitchingGames = source.pitchingGames
        this.pitchingGamesStarted = source.pitchingGamesStarted
        this.pitchingBattersRetired = source.pitchingBattersRetired
        this.pitchingOrderPos = source.pitchingOrderPos
        this.pitchingWalks = source.pitchingWalks
        this.pitchingRuns = source.pitchingRuns
        this.pitchingEarnedRuns = source.pitchingEarnedRuns
        this.pitchingStrikeouts = source.pitchingStrikeouts
        this.pitchingHits = source.pitchingHits
        this.pitchingHomers = source.pitchingHomers
        this.pitchingWildPitch = source.pitchingWildPitch
        this.pitchingHitBatter = source.pitchingHitBatter
        this.pitchingBalks = source.pitchingBalks
        this.pitchingEra = source.pitchingEra
        this.pitchingWhip = source.pitchingWhip
        if (source.simPitcher) {
            this.simPitcher = new SimPitcher(source.simPitcher)
        }
        //println "Converting Map to PitcherStats... DONE."
    }

    // Is the specified object equal to this one?
    boolean equals(PitcherStats target) {
        boolean result = true
        def m = "${C}.equals() - "
        if (! compareString("pitchingEra", pitchingEra, target.pitchingEra)) { result = false }
        else if (! compareString("name", name, target.name)) { result = false }
        else if (! compareInt("pitchingGames", pitchingGames, target.pitchingGames)) { result = false }
        else if (! compareInt("pitchingGamesStarted", pitchingGamesStarted, target.pitchingGamesStarted)) { result = false }
        else if (! compareInt("pitchingBattersRetired", pitchingBattersRetired, target.pitchingBattersRetired)) { result = false }
        else if (! compareInt("pitchingOrderPos", pitchingOrderPos, target.pitchingOrderPos)) { result = false }
        else if (! compareInt("pitchingWalks", pitchingWalks, target.pitchingWalks)) { result = false }
        else if (! compareInt("pitchingRuns", pitchingRuns, target.pitchingRuns)) { result = false }
        else if (! compareInt("pitchingEarnedRuns", pitchingEarnedRuns, target.pitchingEarnedRuns)) { result = false }
        else if (! compareInt("pitchingStrikeouts", pitchingStrikeouts, target.pitchingStrikeouts)) { result = false }
        else if (! compareInt("pitchingHits", pitchingHits, target.pitchingHits)) { result = false }
        else if (! compareInt("pitchingHomers", pitchingHomers, target.pitchingHomers)) { result = false }
        else if (! compareInt("pitchingWildPitch", pitchingWildPitch, target.pitchingWildPitch)) { result = false }
        else if (! compareInt("pitchingHitBatter", pitchingHitBatter, target.pitchingHitBatter)) { result = false }
        else if (! compareInt("pitchingBalks", pitchingBalks, target.pitchingBalks)) { result = false }
        else if (! compareObject("simPitcher", simPitcher, target.simPitcher)) { result = false }
        else if (! compareDouble("pitchingWhip", pitchingWhip, target.pitchingWhip)) { result = false }
        return result
    }

    // Is the specified object equal to this one?
    boolean equals(PitcherStats target, List builder) {
        boolean result = true
        def m = "${C}.equals() - "
        if (! compareString("pitchingEra", pitchingEra, target.pitchingEra, builder)) { result = false }
        if (! compareString("name", name, target.name, builder)) { result = false }
        if (! compareInt("pitchingGames", pitchingGames, target.pitchingGames, builder)) { result = false }
        if (! compareInt("pitchingGamesStarted", pitchingGamesStarted, target.pitchingGamesStarted, builder)) { result = false }
        if (! compareInt("pitchingBattersRetired", pitchingBattersRetired, target.pitchingBattersRetired, builder)) { result = false }
        if (! compareInt("pitchingOrderPos", pitchingOrderPos, target.pitchingOrderPos, builder)) { result = false }
        if (! compareInt("pitchingWalks", pitchingWalks, target.pitchingWalks, builder)) { result = false }
        if (! compareInt("pitchingRuns", pitchingRuns, target.pitchingRuns, builder)) { result = false }
        if (! compareInt("pitchingEarnedRuns", pitchingEarnedRuns, target.pitchingEarnedRuns, builder)) { result = false }
        if (! compareInt("pitchingStrikeouts", pitchingStrikeouts, target.pitchingStrikeouts, builder)) { result = false }
        if (! compareInt("pitchingHits", pitchingHits, target.pitchingHits, builder)) { result = false }
        if (! compareInt("pitchingHomers", pitchingHomers, target.pitchingHomers, builder)) { result = false }
        if (! compareInt("pitchingWildPitch", pitchingWildPitch, target.pitchingWildPitch, builder)) { result = false }
        if (! compareInt("pitchingHitBatter", pitchingHitBatter, target.pitchingHitBatter, builder)) { result = false }
        if (! compareInt("pitchingBalks", pitchingBalks, target.pitchingBalks, builder)) { result = false }
        if (! compareDouble("pitchingWhip", pitchingWhip, target.pitchingWhip, builder)) { result = false }
        if (! compareObject("simPitcher", simPitcher, target.simPitcher, builder)) { result = false }

        if (result) {
            builder << "$m PitcherStats match?  OK"
        } else {
            builder << "$m PitcherStats match?  NO MATCH"
        }

        return result
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
