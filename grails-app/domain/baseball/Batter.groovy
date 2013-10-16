package baseball

import org.apache.log4j.Logger

class Batter
{
    def gameLog = Logger.getLogger('gamelog')

    String name
    String teamName
    String year
    String position
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

    static hasMany = [simBatter: SimBatter]

    static constraints = {
        teamName(blank:false, maxSize:40)
        year(blank: false, maxSize: 35)
        name(blank:false, maxSize:35)
        position(blank:false, maxSize:7)
        atBats(nullable: false)
        walks(nullable: false)
        strikeouts(nullable: false)
        hits(nullable: false)
        doubles(nullable: false)
        triples(nullable: false)
        homers(nullable: false)
        hitByPitch(nullable: false)
        stolenBases(nullable: false)
        caughtStealing(nullable: false)
    }

    public def getBattingAvg() {
        BigDecimal.valueOf(hits / atBats)
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

}
