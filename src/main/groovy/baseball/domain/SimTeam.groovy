package baseball.domain


class SimTeam {
    def batters = []
    def pitchers = []
    def lineup = []
    def bench = []
    def remainder = []
    def contact = []
    def power = []
    def speed = []
    def speedAndContact = []
    def rotation = []
    def bullpen = []
    def position1B
    def position2B
    def position3B
    def positionSS
    def positionC
    def positionLF
    def positionCF
    def positionRF
    String teamName = "MyTeam"
    int nextBatter = 0
    GamePitcher starter
    GamePitcher currentPitcher
    GamePitcher closer
    int pitchCount = 0
    def teamRoster = null
    def positions = [:]

    int getNextBatter() {
        nextBatter++
        if (nextBatter > 8) {
            nextBatter = 0
        }
        nextBatter
    }

    SimTeam(def teamRoster) {
        this.teamRoster = teamRoster
        separatePlayers()
    }

    boolean assignPosition(Batter batter, String position) {
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
            throw new Exception("TODO: Not enough starting pitchers!!!!")
        }

        rotation.each { next ->
            println "Rotation: ${next.name} GS: ${next.pitcherStats.pitchingGamesStarted} ERA: ${next.pitcherStats.era}"
        }
        bullpen.each { next ->
            println "Bullpen: ${next.name} GS: ${next.pitcherStats.pitchingGamesStarted} ERA: ${next.pitcherStats.era}"
        }

        // Create lineup.
        // POWER: Who hits the most home runs?
        batters.sort { a,b -> a.homers <=> b.homers }
        int i=batters.size()-1
        // Found cleanup hitter.
        println "#4 hitter: ${batters[i].name} HR: ${batters[i].homers} SB: ${batters[i].stolenBases} Avg: ${batters[i].battingAvg} AB: ${batters[i].atBats}"
        power << batters[i]
        batters.remove(i)
        i--

        // Found other power hitters.
        println "#5 hitter: ${batters[i].name} HR: ${batters[i].homers} SB: ${batters[i].stolenBases} Avg: ${batters[i].battingAvg} AB: ${batters[i].atBats}"
        power << batters[i]
        batters.remove(i)
        i--

        println "#6 hitter: ${batters[i].name} HR: ${batters[i].homers} SB: ${batters[i].stolenBases} Avg: ${batters[i].battingAvg} AB: ${batters[i].atBats}"
        power << batters[i]
        batters.remove(i)
        i--

        // SPEED: Who steals the most bases?
        batters.sort { a,b -> a.stolenBases <=> b.stolenBases }
        i=batters.size()-1
        while (i>=0 && speed.size() < 2) {
            if (batters[i].battingAvg > new BigDecimal(".290") && batters[i].stolenBases > 25) {
                // Speed And Contact
                println "SpeedAndContact Found: ${batters[i].name} SB: ${batters[i].stolenBases} Avg: ${batters[i].battingAvg} AB: ${batters[i].atBats}"
                speedAndContact << batters[i]
                batters.remove(i)
                i--
            } else if (batters[i].stolenBases > 25) {
                // Speed
                println "Speed Found: ${batters[i].name} SB: ${batters[i].stolenBases} Avg: ${batters[i].battingAvg} AB: ${batters[i].atBats}"
                speed << batters[i]
                batters.remove(i)
                i--
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
                println "Contact Found: ${batters[i].name} SB: ${batters[i].stolenBases} Avg: ${batters[i].battingAvg} AB: ${batters[i].atBats}"
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
            println "Remainder Found: ${batters[i].name} SB: ${batters[i].stolenBases} Avg: ${batters[i].battingAvg} AB: ${batters[i].atBats}"
            remainder << batters[i]
            batters.remove(i)
            i--
        }

        // Players organized. Set lineup and defensive positions.
        println ""
        println "Batting Order:"
        int positionsFilled = 0
        // Speed And Contact
        while (speedAndContact.size() > 0) {
            // TODO assign a fielding position and make sure that position is not taken.
            println "   ${positionsFilled+1}: ${speedAndContact[0].name} Pos: ${speedAndContact[0].primaryPosition} SB: ${speedAndContact[0].stolenBases} Avg: ${speedAndContact[0].battingAvg} AB: ${speedAndContact[0].atBats}"
            lineup << speedAndContact[0]
            speedAndContact.remove(0)
            positionsFilled++
        }

        // Speed Only
        while (positionsFilled < 2 && speed.size() > 0) {
            // TODO assign a fielding position and make sure that position is not taken.
            println "   ${positionsFilled+1}: ${speed[0].name} Pos: ${speed[0].primaryPosition} SB: ${speed[0].stolenBases} Avg: ${speed[0].battingAvg} AB: ${speed[0].atBats}"
            lineup << speed[0]
            speed.remove(0)
            positionsFilled++
        }

        // Contact (first third)
        while (positionsFilled < 3 && contact.size() > 0) {
            // TODO assign a fielding position and make sure that position is not taken.
            println "   ${positionsFilled+1}: ${contact[0].name} Pos: ${contact[0].primaryPosition} SB: ${contact[0].stolenBases} Avg: ${contact[0].battingAvg} AB: ${contact[0].atBats}"
            lineup << contact[0]
            contact.remove(0)
            positionsFilled++
        }

        // Power
        while (positionsFilled < 6 && power.size() > 0) {
            // TODO assign a fielding position and make sure that position is not taken.
            println "   ${positionsFilled+1}: ${power[0].name} Pos: ${power[0].primaryPosition} SB: ${power[0].stolenBases} Avg: ${power[0].battingAvg} AB: ${power[0].atBats}"
            lineup << power[0]
            power.remove(0)
            positionsFilled++
        }

        // Fill up rest of line up.
        while (positionsFilled < 9 && contact.size() > 0) {
            // TODO assign a fielding position and make sure that position is not taken.
            println "   ${positionsFilled+1}: ${contact[0].name} Pos: ${contact[0].primaryPosition} SB: ${contact[0].stolenBases} Avg: ${contact[0].battingAvg} AB: ${contact[0].atBats}"
            lineup << contact[0]
            contact.remove(0)
            positionsFilled++
        }

        println "Remaining: speedAndContact: ${speedAndContact.size()} speed: ${speed.size()} power: ${power.size()} contact: ${contact.size()}"
    }

}
