package baseball.domain


import org.bson.types.ObjectId

class Player {
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

    void loadFromMap(def playerMap, def battingStats, def pitchingStats,team_id, String year) {
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
                    pitchingGames = (Integer.parseInt(pitchingStats.g) * 2.7).round(0)
                    pitchingGamesStarted = (Integer.parseInt(pitchingStats.gs) * 2.7).round(0)
                    pitchingBattersRetired = ((Integer.parseInt(pitchingStats.ab) - Integer.parseInt(pitchingStats.h))  * 2.7).round(0)
                    pitchingWalks = (Integer.parseInt(pitchingStats.bb) * 2.7).round(0)
                    pitchingRuns = (Integer.parseInt(pitchingStats.r) * 2.7).round(0)
                    pitchingEarnedRuns = (Integer.parseInt(pitchingStats.er) * 2.7).round(0)
                    pitchingStrikeouts = (Integer.parseInt(pitchingStats.so) * 2.7).round(0)
                    pitchingHits = (Integer.parseInt(pitchingStats.h) * 2.7).round(0)
                    pitchingHomers = (Integer.parseInt(pitchingStats.hr) * 2.7).round(0)
                    pitchingWildPitch = (Integer.parseInt(pitchingStats.wp) * 2.7).round(0)
                    pitchingHitBatter = (Integer.parseInt(pitchingStats.hb) * 2.7).round(0)
                    pitchingBalks = (Integer.parseInt(pitchingStats.bk) * 2.7).round(0)
                    pitchingEra = pitchingStats.era
                    pitchingWhip = pitchingStats.whip
                }
            }
        } else {
            this.atBats = Integer.parseInt(battingStats.ab)
            this.games = Integer.parseInt(battingStats.g)
            this.walks = Integer.parseInt(battingStats.bb)
            this.strikeouts = Integer.parseInt(battingStats.so)
            this.hits = Integer.parseInt(battingStats.h)
            this.doubles = Integer.parseInt(battingStats.d)
            this.triples = Integer.parseInt(battingStats.t)
            this.homers = Integer.parseInt(battingStats.hr)
            this.hitByPitch = Integer.parseInt(battingStats.hbp)
            this.stolenBases = Integer.parseInt(battingStats.sb)
            this.caughtStealing = Integer.parseInt(battingStats.cs)
            this.sacrificeFlies = Integer.parseInt(battingStats.sac)

            if (pitchingStats != null && pitchingStats.size() > 0) {
                this.pitcherStats = new PitcherStats()
                this.pitcherStats.with {
                    pitchingGames = Integer.parseInt(pitchingStats.g)
                    pitchingGamesStarted = Integer.parseInt(pitchingStats.gs)
                    pitchingBattersRetired = Integer.parseInt(pitchingStats.ab) - Integer.parseInt(pitchingStats.h)
                    pitchingWalks = Integer.parseInt(pitchingStats.bb)
                    pitchingRuns = Integer.parseInt(pitchingStats.r)
                    pitchingEarnedRuns = Integer.parseInt(pitchingStats.er)
                    pitchingStrikeouts = Integer.parseInt(pitchingStats.so)
                    pitchingHits = Integer.parseInt(pitchingStats.h)
                    pitchingHomers = Integer.parseInt(pitchingStats.hr)
                    pitchingWildPitch = Integer.parseInt(pitchingStats.wp)
                    pitchingHitBatter = Integer.parseInt(pitchingStats.hb)
                    pitchingBalks = Integer.parseInt(pitchingStats.bk)
                    pitchingEra = pitchingStats.era
                    pitchingWhip = pitchingStats.whip
                }
            }
        }
    }

    void printPlayer() {
        println "Name: ${this.name} Pos: ${this.primaryPosition} ID: ${this.playerID} Year: ${year} Pitcher: ${this.isPitcher} Bats: ${this.armBats} Throws: ${this.armThrows}"
        println "   HR: ${this.homers} 3B: ${this.triples} 2B: ${this.doubles} Hits: ${this.hits} Walks: ${this.walks} Strikeouts: ${this.strikeouts} Steals: ${this.stolenBases} Atbats: ${this.atBats} CS: ${this.caughtStealing} HBP: ${this.hitByPitch} G: ${this.games}"
        if (this.isPitcher) {
            this.pitcherStats.with {
                println "   GS: ${pitchingGamesStarted} G: ${pitchingGames} ERA: ${era} Strikeouts: ${pitchingStrikeouts} Walks: ${pitchingWalks}"
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
