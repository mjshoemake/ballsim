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
class Pitcher extends Batter
{
    //def gameLog = Logger.getLogger('gamelog')


    //String type = Pitcher.class.name
    //boolean isPitcher = false

    int atBats = 0
    int walks = 0
    int strikeouts = 0
    int hits = 0
    int doubles = 0
    int triples = 0
    int homers = 0
    int hitByPitch = 0
    int stolenBases = 0
    int caughtStealing = 0
    int sacrificeFlies = 0
    BigDecimal calculatedRank = BigDecimal.valueOf(0.0)

    // Overall fielding #s
    int putouts = 0
    int assists = 0
    int errors = 0
    int catcherSteals = 0
    int catcherCaught = 0

    int pitchingGamesStarted
    int pitchingBattersRetired
    int pitchingOrderPos
    int pitchingWalks
    int pitchingRuns
    int pitchingStrikeouts
    int pitchingHits
    int pitchingHomers
    int pitchingWildPitch
    int pitchingHitBatter
    int pitchingBalks
    double pitchingWhip
    SimPitcher simPitcher

    Pitcher() {
    _id = new ObjectId()
}

    public def getBattersFaced() {
        battersRetired + walks + hits + hitByPitch
    }

    def getOppBattingAvg() {
        int oppAtBats = battersRetired + hits
        if (oppAtBats == 0) {
            BigDecimal.valueOf(0)
        }  else {
            BigDecimal.valueOf(hits / oppAtBats)
        }
    }

    public def getRate(int num) {
        def rate = BigDecimal.valueOf(num / battersFaced)
        rate
    }

    public def getAvgBattersRetiredPerGame() {
        def avgOuts = BigDecimal.valueOf(battersRetired / games).intValue()
        avgOuts
    }

    public def getRate(int num, int divisor) {
        def rate = BigDecimal.valueOf(num / divisor)
        rate
    }

    public setWhip(String value) {
        BigDecimal result = new BigDecimal(value)
        whip = result.toDouble().doubleValue()
    }

    def getEra() {
        BigDecimal games = new BigDecimal(battersRetired)
        games = games.divide(27, 5, RoundingMode.HALF_UP)
        if (games.intValue() == 0) {
            return "0.00"
        }
        BigDecimal result = new BigDecimal(runs)
        result = result.divide(games, 3, RoundingMode.HALF_UP)
        String padded = result.toString() + "000"
        if (result.intValue() >= 10) {
            format(padded, 5)
        } else {
            format(padded, 4) + ' '
        }
        result
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
