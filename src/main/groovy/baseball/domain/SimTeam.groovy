package baseball.domain

import baseball.processing.HttpHistoricalDataManager
import org.apache.log4j.Logger


class SimTeam extends SimTeamComparable {
    def C = "SimTeam"
    private auditLog = Logger.getLogger('Audit')

    // Primary for game
    def lineup = []
    def bench = []
    def rotation = []
    def bullpen = []
    def pitchersUsed = []
    def reservePitchers = []
    def doneStarters = []
    def doneRelievers = []
    def originalLineup = []
    def originalBench = []
    def originalRotation = []
    def originalBullpen = []
    def additionalStarters = []

    // Need to modify other lists so that they contain IDs, not actual objects.
    // The actual objects should only be found in the roster map.
    def roster = [:]

    String teamName = "MyTeam"
    String city = "Home City"
    String year = "2020"
    int nextBatter = 0
    int nextReliefPitcher = 0
    int nextStartingPitcher = 0
    String starter
    String currentPitcher
    String closer
    int pitchCount = 0
    def positions = [:]
    int wins = 0
    int losses = 0
    int winDiff = 0
    String gamesBack = ""
    boolean lineupSet = false
    Team team = null

    SimTeam() {
    }

    SimTeam(def teamRoster, String city, String teamName, String year) {
        def roster = teamRoster
        this.teamName = teamName
        this.city = city
        this.year = year
        separatePlayers(roster)
    }

    SimTeam(Team team) {
        HttpHistoricalDataManager dataMgr = new HttpHistoricalDataManager()
        def roster = dataMgr.get40ManRoster(team.team_id, team.year)
        this.teamName = team.name
        this.city = team.city
        this.year = team.year
        this.team = team
        separatePlayers(roster)
    }

    SimTeam(Map map) {
        this.teamName = map.teamName
        this.city = map.city
        this.year = map.year
        this.nextBatter = map.nextBatter // playerID
        this.nextReliefPitcher = map.nextReliefPitcher // playerID
        this.nextStartingPitcher = map.nextStartingPitcher // playerID
        this.pitchCount = map.pitchCount
        this.wins = map.wins
        this.losses = map.losses
        this.winDiff = map.winDiff
        this.lineupSet = map.lineupSet
        if (map.team) {
            this.team = new Team(map.team)
        }
        if (map.starter) { // playerID
            this.starter = map.starter
        }
        if (map.currentPitcher) { // playerID
            this.currentPitcher = map.currentPitcher
        }
        if (map.closer) { // playerID
            this.closer = map.closer
        }

        // Repopulate lists and maps.
        map.positions.keySet().each { key -> // playerID
            this.positions[key] = map.positions[key]
        }
        map.roster.keySet().each { key -> // playerID
            Map next = map.roster[key]
            if (next.containsKey("pitcherStats")) {
                // Pitcher
                this.roster[key] = new GamePitcher(next)
            } else {
                // Batter
                this.roster[key] = new GameBatter(next)
            }
        }
        map.lineup.each() { String p -> // playerID
            this.lineup << p
        }
        map.bench.each() { String q -> // playerID
            this.bench << q
        }
        // Repopulate lists and maps.
        map.rotation.each() { String r -> // playerID
            this.rotation << r
        }
        map.bullpen.each() { String s -> // playerID
            this.bullpen << s
        }
        map.pitchersUsed.each() { String s -> // playerID
            this.pitchersUsed << s
        }
        map.reservePitchers.each() { String s -> // playerID
            this.reservePitchers << s
        }
        map.doneStarters.each() { String s -> // playerID
            this.doneStarters << s
        }
        map.doneRelievers.each() { String s -> // playerID
            this.doneRelievers << s
        }
        map.originalRotation.each() { String s -> // playerID
            this.originalRotation << s
        }
        map.originalBullpen.each() { String s -> // playerID
            this.originalBullpen << s
        }
        map.originalLineup.each() { String s -> // playerID
            this.originalLineup << s
        }
        map.originalBench.each() { String s -> // playerID
            this.originalBench << s
        }
        //map.originalReservedPitchers.each() { String s -> // playerID
        //    this.originalReservedPitchers << s
        //}
    }

    String toString() {
        return teamName
    }

    // Is the specified object equal to this one?
    boolean equals(SimTeam target) {
        boolean result = true
        def m = "${C}.equals() - "
        if (! compareString("teamName", teamName, target.teamName)) { result = false }
        if (! compareString("city", city, target.city)) { result = false }
        if (! compareString("year", year, target.year)) { result = false }
        if (! compareString("starter", starter, target.starter)) { result = false }
        if (! compareString("currentPitcher", currentPitcher, target.currentPitcher)) { result = false }
        if (! compareString("closer", closer, target.closer)) { result = false }
        if (! compareInt("nextBatter", nextBatter, target.nextBatter)) { result = false }
        if (! compareInt("nextReliefPitcher", nextReliefPitcher, target.nextReliefPitcher)) { result = false }
        if (! compareInt("nextStartingPitcher", nextStartingPitcher, target.nextStartingPitcher)) { result = false }
        if (! compareInt("pitchCount", pitchCount, target.pitchCount)) { result = false }
        if (! compareInt("wins", wins, target.wins)) { result = false }
        if (! compareInt("losses", losses, target.losses)) { result = false }
        if (! compareInt("winDiff", winDiff, target.winDiff)) { result = false }
        if (! compareObject("team", team, target.team)) { result = false }
        if (! compareList("lineup", lineup, target.lineup)) { result = false }
        if (! compareList("bench", bench, target.bench)) { result = false }
        if (! compareList("rotation", rotation, target.rotation)) { result = false }
        if (! compareList("bullpen", bullpen, target.bullpen)) { result = false }
        if (! compareList("reservePitchers", reservePitchers, target.reservePitchers)) { result = false }
        if (! compareList("pitchersUsed", pitchersUsed, target.pitchersUsed)) { result = false }
        if (! compareMap("roster", roster, target.roster)) { result = false }
        if (! compareMap("positions", positions, target.positions)) { result = false }
        int playerCount = lineup.size() + bench.size() + rotation.size() + bullpen.size() + reservePitchers.size() + doneRelievers.size() + doneStarters.size()
        if (playerCount > 80) {
            throw new Exception("Player count ($playerCount) is greater than 80.")
        }
        if (pitchersUsed.size() > 12) {
            throw new Exception("Pitchers used ($pitchersUsed) should never be greater than 12.")
        }
        return result
    }

    // Is the specified object equal to this one?
    boolean equals(SimTeam target, List builder) {
        boolean result = true
        def m = "${C}.equals() - "
        if (! compareString("teamName", teamName, target.teamName, builder)) { result = false }
        if (! compareString("city", city, target.city, builder)) { result = false }
        if (! compareString("year", year, target.year, builder)) { result = false }
        if (! compareString("starter", starter, target.starter, builder)) { result = false }
        if (! compareString("currentPitcher", currentPitcher, target.currentPitcher, builder)) { result = false }
        if (! compareString("closer", closer, target.closer, builder)) { result = false }
        if (! compareInt("nextBatter", nextBatter, target.nextBatter, builder)) { result = false }
        if (! compareInt("nextReliefPitcher", nextReliefPitcher, target.nextReliefPitcher, builder)) { result = false }
        if (! compareInt("nextStartingPitcher", nextStartingPitcher, target.nextStartingPitcher, builder)) { result = false }
        if (! compareInt("pitchCount", pitchCount, target.pitchCount, builder)) { result = false }
        if (! compareInt("wins", wins, target.wins, builder)) { result = false }
        if (! compareInt("losses", losses, target.losses, builder)) { result = false }
        if (! compareInt("winDiff", winDiff, target.winDiff, builder)) { result = false }
        if (! compareObject("team", team, target.team, builder)) { result = false }
        if (! compareList("lineup", lineup, target.lineup, builder)) { result = false }
        if (! compareList("bench", bench, target.bench, builder)) { result = false }
        if (! compareList("rotation", rotation, target.rotation, builder)) { result = false }
        if (! compareList("bullpen", bullpen, target.bullpen, builder)) { result = false }
        if (! compareList("reservePitchers", reservePitchers, target.reservePitchers, builder)) { result = false }
        if (! compareList("pitchersUsed", pitchersUsed, target.pitchersUsed, builder)) { result = false }
        if (! compareMap("roster", roster, target.roster, builder)) { result = false }
        if (! compareMap("positions", positions, target.positions, builder)) { result = false }
        if (result) {
            if (! hideOK)
               builder << "$m SimTeams match?  OK"
        } else {
            builder << "$m SimTeams match?  NO MATCH"
        }
        int playerCount = lineup.size() + bench.size() + rotation.size() + bullpen.size() + reservePitchers.size() + doneRelievers.size() + doneStarters.size()
        if (playerCount > 40) {
            throw new Exception("Player count ($playerCount) is greater than 40.")
        }
        if (pitchersUsed.size() > 12) {
            throw new Exception("Pitchers used ($pitchersUsed) should never be greater than 12.")
        }

        return result
    }

    boolean assignPosition(Player batter, String position) {
        if (positions.containsKey(position)) {
            return false
        } else {
            positions << [position: batter]
            return true
        }
    }

/*
    List getTeamRoster() {
        []
    }
*/

    String getScheduleLookupKey() {
        team.scheduleLookupKey
    }

    SimTeam clear() {
        // Temporary
        //batters = []
        //pitchers = []
        //remainder = []
        //contact = []
        //power = []
        //speed = []
        //speedAndContact = []

        // Primary for game
        lineup = []
        bench = []
        rotation = []
        bullpen = []
        pitchersUsed = []
        reservePitchers = []
        teamName = "MyTeam"
        city = "Home City"
        year = "2020"
        nextBatter = 0
        nextReliefPitcher = 0
        nextStartingPitcher = 0
        starter = null
        currentPitcher = null
        closer = null
        pitchCount = 0
        teamRoster = null
        positions = [:]
        wins = 0
        losses = 0
        winDiff = 0
        team = null

        this
    }

    void separatePlayers(def teamRoster) {

        // Temporary
        List batters = []
        List pitchers = []
        List remainder = []
        List contact = []
        List power = []
        List speed = []
        List speedAndContact = []
        List middleInfielders = []
        List outerInfielders = []

        // Player groupings.
        List lineupPlayers = []
        List benchPlayers = []
        List bullpenPlayers = []
        List rotationPlayers = []
        List reservePitchersPlayers = []
        List battersPlayers = []

        teamRoster.each() { Player next ->
            String playerID = next.playerID
            if (next.isPitcher) {
                // Pitcher
                GamePitcher gamePitcher = new GamePitcher(next)
                roster[playerID] = gamePitcher
                pitchers << playerID
                if (next.pitcherStats.pitchingGamesStarted <= 7) {
                    if (next.pitcherStats.pitchingGames > 7) {
                        bullpenPlayers << gamePitcher
                        bullpen << playerID
                    } else {
                        reservePitchersPlayers << gamePitcher
                        reservePitchers << playerID
                    }
                } else {
                    rotationPlayers << gamePitcher
                    rotation << playerID
                }
            } else {
                // Batter
                GameBatter gameBatter = new GameBatter(next)
                roster[next.playerID] = gameBatter
                batters << playerID
                battersPlayers << gameBatter
            }
        }

        // Trim down rotation
        rotationPlayers.sort { a, b -> a.historicalEra <=> b.historicalEra }
        if (rotationPlayers.size() > 5) {
            // Remove pitchers to get down to 5.
            while (rotationPlayers.size() > 5) {
                reservePitchersPlayers << rotationPlayers.get(rotationPlayers.size() - 1)
                rotationPlayers.remove(rotationPlayers.size() - 1)
            }
        } else if (rotationPlayers.size() < 5) {
            // Pull pitchers from bullpen to get up to 5.
            auditLog.error("TODO: Not enough starting pitchers!!!!")
            throw new Exception("TODO: Not enough starting pitchers!!!!")
        }

        // Trim down bullpen
        bullpenPlayers.sort { a, b -> a.pitcherStats.era?.toString() <=> b.pitcherStats.era?.toString() }
        if (bullpenPlayers.size() > 5) {
            // Remove pitchers to get down to 5.
            while (bullpenPlayers.size() > 5) {
                reservePitchersPlayers << bullpenPlayers.get(bullpenPlayers.size() - 1)
                bullpenPlayers.remove(bullpenPlayers.size() - 1)
            }
        } else if (bullpenPlayers.size() < 5) {
            // Pull pitchers from bullpen to get up to 5.
            auditLog.error("TODO: Not enough starting pitchers!!!!")
            throw new Exception("TODO: Not enough starting pitchers!!!!")
        }

        // Sort reserve pitchers
        reservePitchersPlayers.sort { a, b -> a.simPitcher.pitcher.pitcherStats.era <=> b.simPitcher.pitcher.pitcherStats.era }

        auditLog.info ""
        auditLog.info "Rotation:  $year $teamName"
        rotationPlayers.each { next ->
            auditLog.info "   ${next.name} GS: ${next.pitcherStats.pitchingGamesStarted} GS: ${next.pitcherStats.pitchingGames} ERA: ${next.pitcherStats.era} Batters Retired: ${next.pitcherStats.pitchingBattersRetired} BB: ${next.pitcherStats.pitchingWalks} R: ${next.pitcherStats.pitchingRuns} ER: ${next.pitcherStats.pitchingEarnedRuns} SO: ${next.pitcherStats.pitchingStrikeouts} H: ${next.pitcherStats.pitchingHits} HR: ${next.pitcherStats.pitchingHomers} WP: ${next.pitcherStats.pitchingWildPitch} HBP: ${next.pitcherStats.pitchingHitBatter} Balk: ${next.pitcherStats.pitchingBalks} WHIP: ${next.pitcherStats.pitchingWhip}"
        }
        auditLog.info ""
        auditLog.info "Bullpen:  $year $teamName"
        bullpenPlayers.each { next ->
            auditLog.info "   ${next.name} GS: ${next.pitcherStats.pitchingGamesStarted} GS: ${next.pitcherStats.pitchingGames} ERA: ${next.pitcherStats.era} Batters Retired: ${next.pitcherStats.pitchingBattersRetired} BB: ${next.pitcherStats.pitchingWalks} R: ${next.pitcherStats.pitchingRuns} ER: ${next.pitcherStats.pitchingEarnedRuns} SO: ${next.pitcherStats.pitchingStrikeouts} H: ${next.pitcherStats.pitchingHits} HR: ${next.pitcherStats.pitchingHomers} WP: ${next.pitcherStats.pitchingWildPitch} HBP: ${next.pitcherStats.pitchingHitBatter} Balk: ${next.pitcherStats.pitchingBalks} WHIP: ${next.pitcherStats.pitchingWhip}"
        }

        // Create lineup.
        // POWER: Who hits the most home runs?
        auditLog.debug ""
        battersPlayers.sort { a, b -> a.homers <=> b.homers }
        int i = battersPlayers.size() - 1
        // Found cleanup hitter.
        auditLog.debug "#4 hitter: ${battersPlayers[i].name} Pos: ${battersPlayers[i].position} HR: ${battersPlayers[i].homers} SB: ${battersPlayers[i].stolenBases} Avg: ${battersPlayers[i].battingAvg} AB: ${battersPlayers[i].atBats}"
        power << battersPlayers[i]
        battersPlayers.remove(i)
        i--

        // Found other power hitters.
        auditLog.debug "#5 hitter: ${battersPlayers[i].name} Pos: ${battersPlayers[i].position} HR: ${battersPlayers[i].homers} SB: ${battersPlayers[i].stolenBases} Avg: ${battersPlayers[i].battingAvg} AB: ${battersPlayers[i].atBats}"
        power << battersPlayers[i]
        battersPlayers.remove(i)
        i--

        auditLog.debug "#6 hitter: ${battersPlayers[i].name} Pos: ${battersPlayers[i].position} HR: ${battersPlayers[i].homers} SB: ${battersPlayers[i].stolenBases} Avg: ${battersPlayers[i].battingAvg} AB: ${battersPlayers[i].atBats}"
        power << battersPlayers[i]
        battersPlayers.remove(i)
        i--

        // SPEED: Who steals the most bases?
        battersPlayers.sort { a, b -> a.stolenBases <=> b.stolenBases }
        i = battersPlayers.size() - 1
        while (i >= 0 && speed.size() < 2) {
            if (battersPlayers[i].battingAvg > new BigDecimal(".290") && batters[i].stolenBases > 25) {
                // Speed And Contact
                auditLog.debug "SpeedAndContact Found: ${battersPlayers[i].name} Pos: ${battersPlayers[i].position} SB: ${battersPlayers[i].stolenBases} Avg: ${battersPlayers[i].battingAvg} AB: ${battersPlayers[i].atBats}"
                speedAndContact << battersPlayers[i]
                battersPlayers.remove(i)
                i--
                //} else if (batters[i].stolenBases > 25) {
                //    // Speed
                //    println "Speed Found: ${batters[i].name} SB: ${batters[i].stolenBases} Avg: ${batters[i].battingAvg} AB: ${batters[i].atBats}"
                //    speed << batters[i]
                //    batters.remove(i)
                //    i--
            } else {
                i--
            }
        }

        // CONTACT: Who has the best batting avg?
        battersPlayers.sort { a, b -> a.battingAvg <=> b.battingAvg }
        i = battersPlayers.size() - 1
        while (i >= 0) {
            if (battersPlayers[i].atBats > 200) {
                // Contact
                auditLog.debug "Contact Found: ${battersPlayers[i].name} Pos: ${battersPlayers[i].position} SB: ${battersPlayers[i].stolenBases} Avg: ${battersPlayers[i].battingAvg} AB: ${battersPlayers[i].atBats}"
                contact << battersPlayers[i]
                battersPlayers.remove(i)
                i--
            } else {
                i--
            }
        }
        i = battersPlayers.size() - 1
        while (i >= 0) {
            // Remainder
            auditLog.debug "Remainder Found: ${battersPlayers[i].name} Pos: ${battersPlayers[i].position} SB: ${battersPlayers[i].stolenBases} Avg: ${battersPlayers[i].battingAvg} AB: ${battersPlayers[i].atBats}"
            remainder << battersPlayers[i]
            battersPlayers.remove(i)
            i--
        }

        // Players organized. Set lineup and defensive positions.
        auditLog.info ""
        auditLog.info "Batting Order:  $year $teamName"
        int positionsFilled = 0
        def primaryPosition
        // Speed And Contact
        while (speedAndContact.size() > 0) {
            primaryPosition = speedAndContact[0].position
            addIfPositionAvailable(primaryPosition, speedAndContact, "Speed-And-Contact", roster, lineup, bench, middleInfielders, outerInfielders, benchPlayers, lineupPlayers)
        }

        // Contact (first third)
        while (lineup.size() < 3 && contact.size() > 0) {
            primaryPosition = contact[0].position
            addIfPositionAvailable(primaryPosition, contact, "Contact", roster, lineup, bench, middleInfielders, outerInfielders, benchPlayers, lineupPlayers)
        }

        // Power
        while (lineup.size() < 6 && power.size() > 0) {
            primaryPosition = power[0].position
            addIfPositionAvailable(primaryPosition, power, "Power", roster, lineup, bench, middleInfielders, outerInfielders, benchPlayers, lineupPlayers)
        }

        // Fill up rest of line up.
        while (lineup.size() < 9 && contact.size() > 0) {
            primaryPosition = contact[0].position
            addIfPositionAvailable(primaryPosition, contact, "Contact", roster, lineup, bench, middleInfielders, outerInfielders, benchPlayers, lineupPlayers)
        }

        // Out of players? Fill up rest of line up using remainder players.
        while (lineup.size() < 9 && remainder.size() > 0) {
            primaryPosition = remainder[0].position
            addIfPositionAvailable(primaryPosition, remainder, "Remainder", roster, lineup, bench, middleInfielders, outerInfielders, benchPlayers, lineupPlayers)
        }
        if (! positions["SS"]) {
            addIfPositionAvailable("SS", middleInfielders, "MiddleInfielders", roster, lineup, bench, middleInfielders, outerInfielders, benchPlayers, lineupPlayers)
        }
        if (! positions["2B"]) {
            addIfPositionAvailable("2B", middleInfielders, "MiddleInfielders", roster, lineup, bench, middleInfielders, outerInfielders, benchPlayers, lineupPlayers)
        }
        if (! positions["3B"]) {
            addIfPositionAvailable("3B", outerInfielders, "OuterInfielders", roster, lineup, bench, middleInfielders, outerInfielders, benchPlayers, lineupPlayers)
        }
        if (! positions["1B"]) {
            addIfPositionAvailable("1B", outerInfielders, "OuterInfielders", roster, lineup, bench, middleInfielders, outerInfielders, benchPlayers, lineupPlayers)
        }

        auditLog.debug "Remaining: speedAndContact: ${speedAndContact.size()} speed: ${speed.size()} power: ${power.size()} contact: ${contact.size()}"

        // Migrate player groupings to permanent lists of playerIDs.
        this.rotation.clear()
        rotationPlayers.each { GamePitcher next ->
            this.rotation << next.playerID
            this.originalRotation << next.playerID
        }
        this.bullpen.clear()
        bullpenPlayers.each { GamePitcher next ->
            this.bullpen << next.playerID
            this.originalBullpen << next.playerID
            if (next.pitcherStats.pitchingGamesStarted > 0) {
                this.additionalStarters << next.playerID
            }
        }
        this.lineup.clear()
        lineupPlayers.each { GameBatter next ->
            this.lineup << next.playerID
            this.originalLineup << next.playerID
        }
        this.bench.clear()
        benchPlayers.each { GameBatter next ->
            this.bench << next.playerID
            this.originalBench << next.playerID
        }
        this.reservePitchers.clear()
        reservePitchersPlayers.each { GamePitcher next ->
            this.reservePitchers << next.playerID
            if (next.pitcherStats.pitchingGamesStarted > 0) {
                this.additionalStarters << next.playerID
            }
        }
    }

    void addIfPositionAvailable(def primaryPosition, def sourceList, def category, Map roster, List lineup, List bench, List middleInfielders, List outerInfielders, List benchPlayers, List lineupPlayers) {
        // Get the playerID and then get the player from the roster.
        def player = sourceList[0]
        if (primaryPosition == "OF") {
            if (! positions["LF"]) {
                primaryPosition = "LF"
            } else if (! positions["RF"]) {
                primaryPosition = "RF"
            } else if (! positions["CF"]) {
                primaryPosition = "CF"
            } else {
                primaryPosition = "LF"
            }
        }
        // Assign a fielding position and make sure that position is not taken.

        if (! (primaryPosition in ['DH','1B','2B','3B','SS','RF','CF','LF','OF','C','P'])) {
            if (! positions["DH"]) {
                // DH is available. Use that.
                sourceList[0].position = "DH"
                positions["DH"] = player?.playerID
                player.position = "DH"
                auditLog.info "   ${lineupPlayers.size() + 1}: ${player.name} Primary Pos: ${primaryPosition} Pos: ${player.position} SB: ${player.stolenBases} HR: ${player.homers} Avg: ${player.battingAvg} AB: ${player.atBats}   ${category}"
                lineupPlayers << player
                if (lineupPlayers.size() > 9) {
                    throw new Exception("Integrity Check failed. Lineup must no more than 9 players.")
                }
                benchPlayers.remove(player)
                sourceList.remove(0)
            }
        } else if (positions[primaryPosition]) {
            // Already taken. DH?
            if (positions["DH"]) {
                // Already taken. Skip this person.
                if (primaryPosition in ["2B","SS"]) {
                   middleInfielders << player
                }
                if (primaryPosition in ["1B","3B"]) {
                    outerInfielders << player
                }
                benchPlayers << player
                sourceList.remove(0)
            } else {
                // DH is available. Use that.
                sourceList[0].position = "DH"
                positions["DH"] = player?.playerID
                player.position = "DH"
                auditLog.info "   ${lineupPlayers.size() + 1}: ${player.name} Primary Pos: ${primaryPosition} Pos: ${player.position} SB: ${player.stolenBases} HR: ${player.homers} Avg: ${player.battingAvg} AB: ${player.atBats}   ${category}"
                lineupPlayers << player
                if (lineupPlayers.size() > 9) {
                    throw new Exception("Integrity Check failed. Lineup must no more than 9 players.")
                }
                benchPlayers.remove(player)
                sourceList.remove(0)
            }
        } else {
            if (sourceList[0] == null) {
                throw new Exception("Batter is null!!!")
            }
            positions[primaryPosition] = player?.playerID
            player?.position = primaryPosition
            auditLog.info "   ${lineupPlayers.size() + 1}: ${player.name} Primary Pos: ${primaryPosition} Pos: ${player.position} SB: ${player.stolenBases} HR: ${player.homers} Avg: ${player.battingAvg} AB: ${player.atBats}   ${category}"
            lineupPlayers << player
            if (lineupPlayers.size() > 9) {
                throw new Exception("Integrity Check failed. Lineup must no more than 9 players.")
            }
            benchPlayers.remove(player)
            sourceList.remove(0)
        }
    }

    GameBatter getBatter(String playerID) {
        GameBatter result = roster[playerID]
        if (! result) {
            return roster[playerID]
        } else {
            return result
        }
    }

    GamePitcher getPitcher(String playerID) {
        GamePitcher result = roster[playerID]
        if (! result) {
            return roster[playerID]
        } else {
            return result
        }
    }

    int getNextBatter() {
        nextBatter++
        if (nextBatter > 8) {
            nextBatter = 0
        }
        nextBatter
    }

    int getNextStartingPitcher() {
        int result = nextStartingPitcher
        nextStartingPitcher++
        if (nextStartingPitcher > 4) {
            nextStartingPitcher = 0
        }
        result
    }

}
