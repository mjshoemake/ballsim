package baseball.domain

import baseball.processing.HttpHistoricalDataManager

class SimBatter extends Comparable {

    def C = "SimBatter"
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
    Player batter
    boolean maxedOut = false

    SimBatter(Player batter) {
        this.batter = batter
    }

    SimBatter(Map map) {
        this.battingPos = map.battingPos
        this.atBats = map.atBats
        this.walks = map.walks
        this.strikeouts = map.strikeouts
        this.hits = map.hits
        this.doubles = map.doubles
        this.triples = map.triples
        this.homers = map.homers
        this.hitByPitch = map.hitByPitch
        this.stolenBases = map.stolenBases
        this.caughtStealing = map.caughtStealing
        this.runs = map.runs
        this.rbi = map.rbi
        if (map.batter instanceof Player) {
            this.batter = map.batter
        } else {
            this.batter = new Player(map.batter)
        }
        this.maxedOut = map.maxedOut
    }

    // Is the specified object equal to this one?
    boolean equals(SimBatter target) {
        boolean result = true
        def m = "${C}.equals() - "
        if (! compareString("nameFirst", nameFirst, target.nameFirst)) { result = false }
        else if (! compareString("nameLast", nameLast, target.nameLast)) { result = false }
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
        else if (! compareInt("maxedOut", maxedOut, target.maxedOut)) { result = false }
        else if (! compareObject("batter", batter, target.batter)) { result = false }

        return result
    }

    // Is the specified object equal to this one?
    boolean equals(SimBatter target, List builder) {
        boolean result = true
        def m = "${C}.equals() - "
        if (! compareString("nameFirst", nameFirst, target.nameFirst, builder)) { result = false }
        if (! compareString("nameLast", nameLast, target.nameLast, builder)) { result = false }
        if (! compareString("primaryPosition", primaryPosition, target.primaryPosition, builder)) { result = false }
        if (! compareInt("battingPos", battingPos, target.battingPos, builder)) { result = false }
        if (! compareInt("atBats", atBats, target.atBats, builder)) { result = false }
        if (! compareInt("walks", walks, target.walks, builder)) { result = false }
        if (! compareInt("strikeouts", strikeouts, target.strikeouts, builder)) { result = false }
        if (! compareInt("hits", hits, target.hits, builder)) { result = false }
        if (! compareInt("doubles", doubles, target.doubles, builder)) { result = false }
        if (! compareInt("triples", triples, target.triples, builder)) { result = false }
        if (! compareInt("homers", homers, target.homers, builder)) { result = false }
        if (! compareInt("runs", runs, target.runs, builder)) { result = false }
        if (! compareInt("rbi", rbi, target.rbi, builder)) { result = false }
        if (! compareInt("hitByPitch", hitByPitch, target.hitByPitch, builder)) { result = false }
        if (! compareInt("stolenBases", stolenBases, target.stolenBases, builder)) { result = false }
        if (! compareInt("caughtStealing", caughtStealing, target.caughtStealing, builder)) { result = false }
        if (! compareBoolean("maxedOut", maxedOut, target.maxedOut, builder)) { result = false }
        if (! compareObject("batter", batter, target.batter, builder)) { result = false }
        if (result) {
            if (! hideOK)
            builder << "$m SimBatters match?  OK"
        } else {
            builder << "$m SimBatters match?  NO MATCH"
        }

        return result
    }

    void setBatter(Player batter) {
        this.batter = batter
    }

    String getPlayerID() {
        batter?.playerID
    }

    String getNameFirst() {
        batter?.nameFirst
    }

    String getNameLast() {
        batter?.nameLast
    }

    String getPrimaryPosition() {
        batter?.primaryPosition
    }

    void setNameFirst(String value) {
        // Do nothing. This field references the Player object.
    }

    void setNameLast(String value) {
        // Do nothing. This field references the Player object.
    }

    void setPrimaryPosition(String value) {
        // Do nothing. This field references the Player object.
    }

    void setAtBats(int value) {
        atBats = value
        if (atBats > batter.atBats) {
            maxedOut = true
        }
    }

    def getBattingAvg() {
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
