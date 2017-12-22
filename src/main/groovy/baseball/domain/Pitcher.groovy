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
class Pitcher
{
    //def gameLog = Logger.getLogger('gamelog')

    ObjectId _id
    String type = Pitcher.class.name
    String teamID
    String playerID
    String nameFirst
    String nameLast
    String nameNick
    int birthYear
    String armBats
    String armThrows
    String year
    String position
    int games
    int gamesStarted
    int battersRetired
    int orderPos
    int walks
    int runs
    int strikeouts
    int hits
    int homers
    int wildPitch
    int hitByPitch
    int balks
    double whip
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
