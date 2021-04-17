package baseball.domain

import java.math.RoundingMode

class GamePitcher extends GamePitcherComparable
{
    def C = "GamePitcher"
    def simPitcher
    int battersRetired = 0
    int order = 0
    int walks = 0
    int strikeouts = 0
    int hits = 0
    int homers = 0
    int hitByPitch = 0
    int whip = 0
    int balks = 0
    int runs = 0
    int pitches = 0

    GamePitcher(Player player) {
        simPitcher = new SimPitcher(player)
    }

    GamePitcher(Map map) {
        this.battersRetired = map.battersRetired
        this.order = map.order
        this.walks = map.walks
        this.strikeouts = map.strikeouts
        this.hits = map.hits
        this.homers = map.homers
        this.hitByPitch = map.hitByPitch
        this.whip = map.whip
        this.balks = map.balks
        this.runs = map.runs
        this.pitches = map.pitches
        simPitcher = new SimPitcher(map.simPitcher)
    }

    // Is the specified object equal to this one?
    boolean equals(GamePitcher target) {
        boolean result = true
        def m = "${C}.equals() - "
        if (! compareString("nameFirst", nameFirst, target.nameFirst)) { result = false }
        else if (! compareString("nameLast", nameLast, target.nameLast)) { result = false }
        else if (! compareInt("battersRetired", battersRetired, target.battersRetired)) { result = false }
        else if (! compareInt("order", order, target.order)) { result = false }
        else if (! compareInt("walks", walks, target.walks)) { result = false }
        else if (! compareInt("strikeouts", strikeouts, target.strikeouts)) { result = false }
        else if (! compareInt("hits", hits, target.hits)) { result = false }
        else if (! compareInt("homers", homers, target.homers)) { result = false }
        else if (! compareInt("hitByPitch", hitByPitch, target.hitByPitch)) { result = false }
        else if (! compareInt("whip", whip, target.whip)) { result = false }
        else if (! compareInt("balks", balks, target.balks)) { result = false }
        else if (! compareInt("runs", runs, target.runs)) { result = false }
        else if (! compareInt("pitches", pitches, target.pitches)) { result = false }
        else if (! compareObject("simPitcher", simPitcher, target.simPitcher)) { result = false }

        return result
    }

    // Is the specified object equal to this one?
    boolean equals(GamePitcher target, List builder) {
        boolean result = true
        def m = "${C}.equals() - "
        if (! compareString("nameFirst", nameFirst, target.nameFirst, builder)) { result = false }
        else if (! compareString("nameLast", nameLast, target.nameLast, builder)) { result = false }
        else if (! compareInt("battersRetired", battersRetired, target.battersRetired, builder)) { result = false }
        else if (! compareInt("order", order, target.order, builder)) { result = false }
        else if (! compareInt("walks", walks, target.walks, builder)) { result = false }
        else if (! compareInt("strikeouts", strikeouts, target.strikeouts, builder)) { result = false }
        else if (! compareInt("hits", hits, target.hits, builder)) { result = false }
        else if (! compareInt("homers", homers, target.homers, builder)) { result = false }
        else if (! compareInt("hitByPitch", hitByPitch, target.hitByPitch, builder)) { result = false }
        else if (! compareInt("whip", whip, target.whip, builder)) { result = false }
        else if (! compareInt("balks", balks, target.balks, builder)) { result = false }
        else if (! compareInt("runs", runs, target.runs, builder)) { result = false }
        else if (! compareInt("pitches", pitches, target.pitches, builder)) { result = false }
        else if (! compareObject("simPitcher", simPitcher, target.simPitcher, builder)) { result = false }
        if (result) {
            if (! hideOK)
                builder << "$m Pitchers match?  OK"
        } else {
            builder << "$m Pitchers match?  NO MATCH"
        }

        return result
    }

    PitcherStats getPitcherStats() {
        this.simPitcher?.pitcher.pitcherStats
    }

    String getHistoricalEra() {
        this.simPitcher?.pitcher.pitcherStats.pitchingEra
    }

    String getPlayerID() {
        this.simPitcher?.playerID
    }

    String toString() {
        "${getName()}  Starts: ${simPitcher.gamesStarted}/${simPitcher.pitcher.pitcherStats.pitchingGamesStarted} Games: ${simPitcher.games}/${simPitcher.pitcher.pitcherStats.pitchingGames}"
    }

    String getName() {
        this.simPitcher?.pitcher.name
    }

    String getNameFirst() {
        this.simPitcher?.nameFirst
    }

    String getNameLast() {
        this.simPitcher?.nameLast
    }

    void setNameFirst(String value) {
        // Do nothing. This field references the Player object.
    }

    void setNameLast(String value) {
        // Do nothing. This field references the Player object.
    }

    def getBattersFaced() {
        battersRetired + walks + hits + hitByPitch
    }

    def getRate(int num) {
        BigDecimal.valueOf(num / battersFaced)
    }

    def getRate(int num, int divisor) {
        BigDecimal.valueOf(num / divisor)
    }

    def getEra() {
        BigDecimal games = new BigDecimal(battersRetired)
        games = games.divide(27, 5, RoundingMode.HALF_UP)
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
    def getOppAvg() {
        hits / (battersRetired + hits)
    }

    def reset() {
        battersRetired = 0
        order = 0
        walks = 0
        strikeouts = 0
        hits = 0
        homers = 0
        hitByPitch = 0
        whip = 0
        balks = 0
        runs = 0
        pitches = 0
    }

    public boolean pitcherExhausted(String teamName) {
        boolean exhausted = (battersRetired >= avgBattersRetiredPerGame)
        //if (exhausted) {
            //println "Is pitcher exhausted: $exhausted   Batters retired ($battersRetired) > Avg batters retired /game ($avgBattersRetiredPerGame);  Games: ${simPitcher.pitcher.pitcherStats.pitchingGames}  Games Started: ${simPitcher.pitcher.pitcherStats.pitchingGamesStarted} "
            //println "   Pitcher: $name ($teamName)"
            //println "   Avg batters retired per game: ${avgBattersRetiredPerGame}"
            //println "TEMP"
        //}
        exhausted
    }

    public def getAvgBattersRetiredPerGame() {
        PitcherStats pitcherStats = simPitcher.pitcher.pitcherStats
        int games = pitcherStats.pitchingGames
        int gamesStarted = pitcherStats.pitchingGamesStarted
        if (pitcherStats.pitchingGames != gamesStarted) {
            if (simPitcher.gamesStarted < gamesStarted) {
                return 18
            } else {
                // Could calculate avg batters retired per game in relief, based on the
                // 6 times the number of
                int battersRetired = pitcherStats.pitchingBattersRetired
                int battersRetiredInRelief = battersRetired - (pitcherStats.pitchingGamesStarted * 18)
                if (games - gamesStarted > 0) {
                    return BigDecimal.valueOf(battersRetiredInRelief / (games - gamesStarted)).intValue()
                } else {
                    return 1
                }
            }
        } else {
            simPitcher.avgBattersRetiredPerGame
        }
    }

}
