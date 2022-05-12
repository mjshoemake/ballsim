package baseball.domain

import org.apache.log4j.Logger
import org.bson.types.ObjectId

class Player extends Comparable {
    def C = "Player"
    private Logger log = Logger.getLogger("Debug");
    ObjectId _id
    String type = Player.class.name
    String year
    String teamID
    String playerID
    String nameFirst
    String nameLast
    String nameNick
    String name
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
    int rbi = 0
    int hitByPitch = 0
    int stolenBases = 0
    int caughtStealing = 0
    int sacrificeFlies = 0
    boolean isPitcher = false
    String primaryPosition
    PitcherStats pitcherStats = null
    BigDecimal calculatedRank = BigDecimal.valueOf(0.0)

    // Overall fielding #s
    int putouts = 0
    int assists = 0
    int errors = 0
    int catcherSteals = 0
    int catcherCaught = 0

    //Map<String, FieldingPosition> position = new HashMap<String, FieldingPosition>()

    Player() {
        //println "Creating Player..."
    }

    Player(Map source) {
        //println "Converting Map to Player..."
        this._id = source._id
        this.type = Player.class.name
        this.year = source.year
        this.teamID = source.teamID
        this.playerID = source.playerID
        this.nameFirst = source.nameFirst
        this.nameLast = source.nameLast
        this.nameNick = source.nameNick
        this.name = source.name
        this.birthYear = source.birthYear
        this.armBats = source.armBats
        this.armThrows = source.armThrows
        this.games = source.games
        this.atBats = source.atBats
        this.walks = source.walks
        this.strikeouts = source.strikeouts
        this.hits = source.hits
        this.doubles = source.doubles
        this.triples = source.triples
        this.homers = source.homers
        this.rbi = source.rbi
        this.hitByPitch = source.hitByPitch
        this.stolenBases = source.stolenBases
        this.caughtStealing = source.caughtStealing
        this.sacrificeFlies = source.sacrificeFlies
        this.isPitcher = source.isPitcher
        this.primaryPosition = source.primaryPosition
        if (source.pitcherStats) {
            this.pitcherStats = new PitcherStats(source.pitcherStats)
        }
        this.calculatedRank = BigDecimal.valueOf(source.calculatedRank)

        // Overall fielding #s
        this.putouts = source.putouts
        this.assists = source.assists
        this.errors = source.errors
        this.catcherSteals = source.catcherSteals
        this.catcherCaught = source.catcherCaught
    }

    // Is the specified object equal to this one?
    boolean equals(Player target) {
        boolean result = true
        def m = "${C}.equals() - "
        if (compareString("type", type.toString(), target.type.toString())) { result = false }
        else if (! compareString("year", year, target.year)) { result = false }
        else if (! compareString("teamID", teamID, target.teamID)) { result = false }
        else if (! compareString("playerID", playerID, target.playerID)) { result = false }
        else if (! compareString("nameFirst", nameFirst, target.nameFirst)) { result = false }
        else if (! compareString("nameLast", nameLast, target.nameLast)) { result = false }
        else if (! compareString("nameNick", nameNick, target.nameNick)) { result = false }
        else if (! compareString("name", name, target.name)) { result = false }
        else if (! compareString("armBats", armBats, target.armBats)) { result = false }
        else if (! compareString("armThrows", armThrows, target.armThrows)) { result = false }
        else if (! compareString("teamID", teamID, target.teamID)) { result = false }
        else if (! compareString("teamID", teamID, target.teamID)) { result = false }
        else if (! compareString("primaryPosition", primaryPosition, target.primaryPosition)) { result = false }
        else if (! compareInt("birthYear", birthYear, target.birthYear)) { result = false }
        else if (! compareInt("games", games, target.games)) { result = false }
        else if (! compareInt("atBats", atBats, target.atBats)) { result = false }
        else if (! compareInt("walks", walks, target.walks)) { result = false }
        else if (! compareInt("strikeouts", strikeouts, target.strikeouts)) { result = false }
        else if (! compareInt("hits", hits, target.hits)) { result = false }
        else if (! compareInt("doubles", doubles, target.doubles)) { result = false }
        else if (! compareInt("triples", triples, target.triples)) { result = false }
        else if (! compareInt("homers", homers, target.homers)) { result = false }
        else if (! compareInt("rbi", rbi, target.rbi)) { result = false }
        else if (! compareInt("hitByPitch", hitByPitch, target.hitByPitch)) { result = false }
        else if (! compareInt("stolenBases", stolenBases, target.stolenBases)) { result = false }
        else if (! compareInt("caughtStealing", caughtStealing, target.caughtStealing)) { result = false }
        else if (! compareInt("sacrificeFlies", sacrificeFlies, target.sacrificeFlies)) { result = false }
        else if (! compareInt("putouts", putouts, target.putouts)) { result = false }
        else if (! compareInt("assists", assists, target.assists)) { result = false }
        else if (! compareInt("errors", errors, target.errors)) { result = false }
        else if (! compareInt("catcherSteals", catcherSteals, target.catcherSteals)) { result = false }
        else if (! compareInt("catcherCaught", catcherCaught, target.catcherCaught)) { result = false }
        else if (! compareBoolean("isPitcher", isPitcher, target.isPitcher)) { result = false }
        else if (! pitcherStats.equals(target.pitcherStats)) { result = false }

        return result
    }

    // Is the specified object equal to this one?
    boolean equals(Player target, List builder) {
        boolean result = true
        def m = "${C}.equals() - "
        if (! compareString("year", year, target.year, builder)) { result = false }
        if (! compareString("teamID", teamID, target.teamID, builder)) { result = false }
        if (! compareString("playerID", playerID, target.playerID, builder)) { result = false }
        if (! compareString("nameFirst", nameFirst, target.nameFirst, builder)) { result = false }
        if (! compareString("nameLast", nameLast, target.nameLast, builder)) { result = false }
        if (! compareString("nameNick", nameNick, target.nameNick, builder)) { result = false }
        if (! compareString("name", name, target.name, builder)) { result = false }
        if (! compareString("armBats", armBats, target.armBats, builder)) { result = false }
        if (! compareString("armThrows", armThrows, target.armThrows, builder)) { result = false }
        if (! compareString("teamID", teamID, target.teamID, builder)) { result = false }
        if (! compareString("teamID", teamID, target.teamID, builder)) { result = false }
        if (! compareString("primaryPosition", primaryPosition, target.primaryPosition, builder)) { result = false }
        if (! compareInt("birthYear", birthYear, target.birthYear, builder)) { result = false }
        if (! compareInt("games", games, target.games, builder)) { result = false }
        if (! compareInt("atBats", atBats, target.atBats, builder)) { result = false }
        if (! compareInt("walks", walks, target.walks, builder)) { result = false }
        if (! compareInt("strikeouts", strikeouts, target.strikeouts, builder)) { result = false }
        if (! compareInt("hits", hits, target.hits, builder)) { result = false }
        if (! compareInt("doubles", doubles, target.doubles, builder)) { result = false }
        if (! compareInt("triples", triples, target.triples, builder)) { result = false }
        if (! compareInt("homers", homers, target.homers, builder)) { result = false }
        if (! compareInt("rbi", rbi, target.rbi, builder)) { result = false }
        if (! compareInt("hitByPitch", hitByPitch, target.hitByPitch, builder)) { result = false }
        if (! compareInt("stolenBases", stolenBases, target.stolenBases, builder)) { result = false }
        if (! compareInt("caughtStealing", caughtStealing, target.caughtStealing, builder)) { result = false }
        if (! compareInt("sacrificeFlies", sacrificeFlies, target.sacrificeFlies, builder)) { result = false }
        if (! compareInt("putouts", putouts, target.putouts, builder)) { result = false }
        if (! compareInt("assists", assists, target.assists, builder)) { result = false }
        if (! compareInt("errors", errors, target.errors, builder)) { result = false }
        if (! compareInt("catcherSteals", catcherSteals, target.catcherSteals, builder)) { result = false }
        if (! compareInt("catcherCaught", catcherCaught, target.catcherCaught, builder)) { result = false }
        if (! compareBoolean("isPitcher", isPitcher, target.isPitcher, builder)) { result = false }
        //if (! compareString("calculatedRank", calculatedRank.toString(), target.calculatedRank.toString(), builder)) { result = false }
        if (! compareObject("pitcherStats", pitcherStats, target.pitcherStats, builder)) { result = false }
        if (! compareString("type", type.toString(), target.type.toString(), builder)) { result = false }
        if (result) {
            if (! hideOK)
                builder << "$m Players match?  OK"
        } else {
            builder << "$m Players match?  NO MATCH"
        }

        return result
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
            BigDecimal.valueOf(hits).divide(BigDecimal.valueOf(atBats), 3,3)
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

    void loadFromMap(def playerMap, def battingStats, def pitchingStats,team_id, String year) {
        try {
            this.name = playerMap.name_first_last
            this.pitcherStats?.name = this.name
            this.armBats = playerMap.bats
            this.armThrows = playerMap.throws
            this.isPitcher = playerMap.primary_position == "P"
            this.nameFirst = this.name  // TODO
            this.nameLast = "" // TODO
            this.playerID = playerMap.player_id
            this.teamID = team_id
            this.primaryPosition = playerMap.primary_position
            this.year = year
            if (year == "2020") {
                if (! battingStats) {
                    println "${this.name} - battingStats object is null."
                } else {
                    this.atBats = (Integer.parseInt(battingStats.ab) * 2.7).round(0)
                    this.games = (Integer.parseInt(battingStats.g) * 2.7).round(0)
                    this.walks = (Integer.parseInt(battingStats.bb) * 2.7).round(0)
                    this.strikeouts = (Integer.parseInt(battingStats.so) * 2.7).round(0)
                    this.hits = (Integer.parseInt(battingStats.h) * 2.7).round(0)
                    this.doubles = (Integer.parseInt(battingStats.d) * 2.7).round(0)
                    this.triples = (Integer.parseInt(battingStats.t) * 2.7).round(0)
                    this.homers = (Integer.parseInt(battingStats.hr) * 2.7).round(0)
                    this.hitByPitch = (Integer.parseInt(battingStats.hbp) * 2.7).round(0)
                    this.stolenBases = (Integer.parseInt(battingStats.sb) * 2.7).round(0)
                    this.caughtStealing = (Integer.parseInt(battingStats.cs) * 2.7).round(0)
                    this.sacrificeFlies = (Integer.parseInt(battingStats.sac) * 2.7).round(0)
                }

                if (pitchingStats != null && pitchingStats.size() > 0) {
                    this.pitcherStats = new PitcherStats()
                    this.pitcherStats.with {
                        it.pitchingGames = (Integer.parseInt(pitchingStats.g) * 2.7).round(0)
                        it.pitchingGamesStarted = (Integer.parseInt(pitchingStats.gs) * 2.7).round(0)
                        it.pitchingBattersRetired = ((Integer.parseInt(pitchingStats.ab) - Integer.parseInt(pitchingStats.h))  * 2.7).round(0)
                        it.pitchingWalks = (Integer.parseInt(pitchingStats.bb) * 2.7).round(0)
                        it.pitchingRuns = (Integer.parseInt(pitchingStats.r) * 2.7).round(0)
                        it.pitchingEarnedRuns = (Integer.parseInt(pitchingStats.er) * 2.7).round(0)
                        it.pitchingStrikeouts = (Integer.parseInt(pitchingStats.so) * 2.7).round(0)
                        it.pitchingHits = (Integer.parseInt(pitchingStats.h) * 2.7).round(0)
                        it.pitchingHomers = (Integer.parseInt(pitchingStats.hr) * 2.7).round(0)
                        it.pitchingWildPitch = (Integer.parseInt(pitchingStats.wp) * 2.7).round(0)
                        it.pitchingHitBatter = (Integer.parseInt(pitchingStats.hb) * 2.7).round(0)
                        it.pitchingBalks = (Integer.parseInt(pitchingStats.bk) * 2.7).round(0)
                        it.pitchingWins = (Integer.parseInt(pitchingStats.w) * 2.7).round(0)
                        it.pitchingLosses = (Integer.parseInt(pitchingStats.l) * 2.7).round(0)
                        it.pitchingEra = pitchingStats.era
                        it.pitchingWhip = pitchingStats.whip
                    }
                }
            } else {
                if (battingStats instanceof List) {
                    this.atBats = Integer.parseInt(battingStats?.ab[0] ?: "0") + Integer.parseInt(battingStats?.ab[1] ?: "0")
                    this.games = Integer.parseInt(battingStats?.g[0] ?: "0") + Integer.parseInt(battingStats?.g[1] ?: "0")
                    this.walks = Integer.parseInt(battingStats?.bb[0] ?: "0") + Integer.parseInt(battingStats?.bb[1] ?: "0")
                    this.strikeouts = Integer.parseInt(battingStats?.so[0] ?: "0") + Integer.parseInt(battingStats?.so[1] ?: "0")
                    this.hits = Integer.parseInt(battingStats?.h[0] ?: "0") + Integer.parseInt(battingStats?.h[1] ?: "0")
                    this.doubles = Integer.parseInt(battingStats?.d[0] ?: "0") + Integer.parseInt(battingStats?.d[1] ?: "0")
                    this.triples = Integer.parseInt(battingStats?.t[0] ?: "0") + Integer.parseInt(battingStats?.t[1] ?: "0")
                    this.homers = Integer.parseInt(battingStats?.hr[0] ?: "0") + Integer.parseInt(battingStats?.hr[1] ?: "0")
                    this.hitByPitch = Integer.parseInt(battingStats?.hbp[0] ?: "0") + Integer.parseInt(battingStats?.hbp[1] ?: "0")
                    this.stolenBases = Integer.parseInt(battingStats?.sb[0] ?: "0") + Integer.parseInt(battingStats?.sb[1] ?: "0")
                    this.caughtStealing = Integer.parseInt(battingStats?.cs[0] ?: "0") + Integer.parseInt(battingStats?.cs[1] ?: "0")
                    this.sacrificeFlies = Integer.parseInt(battingStats?.sac[0] ?: "0") + Integer.parseInt(battingStats?.sac[1] ?: "0")
                } else {
                    this.atBats = Integer.parseInt(battingStats?.ab ?: "0")
                    this.games = Integer.parseInt(battingStats?.g ?: "0")
                    this.walks = Integer.parseInt(battingStats?.bb ?: "0")
                    this.strikeouts = Integer.parseInt(battingStats?.so ?: "0")
                    this.hits = Integer.parseInt(battingStats?.h ?: "0")
                    this.doubles = Integer.parseInt(battingStats?.d ?: "0")
                    this.triples = Integer.parseInt(battingStats?.t ?: "0")
                    this.homers = Integer.parseInt(battingStats?.hr ?: "0")
                    this.hitByPitch = Integer.parseInt(battingStats?.hbp ?: "0")
                    this.stolenBases = Integer.parseInt(battingStats?.sb ?: "0")
                    this.caughtStealing = Integer.parseInt(battingStats?.cs ?: "0")
                    this.sacrificeFlies = Integer.parseInt(battingStats?.sac ?: "0")
                }

                if (pitchingStats != null && pitchingStats.size() > 0) {
                    this.pitcherStats = new PitcherStats()
                    this.pitcherStats.with {
                        it.pitchingGames = Integer.parseInt(pitchingStats.g)
                        it.pitchingGamesStarted = Integer.parseInt(pitchingStats.gs)
                        it.pitchingBattersRetired = Integer.parseInt(pitchingStats.ab) - Integer.parseInt(pitchingStats.h)
                        it.pitchingWalks = Integer.parseInt(pitchingStats.bb)
                        it.pitchingRuns = Integer.parseInt(pitchingStats.r)
                        it.pitchingEarnedRuns = Integer.parseInt(pitchingStats.er)
                        it.pitchingStrikeouts = Integer.parseInt(pitchingStats.so)
                        it.pitchingHits = Integer.parseInt(pitchingStats.h)
                        it.pitchingHomers = Integer.parseInt(pitchingStats.hr)
                        it.pitchingWins = Integer.parseInt(pitchingStats.w)
                        it.pitchingLosses = Integer.parseInt(pitchingStats.l)
                        it.pitchingSaves = Integer.parseInt(pitchingStats.sv)
                        if (pitchingStats.wp.equals("")) {
                            it.pitchingWildPitch = 0
                        } else {
                            it.pitchingWildPitch = Integer.parseInt(pitchingStats.wp)
                        }
                        it.pitchingHitBatter = Integer.parseInt(pitchingStats.hb)
                        it.pitchingBalks = Integer.parseInt(pitchingStats.bk)
                        it.pitchingEra = pitchingStats.era
                        it.pitchingWhip = pitchingStats.whip
                    }
                }
            }
        } catch (Exception e) {
            log.info("Error processing playerMap!! ${this.name} (${this.primaryPosition})",e)
            throw e
        }
    }

    void setPitcherStats(Map value) {
        this.pitcherStats = new PitcherStats(value)
        if (this.pitcherStats != null) {
            println "Added pitcherStats for $name."
        }
    }

    void setCalculatedRank(Object value) {
        this.calculatedRank = BigDecimal.valueOf(value)
    }

    void printPlayer() {
        println "Name: ${this.name} Pos: ${this.primaryPosition} ID: ${this.playerID} Year: ${year} Pitcher: ${this.isPitcher} Bats: ${this.armBats} Throws: ${this.armThrows}"
        println "   HR: ${this.homers} 3B: ${this.triples} 2B: ${this.doubles} Hits: ${this.hits} Walks: ${this.walks} Strikeouts: ${this.strikeouts} Steals: ${this.stolenBases} Atbats: ${this.atBats} CS: ${this.caughtStealing} HBP: ${this.hitByPitch} G: ${this.games}"
        if (this.isPitcher) {
            if (! pitcherStats) {
                println "   This is a pitcher, but pitcherStats is NULL..."
            }
            this.pitcherStats.with {
                println "   GS: ${it.pitchingGamesStarted} G: ${it.pitchingGames} ERA: ${it.era} Strikeouts: ${it.pitchingStrikeouts} Walks: ${it.pitchingWalks}"
            }
        }
    }

    void setName(String value) {
        this.name = value
        if (pitcherStats) {
            this.pitcherStats.name = value
        }
    }

}
