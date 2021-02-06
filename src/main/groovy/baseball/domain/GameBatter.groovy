package baseball.domain

class GameBatter extends GameBatterComparable {

    def C = "GameBatter"
    def simBatter

    String nameFirst, nameLast
    String position
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

    GameBatter() {
    }

    GameBatter(Player player) {
        simBatter = new SimBatter()
        simBatter.batter = player
    }


    // Is the specified object equal to this one?
    boolean equals(GameBatter target) {
        boolean result = true
        def m = "${C}.equals() - "
        if (! compareString("nameFirst", nameFirst, target.nameFirst)) { result = false }
        else if (! compareString("nameLast", nameLast, target.nameLast)) { result = false }
        else if (! compareString("position", position, target.position)) { result = false }
        else if (! compareInt("battingPos", battingPos, target.battingPos)) { result = false }
        else if (! compareInt("atBats", atBats, target.atBats)) { result = false }
        else if (! compareInt("walks", walks, target.walks)) { result = false }
        else if (! compareInt("strikeouts", strikeouts, target.strikeouts)) { result = false }
        else if (! compareInt("hits", hits, target.hits)) { result = false }
        else if (! compareInt("doubles", doubles, target.doubles)) { result = false }
        else if (! compareInt("triples", triples, target.triples)) { result = false }
        else if (! compareInt("homers", homers, target.homers)) { result = false }
        else if (! compareInt("runs", runs, target.runs)) { result = false }
        else if (! compareInt("rbi", rbi, target.rbi)) { result = false }
        else if (! compareInt("hitByPitch", hitByPitch, target.hitByPitch)) { result = false }
        else if (! compareInt("stolenBases", stolenBases, target.stolenBases)) { result = false }
        else if (! compareInt("caughtStealing", caughtStealing, target.caughtStealing)) { result = false }
        else if (! compareObject("simBatter", simBatter, target.simBatter)) { result = false }

        return result
    }

    // Is the specified object equal to this one?
    boolean equals(GameBatter target, List builder) {
        boolean result = true
        def m = "${C}.equals() - "
        if (! compareString("nameFirst", nameFirst, target.nameFirst, builder)) { result = false }
        else if (! compareString("nameLast", nameLast, target.nameLast, builder)) { result = false }
        else if (! compareString("position", position, target.position, builder)) { result = false }
        else if (! compareInt("battingPos", battingPos, target.battingPos, builder)) { result = false }
        else if (! compareInt("atBats", atBats, target.atBats, builder)) { result = false }
        else if (! compareInt("walks", walks, target.walks, builder)) { result = false }
        else if (! compareInt("strikeouts", strikeouts, target.strikeouts, builder)) { result = false }
        else if (! compareInt("hits", hits, target.hits, builder)) { result = false }
        else if (! compareInt("doubles", doubles, target.doubles, builder)) { result = false }
        else if (! compareInt("triples", triples, target.triples, builder)) { result = false }
        else if (! compareInt("homers", homers, target.homers, builder)) { result = false }
        else if (! compareInt("runs", runs, target.runs, builder)) { result = false }
        else if (! compareInt("rbi", rbi, target.rbi, builder)) { result = false }
        else if (! compareInt("hitByPitch", hitByPitch, target.hitByPitch, builder)) { result = false }
        else if (! compareInt("stolenBases", stolenBases, target.stolenBases, builder)) { result = false }
        else if (! compareInt("caughtStealing", caughtStealing, target.caughtStealing, builder)) { result = false }
        else if (! compareObject("simBatter", simBatter, target.simBatter, builder)) { result = false }
        if (result) {
            builder << "$m Batters match?  OK"
        } else {
            builder << "$m Batters match?  NO MATCH"
        }

        return result
    }

    def getPositions() {
        Player myBatter = simBatter.batter
        List<String> result = []
        myBatter.position.each() {
            result << it.position
        }
        return result
    }

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
