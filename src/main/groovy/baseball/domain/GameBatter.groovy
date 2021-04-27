package baseball.domain

class GameBatter extends GameBatterComparable {

    def C = "GameBatter"
    SimBatter simBatter
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

    GameBatter(Player player) {
        simBatter = new SimBatter(player)
    }

    GameBatter(Map map) {
        this.battingPos = map.battingPos
        this.atBats = map.atBats
        this.walks = map.walks
        this.strikeouts = map.strikeouts
        this.hits = map.hits
        this.doubles = map.doubles
        this.triples = map.triples
        this.homers = map.homers
        this.runs = map.runs
        this.rbi = map.rbi
        this.hitByPitch = map.hitByPitch
        this.stolenBases = map.stolenBases
        this.caughtStealing = map.caughtStealing
        this.simBatter = new SimBatter(map.simBatter)
    }

    // Is the specified object equal to this one?
    boolean equals(GameBatter target) {
        boolean result = true
        def m = "${C}.equals() - "
        if (! compareString("nameFirst", nameFirst, target.nameFirst)) { result = false }
        if (! compareString("nameLast", nameLast, target.nameLast)) { result = false }
        if (! compareString("position", position, target.position)) { result = false }
        if (! compareInt("battingPos", battingPos, target.battingPos)) { result = false }
        if (! compareInt("atBats", atBats, target.atBats)) { result = false }
        if (! compareInt("walks", walks, target.walks)) { result = false }
        if (! compareInt("strikeouts", strikeouts, target.strikeouts)) { result = false }
        if (! compareInt("hits", hits, target.hits)) { result = false }
        if (! compareInt("doubles", doubles, target.doubles)) { result = false }
        if (! compareInt("triples", triples, target.triples)) { result = false }
        if (! compareInt("homers", homers, target.homers)) { result = false }
        if (! compareInt("runs", runs, target.runs)) { result = false }
        if (! compareInt("rbi", rbi, target.rbi)) { result = false }
        if (! compareInt("hitByPitch", hitByPitch, target.hitByPitch)) { result = false }
        if (! compareInt("stolenBases", stolenBases, target.stolenBases)) { result = false }
        if (! compareInt("caughtStealing", caughtStealing, target.caughtStealing)) { result = false }
        if (! compareObject("simBatter", simBatter, target.simBatter)) { result = false }

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
            if (! hideOK)
                builder << "$m Batters match?  OK"
        } else {
            builder << "$m Batters match?  NO MATCH"
        }

        return result
    }

    String toString() {
        "${getName()} (${position}/${simBatter.batter.primaryPosition}) Games: ${simBatter?.games}/${simBatter?.batter.games} HR: ${simBatter?.batter?.homers} Avg: ${simBatter?.batter?.battingAvg} SB: ${simBatter?.batter?.stolenBases} AB: ${simBatter?.atBats}/${simBatter?.batter?.atBats}"
    }

    String getPlayerID() {
        this.simBatter?.playerID
    }

    String getName() {
        this.simBatter?.batter.name
    }

    String getNameFirst() {
        this.simBatter?.nameFirst
    }

    String getNameLast() {
        this.simBatter?.nameLast
    }

    String getPosition() {
        this.simBatter?.primaryPosition
    }

    void setPosition(String primaryPosition) {
        this.simBatter?.primaryPosition = primaryPosition
    }

    void setNameFirst(String value) {
        // Do nothing. This field references the Player object.
    }

    void setNameLast(String value) {
        // Do nothing. This field references the Player object.
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
        if (atBats != 0) {
            BigDecimal.valueOf(hits / atBats)
        } else {
            0
        }
    }

    void setPlateAppearances(int value) {
       // Do nothing. This is a calculated field.
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
