package baseball.domain

import java.math.RoundingMode

class SimPitcher extends Comparable {

    def C = "SimPitcher"
    int wins = 0
    int losses = 0
    int saves = 0
    int blownSaves = 0
    int battersRetired = 0
    int orderPos = 0
    int walks = 0
    int strikeouts = 0
    int hits = 0
    int homers = 0
    int hitByPitch = 0
    double whip = 0
    int balks = 0
    int runs = 0
    int gamesStarted = 0
    int games = 0
    int pitchesGame1 = 0
    int pitchesGame2 = 0
    int pitchesGame3 = 0
    int pitchesGame4 = 0
    int pitchesGame5 = 0
    Player pitcher

    SimPitcher(Player pitcher) {
        this.pitcher = pitcher
    }

    SimPitcher(Map map) {
        this.wins = map.wins
        this.losses = map.losses
        this.saves =  map.saves
        this.blownSaves =  map.blownSaves
        this.battersRetired =  map.battersRetired
        this.orderPos =  map.orderPos
        this.walks =  map.walks
        this.strikeouts =  map.strikeouts
        this.hits =  map.hits
        this.homers = homers
        this.hitByPitch =  map.hitByPitch
        this.whip =  map.whip
        this.balks =  map.balks
        this.runs =  map.runs
        this.pitchesGame1 = map.pitchesGame1
        this.pitchesGame2 = map.pitchesGame2
        this.pitchesGame3 = map.pitchesGame3
        this.pitchesGame4 = map.pitchesGame4
        this.pitchesGame5 = map.pitchesGame5
        this.games = map.games
        this.gamesStarted = map.gamesStarted
        if (map.pitcher instanceof Player) {
            this.pitcher = map.pitcher
        } else {
            this.pitcher = new Player(map.pitcher)
        }
    }

    // Is the specified object equal to this one?
    boolean equals(SimPitcher target) {
        boolean result = true
        def m = "${C}.equals() - "
        if (! compareString("nameFirst", nameFirst, target.nameFirst)) { result = false }
        else if (! compareString("nameLast", nameLast, target.nameLast)) { result = false }
        else if (! compareInt("wins", wins, target.wins)) { result = false }
        else if (! compareInt("losses", losses, target.losses)) { result = false }
        else if (! compareInt("saves", saves, target.saves)) { result = false }
        else if (! compareInt("blownSaves", blownSaves, target.blownSaves)) { result = false }
        else if (! compareInt("battersRetired", battersRetired, target.battersRetired)) { result = false }
        else if (! compareInt("orderPos", orderPos, target.orderPos)) { result = false }
        else if (! compareInt("walks", walks, target.walks)) { result = false }
        else if (! compareInt("strikeouts", strikeouts, target.strikeouts)) { result = false }
        else if (! compareInt("hits", hits, target.hits)) { result = false }
        else if (! compareInt("homers", homers, target.homers)) { result = false }
        else if (! compareInt("hitByPitch", hitByPitch, target.hitByPitch)) { result = false }
        else if (! compareInt("balks", balks, target.balks)) { result = false }
        else if (! compareInt("runs", runs, target.runs)) { result = false }
        else if (! compareInt("gamesStarted", gamesStarted, target.gamesStarted)) { result = false }
        else if (! compareInt("games", games, target.games)) { result = false }
        else if (! compareInt("pitchesGame1", pitchesGame1, target.pitchesGame1)) { result = false }
        else if (! compareInt("pitchesGame2", pitchesGame2, target.pitchesGame2)) { result = false }
        else if (! compareInt("pitchesGame3", pitchesGame3, target.pitchesGame3)) { result = false }
        else if (! compareInt("pitchesGame4", pitchesGame4, target.pitchesGame4)) { result = false }
        else if (! compareInt("pitchesGame5", pitchesGame5, target.pitchesGame5)) { result = false }
        else if (! compareDouble("whip", whip, target.whip)) { result = false }
        else if (! compareObject("pitcher", pitcher, target.pitcher)) { result = false }

        return result
    }

    // Is the specified object equal to this one?
    boolean equals(SimPitcher target, List builder) {
        boolean result = true
        def m = "${C}.equals() - "
        if (! compareString("nameFirst", nameFirst, target.nameFirst, builder)) { result = false }
        else if (! compareString("nameLast", nameLast, target.nameLast, builder)) { result = false }
        else if (! compareInt("wins", wins, target.wins, builder)) { result = false }
        else if (! compareInt("losses", losses, target.losses, builder)) { result = false }
        else if (! compareInt("saves", saves, target.saves, builder)) { result = false }
        else if (! compareInt("blownSaves", blownSaves, target.blownSaves, builder)) { result = false }
        else if (! compareInt("battersRetired", battersRetired, target.battersRetired, builder)) { result = false }
        else if (! compareInt("orderPos", orderPos, target.orderPos, builder)) { result = false }
        else if (! compareInt("walks", walks, target.walks, builder)) { result = false }
        else if (! compareInt("strikeouts", strikeouts, target.strikeouts, builder)) { result = false }
        else if (! compareInt("hits", hits, target.hits, builder)) { result = false }
        else if (! compareInt("homers", homers, target.homers, builder)) { result = false }
        else if (! compareInt("hitByPitch", hitByPitch, target.hitByPitch, builder)) { result = false }
        else if (! compareInt("balks", balks, target.balks, builder)) { result = false }
        else if (! compareInt("runs", runs, target.runs, builder)) { result = false }
        else if (! compareInt("gamesStarted", gamesStarted, target.gamesStarted, builder)) { result = false }
        else if (! compareInt("games", games, target.games, builder)) { result = false }
        else if (! compareInt("pitchesGame1", pitchesGame1, target.pitchesGame1, builder)) { result = false }
        else if (! compareInt("pitchesGame2", pitchesGame2, target.pitchesGame2, builder)) { result = false }
        else if (! compareInt("pitchesGame3", pitchesGame3, target.pitchesGame3, builder)) { result = false }
        else if (! compareInt("pitchesGame4", pitchesGame4, target.pitchesGame4, builder)) { result = false }
        else if (! compareInt("pitchesGame5", pitchesGame5, target.pitchesGame5, builder)) { result = false }
        else if (! compareDouble("whip", whip, target.whip, builder)) { result = false }
        else if (! compareObject("pitcher", pitcher, target.pitcher, builder)) { result = false }
        if (result) {
            if (! hideOK)
                builder << "$m SimPitchers match?  OK"
        } else {
            builder << "$m SimPitchers match?  NO MATCH"
        }

        return result
    }

    String getPlayerID() {
        pitcher?.playerID
    }

    String getNameFirst() {
        pitcher?.nameFirst
    }

    String getNameLast() {
        pitcher?.nameLast
    }

    String getName() {
        pitcher?.name
    }

    void setNameFirst(String value) {
        // Do nothing. This field references the Player object.
    }

    void setNameLast(String value) {
        // Do nothing. This field references the Player object.
    }

    def getBattersFaced() {7
        battersRetired + walks + hits + hitByPitch
    }

    def getRate(int num) {
        BigDecimal.valueOf(num / battersFaced)
    }

    def getRate(int num, int divisor) {
        BigDecimal.valueOf(num / divisor)
    }

    public def getAvgBattersRetiredPerGame() {
        pitcher.pitcherStats.avgBattersRetiredPerGame
    }

    def getOppBattingAvg() {
        int oppAtBats = battersRetired + hits
        if (oppAtBats == 0) {
            BigDecimal.valueOf(0)
        }  else {
            BigDecimal.valueOf(hits / oppAtBats)
        }
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
            padded = format(padded, 5)
        } else {
            padded = format(padded, 4) + ' '
        }
        padded
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
