package baseball.domain

import baseball.processing.ScheduleLoader
import org.apache.log4j.Logger

class Season {

    /**
     * The Log4J logger used by this object.
     */
    protected Logger log = Logger.getLogger("Core");
    def highlightsLog = Logger.getLogger('highlights')
    def seasonStatsLog = Logger.getLogger('seasonStats')

    String year
    int nextGameIndex = 0
    Map leagues = [:]
    def final teamMap = [:]

    def final schedule

    Season(Map teamMap) {
        this.year = teamMap.year
        this.teamMap = teamMap
        League league = null
        teamMap.remove("year")
        teamMap.each() { next ->
            addTeamToSeason(next.value)
        }
        String scheduleName = getScheduleName()
        // Schedule
        def scheduleLoader = new ScheduleLoader()
        def schedule = scheduleLoader.loadRoundRobinScheduleFromFile(league)

    }

    Season(def league, schedule) {
        this.league = league
        this.schedule = schedule
        def it = league.keySet().iterator()
        while (it.hasNext()) {
            def div = league[it.next()]
            div.each { next ->
                teamList << next
            }
        }
        String scheduleName = getScheduleName()
    }

    void addTeamToSeason(Team team) {
        League league = null
        if (! leagues.containsKey(team.league)) {
            league = new League(team.league)
            leagues[team.league] = league
        } else {
            league = leagues[team.league]
        }
        league.addTeam(team)
    }

    String getScheduleName() {
        StringBuilder builder = new StringBuilder()
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

        int i = 1
        leagues.keySet().each { nextKey ->
            if (i > 1) {
                builder.append("_")
            }
            League nextLeague = leagues[nextKey]
            String lID = alphabet.substring(i-1,i)
            int j = 1
            nextLeague.divisions.keySet().each() { nextDiv ->
                if (j > 1) {
                    builder.append("-")
                }
                Division nextDivision = nextLeague.divisions[nextDiv]
                String dID = alphabet.substring(j-1,j)
                int dSize = nextDivision.teams.size()
                builder.append("$lID$dID$dSize")
                j++
            }
            i++
        }
        def result = builder.toString()
        println "Schedule name: $result"
        result
    }


    void playSeason() {
        playSeason(162)
    }

    void playSeason(int maxGamesPerTeam) {
        playSeason(maxGamesPerTeam, true)
    }

    void playSeason(int maxGamesPerTeam, boolean reworkLineup) {
        int gameCount = 0
        while (! schedule.isEmpty() && gameCount < (maxGamesPerTeam * teamList.size())) {
            def game = schedule[0]
            schedule.remove(0)
            def ballGame = new BallGame()
            ballGame.gameLogEnabled = this.gameLogEnabled
            ballGame.highlightsLogEnabled = this.highlightsLogEnabled
            ballGame.boxscoreLogEnabled = this.boxscoreLogEnabled
            ballGame.homeTeam = teamList[game.homeTeamIndex]
            ballGame.awayTeam = teamList[game.awayTeamIndex]

            // Rework lineup automatically
            if (reworkLineup) {
                adjustLineup(ballGame.homeTeam)
                adjustLineup(ballGame.awayTeam)
            }


            ballGame.start()
            gameCount++
            if ((gameCount % (teamList.size()*20)) == 0) {
                logStandings(seasonStatsLog)
            }
        }

        logStandings(seasonStatsLog)
        logTeamStats(teamList)
    }

    private void reworkLineup(Map team) {

    }

    private String[] logStandings(Logger log) {
        highlightsLog.debug("")
        highlightsLog.debug("")
        highlightsLog.debug("Standings:")
        highlightsLog.debug("")
        def it = league.keySet().iterator()
        while (it.hasNext()) {
            String key = it.next()
            def div = league[key]
            logDivisionStandings(log, key, div)
        }
        highlightsLog.debug("")
    }

    private String[] logDivisionStandings(Logger log, String name, def division) {
        highlightsLog.debug("$name:")
        highlightsLog.debug "   ${format("Team", 25)}:    ${format("Wins", 4)}   ${format("Losses", 6)}   GB"
        def sorted = orderTeamsByRecord(division)
        int bestWins = -1
        int bestLosses = -1
        sorted.each {
            if (bestWins == -1) {
                // Found first place
                bestWins = it["wins"]
                bestLosses = it["losses"]
                it["gamesBack"] = "--"
            } else {
                // Others
                double gb = (((bestWins - it["wins"]) + (it["losses"] - bestLosses)) / 2)
                if (gb == 0.0) {
                    it["gamesBack"] = "--"
                } else {
                    it["gamesBack"] = gb + ""
                }
            }
            highlightsLog.debug "   ${format(it["teamName"], 25)}:    ${format(it["wins"], 4)}   ${format(it["losses"], 6)}   ${format(it["gamesBack"], 4)}"
        }
        highlightsLog.debug("")
    }

    private def orderTeamsByRecord(def teamList) {
        def newList = []
        teamList.each() {
            newList << it
        }
        newList.sort {it.winDiff}
        newList.reverse(true)
        newList
    }

    private def logTeamStats(def teamList) {
        seasonStatsLog.debug ""
        seasonStatsLog.debug "END OF SEASON STATS"

        teamList.each { team ->
            def teamName = team["teamName"]
            def lineup = team["originalLineup"]
            def bench = team["originalBench"]
            def rotation = team["rotation"]
            def bullpen = team["bullpen"]
            seasonStatsLog.debug ""
            seasonStatsLog.debug "${format(teamName, 25)}:    ${format(team["wins"], 4)}   ${format(team["losses"], 6)}"
            seasonStatsLog.debug ""
            seasonStatsLog.debug "$teamName:"
            seasonStatsLog.debug "   Lineup:"
            seasonStatsLog.debug "      ${format("Batter", 20)}  ${format("AB", 3)}  ${format("R", 3)}  ${format("H", 3)}  ${format("2B", 3)}  ${format("3B", 3)}  ${format("HR", 3)}  ${format("RBI", 4)}  ${format("BB", 3)}  ${format("SO", 3)}  ${format("SB", 3)}  ${format("CS", 3)}  ${format("AVG", 5)} "
            lineup.each {
                seasonStatsLog.debug "      ${format(it.nameFirst + " " + it.nameLast, 20)}  ${format(it.simBatter.atBats, 3)}  ${format(it.simBatter.runs, 3)}  ${format(it.simBatter.hits, 3)}  ${format(it.simBatter.doubles, 3)}  ${format(it.simBatter.triples, 3)}  ${format(it.simBatter.homers, 3)}  ${format(it.simBatter.rbi, 4)}  ${format(it.simBatter.walks, 3)}  ${format(it.simBatter.strikeouts, 3)}  ${format(it.simBatter.stolenBases, 3)}  ${format(it.simBatter.caughtStealing, 3)}  ${format(it.simBatter.battingAvg, 5)}"
            }
            seasonStatsLog.debug ""
            seasonStatsLog.debug "   Bench:"
            seasonStatsLog.debug "      ${format("Batter", 20)}  ${format("AB", 3)}  ${format("R", 3)}  ${format("H", 3)}  ${format("2B", 3)}  ${format("3B", 3)}  ${format("HR", 3)}  ${format("RBI", 4)}  ${format("BB", 3)}  ${format("SO", 3)}  ${format("SB", 3)}  ${format("CS", 3)}  ${format("AVG", 5)} "
            bench.each {
                seasonStatsLog.debug "      ${format(it.nameFirst + " " + it.nameLast, 20)}  ${format(it.simBatter.atBats, 3)}  ${format(it.simBatter.runs, 3)}  ${format(it.simBatter.hits, 3)}  ${format(it.simBatter.doubles, 3)}  ${format(it.simBatter.triples, 3)}  ${format(it.simBatter.homers, 3)}  ${format(it.simBatter.rbi, 4)}  ${format(it.simBatter.walks, 3)}  ${format(it.simBatter.strikeouts, 3)}  ${format(it.simBatter.stolenBases, 3)}  ${format(it.simBatter.caughtStealing, 3)}  ${format(it.simBatter.battingAvg, 5)}"
            }
            seasonStatsLog.debug ""
            seasonStatsLog.debug "   Pitchers:"
            seasonStatsLog.debug "      ${format("Pitcher", 20)}  ${format("W", 3)}  ${format("L", 3)}  ${format("IP", 5)}  ${format("R", 3)}  ${format("ERA", 5)}  ${format("H", 3)}  ${format("HR", 3)}  ${format("BB", 3)}  ${format("SO", 3)}   ${format("OBA", 3)}"
            rotation.each {
                seasonStatsLog.debug "      ${format(it.nameFirst + " " + it.nameLast, 20)}  ${format(it.simPitcher.wins, 3)}  ${format(it.simPitcher.losses, 3)}  ${format(it.simPitcher.battersRetired/3, 5)}  ${format(it.simPitcher.runs, 3)}  ${it.simPitcher.era}  ${format(it.simPitcher.hits, 3)}  ${format(it.simPitcher.homers, 3)}  ${format(it.simPitcher.walks, 3)}  ${format(it.simPitcher.strikeouts, 3)}   ${format(((SimPitcher)it.simPitcher).oppBattingAvg, 5)}"
            }
            bullpen.each {
                seasonStatsLog.debug "      ${format(it.nameFirst + " " + it.nameLast, 20)}  ${format(it.simPitcher.wins, 3)}  ${format(it.simPitcher.losses, 3)}  ${format(it.simPitcher.battersRetired/3, 5)}  ${format(it.simPitcher.runs, 3)}  ${it.simPitcher.era}  ${format(it.simPitcher.hits, 3)}  ${format(it.simPitcher.homers, 3)}  ${format(it.simPitcher.walks, 3)}  ${format(it.simPitcher.strikeouts, 3)}   ${format(((SimPitcher)it.simPitcher).oppBattingAvg, 5)}"
            }
            seasonStatsLog.debug ""
            seasonStatsLog.debug ""
        }

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

    def adjustLineup(Map team) {
        def originalLineup = team["lineup"]
        def originalBench = team["bench"]
        def selected = []
        def lineup = []

        // Sort bench by rank
        def sortedBench = originalBench.clone()
        sortedBench.each {
            it.sortBy = "rank"
        }
        Collections.sort(sortedBench)

        def P = []
        def C = []
        def first = []
        def second = []
        def third = []
        def shortstop = []
        def LF = []
        def RF = []
        def CF = []
        def benchByPos = ["P":P, "C":C, "1B":first, "2B":second, "3B":third, "SS":shortstop, "LF":LF, "CF":CF, "RF":RF]

        // Break bench up by positions
        sortedBench.each() { nextBatter ->
            Map positions = nextBatter.simBatter.batter.position
            positions.keySet().each() { next ->
                switch (next) {
                    case "P":
                        P << nextBatter
                        break
                    case "C":
                        C << nextBatter
                        break
                    case "1B":
                        first << nextBatter
                        break
                    case "2B":
                        second << nextBatter
                        break
                    case "3B":
                        third << nextBatter
                        break
                    case "SS":
                        shortstop << nextBatter
                        break
                    case "LF":
                        LF << nextBatter
                        break
                    case "RF":
                        RF << nextBatter
                        break
                    case "CF":
                        CF << nextBatter
                        break
                }
            }
        }

        boolean lineupAdjusted = false
        originalLineup.each {
            if (it.simBatter.maxedOut) {
                // Find a replacement
                int i = 0
                def posList = benchByPos[it.position]
                while (i <= posList.size()-1 && posList[i].simBatter.maxedOut) {
                    i++
                }
                if (i <= posList.size()-1) {
                    // Match found.
                    log.debug("${team["teamName"]}: Replacing ${it.getNameFirst()} ${it.getNameLast()} with ${posList[i].getNameFirst()} ${posList[i].getNameLast()}.")
                    originalBench << it
                    posList[i].position = it.position
                    selected << posList[i]
                    originalBench.remove(posList[i])
                    log.debug("${team["teamName"]}: New Bench")
                    originalBench.each {
                        log.debug("   ${it.getNameFirst()} ${it.getNameLast()}")
                    }
                    lineupAdjusted = true
                } else {
                    selected << it
                }
            } else {
                selected << it
            }
        }

        // Adjusting hitting slots
        if (lineupAdjusted) {
            log.debug("${team["teamName"]}: Selected Lineup")
            selected.each {
                log.debug("   ${it.getNameFirst()} ${it.getNameLast()}")
            }
            log.debug("Lineup adjusted.")
        }
        def power = []

        selected.each {item -> item.sortBy = "homers"}
        Collections.sort(selected)

        // Pick out the two players with the best homers (4 and 5 in the order)
        power << selected[0]
        selected.remove(0)
        power << selected[0]
        selected.remove(0)

        selected.each {item -> item.sortBy = "obp"}
        Collections.sort(selected)

        log.debug("Selected count: (should be 8)  ${selected.size()}")
        // Pick out the three players with the best OBP (1, 2, and 3 in the order)
        lineup << selected[0]
        selected.remove(0)
        lineup << selected[0]
        selected.remove(0)
        lineup << selected[0]
        selected.remove(0)

        // Sort remaining players by OPS
        selected.each() {item -> item.sortBy = "ops"}
        Collections.sort(selected)

        // Add power hitters to lineup
        power.each {item -> lineup << item}
        // Add remaining players to lineup
        selected.each {item -> lineup << item}

        // Save new bench
        team["bench"] = originalBench

        int index = 1
        log.debug("Final Lineup:")
        lineup.each {
            log.debug("   $index: ${it.simBatter.batter.nameFirst} ${it.simBatter.batter.nameLast} (${it.position})")
            index++
        }

        team["lineup"] = lineup
    }

}
