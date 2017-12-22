package baseball.domain

import org.bson.types.ObjectId

import java.lang.reflect.Field

class Batter extends BatterComparable
{
    ObjectId _id
    String type = Batter.class.name
    String year
    String teamID
    String playerID
    String nameFirst
    String nameLast
    String nameNick
    int birthYear
    String armBats
    String armThrows
    int games = 0
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

    Map<String, FieldingPosition> position = new HashMap<String, FieldingPosition>()
    SimBatter simBatter

    Batter() {
    }

    static String getBoxScoreHeader() {
        return ""
    }

    public String getBoxScore() {
        return "${name.padRight(30, ' ')}  ${hits.toString().padRight(3)} ${atBats.toString().padRight(4)}  ${homers.toString().padRight(4)}  "
    }

    public def getBattingAvg() {
        if (hits == 0 || atBats == 0) {
            BigDecimal.valueOf(0.0)
        } else {
            BigDecimal.valueOf(hits / atBats)
        }
    }

    public def getFieldingPercentage() {
        try {
            BigDecimal.valueOf((assists + putouts) / (assists + putouts + errors))
        } catch (Exception e) {
            new BigDecimal(0)
        }
    }

    public def getPlateAppearances() {
        atBats + walks + hitByPitch
    }

    public def getRate(int num) {
        if (plateAppearances == 0) {
            BigDecimal.valueOf(0)
        } else {
            BigDecimal.valueOf(num / plateAppearances)
        }
    }

    public def getRate(int num, int divisor) throws Exception {
        if (divisor == 0) {
            BigDecimal.valueOf(0)
        } else {
            BigDecimal.valueOf(num / divisor)
        }
    }

    public def getSluggingPercentage() {
        int singles = hits - homers - triples - doubles
        int numerator = (4 * homers) + (3 * triples) + (2 * doubles) + singles
        if (numerator == 0 || atBats == 0) {
            BigDecimal.valueOf(0)
        } else {
            BigDecimal.valueOf(numerator / atBats)
        }
    }

    public def getOnBasePercentage() {
        int numerator = hits + walks + hitByPitch
        int denominator = atBats + walks + hitByPitch + sacrificeFlies
        if (numerator == 0 || denominator == 0) {
            BigDecimal.valueOf(0)
        } else {
            BigDecimal.valueOf(numerator / denominator)
        }
    }

    public def getOps() {
        sluggingPercentage + onBasePercentage
    }

    public def getRank() {
        def value = BigDecimal.valueOf((ops + fieldingPercentage) / 3)
        if (atBats < 40) {
            value = BigDecimal.ZERO
        } else if (atBats < 80) {
            value = value * 0.5
        }
        calculatedRank = value
        value
    }

}
