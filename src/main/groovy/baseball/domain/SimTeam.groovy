package baseball.domain

import baseball.processing.HttpHistoricalDataManager
import org.apache.log4j.Logger


class SimTeam extends Comparable {
    def C = "SimTeam"
    private auditLog = Logger.getLogger('Audit')

    // Temporary
    def batters = []
    def pitchers = []
    def remainder = []
    def contact = []
    def power = []
    def speed = []
    def speedAndContact = []
    def middleInfielders = []
    def outerInfielders = []

    // Primary for game
    def lineup = []
    def bench = []
    def rotation = []
    def bullpen = []
    def pitchersUsed = []
    def reservePitchers = []
    String teamName = "MyTeam"
    String city = "Home City"
    String year = "2020"
    int nextBatter = 0
    int nextReliefPitcher = 0
    int nextStartingPitcher = 0
    GamePitcher starter
    GamePitcher currentPitcher
    GamePitcher closer
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
        this.nextBatter = map.nextBatter
        this.nextReliefPitcher = map.nextReliefPitcher
        this.nextStartingPitcher = map.nextStartingPitcher
        this.pitchCount = map.pitchCount
        this.wins = map.wins
        this.losses = map.losses
        this.winDiff = map.winDiff
        this.lineupSet = map.lineupSet
        if (map.team) {
            this.team = new Team(map.team)
        }
        if (map.starter) {
            this.starter = new GamePitcher(map.starter)
        }
        if (map.currentPitcher) {
            this.currentPitcher = new GamePitcher(map.currentPitcher)
        }
        if (map.closer) {
            this.closer = new GamePitcher(map.closer)
        }

        // Repopulate lists and maps.
        map.positions.keySet().each { key ->
            GameBatter batter = new GameBatter(map.positions[key])
            this.positions[key] = batter
        }
        map.lineup.each() { Map p ->
            this.lineup << new GameBatter(p)
        }
        map.bench.each() { Map q ->
            this.bench << new GameBatter(q)
        }
        // Repopulate lists and maps.
        map.rotation.each() { Map r ->
            this.rotation << new GamePitcher(r)
        }
        map.bullpen.each() { Map s ->
            this.bullpen << new GamePitcher(s)
        }
        map.pitchersUsed.each() { Map s ->
            this.pitchersUsed << new GamePitcher(s)
        }
        map.reservePitchers.each() { Map s ->
            this.reservePitchers << new GamePitcher(s)
        }
    }

    // Is the specified object equal to this one?
    boolean equals(SimTeam target) {
        boolean result = true
        def m = "${C}.equals() - "
        if (! compareString("teamName", teamName, target.teamName)) { result = false }
        else if (! compareString("city", city, target.city)) { result = false }
        else if (! compareString("year", year, target.year)) { result = false }
        else if (! compareInt("nextBatter", nextBatter, target.nextBatter)) { result = false }
        else if (! compareInt("nextReliefPitcher", nextReliefPitcher, target.nextReliefPitcher)) { result = false }
        else if (! compareInt("nextStartingPitcher", nextStartingPitcher, target.nextStartingPitcher)) { result = false }
        else if (! compareInt("pitchCount", pitchCount, target.pitchCount)) { result = false }
        else if (! compareInt("wins", wins, target.wins)) { result = false }
        else if (! compareInt("losses", losses, target.losses)) { result = false }
        else if (! compareInt("winDiff", winDiff, target.winDiff)) { result = false }
        else if (! compareObject("starter", starter, target.starter)) { result = false }
        else if (! compareObject("currentPitcher", currentPitcher, target.currentPitcher)) { result = false }
        else if (! compareObject("closer", closer, target.closer)) { result = false }
        else if (! compareObject("team", team, target.team)) { result = false }
        else if (! teamRoster.equals(target.teamRoster)) { result = false }
        else if (! positions.equals(target.positions)) { result = false }
        else if (! lineup.equals(target.lineup)) { result = false }
        else if (! bench.equals(target.bench)) { result = false }
        else if (! rotation.equals(target.rotation)) { result = false }
        else if (! pitchersUsed.equals(target.pitchersUsed)) { result = false }
        else if (! bullpen.equals(target.bullpen)) { result = false }
        else if (! reservePitchers.equals(target.reservePitchers)) { result = false }

        return result
    }

    // Is the specified object equal to this one?
    boolean equals(SimTeam target, List builder) {
        boolean result = true
        def m = "${C}.equals() - "
        if (! compareString("teamName", teamName, target.teamName, builder)) { result = false }
        if (! compareString("city", city, target.city, builder)) { result = false }
        if (! compareString("year", year, target.year, builder)) { result = false }
        if (! compareInt("nextBatter", nextBatter, target.nextBatter, builder)) { result = false }
        if (! compareInt("nextReliefPitcher", nextReliefPitcher, target.nextReliefPitcher, builder)) { result = false }
        if (! compareInt("nextStartingPitcher", nextStartingPitcher, target.nextStartingPitcher, builder)) { result = false }
        if (! compareInt("pitchCount", pitchCount, target.pitchCount, builder)) { result = false }
        if (! compareInt("wins", wins, target.wins, builder)) { result = false }
        if (! compareInt("losses", losses, target.losses, builder)) { result = false }
        if (! compareInt("winDiff", winDiff, target.winDiff, builder)) { result = false }
        if (! compareObject("starter", starter, target.starter, builder)) { result = false }
        if (! compareObject("currentPitcher", currentPitcher, target.currentPitcher, builder)) { result = false }
        if (! compareObject("closer", closer, target.closer, builder)) { result = false }
        if (! compareObject("team", team, target.team, builder)) { result = false }
        if (! compareList("lineup", lineup, target.lineup, builder)) { result = false }
        if (! compareList("bench", bench, target.bench, builder)) { result = false }
        if (! compareList("rotation", rotation, target.rotation, builder)) { result = false }
        if (! compareList("bullpen", bullpen, target.bullpen, builder)) { result = false }
        if (! compareList("reservePitchers", reservePitchers, target.reservePitchers, builder)) { result = false }
        if (! compareList("pitchersUsed", pitchersUsed, target.pitchersUsed, builder)) { result = false }
        if (! compareList("teamRoster", teamRoster, target.teamRoster, builder)) { result = false }
        if (! compareMap("positions", positions, target.positions, builder)) { result = false }
        if (result) {
            builder << "$m SimTeams match?  OK"
        } else {
            builder << "$m SimTeams match?  NO MATCH"
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

    List getTeamRoster() {
        []
    }

    String getScheduleLookupKey() {
        team.scheduleLookupKey
    }

    SimTeam clear() {
        // Temporary
        batters = []
        pitchers = []
        remainder = []
        contact = []
        power = []
        speed = []
        speedAndContact = []

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
        teamRoster.each() { next ->
            if (next.isPitcher) {
                // Pitcher
                pitchers << next
                if (next.pitcherStats.pitchingGamesStarted <= 7) {
                    if (next.pitcherStats.pitchingGames > 7) {
                        bullpen << next
                    }
                } else {
                    rotation << next
                }
            } else {
                // Batter
                batters << next
            }
        }

        // Trim down rotation
        rotation.sort { a, b -> a.pitcherStats.era <=> b.pitcherStats.era }
        if (rotation.size() > 5) {
            // Remove pitchers to get down to 5.
            while (rotation.size() > 5) {
                reservePitchers << new GamePitcher(rotation.get(rotation.size() - 1))
                rotation.remove(rotation.size() - 1)
            }
        } else if (rotation.size() < 5) {
            // Pull pitchers from bullpen to get up to 5.
            auditLog.error("TODO: Not enough starting pitchers!!!!")
            throw new Exception("TODO: Not enough starting pitchers!!!!")
        }

        // Trim down bullpen
        bullpen.sort { a, b -> a.pitcherStats.era?.toString() <=> b.pitcherStats.era?.toString() }
        if (bullpen.size() > 5) {
            // Remove pitchers to get down to 5.
            while (bullpen.size() > 5) {
                reservePitchers << new GamePitcher(bullpen.get(bullpen.size() - 1))
                bullpen.remove(bullpen.size() - 1)
            }
        } else if (bullpen.size() < 5) {
            // Pull pitchers from bullpen to get up to 5.
            auditLog.error("TODO: Not enough starting pitchers!!!!")
            throw new Exception("TODO: Not enough starting pitchers!!!!")
        }

        // Sort reserve pitchers
        reservePitchers.sort { a, b -> a.simPitcher.pitcher.pitcherStats.era <=> b.simPitcher.pitcher.pitcherStats.era }

        auditLog.info ""
        auditLog.info "Rotation:  $year $teamName"
        rotation.each { next ->
            auditLog.info "   ${next.name} GS: ${next.pitcherStats.pitchingGamesStarted} GS: ${next.pitcherStats.pitchingGames} ERA: ${next.pitcherStats.era} Batters Retired: ${next.pitcherStats.pitchingBattersRetired} BB: ${next.pitcherStats.pitchingWalks} R: ${next.pitcherStats.pitchingRuns} ER: ${next.pitcherStats.pitchingEarnedRuns} SO: ${next.pitcherStats.pitchingStrikeouts} H: ${next.pitcherStats.pitchingHits} HR: ${next.pitcherStats.pitchingHomers} WP: ${next.pitcherStats.pitchingWildPitch} HBP: ${next.pitcherStats.pitchingHitBatter} Balk: ${next.pitcherStats.pitchingBalks} WHIP: ${next.pitcherStats.pitchingWhip}"
        }
        auditLog.info ""
        auditLog.info "Bullpen:  $year $teamName"
        bullpen.each { next ->
            auditLog.info "   ${next.name} GS: ${next.pitcherStats.pitchingGamesStarted} GS: ${next.pitcherStats.pitchingGames} ERA: ${next.pitcherStats.era} Batters Retired: ${next.pitcherStats.pitchingBattersRetired} BB: ${next.pitcherStats.pitchingWalks} R: ${next.pitcherStats.pitchingRuns} ER: ${next.pitcherStats.pitchingEarnedRuns} SO: ${next.pitcherStats.pitchingStrikeouts} H: ${next.pitcherStats.pitchingHits} HR: ${next.pitcherStats.pitchingHomers} WP: ${next.pitcherStats.pitchingWildPitch} HBP: ${next.pitcherStats.pitchingHitBatter} Balk: ${next.pitcherStats.pitchingBalks} WHIP: ${next.pitcherStats.pitchingWhip}"
        }

        // Prepare rotation for game (wrap with SimPitcher and GamePitcher).
        // SimPitcher for season stats, GamePitcher for game stats.
        def templist = []
        rotation.each { next ->
            templist << new GamePitcher(next)
        }
        rotation = templist

        // Prepare bullpen for game (wrap with SimPitcher and GamePitcher).
        // SimPitcher for season stats, GamePitcher for game stats.
        templist = []
        bullpen.each { next ->
            templist << new GamePitcher(next)
        }
        bullpen = templist

        // Create lineup.
        // POWER: Who hits the most home runs?
        auditLog.debug ""
        batters.sort { a, b -> a.homers <=> b.homers }
        int i = batters.size() - 1
        // Found cleanup hitter.
        auditLog.debug "#4 hitter: ${batters[i].name} Pos: ${batters[i].primaryPosition} HR: ${batters[i].homers} SB: ${batters[i].stolenBases} Avg: ${batters[i].battingAvg} AB: ${batters[i].atBats}"
        power << batters[i]
        batters.remove(i)
        i--

        // Found other power hitters.
        auditLog.debug "#5 hitter: ${batters[i].name} Pos: ${batters[i].primaryPosition} HR: ${batters[i].homers} SB: ${batters[i].stolenBases} Avg: ${batters[i].battingAvg} AB: ${batters[i].atBats}"
        power << batters[i]
        batters.remove(i)
        i--

        auditLog.debug "#6 hitter: ${batters[i].name} Pos: ${batters[i].primaryPosition} HR: ${batters[i].homers} SB: ${batters[i].stolenBases} Avg: ${batters[i].battingAvg} AB: ${batters[i].atBats}"
        power << batters[i]
        batters.remove(i)
        i--

        // SPEED: Who steals the most bases?
        batters.sort { a, b -> a.stolenBases <=> b.stolenBases }
        i = batters.size() - 1
        while (i >= 0 && speed.size() < 2) {
            if (batters[i].battingAvg > new BigDecimal(".290") && batters[i].stolenBases > 25) {
                // Speed And Contact
                auditLog.debug "SpeedAndContact Found: ${batters[i].name} Pos: ${batters[i].primaryPosition} SB: ${batters[i].stolenBases} Avg: ${batters[i].battingAvg} AB: ${batters[i].atBats}"
                speedAndContact << batters[i]
                batters.remove(i)
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
        batters.sort { a, b -> a.battingAvg <=> b.battingAvg }
        i = batters.size() - 1
        while (i >= 0) {
            if (batters[i].atBats > 200) {
                // Contact
                auditLog.debug "Contact Found: ${batters[i].name} Pos: ${batters[i].primaryPosition} SB: ${batters[i].stolenBases} Avg: ${batters[i].battingAvg} AB: ${batters[i].atBats}"
                contact << batters[i]
                batters.remove(i)
                i--
            } else {
                i--
            }
        }
        i = batters.size() - 1
        while (i >= 0) {
            // Remainder
            auditLog.debug "Remainder Found: ${batters[i].name} Pos: ${batters[i].primaryPosition} SB: ${batters[i].stolenBases} Avg: ${batters[i].battingAvg} AB: ${batters[i].atBats}"
            remainder << batters[i]
            batters.remove(i)
            i--
        }

        // Players organized. Set lineup and defensive positions.
        auditLog.info ""
        auditLog.info "Batting Order:  $year $teamName"
        int positionsFilled = 0
        def primaryPosition
        // Speed And Contact
        while (speedAndContact.size() > 0) {
            primaryPosition = speedAndContact[0].primaryPosition
            addIfPositionAvailable(primaryPosition, speedAndContact, "Speed-And-Contact")
        }

        // Contact (first third)
        while (lineup.size() < 3 && contact.size() > 0) {
            primaryPosition = contact[0].primaryPosition
            addIfPositionAvailable(primaryPosition, contact, "Contact")
        }

        // Power
        while (lineup.size() < 6 && power.size() > 0) {
            primaryPosition = power[0].primaryPosition
            addIfPositionAvailable(primaryPosition, power, "Power")
        }

        // Fill up rest of line up.
        while (lineup.size() < 9 && contact.size() > 0) {
            primaryPosition = contact[0].primaryPosition
            addIfPositionAvailable(primaryPosition, contact, "Contact")
        }

        // Out of players? Fill up rest of line up using remainder players.
        while (lineup.size() < 9 && remainder.size() > 0) {
            primaryPosition = remainder[0].primaryPosition
            addIfPositionAvailable(primaryPosition, remainder, "Remainder")
        }
        if (! positions["SS"]) {
            addIfPositionAvailable("SS", middleInfielders, "MiddleInfielders")
        }
        if (! positions["2B"]) {
            addIfPositionAvailable("2B", middleInfielders, "MiddleInfielders")
        }
        if (! positions["3B"]) {
            addIfPositionAvailable("3B", outerInfielders, "OuterInfielders")
        }
        if (! positions["1B"]) {
            addIfPositionAvailable("1B", outerInfielders, "OuterInfielders")
        }

        batters = []
        pitchers = []
        remainder = []
        contact = []
        power = []
        speed = []
        speedAndContact = []
        middleInfielders = []
        outerInfielders = []

        auditLog.debug "Remaining: speedAndContact: ${speedAndContact.size()} speed: ${speed.size()} power: ${power.size()} contact: ${contact.size()}"
    }

    void addIfPositionAvailable(def primaryPosition, def sourceList, def category) {
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
        if (positions[primaryPosition]) {
            // Already taken. DH?
            if (positions["DH"]) {
                // Already taken. Skip this person.
                def batter = new GameBatter(sourceList[0])
                if (primaryPosition in ["2B","SS"]) {
                   middleInfielders << sourceList[0]
                }
                if (primaryPosition in ["1B","3B"]) {
                    outerInfielders << sourceList[0]
                }
                bench << batter
                sourceList.remove(0)
            } else {
                // DH is available. Use that.
                sourceList[0].primaryPosition = "DH"
                def batter = new GameBatter(sourceList[0])
                positions["DH"] = batter
                batter.position = "DH"
                auditLog.info "   ${lineup.size() + 1}: ${sourceList[0].name} Pos: ${batter.position} SB: ${sourceList[0].stolenBases} HR: ${sourceList[0].homers} Avg: ${sourceList[0].battingAvg} AB: ${sourceList[0].atBats}   ${category}"
                lineup << batter
                bench.remove(batter)
                sourceList.remove(0)
            }
        } else {
            if (sourceList[0] == null) {
                throw new Exception("Batter is null!!!")
            }
            def batter = new GameBatter(sourceList[0])
            positions[primaryPosition] = batter
            batter.position = primaryPosition
            auditLog.info "   ${lineup.size() + 1}: ${sourceList[0].name} Pos: ${batter.position} SB: ${sourceList[0].stolenBases} HR: ${sourceList[0].homers} Avg: ${sourceList[0].battingAvg} AB: ${sourceList[0].atBats}   ${category}"
            lineup << batter
            bench.remove(batter)
            sourceList.remove(0)
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
