package baseball.domain


class SimTeam {
    def batters = []
    def pitchers = []
    def lineup = []
    def bench = []
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

    void separatePlayers() {
        teamRoster.each() { next ->
            if (next.isPitcher) {
                // Pitcher
                pitchers << next
                if (next.pitcherStats.pitchingGamesStarted <= 10) {
                    if (next.pitcherStats.pitchingGames > 10) {
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
        batters.sort { a,b -> a.stolenBases <=> b.stolenBases }
        int i=batters.size()-1
        while (i>=0 && batters[i].battingAvg < new BigDecimal(".290") && batters[i].stolenBases > 20) {

            println "Skipping: ${batters[i].name} SB: ${batters[i].stolenBases} Avg: ${batters[i].battingAvg}"
            i--
        }
        if (i>=0 && batters[i].battingAvg < new BigDecimal(".290") && batters[i].stolenBases > 30) {
            // Found leadoff hitter.
            println "Leadoff hitter: ${batters[i].name} SB: ${batters[i].stolenBases} Avg: ${batters[i].battingAvg} AB: ${batters[i].atBats}"
        } else {
            // Select based on batting avg.
            batters.sort { a,b -> a.battingAvg <=> b.battingAvg }
            i=batters.size()-1
            while (i>=0 && batters[i].atBats < 200) {
                println "Skipping: ${batters[i].name} SB: ${batters[i].stolenBases} Avg: ${batters[i].battingAvg} AB: ${batters[i].atBats}"
                i--
            }
            if (i>=0 && batters[i].atBats > 200) {
                // Found leadoff hitter.
                println "Leadoff hitter: ${batters[i].name} SB: ${batters[i].stolenBases} Avg: ${batters[i].battingAvg} AB: ${batters[i].atBats}"
            }
        }
    }

}
