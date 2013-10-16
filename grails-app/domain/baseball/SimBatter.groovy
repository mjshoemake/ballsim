package baseball

class SimBatter {

    static hasOne = [batter: Batter]

    int battingPos = 0
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
    int runs = 0
    int rbi = 0

    static constraints = {
        battingPos(nullable: false)
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
        runs(nullable: false)
        rbi(nullable: false)
    }

    public def getBattingAvg() {
        if (atBats == 0) {
            BigDecimal.valueOf(0)
        } else {
            BigDecimal.valueOf(hits / atBats)
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

    public def getRate(int num, int divisor) {
        if (divisor == 0) {
            BigDecimal.valueOf(0)
        } else {
            BigDecimal.valueOf(num / divisor)
        }

    }


}
