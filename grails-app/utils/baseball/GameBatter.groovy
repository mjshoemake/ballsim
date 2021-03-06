package baseball

class GameBatter {

    def simBatter

    int battingPos = 0
    int atBats = 0
    int walks = 0
    int strikeouts = 0
    int hits = 0
    int doubles = 0
    int triples = 0
    int homers = 0
    int runs = 0
    int rbi = 0
    int hitByPitch = 0
    int stolenBases = 0
    int caughtStealing = 0

    def getBattingAvg() {
        BigDecimal.valueOf(hits / atBats)
    }

    def getPlateAppearances() {
        atBats + walks + hitByPitch
    }

    def getRate(int num) {
        BigDecimal.valueOf(num / plateAppearances)
    }

    def getRate(int num, int divisor) {
        BigDecimal.valueOf(num / divisor)
    }

    def reset() {
        battingPos = 0
        atBats = 0
        walks = 0
        strikeouts = 0
        hits = 0
        doubles = 0
        triples = 0
        homers = 0
        runs = 0
        rbi = 0
        hitByPitch = 0
        stolenBases = 0
        caughtStealing = 0
    }

}
