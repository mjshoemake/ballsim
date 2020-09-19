package baseball.domain

import org.apache.log4j.Logger


class SimTeam {
    def auditLog = Logger.getLogger('Audit')

    // Temporary
    def batters = []
    def pitchers = []
    def remainder = []
    def contact = []
    def power = []
    def speed = []
    def speedAndContact = []

    // Primary for game
    def lineup = []
    def bench = []
    def rotation = []
    def bullpen = []
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
    def teamRoster = null
    def positions = [:]
    int wins = 0
    int losses = 0
    int winDiff = 0

    SimTeam(def teamRoster, String city, String teamName, String year) {
        this.teamRoster = teamRoster
        this.teamName = teamName
        this.city = city
        this.year = year
        separatePlayers()
    }

    boolean assignPosition(Player batter, String position) {
        if (positions.containsKey(position)) {
            return false
        } else {
            positions << [position: batter]
            return true
        }
    }

    void separatePlayers() {
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
        rotation.sort { a,b -> a.pitcherStats.era <=> b.pitcherStats.era }
        if (rotation.size() > 5) {
            // Remove pitchers to get down to 5.
            while (rotation.size() > 5) {
                rotation.remove(rotation.size()-1)
            }
        } else if (rotation.size() < 5) {
            // Pull pitchers from bullpen to get up to 5.
            auditLog.error("TODO: Not enough starting pitchers!!!!")
            throw new Exception("TODO: Not enough starting pitchers!!!!")
        }

        // Trim down bullpen
        bullpen.sort { a,b -> a.pitcherStats.era?.toString() <=> b.pitcherStats.era?.toString() }
        if (bullpen.size() > 5) {
            // Remove pitchers to get down to 5.
            while (bullpen.size() > 5) {
                bullpen.remove(bullpen.size()-1)
            }
        } else if (bullpen.size() < 5) {
            // Pull pitchers from bullpen to get up to 5.
            auditLog.error("TODO: Not enough starting pitchers!!!!")
            throw new Exception("TODO: Not enough starting pitchers!!!!")
        }

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
        batters.sort { a,b -> a.homers <=> b.homers }
        int i=batters.size()-1
        // Found cleanup hitter.
        auditLog.debug "#4 hitter: ${batters[i].name} HR: ${batters[i].homers} SB: ${batters[i].stolenBases} Avg: ${batters[i].battingAvg} AB: ${batters[i].atBats}"
        power << batters[i]
        batters.remove(i)
        i--

        // Found other power hitters.
        auditLog.debug "#5 hitter: ${batters[i].name} HR: ${batters[i].homers} SB: ${batters[i].stolenBases} Avg: ${batters[i].battingAvg} AB: ${batters[i].atBats}"
        power << batters[i]
        batters.remove(i)
        i--

        auditLog.debug "#6 hitter: ${batters[i].name} HR: ${batters[i].homers} SB: ${batters[i].stolenBases} Avg: ${batters[i].battingAvg} AB: ${batters[i].atBats}"
        power << batters[i]
        batters.remove(i)
        i--

        // SPEED: Who steals the most bases?
        batters.sort { a,b -> a.stolenBases <=> b.stolenBases }
        i=batters.size()-1
        while (i>=0 && speed.size() < 2) {
            if (batters[i].battingAvg > new BigDecimal(".290") && batters[i].stolenBases > 25) {
                // Speed And Contact
                auditLog.debug "SpeedAndContact Found: ${batters[i].name} SB: ${batters[i].stolenBases} Avg: ${batters[i].battingAvg} AB: ${batters[i].atBats}"
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
        batters.sort { a,b -> a.battingAvg <=> b.battingAvg }
        i=batters.size()-1
        while (i>=0) {
            if (batters[i].atBats > 200) {
                // Contact
                auditLog.debug "Contact Found: ${batters[i].name} SB: ${batters[i].stolenBases} Avg: ${batters[i].battingAvg} AB: ${batters[i].atBats}"
                contact << batters[i]
                batters.remove(i)
                i--
            } else {
                i--
            }
        }
        i=batters.size()-1
        while (i>=0) {
            // Remainder
            auditLog.debug "Remainder Found: ${batters[i].name} SB: ${batters[i].stolenBases} Avg: ${batters[i].battingAvg} AB: ${batters[i].atBats}"
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

        auditLog.debug "Remaining: speedAndContact: ${speedAndContact.size()} speed: ${speed.size()} power: ${power.size()} contact: ${contact.size()}"
    }

    void addIfPositionAvailable(def primaryPosition, def sourceList, def category) {
        // Assign a fielding position and make sure that position is not taken.
        if (positions[primaryPosition]) {
            // Already taken. DH?
            if (positions["DH"]) {
                // Already taken. Skip this person.
                def batter = new GameBatter(sourceList[0])
                bench << batter
                sourceList.remove(0)
            } else {
                // DH is available. Use that.
                sourceList[0].primaryPosition = "DH"
                def batter = new GameBatter(sourceList[0])
                positions["DH"] = batter
                auditLog.info "   ${lineup.size()+1}: ${sourceList[0].name} Pos: ${sourceList[0].primaryPosition} SB: ${sourceList[0].stolenBases} HR: ${sourceList[0].homers} Avg: ${sourceList[0].battingAvg} AB: ${sourceList[0].atBats}   ${category}"
                lineup << batter
                sourceList.remove(0)
            }
        } else {
            def batter = new GameBatter(sourceList[0])
            positions[primaryPosition] = batter
            auditLog.info "   ${lineup.size()+1}: ${sourceList[0].name} Pos: ${sourceList[0].primaryPosition} SB: ${sourceList[0].stolenBases} HR: ${sourceList[0].homers} Avg: ${sourceList[0].battingAvg} AB: ${sourceList[0].atBats}   ${category}"
            lineup << batter
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
