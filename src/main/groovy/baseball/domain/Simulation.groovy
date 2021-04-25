package baseball.domain


import groovy.json.JsonOutput
import mjs.common.utils.TransactionIdGen
import org.apache.log4j.Logger
import org.bson.types.ObjectId

class Simulation extends Comparable {

    /**
     * The Log4J logger used by this object.
     */
    private Logger log = Logger.getLogger("Debug");
    private Logger auditLog = Logger.getLogger('Audit')
    private Logger highlightsLog = Logger.getLogger('highlights')
    private Logger seasonStatsLog = Logger.getLogger('seasonStats')

    def C = "Simulation"
    ObjectId _id
    String simulationID = "TestSim"
    String simulationName = "Test Sim"
    String year
    String name
    //Map leagues = [:]
    List leagueKeys = []
    // List of League objects, to preserve order.
    List leagueList = []
    // Letters to use for divisionKey.
    List<String> keyTokens = ["A","B","C","D","E","F","G","H","I","J","K","L"]

    ScheduledSeason schedule = null

    Simulation() {
    }

    // MJS: TODO This is WRONG. Does not rebuild correctly.
    Simulation(Map map) {
        this.simulationID = map.simulationID
        this.simulationName = map.simulationName
        this.year = map.year
        this.name = map.name
        map.leagueList.each() {
            this.leagueList << new League(it)
        }
        map.leagueKeys.each { key ->
            this.leagueKeys << key
        }
        this.leagueKeys = this.leagueKeys.sort()
        //this.schedule = new ScheduledSeason(map.schedule)
        //this.schedule.simulationID = this.simulationID
    }

    // Is the specified object equal to this one?
    boolean equals(Simulation target) {
        boolean result = true
        def m = "${C}.equals() - "
        if (! compareString("simulationName", simulationName, target.simulationName)) { result = false }
        if (! compareString("simulationID", simulationID, target.simulationID)) { result = false }
        if (! compareString("name", name, target.name)) { result = false }
        if (! compareString("year", year, target.year)) { result = false }
        try {
            if (! compareList("leagueKeys", leagueKeys.sort(), target.leagueKeys.sort())) { result = false }
        } catch (Exception e) {
            println "ERROR!!!! ${e.getMessage()}"
        }
        if (! compareList("leagueList", leagueList.sort(), target.leagueList.sort())) { result = false }
//        if (! compareMap("teamMap", teamMap, target.teamMap)) { result = false }
//        if (! compareMap("scheduleTeamLookup", scheduleTeamLookup, target.scheduleTeamLookup)) { result = false }
//        if (! compareMap("leagues", leagues, target.leagues)) { result = false }
        if (! compareObject("schedule", schedule, target.schedule)) { result = false }
//        if (! compareList("leagueList", leagueList, target.leagueList)) { result = false }
        if (schedule.rounds.size() > 170) {
            throw new Exception("Error: Schedule rounds (${schedule.rounds.size()}) should never be greater than 170.")
        }
        if (leagues.size() > 4) {
            throw new Exception("Error: League count (${leagues.size()}) should never be greater than 4.")
        }
        if (teamMap.size() > 40) {
            throw new Exception("Error: Team count (${teamMap.size()}) should never be greater than 40.")
        }
        return result
    }

    Map toMap() {
        Map result = [:]
        result["simulationID"] = simulationID
        result["simulationName"] = simulationName
        result["year"] = year
        result["name"] = name
        log.debug "Converting leagueList to Maps..."
        result["leagueList"] = toListOfMaps(leagueList.sort())
        log.debug "Converting leagueKeys to Maps..."
        result["leagueKeys"] = toListOfMaps(leagueKeys.sort())
        log.debug "Conversions DONE."

        log.debug "logSizeOfObject 'Simulation'..."
        logSizeOfObject(1, "Simulation", result)
        //log.debug "logSizeOfObject 'Simulation.leagueKeys'..."
        //logSizeOfObject(2, "Simulation.leagueKeys", result["leagueKeys"])
        //log.debug "logSizeOfObject 'Simulation.leagueList'..."
        //logSizeOfObject(2, "Simulation.leagueList", result["leagueList"])
        log.debug "logSizeOfObject DONE."
        //schedule.simulationID = simulationID
        //result["schedule"] = schedule
        //result["teamMap"] = teamMap
        //result["scheduleTeamLookup"] = scheduleTeamLookup
        result
    }

    Map getLeagues() {
        Map result = [:]
        leagueList.each() { League nextLeague ->
            result[nextLeague.abbreviation] = nextLeague
        }
        return result
    }

    List<SimTeam> getTeams() {
        List<SimTeam> teamsList = []
        leagueList.each() { League nextLeague ->
            teamsList << nextLeague.teams
        }
        def result = teamsList.flatten()
        result
    }

    Map getTeamMap() {
        Map result = [:]
        teams.each() { SimTeam next ->
            result[next.teamName] = next
        }
        return result
    }

    Map getScheduleTeamLookup() {
        Map result = [:]
        teams.each() { SimTeam next ->
            result[next.scheduleLookupKey] = next
        }
        return result
    }


    /*
    Map toJsonMap() {
        Map result = new HashMap()
        result["simulationID"] = simulationID
        result["simulationName"] = simulationName
        result["year"] = year
        result["name"] = name
        //result["leagues"] = leagues
        //result["leagueList"] = leagueList
        //result["teamMap"] = teamMap
        //result["schedule"] = schedule
        //result["scheduleTeamLookup"] = scheduleTeamLookup
        result
    }
    */

    String toJson() {
        def m = "${C}.toJson() - "
        //Map jsonMap = toJsonMap()
        Map jsonMap = toMap()
        //String jSim = new JsonBuilder(jsonMap).toString()
        String jSim = JsonOutput.toJson(jsonMap)
        log.debug("$m Converted to JSON successfully.")
    }

    synchronized ScheduledGame popNextGame() {
        ScheduledGame game = null
        if (schedule?.rounds?.size() > 0) {
            ScheduledRound round = schedule?.rounds[0]
            if (round?.games.size() > 0) {
                game = round?.games[0]
                // Removing the game from the list.
                round?.games.remove(game)
            } else {
                throw new SimulationException("Expected at least one game remaining in round but none found.")
            }
            if (round?.games.size() == 0) {
                // Done with this round. Removing from the list of rounds.
                schedule.rounds.remove(round)
            }

        }
        game
    }

    synchronized int countGamesLeftInRound() {
        if (schedule?.rounds?.size() > 0) {
            ScheduledRound round = schedule?.rounds[0]
            return round.games.size()
        } else {
            return 0
        }
    }

    synchronized int countRoundsLeftInSeason() {
        if (schedule?.rounds?.size() > 0) {
            return schedule?.rounds?.size()
        } else {
            return 0
        }
    }

    synchronized int getCurrentRoundNumber() {
        if (schedule?.rounds?.size() > 0) {
            ScheduledRound round = schedule?.rounds[0]
            return round.roundNum
        } else {
            return -1
        }
    }

    static String generateSimulationID() {
        generateSimulationID("Test")
    }

    static String generateSimulationID(String prefix) {
        if (prefix) {
            TransactionIdGen.nextVal(prefix)
        } else {
            TransactionIdGen.nextVal("Test")
        }
    }

    void addTeamToSeason(Team team) {
        League league
        if (! leagues.containsKey(team.league)) {
            String leagueKey = keyTokens[leagues.size()]
            league = new League(team.league, leagueKey, simulationID)
            leagueList << league
            leagueKeys << team.league
        } else {
            league = leagues[team.league]
        }
        league.addTeam(team)
        leagueKeys = leagueKeys.sort()
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

    int getTeamCount() {
        int total = 0
        leagues.keySet().each { nextKey ->
            League nextLeague = leagues[nextKey]
            nextLeague.divisions.keySet().each() { nextDiv ->
                Division nextDivision = nextLeague.divisions[nextDiv]
                int dSize = nextDivision.teams.size()
                total += dSize
            }
        }
        total
    }

    void playSimRound(int numRounds) {
        playSimGame(false, numRounds)
    }

    void playSimRound(boolean logStandingsEnabled, int numRounds) {
        def m = "${C}.playSimRound() - "

        int initialRoundNum = getCurrentRoundNumber()
        while (getCurrentRoundNumber() == initialRoundNum) {
            // Get the next game to play.
            ScheduledGame game = popNextGame()
            //log.debug "$m scheduleTeamLookup:"
            //LogUtils.debug(log, sim.scheduleTeamLookup, "   ", true)

            // Get the next two teams.
            SimTeam homeTeam = scheduleTeamLookup[game.homeTeam]
            SimTeam awayTeam = scheduleTeamLookup[game.awayTeam]
            log.debug "$m Next game:  Home: ${homeTeam.teamName}  Away: ${awayTeam.teamName}  Round #: ${game.roundNum}  Game #: ${game.gameNum}"

            // Play game.
            def ballGame = new BallGame()
            ballGame.gameLogEnabled = true
            ballGame.highlightsLogEnabled = true
            ballGame.boxscoreLogEnabled = true
            boolean reworkLineup = true
            ballGame.homeTeam = homeTeam
            ballGame.awayTeam = awayTeam
            ballGame.start()
        }

        if (logStandingsEnabled) {
            logStandings()
        }
    }

    void playSimGame() {
        playSimGame(false)
    }

    void playSimGame(boolean logStandingsEnabled) {
        def m = "${C}.playSimGame() - "
        // Get the next game to play.
        ScheduledGame game = popNextGame()
        if (game != null) {
            //log.debug "$m scheduleTeamLookup:"
            //LogUtils.debug(log, sim.scheduleTeamLookup, "   ", true)

            // Get the next two teams.
            SimTeam homeTeam = scheduleTeamLookup[game.homeTeam]
            SimTeam awayTeam = scheduleTeamLookup[game.awayTeam]
            log.debug "$m Next game:  Home: ${homeTeam.teamName}  Away: ${awayTeam.teamName}  Round #: ${game.roundNum}  Game #: ${game.gameNum}"

            // Play game.
            def ballGame = new BallGame()
            ballGame.gameLogEnabled = true
            ballGame.highlightsLogEnabled = true
            ballGame.boxscoreLogEnabled = true
            boolean reworkLineup = true
            ballGame.homeTeam = homeTeam
            ballGame.awayTeam = awayTeam

            // Rework lineup automatically
            //if (reworkLineup) {
            //    adjustLineup(ballGame.homeTeam)
            //    adjustLineup(ballGame.awayTeam)
            //}
            ballGame.start()
        }
        if (logStandingsEnabled) {
            logStandings()
        }
    }

    void loadFromJson(def json) {

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
            //if (reworkLineup) {
            //    adjustLineup(ballGame.homeTeam)
            //    adjustLineup(ballGame.awayTeam)
            //}


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

    void logDivisions(Logger log) {
       leagueList.each() { next ->
           log.info("League: ${next.abbreviation} key=${next.leagueKey}")
           println("League: ${next.abbreviation} key=${next.leagueKey}")
           next.divisionsList.each() { nextDiv ->
               log.info("   Division: ${nextDiv.name}  (${nextDiv.abbreviation})  key: ${nextDiv.divisionKey}")
               println("   Division: ${nextDiv.name}  (${nextDiv.abbreviation})  key: ${nextDiv.divisionKey}")
           }
       }
    }

    void logStandings() {
        highlightsLog.debug("")
        highlightsLog.debug("")
        highlightsLog.debug("Standings:")
        highlightsLog.debug("")
        def keys = leagues.keySet().iterator()
        while (keys.hasNext()) {
            String key = keys.next()
            League league = leagues[key]
            def divKeys = league.divisions.keySet().iterator()
            while (divKeys.hasNext()) {
                String divKey = divKeys.next()
                Division division = league.divisions[divKey]
                logDivisionStandings(key, divKey, division)
            }
        }
        highlightsLog.debug("")
    }

    private void logDivisionStandings(String leagueKey, String divKey, def division) {
        highlightsLog.debug("$leagueKey $divKey:")
        highlightsLog.debug "   ${format("Team", 25)}:    ${format("Wins", 4)}   ${format("Losses", 6)}   GB"
        def sorted = orderTeamsByRecord(division.teams)
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

    void logSchedule() {
        auditLog.debug("")
        auditLog.debug("Standings:")
        auditLog.debug("")
        auditLog.debug("Num Rounds: ${schedule.rounds.size()}  Rounds Completed: ${schedule.roundCompleted}")
        int nextRound = 1
        schedule.rounds.each { ScheduledRound round ->
            if (round.roundNum != nextRound) {
                auditLog.debug("   Mismatch: ${round.roundNum} != ${nextRound}")
            }
            nextRound++
        }
        auditLog.debug("Done!")
    }

    void logPitchingStatsAccuracy() {
        int pTeamWins = 0
        int numPlayers = 0
        auditLog.debug("")
        auditLog.debug("Pitching Stats Accuracy:")
        auditLog.debug("")

        int numPitchers = 0
        int simSaves, simWins, simLosses, simGamesStarted, simGames = 0
        int saves, wins, losses, gamesStarted, games = 0
        def ttlWinPercent = 0
        def ttlLossPercent = 0
        def ttlGamesStartedPercent = 0
        def ttlGamesPercent = 0

        teams.each() { SimTeam next ->
            pTeamWins = 0
            numPlayers = 0

            auditLog.debug("")
            auditLog.debug("${next.teamName} Potential Starters:")
            // How many potential starters?
            int possibleStarters = 0
            next.originalBullpen.each() { String playerID ->
                GamePitcher pitcher = next.getPitcher(playerID)
                if (pitcher.pitcherStats.pitchingGamesStarted > 0) {
                    possibleStarters++
                    auditLog.debug("   ${pitcher.name}")
                }
            }
            next.reservePitchers.each() { String playerID ->
                GamePitcher pitcher = next.getPitcher(playerID)
                if (pitcher.pitcherStats.pitchingGamesStarted > 0) {
                    possibleStarters++
                    auditLog.debug("   ${pitcher.name}")
                }
            }

            auditLog.debug("${next.teamName} Done Starters:")
            next.doneStarters.each() {
                GamePitcher pitcher = next.getPitcher(it)
                auditLog.debug("   ${pitcher.name}")
            }
            auditLog.debug format("${next.teamName} Rotation: (${next.originalRotation.size()})", 50) + "  " +
                                   format("Wins",20) + "  " +
                                   format("Losses",20) + "  " +
                                   format("Games Started",20) + "  " +
                                   format("ERA",20)

            next.originalRotation.each() { String playerID ->
                GamePitcher nextPitcher = next.getPitcher(playerID)
                SimPitcher simPitcher = nextPitcher.simPitcher
                def winPercent = 0
                def lossPercent = 0
                def gsPercent = 0
                numPitchers++
                wins += simPitcher.pitcher.pitcherStats.pitchingWins
                simWins += simPitcher.wins
                losses += simPitcher.pitcher.pitcherStats.pitchingLosses
                simLosses += simPitcher.losses
                gamesStarted += simPitcher.pitcher.pitcherStats.pitchingGamesStarted
                simGamesStarted += simPitcher.gamesStarted
                if (simPitcher.pitcher.pitcherStats.pitchingWins != 0) {
                    winPercent = ((simPitcher.wins - simPitcher.pitcher.pitcherStats.pitchingWins) / simPitcher.pitcher.pitcherStats.pitchingWins) * 100
                }
                if (simPitcher.pitcher.pitcherStats.pitchingLosses != 0) {
                    lossPercent = ((simPitcher.losses - simPitcher.pitcher.pitcherStats.pitchingLosses) / simPitcher.pitcher.pitcherStats.pitchingLosses) * 100
                }
                if (simPitcher.pitcher.pitcherStats.pitchingGamesStarted != 0) {
                    gsPercent = ((simPitcher.gamesStarted - simPitcher.pitcher.pitcherStats.pitchingGamesStarted) / simPitcher.pitcher.pitcherStats.pitchingGamesStarted) * 100
                }
                StringBuilder builder = new StringBuilder("   ${format(next.teamName, 15)} ")
                builder << " ${format(simPitcher.nameFirst + " " + simPitcher.nameLast, 30)}"
                builder << "  ${format(winPercent.toInteger() + "%  -> " + simPitcher.wins + " / " + simPitcher.pitcher.pitcherStats.pitchingWins, 20)}"
                builder << "  ${format(lossPercent.toInteger() + "%  -> " + simPitcher.losses + " / " + simPitcher.pitcher.pitcherStats.pitchingLosses, 20)}"
                builder << "  ${format(gsPercent.toInteger() + "%  -> " + simPitcher.gamesStarted + " / " + simPitcher.pitcher.pitcherStats.pitchingGamesStarted, 20)}"
                builder << "  ${format(simPitcher.era + " / " + simPitcher.pitcher.pitcherStats.pitchingEra, 20)}"
                auditLog.debug builder.toString()
            }

            auditLog.debug format("${next.teamName} Current Rotation: (${next.rotation.size()})", 50) + "  " +
                           format("Wins",20) + "  " +
                           format("Losses",20) + "  " +
                           format("Games Started",20) + "  " +
                           format("ERA",20)

            next.rotation.each() { String playerID ->
                GamePitcher nextPitcher = next.getPitcher(playerID)
                SimPitcher simPitcher = nextPitcher.simPitcher
                def winPercent = 0
                def lossPercent = 0
                def gsPercent = 0
                numPitchers++
                wins += simPitcher.pitcher.pitcherStats.pitchingWins
                simWins += simPitcher.wins
                losses += simPitcher.pitcher.pitcherStats.pitchingLosses
                simLosses += simPitcher.losses
                gamesStarted += simPitcher.pitcher.pitcherStats.pitchingGamesStarted
                simGamesStarted += simPitcher.gamesStarted
                if (simPitcher.pitcher.pitcherStats.pitchingWins != 0) {
                    winPercent = ((simPitcher.wins - simPitcher.pitcher.pitcherStats.pitchingWins) / simPitcher.pitcher.pitcherStats.pitchingWins) * 100
                }
                if (simPitcher.pitcher.pitcherStats.pitchingLosses != 0) {
                    lossPercent = ((simPitcher.losses - simPitcher.pitcher.pitcherStats.pitchingLosses) / simPitcher.pitcher.pitcherStats.pitchingLosses) * 100
                }
                if (simPitcher.pitcher.pitcherStats.pitchingGamesStarted != 0) {
                    gsPercent = ((simPitcher.gamesStarted - simPitcher.pitcher.pitcherStats.pitchingGamesStarted) / simPitcher.pitcher.pitcherStats.pitchingGamesStarted) * 100
                }
                StringBuilder builder = new StringBuilder("   ${format(next.teamName, 15)} ")
                builder << " ${format(simPitcher.nameFirst + " " + simPitcher.nameLast, 30)}"
                builder << "  ${format(winPercent.toInteger() + "%  -> " + simPitcher.wins + " / " + simPitcher.pitcher.pitcherStats.pitchingWins, 20)}"
                builder << "  ${format(lossPercent.toInteger() + "%  -> " + simPitcher.losses + " / " + simPitcher.pitcher.pitcherStats.pitchingLosses, 20)}"
                builder << "  ${format(gsPercent.toInteger() + "%  -> " + simPitcher.gamesStarted + " / " + simPitcher.pitcher.pitcherStats.pitchingGamesStarted, 20)}"
                builder << "  ${format(simPitcher.era + " / " + simPitcher.pitcher.pitcherStats.pitchingEra, 20)}"
                auditLog.debug builder.toString()
            }

            List additionalStarters = []
            next.rotation.each() { String playerID ->
                if (! next.originalRotation.contains(playerID)) {
                    additionalStarters << playerID
                }
            }
            auditLog.debug format("${next.teamName} Additional Starters: (${next.additionalStarters.size()})", 50) + "  " +
                    format("Wins",20) + "  " +
                    format("Losses",20) + "  " +
                    format("Games Started",20)
            next.additionalStarters.each() { String playerID ->
                GamePitcher nextPitcher = next.getPitcher(playerID)
                SimPitcher simPitcher = nextPitcher.simPitcher
                def winPercent = 0
                def lossPercent = 0
                def gsPercent = 0
                numPitchers++
                wins += simPitcher.pitcher.pitcherStats.pitchingWins
                simWins += simPitcher.wins
                losses += simPitcher.pitcher.pitcherStats.pitchingLosses
                simLosses += simPitcher.losses
                gamesStarted += simPitcher.pitcher.pitcherStats.pitchingGamesStarted
                simGamesStarted += simPitcher.gamesStarted
                if (simPitcher.pitcher.pitcherStats.pitchingWins != 0) {
                    winPercent = ((simPitcher.wins - simPitcher.pitcher.pitcherStats.pitchingWins) / simPitcher.pitcher.pitcherStats.pitchingWins) * 100
                }
                if (simPitcher.pitcher.pitcherStats.pitchingLosses != 0) {
                    lossPercent = ((simPitcher.losses - simPitcher.pitcher.pitcherStats.pitchingLosses) / simPitcher.pitcher.pitcherStats.pitchingLosses) * 100
                }
                if (simPitcher.pitcher.pitcherStats.pitchingGamesStarted != 0) {
                    gsPercent = ((simPitcher.gamesStarted - simPitcher.pitcher.pitcherStats.pitchingGamesStarted) / simPitcher.pitcher.pitcherStats.pitchingGamesStarted) * 100
                }
                StringBuilder builder = new StringBuilder("   ${format(next.teamName, 15)} ")
                builder << " ${format(simPitcher.nameFirst + " " + simPitcher.nameLast, 30)}"
                builder << "  ${format(winPercent.toInteger() + "%  -> " + simPitcher.wins + " / " + simPitcher.pitcher.pitcherStats.pitchingWins, 20)}"
                builder << "  ${format(lossPercent.toInteger() + "%  -> " + simPitcher.losses + " / " + simPitcher.pitcher.pitcherStats.pitchingLosses, 20)}"
                builder << "  ${format(gsPercent.toInteger() + "%  -> " + simPitcher.gamesStarted + " / " + simPitcher.pitcher.pitcherStats.pitchingGamesStarted, 20)}"
                builder << "  ${format(simPitcher.era + " / " + simPitcher.pitcher.pitcherStats.pitchingEra, 20)}"
                auditLog.debug builder.toString()
            }

            next.doneStarters.each() { String playerID ->
                if (! next.originalRotation.contains(playerID)) {
                    GamePitcher nextPitcher = next.getPitcher(playerID)
                    SimPitcher simPitcher = nextPitcher.simPitcher
                    def winPercent = 0
                    def lossPercent = 0
                    def gsPercent = 0
                    numPitchers++
                    wins += simPitcher.pitcher.pitcherStats.pitchingWins
                    simWins += simPitcher.wins
                    losses += simPitcher.pitcher.pitcherStats.pitchingLosses
                    simLosses += simPitcher.losses
                    gamesStarted += simPitcher.pitcher.pitcherStats.pitchingGamesStarted
                    simGamesStarted += simPitcher.gamesStarted
                    if (simPitcher.pitcher.pitcherStats.pitchingWins != 0) {
                        winPercent = ((simPitcher.wins - simPitcher.pitcher.pitcherStats.pitchingWins) / simPitcher.pitcher.pitcherStats.pitchingWins) * 100
                    }
                    if (simPitcher.pitcher.pitcherStats.pitchingLosses != 0) {
                        lossPercent = ((simPitcher.losses - simPitcher.pitcher.pitcherStats.pitchingLosses) / simPitcher.pitcher.pitcherStats.pitchingLosses) * 100
                    }
                    if (simPitcher.pitcher.pitcherStats.pitchingGamesStarted != 0) {
                        gsPercent = ((simPitcher.gamesStarted - simPitcher.pitcher.pitcherStats.pitchingGamesStarted) / simPitcher.pitcher.pitcherStats.pitchingGamesStarted) * 100
                    }
                    StringBuilder builder = new StringBuilder("   ${format(next.teamName, 15)} ")
                    builder << " ${format(simPitcher.nameFirst + " " + simPitcher.nameLast, 30)}"
                    builder << "  ${format(winPercent.toInteger() + "%  -> " + simPitcher.wins + " / " + simPitcher.pitcher.pitcherStats.pitchingWins, 20)}"
                    builder << "  ${format(lossPercent.toInteger() + "%  -> " + simPitcher.losses + " / " + simPitcher.pitcher.pitcherStats.pitchingLosses, 20)}"
                    builder << "  ${format(gsPercent.toInteger() + "%  -> " + simPitcher.gamesStarted + " / " + simPitcher.pitcher.pitcherStats.pitchingGamesStarted, 20)}"
                    builder << "  ${format(simPitcher.era + " / " + simPitcher.pitcher.pitcherStats.pitchingEra, 20)}"
                    auditLog.debug builder.toString()
                }
            }

            auditLog.debug format("${next.teamName} Original Bullpen: (${next.originalBullpen.size()})", 50) + "  " +
                    format("Wins",20) + "  " +
                    format("Losses",20) + "  " +
                    format("Saves",20) + "  " +
                    format("Games",20) + "  " +
                    format("Games Started",20)
            next.originalBullpen.each() { String playerID ->
                GamePitcher nextPitcher = next.getPitcher(playerID)
                SimPitcher simPitcher = nextPitcher.simPitcher
                def winPercent = 0
                def lossPercent = 0
                def savePercent = 0
                def gsPercent = 0
                def gPercent = 0
                numPitchers++
                wins += simPitcher.pitcher.pitcherStats.pitchingWins
                simWins += simPitcher.wins
                losses += simPitcher.pitcher.pitcherStats.pitchingLosses
                simLosses += simPitcher.losses
                simSaves += simPitcher.saves
                saves += simPitcher.pitcher.pitcherStats.pitchingSaves
                gamesStarted += simPitcher.pitcher.pitcherStats.pitchingGamesStarted
                simGamesStarted += simPitcher.gamesStarted
                games += simPitcher.pitcher.pitcherStats.pitchingGames
                simGames += simPitcher.games
                if (simPitcher.pitcher.pitcherStats.pitchingWins != 0) {
                    winPercent = ((simPitcher.wins - simPitcher.pitcher.pitcherStats.pitchingWins) / simPitcher.pitcher.pitcherStats.pitchingWins) * 100
                }
                if (simPitcher.pitcher.pitcherStats.pitchingLosses != 0) {
                    lossPercent = ((simPitcher.losses - simPitcher.pitcher.pitcherStats.pitchingLosses) / simPitcher.pitcher.pitcherStats.pitchingLosses) * 100
                }
                if (simPitcher.pitcher.pitcherStats.pitchingSaves != 0) {
                    savePercent = ((simPitcher.saves - simPitcher.pitcher.pitcherStats.pitchingSaves) / simPitcher.pitcher.pitcherStats.pitchingSaves) * 100
                }
                if (simPitcher.pitcher.pitcherStats.pitchingGamesStarted != 0) {
                    gsPercent = ((simPitcher.gamesStarted - simPitcher.pitcher.pitcherStats.pitchingGamesStarted) / simPitcher.pitcher.pitcherStats.pitchingGamesStarted) * 100
                }
                if (simPitcher.pitcher.pitcherStats.pitchingGames != 0) {
                    gPercent = ((simPitcher.games - simPitcher.pitcher.pitcherStats.pitchingGames) / simPitcher.pitcher.pitcherStats.pitchingGames) * 100
                }
                StringBuilder builder = new StringBuilder("   ${format(next.teamName, 15)} ")
                builder << " ${format(simPitcher.nameFirst + " " + simPitcher.nameLast, 30)}"
                builder << "  ${format(winPercent.toInteger() + "%  -> " + simPitcher.wins + " / " + simPitcher.pitcher.pitcherStats.pitchingWins, 20)}"
                builder << "  ${format(lossPercent.toInteger() + "%  -> " + simPitcher.losses + " / " + simPitcher.pitcher.pitcherStats.pitchingLosses, 20)}"
                builder << "  ${format(savePercent.toInteger() + "%  -> " + simPitcher.saves + " / " + simPitcher.pitcher.pitcherStats.pitchingSaves, 20)}"
                builder << "  ${format(gPercent.toInteger() + "%  -> " + simPitcher.games + " / " + simPitcher.pitcher.pitcherStats.pitchingGames, 20)}"
                builder << "  ${format(gsPercent.toInteger() + "%  -> " + simPitcher.gamesStarted + " / " + simPitcher.pitcher.pitcherStats.pitchingGamesStarted, 20)}"
                builder << "  ${format(simPitcher.era + " / " + simPitcher.pitcher.pitcherStats.pitchingEra, 20)}"
                auditLog.debug builder.toString()
            }

            auditLog.debug format("${next.teamName} Reserve Pitchers: (${next.reservePitchers.size()})", 50) + "  " +
                    format("Wins",20) + "  " +
                    format("Losses",20) + "  " +
                    format("Saves",20) + "  " +
                    format("Games",20) + "  " +
                    format("Games Started",20)
            next.reservePitchers.each() { String playerID ->
                GamePitcher nextPitcher = next.getPitcher(playerID)
                SimPitcher simPitcher = nextPitcher.simPitcher
                def winPercent = 0
                def lossPercent = 0
                def savePercent = 0
                def gsPercent = 0
                def gPercent = 0
                numPitchers++
                wins += simPitcher.pitcher.pitcherStats.pitchingWins
                simWins += simPitcher.wins
                losses += simPitcher.pitcher.pitcherStats.pitchingLosses
                simLosses += simPitcher.losses
                simSaves += simPitcher.saves
                saves += simPitcher.pitcher.pitcherStats.pitchingSaves
                gamesStarted += simPitcher.pitcher.pitcherStats.pitchingGamesStarted
                simGamesStarted += simPitcher.gamesStarted
                games += simPitcher.pitcher.pitcherStats.pitchingGames
                simGames += simPitcher.games
                if (simPitcher.pitcher.pitcherStats.pitchingWins != 0) {
                    winPercent = ((simPitcher.wins - simPitcher.pitcher.pitcherStats.pitchingWins) / simPitcher.pitcher.pitcherStats.pitchingWins) * 100
                }
                if (simPitcher.pitcher.pitcherStats.pitchingLosses != 0) {
                    lossPercent = ((simPitcher.losses - simPitcher.pitcher.pitcherStats.pitchingLosses) / simPitcher.pitcher.pitcherStats.pitchingLosses) * 100
                }
                if (simPitcher.pitcher.pitcherStats.pitchingSaves != 0) {
                    savePercent = ((simPitcher.saves - simPitcher.pitcher.pitcherStats.pitchingSaves) / simPitcher.pitcher.pitcherStats.pitchingSaves) * 100
                }
                if (simPitcher.pitcher.pitcherStats.pitchingGamesStarted != 0) {
                    gsPercent = ((simPitcher.gamesStarted - simPitcher.pitcher.pitcherStats.pitchingGamesStarted) / simPitcher.pitcher.pitcherStats.pitchingGamesStarted) * 100
                }
                if (simPitcher.pitcher.pitcherStats.pitchingGames != 0) {
                    gPercent = ((simPitcher.games - simPitcher.pitcher.pitcherStats.pitchingGames) / simPitcher.pitcher.pitcherStats.pitchingGames) * 100
                }
                StringBuilder builder = new StringBuilder("   ${format(next.teamName, 15)} ")
                builder << " ${format(simPitcher.nameFirst + " " + simPitcher.nameLast, 30)}"
                builder << "  ${format(winPercent.toInteger() + "%  -> " + simPitcher.wins + " / " + simPitcher.pitcher.pitcherStats.pitchingWins, 20)}"
                builder << "  ${format(lossPercent.toInteger() + "%  -> " + simPitcher.losses + " / " + simPitcher.pitcher.pitcherStats.pitchingLosses, 20)}"
                builder << "  ${format(savePercent.toInteger() + "%  -> " + simPitcher.saves + " / " + simPitcher.pitcher.pitcherStats.pitchingSaves, 20)}"
                builder << "  ${format(gPercent.toInteger() + "%  -> " + simPitcher.games + " / " + simPitcher.pitcher.pitcherStats.pitchingGames, 20)}"
                builder << "  ${format(gsPercent.toInteger() + "%  -> " + simPitcher.gamesStarted + " / " + simPitcher.pitcher.pitcherStats.pitchingGamesStarted, 20)}"
                builder << "  ${format(simPitcher.era + " / " + simPitcher.pitcher.pitcherStats.pitchingEra, 20)}"
                auditLog.debug builder.toString()
            }

            // Closer
            if (next.closer != null) {
                auditLog.debug format("${next.teamName} Closer: (${next.originalBullpen.size()})", 50) + "  " +
                        format("Wins",20) + "  " +
                        format("Losses",20) + "  " +
                        format("Saves",20) + "  " +
                        format("Games",20) + "  " +
                        format("Games Started",20)
                GamePitcher nextPitcher = next.getPitcher(next.closer)
                SimPitcher simPitcher = nextPitcher.simPitcher
                def winPercent = 0
                def lossPercent = 0
                def savePercent = 0
                def gsPercent = 0
                def gPercent = 0
                numPitchers++
                wins += simPitcher.pitcher.pitcherStats.pitchingWins
                simWins += simPitcher.wins
                losses += simPitcher.pitcher.pitcherStats.pitchingLosses
                simSaves += simPitcher.saves
                saves += simPitcher.pitcher.pitcherStats.pitchingSaves
                simLosses += simPitcher.losses
                gamesStarted += simPitcher.pitcher.pitcherStats.pitchingGamesStarted
                simGamesStarted += simPitcher.gamesStarted
                games += simPitcher.pitcher.pitcherStats.pitchingGames
                simGames += simPitcher.games
                if (simPitcher.pitcher.pitcherStats.pitchingWins != 0) {
                    winPercent = ((simPitcher.wins - simPitcher.pitcher.pitcherStats.pitchingWins) / simPitcher.pitcher.pitcherStats.pitchingWins) * 100
                }
                if (simPitcher.pitcher.pitcherStats.pitchingLosses != 0) {
                    lossPercent = ((simPitcher.losses - simPitcher.pitcher.pitcherStats.pitchingLosses) / simPitcher.pitcher.pitcherStats.pitchingLosses) * 100
                }
                if (simPitcher.pitcher.pitcherStats.pitchingSaves != 0) {
                    savePercent = ((simPitcher.saves - simPitcher.pitcher.pitcherStats.pitchingSaves) / simPitcher.pitcher.pitcherStats.pitchingSaves) * 100
                }
                if (simPitcher.pitcher.pitcherStats.pitchingGamesStarted != 0) {
                    gsPercent = ((simPitcher.gamesStarted - simPitcher.pitcher.pitcherStats.pitchingGamesStarted) / simPitcher.pitcher.pitcherStats.pitchingGamesStarted) * 100
                }
                if (simPitcher.pitcher.pitcherStats.pitchingGames != 0) {
                    gPercent = ((simPitcher.games - simPitcher.pitcher.pitcherStats.pitchingGames) / simPitcher.pitcher.pitcherStats.pitchingGames) * 100
                }
                StringBuilder builder = new StringBuilder("   ${format(next.teamName, 15)} ")
                builder << " ${format(simPitcher.nameFirst + " " + simPitcher.nameLast, 30)}"
                builder << "  ${format(winPercent.toInteger() + "%  -> " + simPitcher.wins + " / " + simPitcher.pitcher.pitcherStats.pitchingWins, 20)}"
                builder << "  ${format(lossPercent.toInteger() + "%  -> " + simPitcher.losses + " / " + simPitcher.pitcher.pitcherStats.pitchingLosses, 20)}"
                builder << "  ${format(savePercent.toInteger() + "%  -> " + simPitcher.saves + " / " + simPitcher.pitcher.pitcherStats.pitchingSaves, 20)}"
                builder << "  ${format(gPercent.toInteger() + "%  -> " + simPitcher.games + " / " + simPitcher.pitcher.pitcherStats.pitchingGames, 20)}"
                builder << "  ${format(gsPercent.toInteger() + "%  -> " + simPitcher.gamesStarted + " / " + simPitcher.pitcher.pitcherStats.pitchingGamesStarted, 20)}"
                builder << "  ${format(simPitcher.era + " / " + simPitcher.pitcher.pitcherStats.pitchingEra, 20)}"
                auditLog.debug builder.toString()
            }
        }

        if (wins != 0) {
            ttlWinPercent = ((simWins - wins) / wins) * 100
        }
        if (losses != 0) {
            ttlLossPercent = ((simLosses - losses) / losses) * 100
        }
        if (gamesStarted != 0) {
            ttlGamesStartedPercent = ((simGamesStarted - gamesStarted) / gamesStarted) * 100
        }
        StringBuilder builder = new StringBuilder("   ${format("Total:", 46)} ")
        builder << "  ${format(ttlWinPercent.toInteger() + "%  -> " + simWins + " / " + wins, 20)}"
        builder << "  ${format(ttlLossPercent.toInteger() + "%  -> " + simLosses + " / " + losses, 20)}"
        builder << "  ${format(ttlGamesStartedPercent.toInteger() + "%  -> " + simGamesStarted + " / " + gamesStarted, 20)}"
        auditLog.debug builder.toString()


        int nextRound = 1
        schedule.rounds.each { ScheduledRound round ->
            if (round.roundNum != nextRound) {
                auditLog.debug("   Mismatch: ${round.roundNum} != ${nextRound}")
            }
            nextRound++
        }
        auditLog.debug("Done!")
    }

    void logBattingStatsAccuracy() {
        int pTeamWins = 0
        int numPlayers = 0
        auditLog.debug("")
        auditLog.debug("Batting Stats Accuracy:")
        auditLog.debug("")

        int numPitchers = 0
        int simHomers, simAvg, simDoubles, simTriples, simStolenBases, simWalks, simAtBats = 0
        int homers, avg, doubles, triples, stolenBases, walks, atBats = 0
        def ttlHRPercent = 0
        def ttlStolenBasesPercent = 0
        def ttlAtBatsPercent = 0

        teams.each() { SimTeam next ->
            //pTeamWins = 0
            numPlayers = 0

            auditLog.debug("")
            auditLog.debug format("${next.teamName} Current Lineup: (${next.lineup.size()})", 50) + "  " +
                    format("Avg", 20) + "  " +
                    format("At Bats", 20) + "  " +
                    format("HR", 20) + "  " +
                    format("Doubles", 20) + "  " +
                    format("Triples", 20) + "  " +
                    format("Stolen Bases", 20)

            next.lineup.each() { String playerID ->
                GameBatter nextBatter = next.getBatter(playerID)
                SimBatter simBatter = nextBatter.simBatter
                def atBatsPercent = 0
                def homersPercent = 0
                def doublesPercent = 0
                def triplesPercent = 0
                def stolenBasesPercent = 0
                numPlayers++
                homers += simBatter.batter.homers
                doubles += simBatter.batter.doubles
                triples += simBatter.batter.triples
                atBats += simBatter.batter.atBats
                stolenBases += simBatter.batter.stolenBases
                simHomers += simBatter.homers
                simDoubles += simBatter.doubles
                simTriples += simBatter.triples
                simAtBats += simBatter.atBats
                simStolenBases += simBatter.stolenBases
                if (simBatter.batter.homers != 0) {
                    homersPercent = ((simBatter.homers - simBatter.batter.homers) / simBatter.batter.homers) * 100
                }
                if (simBatter.batter.doubles != 0) {
                    doublesPercent = ((simBatter.doubles - simBatter.batter.doubles) / simBatter.batter.doubles) * 100
                }
                if (simBatter.batter.triples != 0) {
                    triplesPercent = ((simBatter.triples - simBatter.batter.triples) / simBatter.batter.triples) * 100
                }
                if (simBatter.batter.atBats != 0) {
                    atBatsPercent = ((simBatter.atBats - simBatter.batter.atBats) / simBatter.batter.atBats) * 100
                }
                if (simBatter.batter.stolenBases != 0) {
                    stolenBasesPercent = ((simBatter.stolenBases - simBatter.batter.stolenBases) / simBatter.batter.stolenBases) * 100
                }
                StringBuilder builder = new StringBuilder("   ${format(next.teamName, 15)} ")
                builder << " ${format(simBatter.nameFirst + " " + simBatter.nameLast + "  (" + nextBatter.position + ")", 30)}"
                builder << "  ${format(simBatter.battingAvg + " / " + simBatter.batter.battingAvg, 20)}"
                builder << "  ${format(atBatsPercent.toInteger() + "%  -> " + simBatter.atBats + " / " + simBatter.batter.atBats, 20)}"
                builder << "  ${format(homersPercent.toInteger() + "%  -> " + simBatter.homers + " / " + simBatter.batter.homers, 20)}"
                builder << "  ${format(doublesPercent.toInteger() + "%  -> " + simBatter.doubles + " / " + simBatter.batter.doubles, 20)}"
                builder << "  ${format(triplesPercent.toInteger() + "%  -> " + simBatter.triples + " / " + simBatter.batter.triples, 20)}"
                builder << "  ${format(stolenBasesPercent.toInteger() + "%  -> " + simBatter.stolenBases + " / " + simBatter.batter.stolenBases, 20)}"
                auditLog.debug builder.toString()
            }

            auditLog.debug format("${next.teamName} Bench: (${next.lineup.size()})", 50) + "  " +
                    format("Avg", 20) + "  " +
                    format("At Bats", 20) + "  " +
                    format("HR", 20) + "  " +
                    format("Doubles", 20) + "  " +
                    format("Triples", 20) + "  " +
                    format("Stolen Bases", 20)

            next.bench.each() { String playerID ->
                GameBatter nextBatter = next.getBatter(playerID)
                SimBatter simBatter = nextBatter.simBatter
                def atBatsPercent = 0
                def homersPercent = 0
                def doublesPercent = 0
                def triplesPercent = 0
                def stolenBasesPercent = 0
                numPlayers++
                homers += simBatter.batter.homers
                doubles += simBatter.batter.doubles
                triples += simBatter.batter.triples
                atBats += simBatter.batter.atBats
                stolenBases += simBatter.batter.stolenBases
                simHomers += simBatter.homers
                simDoubles += simBatter.doubles
                simTriples += simBatter.triples
                simAtBats += simBatter.atBats
                simStolenBases += simBatter.stolenBases
                if (simBatter.batter.homers != 0) {
                    homersPercent = ((simBatter.homers - simBatter.batter.homers) / simBatter.batter.homers) * 100
                }
                if (simBatter.batter.doubles != 0) {
                    doublesPercent = ((simBatter.doubles - simBatter.batter.doubles) / simBatter.batter.doubles) * 100
                }
                if (simBatter.batter.triples != 0) {
                    triplesPercent = ((simBatter.triples - simBatter.batter.triples) / simBatter.batter.triples) * 100
                }
                if (simBatter.batter.atBats != 0) {
                    atBatsPercent = ((simBatter.atBats - simBatter.batter.atBats) / simBatter.batter.atBats) * 100
                }
                if (simBatter.batter.stolenBases != 0) {
                    stolenBasesPercent = ((simBatter.stolenBases - simBatter.batter.stolenBases) / simBatter.batter.stolenBases) * 100
                }
                StringBuilder builder = new StringBuilder("   ${format(next.teamName, 15)} ")
                builder << " ${format(simBatter.nameFirst + " " + simBatter.nameLast + "  (" + simBatter.primaryPosition + ")", 30)}"
                builder << "  ${format(simBatter.battingAvg + " / " + simBatter.batter.battingAvg, 20)}"
                builder << "  ${format(atBatsPercent.toInteger() + "%  -> " + simBatter.atBats + " / " + simBatter.batter.atBats, 20)}"
                builder << "  ${format(homersPercent.toInteger() + "%  -> " + simBatter.homers + " / " + simBatter.batter.homers, 20)}"
                builder << "  ${format(doublesPercent.toInteger() + "%  -> " + simBatter.doubles + " / " + simBatter.batter.doubles, 20)}"
                builder << "  ${format(triplesPercent.toInteger() + "%  -> " + simBatter.triples + " / " + simBatter.batter.triples, 20)}"
                builder << "  ${format(stolenBasesPercent.toInteger() + "%  -> " + simBatter.stolenBases + " / " + simBatter.batter.stolenBases, 20)}"
                auditLog.debug builder.toString()
            }
        }

        if (homers != 0) {
            ttlHRPercent = ((simHomers - homers) / homers) * 100
        }
        if (stolenBases != 0) {
            ttlStolenBasesPercent = ((simStolenBases - stolenBases) / simStolenBases) * 100
        }
        StringBuilder builder = new StringBuilder("   ${format("Total:", 46)} ")
        builder << "  ${format(ttlHRPercent.toInteger() + "%  -> " + simHomers + " / " + homers, 20)}"
        builder << "  ${format(ttlStolenBasesPercent.toInteger() + "%  -> " + simStolenBases + " / " + stolenBases, 20)}"
        auditLog.debug builder.toString()


        int nextRound = 1
        schedule.rounds.each { ScheduledRound round ->
            if (round.roundNum != nextRound) {
                auditLog.debug("   Mismatch: ${round.roundNum} != ${nextRound}")
            }
            nextRound++
        }
        auditLog.debug("Done!")
    }


    void adjustLineup(SimTeam team) {
        if (! team.lineupSet) {
            def originalLineup = team.lineup
            def originalBench = team.bench
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
            def DH = []
            def benchByPos = ["P":P, "C":C, "1B":first, "2B":second, "3B":third, "SS":shortstop, "LF":LF, "CF":CF, "RF":RF, "DH":DH]

            // Break bench up by positions
            sortedBench.each() { nextBatter ->
                String position = nextBatter.simBatter.batter.primaryPosition
                switch (position) {
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
                    case "DH":
                        DH << nextBatter
                        break
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
            team.bench = originalBench

            int index = 1
            log.debug("Final Lineup:")
            lineup.each {
                log.debug("   $index: ${it.simBatter.batter.nameFirst} ${it.simBatter.batter.nameLast} (${it.position})")
                index++
            }

            team.lineup = lineup
            team.lineupSet = true
        }
    }

}
