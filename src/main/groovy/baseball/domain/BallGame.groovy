package baseball.domain

import org.apache.log4j.Logger

class BallGame {
    def gameLog = Logger.getLogger('gamelog')
    def highlightsLog = Logger.getLogger('highlights')
    def boxscoreLog = Logger.getLogger('boxscore')
    def auditLog = Logger.getLogger('Audit')
    def random = new Random()
    def gameLogEnabled = true
    def highlightsLogEnabled = true
    def boxscoreLogEnabled = true
    def isGameOver = false
    def inning = 1
    def outs = 0
    def side = HalfInning.TOP
    def bases = Bases.EMPTY

    def awayInnings = [0, 0, 0, 0, 0, 0, 0, 0, 0]
    def homeInnings = [0, 0, 0, 0, 0, 0, 0, 0, 0]

    def awayScore = 0
    def homeScore = 0
    def awayHits = 0
    def homeHits = 0
    def awayErrors = 0
    def homeErrors = 0
    def awayDoublePlays = 0
    def homeDoublePlays = 0

    def simType = SimType.ACCURATE
    GameBatter runnerFirst = null, runnerSecond = null, runnerThird = null
    SimTeam awayTeam, homeTeam
    def homeTeamWon = false

    def start() {
        auditLog.info("Game starting... ${homeTeam.year} ${homeTeam.teamName} ${homeTeam.wins}    ${awayTeam.year} ${awayTeam.teamName} ${awayTeam.wins} ")
        homeTeam.starter = homeTeam.rotation[homeTeam.nextStartingPitcher]
        awayTeam.starter = awayTeam.rotation[awayTeam.nextStartingPitcher]
        homeTeam.currentPitcher = homeTeam.starter
        awayTeam.currentPitcher = awayTeam.starter
        homeTeam.pitchersUsed = []
        awayTeam.pitchersUsed = []
        homeTeam.pitchersUsed << homeTeam.starter
        awayTeam.pitchersUsed << awayTeam.starter
        homeTeam.nextReliefPitcher = 0
        awayTeam.nextReliefPitcher = 0
        homeTeam.pitcherOfRecord = null
        awayTeam.pitcherOfRecord = null
        homeTeam.allPitchersProcessed = false
        awayTeam.allPitchersProcessed = false
        if (homeTeam.starter == null) {
            throw new Exception("${homeTeam.teamName} has a null starter (next starting pitcher: ${homeTeam.nextStartingPitcher}).")
        }
        if (awayTeam.starter == null) {
            throw new Exception("${awayTeam.teamName} has a null starter (next starting pitcher: ${awayTeam.nextStartingPitcher}).")
        }
        SimPitcher homeSimPitcherStarter = getHomeSimPitcher(homeTeam.starter)
        SimPitcher awaySimPitcherStarter = getAwaySimPitcher(awayTeam.starter)
        if (homeSimPitcherStarter.nameLast == "Wickman" && homeSimPitcherStarter.gamesStarted >= homeSimPitcherStarter.pitcher.pitcherStats.pitchingGamesStarted) {
           auditLog.debug("Wickman found!!")
        }
        if (awaySimPitcherStarter.nameLast == "Wickman" && awaySimPitcherStarter.gamesStarted >= awaySimPitcherStarter.pitcher.pitcherStats.pitchingGamesStarted) {
            auditLog.debug("Wickman found!!")
        }
        if (homeSimPitcherStarter.pitcher.name.contains("Bielecki")) {
            if (homeSimPitcherStarter.gamesStarted >= homeSimPitcherStarter.pitcher.pitcherStats.pitchingGamesStarted) {
                auditLog.debug("Bielecki done!!")
            }
        }
        if (awaySimPitcherStarter.pitcher.name.contains("Bielecki")) {
            if (awaySimPitcherStarter.gamesStarted >= awaySimPitcherStarter.pitcher.pitcherStats.pitchingGamesStarted) {
                auditLog.debug("Bielecki done!!")
            }
        }

        checkPitcherMaxGames(homeSimPitcherStarter, homeTeam)
        checkPitcherMaxGames(awaySimPitcherStarter, awayTeam)

        homeSimPitcherStarter = getHomeSimPitcher(homeTeam.starter)
        awaySimPitcherStarter = getAwaySimPitcher(awayTeam.starter)
        homeSimPitcherStarter.gamesStarted += 1
        homeSimPitcherStarter.games += 1
        awaySimPitcherStarter.gamesStarted += 1
        awaySimPitcherStarter.games += 1

        awayTeam.lineup.each {
            resetAwayGamePlayer(it)
        }
        homeTeam.lineup.each {
            resetHomeGamePlayer(it)
        }

        awayTeam.rotation.each {
            resetAwayGamePlayer(it)
        }
        homeTeam.rotation.each {
            resetHomeGamePlayer(it)
        }

        awayTeam.bullpen.each {
            resetAwayGamePlayer(it)
        }
        homeTeam.bullpen.each {
            resetHomeGamePlayer(it)
        }

        awayTeam.reservePitchers.each {
            resetAwayGamePlayer(it)
        }
        homeTeam.reservePitchers.each {
            resetHomeGamePlayer(it)
        }


        while (! isGameOver) {
//            playHalfInning(SimStyle.COMBINED)
            int doublePlayCount
            if (side == HalfInning.TOP) {
                doublePlayCount = homeDoublePlays
            } else {
                doublePlayCount = awayDoublePlays
            }

            playHalfInning(SimStyle.BATTER_HEAVY, doublePlayCount)
            if (inning > 50) { isGameOver = true }
        }
    }

    private void checkPitcherMaxGames(SimPitcher simPitcherStarter, SimTeam team) {
        GamePitcher nextPitcher = null

        if (simPitcherStarter.gamesStarted >= simPitcherStarter.pitcher.pitcherStats.pitchingGamesStarted) {

            // This starter is maxed out. Pull a new starter out of the reserved pitchers list.
            auditLog.debug("${simPitcherStarter.name} is maxed out. Looking for a new starter...")
            int next = 0
            nextPitcher = getPitcher(team.bullpen.get(next))
            while (((nextPitcher.simPitcher.pitcher.pitcherStats.pitchingGamesStarted == 0 ||
                    nextPitcher.simPitcher.gamesStarted > 0 ||
                    team.doneStarters.contains(nextPitcher.playerID) ||
                    team.rotation.contains(nextPitcher.playerID))
                    || (nextPitcher.simPitcher.gamesStarted >= nextPitcher.simPitcher.pitcher.pitcherStats.pitchingGamesStarted))
                    && (next < team.bullpen.size()-1)) {
                next++
                nextPitcher = getPitcher(team.bullpen.get(next))
            }
            if (nextPitcher.simPitcher.pitcher.pitcherStats.pitchingGamesStarted == 0 ||
                nextPitcher.simPitcher.gamesStarted > 0 ||
                team.doneStarters.contains(nextPitcher.playerID) ||
                team.rotation.contains(nextPitcher.playerID)) {
                next = 0
                nextPitcher = getPitcher(team.reservePitchers.get(next))
                while (((nextPitcher.simPitcher.pitcher.pitcherStats.pitchingGamesStarted == 0) ||
                        nextPitcher.simPitcher.gamesStarted > 0 ||
                        (nextPitcher.simPitcher.gamesStarted >= nextPitcher.simPitcher.pitcher.pitcherStats.pitchingGamesStarted)) &&
                        (next < team.reservePitchers.size()-1)) {
                    next++
                    nextPitcher = getPitcher(team.reservePitchers.get(next))
                }
            }
            if (nextPitcher.simPitcher.pitcher.pitcherStats.pitchingGamesStarted > 0 &&
                nextPitcher.simPitcher.gamesStarted == 0 &&
                ! team.doneStarters.contains(nextPitcher.playerID) &&
                ! team.rotation.contains(nextPitcher.playerID)) {

                // Found a starter!
                //homeTeam.rotation[homeTeam.nextStartingPitcher] = newStarter
                team.doneStarters << team.starter
                int index = team.rotation.indexOf(team.starter)
                team.rotation[index] = nextPitcher.playerID
                team.starter = nextPitcher.playerID
                auditLog.debug("${simPitcherStarter.name} is maxed out (${simPitcherStarter.gamesStarted} starts). New pitcher: ${nextPitcher.name}  Games Started: ${nextPitcher.pitcherStats.pitchingGamesStarted}")
                if (team.starter == null) {
                    throw new Exception("Home team replacement starter is null.")
                }
            }
        }
    }


    private SimPitcher getHomeSimPitcher(String playerID) {
        try {
            GamePitcher gamePitcher = homeTeam.roster[playerID]
            return gamePitcher.simPitcher
        } catch (Exception e) {
            throw new Exception("Unable to find pitcher for player ID $playerID.")
        }
    }

    private SimPitcher getAwaySimPitcher(String playerID) {
        try {
            GamePitcher gamePitcher = awayTeam.roster[playerID]
            return gamePitcher.simPitcher
        } catch (Exception e) {
            throw new Exception("Unable to find pitcher for player ID $playerID.")
        }
    }

    private GamePitcher getHomeGamePitcher(String playerID) {
        homeTeam.roster[playerID]
    }

    private GamePitcher getAwayGamePitcher(String playerID) {
        awayTeam.roster[playerID]
    }

    private void resetHomeGamePlayer(String playerID) {
        homeTeam.roster[playerID].reset()
    }

    private void resetAwayGamePlayer(String playerID) {
        awayTeam.roster[playerID].reset()
    }

    private def getNewPitcher() {
        GamePitcher nextPitcher = null
        boolean saveSituation = false
        if (side == HalfInning.TOP) {
            if (inning >= 9 && homeScore > awayScore && ((homeScore - awayScore) <= 3) && ! (homeTeam.closer in homeTeam.pitchersUsed)) {
                saveSituation = true
            }
            nextPitcher = getNewPitcher(homeTeam, saveSituation)
        } else {
            if (inning >= 9 && awayScore > homeScore && ((awayScore - homeScore) <= 3) && ! (awayTeam.closer in awayTeam.pitchersUsed)) {
                saveSituation = true
            }
            nextPitcher = getNewPitcher(awayTeam, saveSituation)
        }
        return nextPitcher
    }

    private def getNewPitcher(SimTeam team, boolean saveSituation) {
        // New home pitcher.
        boolean stop = false
        // Is it time for the closer to come in?
        if (saveSituation && team.closer != null) {
            GamePitcher nextPitcher = getPitcher(team.closer)
            team.currentPitcher = team.closer
            gameLog.info "New pitcher for ${team.teamName} (${nextPitcher.simPitcher.pitcher.name})."
            team.pitchersUsed << team.currentPitcher
            nextPitcher.simPitcher.games += 1
            return nextPitcher
        }
        // If not closer, keep looking.
        while (! stop && team.bullpen.size() >= team.nextReliefPitcher+1) {
            String playerID = team.bullpen[team.nextReliefPitcher]
            GamePitcher gamePitcher = getPitcher(playerID)
            if ((gamePitcher.simPitcher.games < gamePitcher.simPitcher.pitcher.pitcherStats.pitchingGames || team.allPitchersProcessed) && ! team.pitchersUsed.contains(playerID)) {
                stop = true
            } else {
                team.nextReliefPitcher++
            }
        }
        if (team.bullpen.size() >= team.nextReliefPitcher+1) {
            team.currentPitcher = team.bullpen[team.nextReliefPitcher]
            def newPitcher = getPitcher(team.currentPitcher)
            gameLog.info "New pitcher for ${team.teamName} (${newPitcher.simPitcher.pitcher.name})."
            team.pitchersUsed << team.currentPitcher
            newPitcher.simPitcher.games += 1
            team.nextReliefPitcher++
            return getPitcher(team.currentPitcher)
        } else {
            stop = false
            while (! stop && team.reservePitchers.size() >= team.nextReliefPitcher-5) {
                String playerID
                if (team.reservePitchers.size() == team.nextReliefPitcher-5) {
                    // All relievers processed. Start over.
                    playerID = team.reservePitchers[team.reservePitchers.size()-1]
                    team.allPitchersProcessed = true
                } else {
                    playerID = team.reservePitchers[team.nextReliefPitcher-5]
                }
                if (playerID == null) {
                    auditLog.error("PlayerID is null. team=${team.teamName} reservePitchersSize=${team.reservePitchers.size()} index=${team.nextReliefPitcher-5}")
                }
                GamePitcher gamePitcher = getPitcher(playerID)
                if ((gamePitcher.simPitcher.games < gamePitcher.simPitcher.pitcher.pitcherStats.pitchingGames || team.allPitchersProcessed) && ! team.pitchersUsed.contains(playerID)) {
                    stop = true
                } else {
                    team.nextReliefPitcher++
                }
            }
            if (team.nextReliefPitcher-5 < team.reservePitchers.size()) {
                // Bullpen empty. Use reserve pitchers.
                team.currentPitcher = team.reservePitchers[team.nextReliefPitcher-5]
                def homePitcher = getPitcher(team.currentPitcher)
                gameLog.info "New pitcher for home team (${homePitcher.simPitcher.pitcher.name})."
                team.pitchersUsed << team.currentPitcher
                homePitcher.simPitcher.games += 1
                team.nextReliefPitcher++
                return getPitcher(team.currentPitcher)
            } else {
                // Keep the current pitcher. Out of pitchers.
                auditLog.info "Out of pitchers. Going back to the starter."
                team.currentPitcher = team.starter
                return getPitcher(team.currentPitcher)
            }
        }
    }

    GameBatter getBatter(String playerID) {
        GameBatter result = homeTeam.roster[playerID]
        if (! result) {
            return awayTeam.roster[playerID]
        } else {
            return result
        }
    }

    GamePitcher getPitcher(String playerID) {
        if (playerID == null) {
            throw new Exception("getPitcher(ID=null)")
        }
        GamePitcher result = homeTeam.roster[playerID]
        if (! result) {
            result = awayTeam.roster[playerID]
            //auditLog.debug("getPitcher(ID=$playerID) = ${result.toString()}")
            return result
        } else {
            //auditLog.debug("getPitcher(ID=$playerID) = ${result.toString()}")
            return result
        }
    }

    private def playHalfInning(SimStyle simStyle, int doublePlayCount) {
        boolean readyToStart = isReadyToStart()
        int pitcherCount
        def pitcher
        String teamName
        if (side == HalfInning.TOP) {
            pitcher = getPitcher(homeTeam.currentPitcher)
            teamName = homeTeam.teamName
        } else {
            pitcher = getPitcher(awayTeam.currentPitcher)
            teamName = awayTeam.teamName
        }
        outs = 0
        bases = Bases.EMPTY
        runnerFirst = null
        runnerSecond = null
        runnerThird = null
        while (outs < 3) {
            if (pitcher.pitcherExhausted(teamName)) {
                pitcher = getNewPitcher()
            }

            def batter = getNextBatter()
            def atBatResult = pitchToBatter(batter, pitcher, pitcherCount, simStyle)
            ballInPlay(batter, pitcher, atBatResult, doublePlayCount)
        }
        inningOver()
    }

    private boolean isReadyToStart() throws Exception {
        boolean result = true
        if (awayTeam.lineup.size() != 9) {
            throw new Exception("Unable to start game.  The away lineup does not contain 9 batters (${awayTeam.lineup.size()}).")
        }
        if (homeTeam.lineup.size() != 9) {
            throw new Exception("Unable to start game.  The home lineup does not contain 9 batters (${homeTeam.lineup.size()}).")
        }
        if (awayTeam.bullpen.size() == 0) {
            throw new Exception("Unable to start game.  The away bullpen is empty.")
        }
        if (homeTeam.bullpen.size() == 0) {
            throw new Exception("Unable to start game.  The home bullpen is empty.")
        }
        if (awayTeam.rotation.size() == 0) {
            throw new Exception("Unable to start game.  The away rotation is empty.")
        }
        if (homeTeam.rotation.size() == 0) {
            throw new Exception("Unable to start game.  The home rotation is empty.")
        }
        //if (awayStarter == null) {
        //   throw new Exception("Unable to start game.  The away starter has not been specified.")
        //}
        //if (homeStarter == null) {
        //    throw new Exception("Unable to start game.  The home starter has not been specified.")
        //}
        if (awayTeam.currentPitcher == null) {
            awayTeam.currentPitcher = awayTeam.rotation[0]
        }
        if (homeTeam.currentPitcher == null) {
            homeTeam.currentPitcher = homeTeam.rotation[0]
        }
    }

    private def getNextBatter() {
        def batter
        if (side == HalfInning.TOP) {
            batter = awayTeam.lineup[awayTeam.nextBatter]
        } else {
            batter = homeTeam.lineup[homeTeam.nextBatter]
        }
        getBatter(batter)
    }

    private void updatePitchersOfRecord() {
        if (homeScore == awayScore) {
            homeTeam.pitcherOfRecord = null
            awayTeam.pitcherOfRecord = null
        } else if (homeScore > awayScore) {
            awayTeam.pitcherOfRecord = awayTeam.currentPitcher
            if (inning == 5 && side == HalfInning.BOTTOM) {
                homeTeam.pitcherOfRecord = homeTeam.currentPitcher
            } else if (inning > 5 && homeTeam.pitcherOfRecord == null) {
                homeTeam.pitcherOfRecord = homeTeam.currentPitcher
            }
            if (awayTeam.pitcherOfRecord == null) {
                awayTeam.pitcherOfRecord = awayTeam.currentPitcher
            }
        } else {
            if (inning == 6 && side == HalfInning.TOP) {
                awayTeam.pitcherOfRecord = awayTeam.currentPitcher
            } else if (inning > 5 && awayTeam.pitcherOfRecord == null) {
                awayTeam.pitcherOfRecord = awayTeam.currentPitcher
            }
            if (homeTeam.pitcherOfRecord == null) {
                homeTeam.pitcherOfRecord = homeTeam.currentPitcher
            }
        }
    }

    private def inningOver() {
        if (side == HalfInning.TOP) {
            updatePitchersOfRecord()
            if (inning == 9 && homeScore > awayScore) {
                isGameOver = true
            } else {
                def homePitcher = getPitcher(homeTeam.currentPitcher)
                side = HalfInning.BOTTOM
                gameLog.debug("End of top of inning $inning. Current pitcher: $homePitcher.simPitcher.pitcher.name")
                gameLog.debug "${format("Pitcher", 20)}  ${format("W", 3)}  ${format("L", 3)}  ${format("IP", 5)}  ${format("R", 3)}  ${format("ERA", 5)}  ${format("H", 3)}  ${format("HR", 3)}  ${format("BB", 3)}  ${format("SO", 3)}"
                gameLog.debug "${format(homePitcher.simPitcher.pitcher.name, 20)}  ${format(homePitcher.simPitcher.wins, 3)}  ${format(homePitcher.simPitcher.losses, 3)}  ${format(homePitcher.battersRetired/3, 5)}  ${format(homePitcher.runs, 3)}  ${homePitcher.simPitcher.era}  ${format(homePitcher.hits, 3)}  ${format(homePitcher.homers, 3)}  ${format(homePitcher.walks, 3)}  ${format(homePitcher.strikeouts, 3)}"
                printTeamHittingStats(awayTeam.lineup)
            }
        } else {
            updatePitchersOfRecord()
            if (inning >= 9 && awayScore != homeScore) {
                isGameOver = true
            } else {
                def awayPitcher = getPitcher(awayTeam.currentPitcher)
                gameLog.debug("End of bottom of inning $inning. Current pitcher: $awayPitcher.simPitcher.pitcher.name")
                side = HalfInning.TOP
                gameLog.debug "${format("Pitcher", 20)}  ${format("W", 3)}  ${format("L", 3)}  ${format("IP", 5)}  ${format("R", 3)}  ${format("ERA", 5)}  ${format("H", 3)}  ${format("HR", 3)}  ${format("BB", 3)}  ${format("SO", 3)}"
                gameLog.debug "${format(awayPitcher.simPitcher.pitcher.name, 20)}  ${format(awayPitcher.simPitcher.wins, 3)}  ${format(awayPitcher.simPitcher.losses, 3)}  ${format(awayPitcher.battersRetired/3, 5)}  ${format(awayPitcher.runs, 3)}  ${awayPitcher.simPitcher.era}  ${format(awayPitcher.hits, 3)}  ${format(awayPitcher.homers, 3)}  ${format(awayPitcher.walks, 3)}  ${format(awayPitcher.strikeouts, 3)}"
                printTeamHittingStats(homeTeam.lineup)
                inning++
                awayInnings.add(0)
                homeInnings.add(0)
            }
        }

        if (isGameOver) {
            def homeStarter = getPitcher(homeTeam.starter)
            def awayStarter = getPitcher(awayTeam.starter)
            if (homeScore > awayScore) {
                homeTeam.wins++
                awayTeam.losses++
                homeTeam.winDiff = homeTeam.wins - homeTeam.losses
                awayTeam.winDiff = awayTeam.wins - awayTeam.losses
                if (homeTeam.pitcherOfRecord != null) {
                    getPitcher(homeTeam.pitcherOfRecord).simPitcher.wins += 1
                } else {
                    homeStarter.simPitcher.wins += 1
                }
                if (awayTeam.pitcherOfRecord != null) {
                    getPitcher(awayTeam.pitcherOfRecord).simPitcher.losses += 1
                } else {
                    awayStarter.simPitcher.losses += 1
                }
            } else {
                awayTeam.wins++
                homeTeam.losses++
                homeTeam.winDiff = homeTeam.wins - homeTeam.losses
                awayTeam.winDiff = awayTeam.wins - awayTeam.losses
                if (homeTeam.pitcherOfRecord != null) {
                    getPitcher(homeTeam.pitcherOfRecord).simPitcher.losses += 1
                } else {
                    homeStarter.simPitcher.losses += 1
                }
                if (awayTeam.pitcherOfRecord != null) {
                    getPitcher(awayTeam.pitcherOfRecord).simPitcher.wins += 1
                } else {
                    awayStarter.simPitcher.wins += 1
                }
            }
        }

        if (isGameOver) {
            if (gameLogEnabled) {
                gameLog.debug ""
                gameLog.debug ""
                gameLog.debug "Final score:  ${awayTeam.year} ${awayTeam.teamName} ${awayScore}     ${homeTeam.year} ${homeTeam.teamName} ${homeScore}"
            }
            // TODO Need to handle extra innings and X if bottom half not played.
            highlightsLog.debug ""
            highlightsLog.debug "${format("Teams", 20)}  1  2  3  4  5  6  7  8  9   ${format("R", 3)}  ${format("H", 3)}  ${format("E", 3)}"
            highlightsLog.debug "${format(awayTeam.year + " " + awayTeam.teamName, 20)}  ${format(awayInnings[0], 2)} ${format(awayInnings[1], 2)} ${format(awayInnings[2], 2)} ${format(awayInnings[3], 2)} ${format(awayInnings[4], 2)} ${format(awayInnings[5], 2)} ${format(awayInnings[6], 2)} ${format(awayInnings[7], 2)} ${format(awayInnings[8], 2)}  ${format(awayScore, 3)}  ${format(awayHits, 3)}  ${format(awayErrors, 3)}"
            highlightsLog.debug "${format(homeTeam.year + " " + homeTeam.teamName, 20)}  ${format(homeInnings[0], 2)} ${format(homeInnings[1], 2)} ${format(homeInnings[2], 2)} ${format(homeInnings[3], 2)} ${format(homeInnings[4], 2)} ${format(homeInnings[5], 2)} ${format(homeInnings[6], 2)} ${format(homeInnings[7], 2)} ${format(homeInnings[8], 2)}  ${format(homeScore, 3)}  ${format(homeHits, 3)}  ${format(homeErrors, 3)}"
            logBoxScore()
            auditLog.debug "Score: ${awayTeam.year} ${awayTeam.teamName}   ${awayScore}        ${homeTeam.year} ${homeTeam.teamName}   ${homeScore}"
        } else {
            if (gameLogEnabled) {
                gameLog.debug "Score: ${awayTeam.year} ${awayTeam.teamName}   ${awayScore}        ${homeTeam.year} ${homeTeam.teamName}   ${homeScore}       ${side} inning ${inning}"
                gameLog.debug ""
            }
        }
    }

    private def logBoxScore() {
        if (boxscoreLogEnabled) {
            boxscoreLog.debug ""
            boxscoreLog.debug ""
            boxscoreLog.debug "Final score:  ${awayTeam.year} ${awayTeam.teamName} ${awayScore}   ${homeTeam.year} ${homeTeam.teamName} ${homeScore}"
            boxscoreLog.debug ""
            boxscoreLog.debug "BOX SCORE"
            boxscoreLog.debug ""
            boxscoreLog.debug "${awayTeam.year} ${awayTeam.teamName}:"
            boxscoreLog.debug "${format("Batter", 20)}  ${format("AB", 3)}  ${format("R", 3)}  ${format("H", 3)}  ${format("2B", 3)}  ${format("3B", 3)}  ${format("HR", 3)}  ${format("RBI", 4)}  ${format("BB", 3)}  ${format("SO", 3)}  ${format("SB", 3)}  ${format("CS", 3)}  ${format("AVG", 5)} "
            awayTeam.lineup.each {
                def gameBatter = getBatter(it)
                boxscoreLog.debug "${format(gameBatter.simBatter.batter.name, 20)}  ${format(gameBatter.atBats, 3)}  ${format(gameBatter.runs, 3)}  ${format(gameBatter.hits, 3)}  ${format(gameBatter.doubles, 3)}  ${format(gameBatter.triples, 3)}  ${format(gameBatter.homers, 3)}  ${format(gameBatter.rbi, 4)}  ${format(gameBatter.walks, 3)}  ${format(gameBatter.strikeouts, 3)}  ${format(gameBatter.stolenBases, 3)}  ${format(gameBatter.caughtStealing, 3)}  ${format(gameBatter.simBatter.battingAvg + "0000", 5)}"
            }
            boxscoreLog.debug ""
            def era
            try {
                era = awayTeam.starter.runs / (awayTeam.starter.battersRetired/27)
            } catch (Exception e) {
                era = new BigDecimal(0)
            }

            boxscoreLog.debug "${format("Pitcher", 20)}  ${format("W", 3)}  ${format("L", 3)}  ${format("IP", 5)}  ${format("R", 3)}  ${format("ERA", 5)}  ${format("H", 3)}  ${format("HR", 3)}  ${format("BB", 3)}  ${format("SO", 3)}"
            awayTeam.pitchersUsed.each() { next ->
                def gamePitcher = getPitcher(next)
                boxscoreLog.debug "${format(gamePitcher.simPitcher.pitcher.name, 20)}  ${format(gamePitcher.simPitcher.wins, 3)}  ${format(gamePitcher.simPitcher.losses, 3)}  ${format(gamePitcher.battersRetired/3, 5)}  ${format(gamePitcher.runs, 3)}  ${gamePitcher.simPitcher.era}  ${format(gamePitcher.hits, 3)}  ${format(gamePitcher.homers, 3)}  ${format(gamePitcher.walks, 3)}  ${format(gamePitcher.strikeouts, 3)}"
            }
            boxscoreLog.debug ""
            boxscoreLog.debug ""
            boxscoreLog.debug "${homeTeam.year} ${homeTeam.teamName}:"
            boxscoreLog.debug "${format("Batter", 20)}  ${format("AB", 3)}  ${format("R", 3)}  ${format("H", 3)}  ${format("2B", 3)}  ${format("3B", 3)}  ${format("HR", 3)}  ${format("RBI", 4)}  ${format("BB", 3)}  ${format("SO", 3)}  ${format("SB", 3)}  ${format("CS", 3)}  ${format("AVG", 5)} "
            homeTeam.lineup.each {
                def gameBatter = getBatter(it)
                boxscoreLog.debug "${format(gameBatter.simBatter.batter.name, 20)}  ${format(gameBatter.atBats, 3)}  ${format(gameBatter.runs, 3)}  ${format(gameBatter.hits, 3)}  ${format(gameBatter.doubles, 3)}  ${format(gameBatter.triples, 3)}  ${format(gameBatter.homers, 3)}  ${format(gameBatter.rbi, 4)}  ${format(gameBatter.walks, 3)}  ${format(gameBatter.strikeouts, 3)}  ${format(gameBatter.stolenBases, 3)}  ${format(gameBatter.caughtStealing, 3)}  ${format(gameBatter.simBatter.battingAvg + "0000", 5)}"
            }
            boxscoreLog.debug ""
            boxscoreLog.debug "${format("Pitcher", 20)}  ${format("W", 3)}  ${format("L", 3)}  ${format("IP", 5)}  ${format("R", 3)}  ${format("ERA", 5)}  ${format("H", 3)}  ${format("HR", 3)}  ${format("BB", 3)}  ${format("SO", 3)}"
            homeTeam.pitchersUsed.each() { next ->
                def gamePitcher = getPitcher(next)
                if (gamePitcher == null) {
                    auditLog.debug("FAILURE: Writing pitcher for team ${homeTeam.teamName}: ID=${next}")
                }
                boxscoreLog.debug "${format(gamePitcher.simPitcher.pitcher.name, 20)}  ${format(gamePitcher.simPitcher.wins, 3)}  ${format(gamePitcher.simPitcher.losses, 3)}  ${format(gamePitcher.battersRetired/3, 5)}  ${format(gamePitcher.runs, 3)}  ${gamePitcher.simPitcher.era}  ${format(gamePitcher.hits, 3)}  ${format(gamePitcher.homers, 3)}  ${format(gamePitcher.walks, 3)}  ${format(gamePitcher.strikeouts, 3)}"
            }
        }
    }

    private void printTeamHittingStats(def lineup) {
        int atBats = 0, runs = 0, hits = 0, doubles = 0, triples = 0, homers = 0, rbi = 0, bb = 0, so = 0, sb = 0, cs = 0
        lineup.each {
            GameBatter batter = getBatter(it)
            atBats += batter.atBats
            runs += batter.runs
            hits += batter.hits
            doubles += batter.doubles
            triples += batter.triples
            homers += batter.homers
            rbi += batter.rbi
            bb += batter.walks
            so += batter.strikeouts
            sb += batter.stolenBases
            cs += batter.caughtStealing
        }
        gameLog.debug "${format("AB", 3)}  ${format("R", 3)}  ${format("H", 3)}  ${format("2B", 3)}  ${format("3B", 3)}  ${format("HR", 3)}  ${format("RBI", 4)}  ${format("BB", 3)}  ${format("SO", 3)}  ${format("SB", 3)}  ${format("CS", 3)}  ${format("AVG", 5)} "
        gameLog.debug "${format(atBats, 3)}  ${format(runs, 3)}  ${format(hits, 3)}  ${format(doubles, 3)}  ${format(triples, 3)}  ${format(homers, 3)}  ${format(rbi, 4)}  ${format(bb, 3)}  ${format(so, 3)}  ${format(sb, 3)}  ${format(cs, 3)}"
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

    private def ballInPlay(def batter, pitcher, atBatResult, int doublePlayCount) {
        def runsScored = 0
        def hitOnPlay = 0
        def notes = ""
        Random generator = new Random()
        if (atBatResult == AtBatResult.SINGLE) {
            if (bases == Bases.EMPTY) {
                bases = Bases.FIRST
            } else if (bases == Bases.FIRST) {
                int dieRoll = generator.nextInt(100) + 1
                if (dieRoll <= 28) {
                    bases = Bases.CORNERS
                    runnerThird = runnerFirst
                } else {
                    bases = Bases.FIRST_AND_SECOND
                    runnerSecond = runnerFirst
                }
            } else if (bases == Bases.SECOND) {
                bases = Bases.FIRST
                runnerSecond.runs += 1
                runnerSecond.simBatter.runs += 1
                runnerSecond = null
                batter.rbi += 1
                batter.simBatter.rbi += 1
                runsScored = 1
            } else if (bases == Bases.THIRD) {
                bases = Bases.FIRST
                runnerThird.runs += 1
                runnerThird.simBatter.runs += 1
                runnerThird = null
                batter.rbi += 1
                batter.simBatter.rbi += 1
                runsScored = 1
            } else if (bases == Bases.FIRST_AND_SECOND) {
                runnerSecond.runs += 1
                runnerSecond.simBatter.runs += 1
                batter.rbi += 1
                batter.simBatter.rbi += 1
                runsScored = 1
                int dieRoll = generator.nextInt(100) + 1
                if (dieRoll <= 28) {
                    bases = Bases.CORNERS
                    runnerThird = runnerFirst
                    runnerSecond = null
                } else {
                    bases = Bases.FIRST_AND_SECOND
                    runnerSecond = runnerFirst
                }
            } else if (bases == Bases.SECOND_AND_THIRD) {
                bases = Bases.FIRST
                runnerSecond.runs += 1
                runnerSecond.simBatter.runs += 1
                runnerThird.runs += 1
                runnerThird.simBatter.runs += 1
                runnerSecond = null
                runnerThird = null
                batter.rbi += 2
                batter.simBatter.rbi += 2
                runsScored = 2
            } else if (bases == Bases.CORNERS) {
                runnerThird.runs += 1
                runnerThird.simBatter.runs += 1
                batter.rbi += 1
                batter.simBatter.rbi += 1
                runsScored = 1
                int dieRoll = generator.nextInt(100) + 1
                if (dieRoll <= 28) {
                    bases = Bases.CORNERS
                    runnerThird = runnerFirst
                    runnerSecond = null
                } else {
                    bases = Bases.FIRST_AND_SECOND
                    runnerSecond = runnerFirst
                    runnerThird = null
                }
            } else if (bases == Bases.LOADED) {
                bases = Bases.FIRST_AND_SECOND
                runnerSecond.runs += 1
                runnerSecond.simBatter.runs += 1
                runnerThird.runs += 1
                runnerThird.simBatter.runs += 1
                int dieRoll = generator.nextInt(100) + 1
                if (dieRoll <= 28) {
                    bases = Bases.CORNERS
                    runnerSecond = null
                    runnerThird = runnerFirst
                } else {
                    bases = Bases.FIRST_AND_SECOND
                    runnerSecond = runnerFirst
                    runnerThird = null
                }
                batter.rbi += 2
                batter.simBatter.rbi += 2
                runsScored = 2
            }
            runnerFirst = batter
            hitOnPlay = 1
        } else if (atBatResult == AtBatResult.CAUGHT_STEALING_SECOND) {
            outs++
            runnerFirst = null
            if (bases == Bases.FIRST) {
                bases = Bases.EMPTY
            } else if (bases == Bases.CORNERS) {
                bases = Bases.THIRD
            }
        } else if (atBatResult == AtBatResult.CAUGHT_STEALING_THIRD) {
            outs++
            runnerSecond = null
            if (bases == Bases.SECOND) {
                bases = Bases.EMPTY
            } else if (bases == Bases.FIRST_AND_SECOND) {
                bases = Bases.FIRST
            }
        } else if (atBatResult == AtBatResult.STOLE_SECOND) {
            runnerSecond = runnerFirst
            runnerFirst = null
            if (bases == Bases.FIRST) {
                bases = Bases.SECOND
            } else if (bases == Bases.CORNERS) {
                bases = Bases.SECOND_AND_THIRD
            }
        } else if (atBatResult == AtBatResult.STOLE_THIRD) {
            runnerThird = runnerSecond
            runnerSecond = null
            if (bases == Bases.SECOND) {
                bases = Bases.THIRD
            } else if (bases == Bases.FIRST_AND_SECOND) {
                bases = Bases.CORNERS
            }
        } else if (atBatResult == AtBatResult.GROUND_OUT) {
            if (bases == Bases.EMPTY) {
                outs++
            } else if (bases == Bases.FIRST) {
                if (outs < 2) {
                    if (doublePlayCount <= 1) {
                        outs += 2
                        notes = "Double play."
                        pitcher.battersRetired += 1
                        pitcher.simPitcher.battersRetired += 1
                        bases = Bases.EMPTY
                        runnerFirst = null
                        if (side == HalfInning.TOP) {
                            homeDoublePlays++
                        } else {
                            awayDoublePlays++
                        }
                    } else {
                        outs++
                        runnerFirst = batter
                    }
                } else {
                    outs++
                    bases = Bases.SECOND
                    runnerSecond = runnerFirst
                }
            } else if (bases == Bases.SECOND) {
                runnerThird = runnerSecond
                runnerFirst = null
                runnerSecond = null
                bases = Bases.THIRD
                outs++
            } else if (bases == Bases.THIRD) {
                runnerFirst = null
                outs++
            } else if (bases == Bases.FIRST_AND_SECOND) {
                if (outs < 2) {
                    if (doublePlayCount <= 1) {
                        outs += 2
                        notes = "Double play."
                        pitcher.battersRetired += 1
                        pitcher.simPitcher.battersRetired += 1
                        bases = Bases.THIRD
                        runnerThird = runnerSecond
                        runnerSecond = null
                        runnerFirst = null
                        if (side == HalfInning.TOP) {
                            homeDoublePlays++
                        } else {
                            awayDoublePlays++
                        }
                    } else {
                        outs++
                        bases = Bases.SECOND_AND_THIRD
                        runnerThird = runnerSecond
                        runnerSecond = runnerFirst
                        runnerFirst = null
                    }
                } else {
                    outs++
                }
            } else if (bases == Bases.SECOND_AND_THIRD) {
                runnerFirst = null
                outs++
            } else if (bases == Bases.CORNERS) {
                bases = Bases.SECOND_AND_THIRD
                runnerSecond = runnerFirst
                runnerFirst = null
                runnerFirst = null
                outs++
            } else if (bases == Bases.LOADED) {
                outs++
            }
        } else if (atBatResult == AtBatResult.DOUBLE) {
            if (bases == Bases.EMPTY) {
                bases = Bases.SECOND
            } else if (bases == Bases.FIRST) {
                int dieRoll = generator.nextInt(100) + 1
                if (dieRoll <= 28) {
                    bases = Bases.SECOND
                    runnerThird = null
                    runnerFirst = null
                    batter.rbi += 1
                    batter.simBatter.rbi += 1
                    runsScored = 1
                } else {
                    bases = Bases.SECOND_AND_THIRD
                    runnerThird = runnerFirst
                }
            } else if (bases == Bases.SECOND) {
                bases = Bases.SECOND
                runnerSecond.runs += 1
                runnerSecond.simBatter.runs += 1
                batter.rbi += 1
                batter.simBatter.rbi += 1
                runsScored = 1
            } else if (bases == Bases.THIRD) {
                bases = Bases.SECOND
                runnerThird.runs += 1
                runnerThird.simBatter.runs += 1
                batter.rbi += 1
                batter.simBatter.rbi += 1
                runsScored = 1
            } else if (bases == Bases.FIRST_AND_SECOND) {
                int dieRoll = generator.nextInt(100) + 1
                if (dieRoll <= 28) {
                    bases = Bases.SECOND
                    runnerFirst.runs += 1
                    runnerFirst.simBatter.runs += 1
                    runnerSecond.runs += 1
                    runnerSecond.simBatter.runs += 1
                    batter.rbi += 2
                    batter.simBatter.rbi += 2
                    runsScored = 2
                } else {
                    bases = Bases.SECOND_AND_THIRD
                    runnerSecond.runs += 1
                    runnerSecond.simBatter.runs += 1
                    runnerThird = runnerFirst
                    batter.rbi += 1
                    batter.simBatter.rbi += 1
                    runsScored = 1
                }
            } else if (bases == Bases.SECOND_AND_THIRD) {
                bases = Bases.SECOND
                runnerSecond.runs += 1
                runnerSecond.simBatter.runs += 1
                runnerThird.runs += 1
                runnerThird.simBatter.runs += 1
                runnerThird = null
                batter.rbi += 2
                batter.simBatter.rbi += 2
                runsScored = 2
            } else if (bases == Bases.CORNERS) {
                int dieRoll = generator.nextInt(100) + 1
                if (dieRoll <= 28) {
                    bases = Bases.SECOND
                    runnerFirst = null
                    runnerThird = null
                    batter.rbi += 2
                    batter.simBatter.rbi += 2
                    runsScored = 2
                } else {
                    bases = Bases.SECOND_AND_THIRD
                    runnerThird.runs += 1
                    runnerThird.simBatter.runs += 1
                    runnerThird = runnerFirst
                    runnerFirst = null
                    batter.rbi += 1
                    batter.simBatter.rbi += 1
                    runsScored = 1
                }
            } else if (bases == Bases.LOADED) {
                int dieRoll = generator.nextInt(100) + 1
                runnerSecond.runs += 1
                runnerSecond.simBatter.runs += 1
                runnerThird.runs += 1
                runnerThird.simBatter.runs += 1
                if (dieRoll <= 28) {
                    bases = Bases.SECOND
                    runnerFirst.runs += 1
                    runnerFirst.simBatter.runs += 1
                    runnerThird = null
                    runnerFirst = null
                    batter.rbi += 3
                    batter.simBatter.rbi += 3
                    runsScored = 3
                } else {
                    bases = Bases.SECOND_AND_THIRD
                    runnerThird = runnerFirst
                    runnerSecond = null
                    runnerFirst = null
                    batter.rbi += 2
                    batter.simBatter.rbi += 2
                    runsScored = 2
                }
            }
            hitOnPlay = 1
            runnerSecond = batter
            runnerFirst = null

        } else if (atBatResult == AtBatResult.TRIPLE) {
            if (bases == Bases.EMPTY) {
                bases = Bases.THIRD
            } else if (bases == Bases.FIRST) {
                bases = Bases.THIRD
                runnerFirst.runs += 1
                runnerFirst.simBatter.runs += 1
                batter.rbi += 1
                batter.simBatter.rbi += 1
                runsScored = 1
            } else if (bases == Bases.SECOND) {
                bases = Bases.THIRD
                runnerSecond.runs += 1
                runnerSecond.simBatter.runs += 1
                batter.rbi += 1
                batter.simBatter.rbi += 1
                runsScored = 1
            } else if (bases == Bases.THIRD) {
                bases = Bases.THIRD
                runnerThird.runs += 1
                runnerThird.simBatter.runs += 1
                batter.rbi += 1
                batter.simBatter.rbi += 1
                runsScored = 1
            } else if (bases == Bases.FIRST_AND_SECOND) {
                bases = Bases.THIRD
                runnerFirst.runs += 1
                runnerFirst.simBatter.runs += 1
                runnerSecond.runs += 1
                runnerSecond.simBatter.runs += 1
                batter.rbi += 2
                batter.simBatter.rbi += 2
                runsScored = 2
            } else if (bases == Bases.SECOND_AND_THIRD) {
                bases = Bases.THIRD
                runnerSecond.runs += 1
                runnerSecond.simBatter.runs += 1
                runnerThird.runs += 1
                runnerThird.simBatter.runs += 1
                batter.rbi += 2
                batter.simBatter.rbi += 2
                runsScored = 2
            } else if (bases == Bases.CORNERS) {
                bases = Bases.THIRD
                runnerFirst.runs += 1
                runnerFirst.simBatter.runs += 1
                runnerThird.runs += 1
                runnerThird.simBatter.runs += 1
                batter.rbi += 2
                batter.simBatter.rbi += 2
                runsScored = 2
            } else if (bases == Bases.LOADED) {
                bases = Bases.THIRD
                runnerFirst.runs += 1
                runnerFirst.simBatter.runs += 1
                runnerSecond.runs += 1
                runnerSecond.simBatter.runs += 1
                runnerThird.runs += 1
                runnerThird.simBatter.runs += 1
                batter.rbi += 3
                batter.simBatter.rbi += 3
                runsScored = 3
            }
            hitOnPlay = 1
            runnerThird = batter
            runnerSecond = null
            runnerFirst = null

        } else if (atBatResult == AtBatResult.HOMER) {
            if (bases == Bases.EMPTY) {
                batter.rbi += 1
                batter.simBatter.rbi += 1
                runsScored = 1
            } else if (bases == Bases.FIRST) {
                runnerFirst.runs += 1
                runnerFirst.simBatter.runs += 1
                batter.rbi += 2
                batter.simBatter.rbi += 2
                runsScored = 2
            } else if (bases == Bases.SECOND) {
                runnerSecond.runs += 1
                runnerSecond.simBatter.runs += 1
                batter.rbi += 2
                batter.simBatter.rbi += 2
                runsScored = 2
            } else if (bases == Bases.THIRD) {
                runnerThird.runs += 1
                runnerThird.simBatter.runs += 1
                batter.rbi += 2
                batter.simBatter.rbi += 2
                runsScored = 2
            } else if (bases == Bases.FIRST_AND_SECOND) {
                runnerFirst.runs += 1
                runnerFirst.simBatter.runs += 1
                runnerSecond.runs += 1
                runnerSecond.simBatter.runs += 1
                batter.rbi += 3
                batter.simBatter.rbi += 3
                runsScored = 3
            } else if (bases == Bases.SECOND_AND_THIRD) {
                runnerSecond.runs += 1
                runnerSecond.simBatter.runs += 1
                runnerThird.runs += 1
                runnerThird.simBatter.runs += 1
                batter.rbi += 3
                batter.simBatter.rbi += 3
                runsScored = 3
            } else if (bases == Bases.CORNERS) {
                runnerFirst.runs += 1
                runnerFirst.simBatter.runs += 1
                runnerThird.runs += 1
                runnerThird.simBatter.runs += 1
                batter.rbi += 3
                batter.simBatter.rbi += 3
                runsScored = 3
            } else if (bases == Bases.LOADED) {
                runnerFirst.runs += 1
                runnerFirst.simBatter.runs += 1
                runnerSecond.runs += 1
                runnerSecond.simBatter.runs += 1
                runnerThird.runs += 1
                runnerThird.simBatter.runs += 1
                batter.rbi += 4
                batter.simBatter.rbi += 4
                runsScored = 4
            }
            bases = Bases.EMPTY
            batter.runs += 1
            batter.simBatter.runs += 1

            runnerFirst = null
            runnerSecond = null
            runnerThird = null
            hitOnPlay = 1
        } else if (atBatResult in [AtBatResult.WALK, AtBatResult.HIT_BY_PITCH]) {
            if (bases == Bases.EMPTY) {
                bases = Bases.FIRST
            } else if (bases == Bases.FIRST) {
                bases = Bases.FIRST_AND_SECOND
                runnerSecond = runnerFirst
            } else if (bases == Bases.SECOND) {
                bases = Bases.FIRST_AND_SECOND
            } else if (bases == Bases.THIRD) {
                bases = Bases.CORNERS
            } else if (bases == Bases.FIRST_AND_SECOND) {
                bases = Bases.LOADED
                runnerThird = runnerSecond
                runnerSecond = runnerFirst
            } else if (bases == Bases.SECOND_AND_THIRD) {
                bases = Bases.LOADED
            } else if (bases == Bases.CORNERS) {
                runnerSecond = runnerFirst
                bases = Bases.LOADED
            } else if (bases == Bases.LOADED) {
                runnerThird.runs += 1
                runnerThird.simBatter.runs += 1
                runnerThird = runnerSecond
                runnerSecond = runnerFirst
                batter.rbi += 1
                batter.simBatter.rbi += 1
                runsScored = 1
            }
            runnerFirst = batter
        } else if (atBatResult in [AtBatResult.STRIKEOUT, AtBatResult.POP_FLY]) {
            outs++
        }

        pitcher.runs += runsScored
        pitcher.simPitcher.runs += runsScored
        if (side == HalfInning.TOP) {
            awayScore += runsScored
            awayInnings[inning-1] += runsScored
            //if (runsScored > 0) {
            //    auditLog.info "AwayInnings[$inning] += ${awayInnings[inning]}"
            //}
            awayHits += hitOnPlay
        } else {
            homeScore += runsScored
            homeInnings[inning-1] += runsScored
            //if (runsScored > 0) {
            //    auditLog.info "HomeInnings[$inning] += $runsScored"
            //}
            homeHits += hitOnPlay
        }

        def textRunsScored = ""
        if (runsScored > 1) {
            textRunsScored = "$runsScored runs scored. "
        } else if (runsScored == 1) {
            textRunsScored = "1 run scored. "
        }


        def msg = new StringBuilder("${batter.simBatter.batter.name}: ${atBatResult.value}.  ${textRunsScored}${bases.value}  $outs out.  ")
        if (notes.trim() != "") {
            msg += "$notes  "
        }
        if (runnerFirst) {
            msg << "${runnerFirst.simBatter.batter.name} at first.  "
        }
        if (runnerSecond) {
            msg << "${runnerSecond.simBatter.batter.name} at second.  "
        }
        if (runnerThird) {
            msg << "${runnerThird.simBatter.batter.name} at third.  "
        }
        if (gameLogEnabled) {
            if (runsScored > 0) {
                highlightsLog.debug "${side} inning ${inning}:  ${msg.toString()}"
            }
            gameLog.debug "${msg.toString()}"
        }
    }

    private def pitchToBatter(GameBatter gameBatter, GamePitcher gamePitcher, int pitcherCount, SimStyle simStyle) {
         if (simType == SimType.ACCURATE && gameBatter.simBatter.atBats > 50 && gamePitcher.simPitcher.battersRetired > 50) {
             pitchToBatterAccurate(gameBatter, gamePitcher, pitcherCount, simStyle)
         } else {
             pitchToBatterRandom(gameBatter, gamePitcher, pitcherCount, simStyle)
         }
    }


    private def pitchToBatterRandom(GameBatter gameBatter, GamePitcher gamePitcher, int pitcherCount, SimStyle simStyle) {

        def stealAttemptResult = tryStolenBase()
        if (stealAttemptResult == AtBatResult.NO_STEAL) {
            // Batter Info
            SimBatter simBatter = gameBatter.simBatter
            SimPitcher simPitcher
            try {
                simPitcher = gamePitcher.simPitcher
            } catch (Exception e) {
                if (side == HalfInning.TOP) {
                    auditLog.error("Next Relief pitcher for Home Team: $homeTeam.nextReliefPitcher")
                } else {
                    auditLog.error("Next Relief pitcher for Away Team: $awayTeam.nextReliefPitcher")
                }
                auditLog.error(e.getMessage(), e)
                auditLog.error("SimPitcher: $simPitcher")
                throw e
            }
            def batter = simBatter.batter
            def pitcher = simPitcher.pitcher.pitcherStats
            def batterHitRate = batter.getRate(batter.hits)
            def batterWalkRate = batter.getRate(batter.walks + batter.hitByPitch)
            def batterStrikeoutRate = batter.getRate(batter.strikeouts)
            def batterHomerRate = batter.getRate(batter.homers, batter.hits)
            def batterDoubleRate = batter.getRate(batter.doubles, batter.hits)
            def batterTripleRate = batter.getRate(batter.triples, batter.hits)

            def adjustment = BigDecimal.ZERO
            // Pitcher Info
            def pitcherHitRate = pitcher.getRate(pitcher.pitchingHits) + adjustment
            def pitcherWalkRate = pitcher.getRate(pitcher.pitchingWalks + pitcher.pitchingHitBatter) + adjustment
            def pitcherBalkRate = pitcher.getRate(pitcher.pitchingBalks)
            def pitcherStrikeoutRate = pitcher.getRate(pitcher.pitchingStrikeouts)
            def pitcherHomerRate = pitcher.getRate(pitcher.pitchingHomers, pitcher.pitchingHits) + adjustment

            def actualHitRate, actualWalkRate, actualStrikeoutRate, actualHomerRate
            if (simStyle == SimStyle.PITCHER_FOCUSED) {
                actualHitRate = pitcherHitRate
                actualWalkRate = pitcherWalkRate
                actualStrikeoutRate = pitcherStrikeoutRate
                actualHomerRate = pitcherHomerRate
            } else if (simStyle == SimStyle.BATTER_FOCUSED) {
                actualHitRate = batterHitRate
                actualWalkRate = batterWalkRate
                actualStrikeoutRate = batterStrikeoutRate
                actualHomerRate = batterHomerRate
            } else if (simStyle == SimStyle.BATTER_HEAVY) {
                actualHitRate = ((batterHitRate + batterHitRate + pitcherHitRate) / 3)
                actualWalkRate = ((batterWalkRate + batterWalkRate + pitcherWalkRate) / 3)
                actualStrikeoutRate = ((batterStrikeoutRate + batterStrikeoutRate + pitcherStrikeoutRate) / 3)
                actualHomerRate = ((batterHomerRate + batterHomerRate + pitcherHomerRate) / 3)
            } else {
                // Combined Rates
                actualHitRate = ((batterHitRate + pitcherHitRate) / 2)
                actualWalkRate = ((batterWalkRate + pitcherWalkRate) / 2)
                actualStrikeoutRate = ((batterStrikeoutRate + pitcherStrikeoutRate) / 2)
                actualHomerRate = ((batterHomerRate + pitcherHomerRate) / 2)
            }

            def hitOrOut = Math.random()
            def result
            if (hitOrOut <= actualHitRate) {
                // Hit
                def hitType = Math.random()
                if (hitType <= actualHomerRate) {
                    // Homer
                    gameBatter.homers += 1
                    simBatter.homers += 1
                    gamePitcher.homers += 1
                    simPitcher.homers += 1
                    result = AtBatResult.HOMER
                } else if (hitType <= actualHomerRate + batterDoubleRate) {
                    // Double
                    gameBatter.doubles += 1
                    simBatter.doubles += 1
                    result = AtBatResult.DOUBLE
                } else if (hitType <= actualHomerRate + batterDoubleRate + batterTripleRate) {
                    // Triple
                    gameBatter.triples += 1
                    simBatter.triples += 1
                    result = AtBatResult.TRIPLE
                } else {
                    // Single
                    result = AtBatResult.SINGLE
                }
                gameBatter.hits += 1
                gamePitcher.hits += 1
                gameBatter.atBats += 1
                simBatter.hits += 1
                simPitcher.hits += 1
                simBatter.atBats += 1
            } else if (hitOrOut <= actualHitRate + actualWalkRate) {
                // Walk
                gameBatter.walks += 1
                gamePitcher.walks += 1
                simBatter.walks += 1
                simPitcher.walks += 1
                result = AtBatResult.WALK
            } else if (hitOrOut <= actualHitRate + actualWalkRate + pitcherStrikeoutRate) {
                // Strikeout
                gameBatter.strikeouts += 1
                gamePitcher.strikeouts += 1
                gameBatter.atBats += 1
                gamePitcher.battersRetired += 1
                simBatter.strikeouts += 1
                simPitcher.strikeouts += 1
                simBatter.atBats += 1
                simPitcher.battersRetired += 1
                result = AtBatResult.STRIKEOUT
            } else {
                // Groundout
                gameBatter.atBats += 1
                gamePitcher.battersRetired += 1
                simBatter.atBats += 1
                simPitcher.battersRetired += 1
                result = AtBatResult.GROUND_OUT
            }
            result
        } else {
            stealAttemptResult
        }
    }

    private def pitchToBatterAccurate(GameBatter gameBatter, GamePitcher gamePitcher, int pitcherCount, SimStyle simStyle) {

        def stealAttemptResult = tryStolenBase()
        if (stealAttemptResult == AtBatResult.NO_STEAL) {
            // Batter Info
            SimBatter simBatter = gameBatter.simBatter
            SimPitcher simPitcher = gamePitcher.simPitcher
            def batter = simBatter.batter
            def pitcher = simPitcher.pitcher.pitcherStats
            def batterHitRate = batter.getRate(batter.hits)
            def batterWalkRate = batter.getRate(batter.walks + batter.hitByPitch, batter.walks + batter.hitByPitch + batter.atBats - batter.hits)
            def batterStrikeoutRate = batter.getRate(batter.strikeouts, batter.walks + batter.hitByPitch + batter.atBats - batter.hits)
            def batterHomerRate = batter.getRate(batter.homers, batter.hits)
            def batterDoubleRate = batter.getRate(batter.doubles, batter.hits)
            def batterTripleRate = batter.getRate(batter.triples, batter.hits)

            def adjustment = BigDecimal.ZERO
            // Pitcher Info
            def pitcherHitRate = pitcher.getRate(pitcher.pitchingHits) + adjustment
            def pitcherWalkRate = pitcher.getRate(pitcher.pitchingWalks + pitcher.pitchingHitBatter, pitcher.pitchingWalks + pitcher.pitchingHitBatter + pitcher.pitchingBattersRetired) + adjustment
            def pitcherBalkRate = pitcher.getRate(pitcher.pitchingBalks)
            def pitcherStrikeoutRate = pitcher.getRate(pitcher.pitchingStrikeouts, pitcher.pitchingWalks + pitcher.pitchingHitBatter + pitcher.pitchingBattersRetired)
            def pitcherHomerRate = pitcher.getRate(pitcher.pitchingHomers, pitcher.pitchingHits) + adjustment

            def actualHitRate, actualWalkRate, actualStrikeoutRate, actualHomerRate
            if (simStyle == SimStyle.PITCHER_FOCUSED) {
                actualHitRate = pitcherHitRate
                actualWalkRate = pitcherWalkRate
                actualStrikeoutRate = pitcherStrikeoutRate
                actualHomerRate = pitcherHomerRate
            } else if (simStyle == SimStyle.BATTER_FOCUSED) {
                actualHitRate = batterHitRate
                actualWalkRate = batterWalkRate
                actualStrikeoutRate = batterStrikeoutRate
                actualHomerRate = batterHomerRate
            } else if (simStyle == SimStyle.BATTER_HEAVY) {
                actualHitRate = ((batterHitRate + batterHitRate + pitcherHitRate) / 3)
                actualWalkRate = ((batterWalkRate + batterWalkRate + pitcherWalkRate) / 3)
                actualStrikeoutRate = ((batterStrikeoutRate + batterStrikeoutRate + pitcherStrikeoutRate) / 3)
                actualHomerRate = ((batterHomerRate + batterHomerRate + pitcherHomerRate) / 3)
            } else {
                // Combined Rates
                actualHitRate = ((batterHitRate + pitcherHitRate) / 2)
                actualWalkRate = ((batterWalkRate + pitcherWalkRate) / 2)
                actualStrikeoutRate = ((batterStrikeoutRate + pitcherStrikeoutRate) / 2)
                actualHomerRate = ((batterHomerRate + pitcherHomerRate) / 2)
            }

            //boxscoreLog.debug ""
            def batterDiff = Math.abs(simBatter.battingAvg - batter.battingAvg)
            //boxscoreLog.debug "${simBatter.batter.name}  Batter Diff: $batterDiff   Avg: ${gameBatter.simBatter.battingAvg}   Real: ${batter.battingAvg}"
            def pitcherDiff = Math.abs(simPitcher.oppBattingAvg - pitcher.oppBattingAvg)
            //boxscoreLog.debug "${gamePitcher.simPitcher.pitcher.name}  Pitcher Diff: $pitcherDiff   Avg: ${gamePitcher.simPitcher.oppBattingAvg}   Real: ${pitcher.oppBattingAvg}"

            boolean hit = false
            if (batterDiff > pitcherDiff) {
                if (gameBatter.simBatter.battingAvg < batter.battingAvg) {
                    //boxscoreLog.debug "   Hitter control: Low.  Hit!!"
                    hit = true
                } else {
                    //boxscoreLog.debug "   Hitter control: Not Low.  Out"
                }
            } else {
                if (gamePitcher.simPitcher.oppBattingAvg < pitcher.oppBattingAvg) {
                    //boxscoreLog.debug "   Pitcher control: Low.  Hit!!"
                    hit = true
                } else {
                    //boxscoreLog.debug "   Pitcher control: Not low.  Out"
                }
            }

            def result
            if (hit) {
                // Hit
                def hitType = Math.random()
                if (hitType <= actualHomerRate) {
                    // Homer
                    gameBatter.homers += 1
                    simBatter.homers += 1
                    gamePitcher.homers += 1
                    simPitcher.homers += 1
                    result = AtBatResult.HOMER
                } else if (hitType <= actualHomerRate + batterDoubleRate) {
                    // Double
                    gameBatter.doubles += 1
                    simBatter.doubles += 1
                    result = AtBatResult.DOUBLE
                } else if (hitType <= actualHomerRate + batterDoubleRate + batterTripleRate) {
                    // Triple
                    gameBatter.triples += 1
                    simBatter.triples += 1
                    result = AtBatResult.TRIPLE
                } else {
                    // Single
                    result = AtBatResult.SINGLE
                }
                gameBatter.hits += 1
                gamePitcher.hits += 1
                gameBatter.atBats += 1
                simBatter.hits += 1
                simPitcher.hits += 1
                simBatter.atBats += 1
            } else {
                def walkOrOut = Math.random()
                if (walkOrOut <= actualWalkRate) {
                    // Walk
                    gameBatter.walks += 1
                    gamePitcher.walks += 1
                    simBatter.walks += 1
                    simPitcher.walks += 1
                    result = AtBatResult.WALK
                } else if (walkOrOut <= actualWalkRate + pitcherStrikeoutRate) {
                    // Strikeout
                    gameBatter.strikeouts += 1
                    gamePitcher.strikeouts += 1
                    gameBatter.atBats += 1
                    gamePitcher.battersRetired += 1
                    simBatter.strikeouts += 1
                    simPitcher.strikeouts += 1
                    simBatter.atBats += 1
                    simPitcher.battersRetired += 1
                    result = AtBatResult.STRIKEOUT
                } else {
                    // Groundout
                    gameBatter.atBats += 1
                    gamePitcher.battersRetired += 1
                    simBatter.atBats += 1
                    simPitcher.battersRetired += 1
                    result = AtBatResult.GROUND_OUT
                }
            }

            result
        } else {
            stealAttemptResult
        }
    }

    private def tryStolenBase() {
        def gameRunner
        def base
        if (runnerThird == null && runnerSecond != null) {
            gameRunner = runnerSecond
            base = 2
        } else if (runnerSecond == null && runnerFirst != null) {
            gameRunner = runnerFirst
            base = 1
        } else {
            gameRunner = null
        }
        if (gameRunner) {
            // Ok to try to steal
            SimBatter simRunner = gameRunner.simBatter
            def runner = simRunner.batter

            // Batter Info
            def runnerAttemptRate = runner.getRate(runner.stolenBases, runner.walks + runner.hitByPitch + runner.atBats - runner.hits)
            def runnerSuccessRate = runner.getRate(runner.stolenBases, runner.stolenBases + runner.caughtStealing)
            if (simRunner.atBats < 50) {
                def attempt = Math.random()
                if (attempt < runnerAttemptRate) {
                    // Steal Attempt!!
                    def stealOrOut = Math.random()
                    if (stealOrOut < runnerAttemptRate) {
                        simRunner.stolenBases += 1
                        gameRunner.stolenBases += 1
                        if (base == 2) {
                            AtBatResult.STOLE_THIRD
                        } else {
                            AtBatResult.STOLE_SECOND
                        }
                    } else {
                        simRunner.caughtStealing += 1
                        gameRunner.caughtStealing += 1
                        if (base == 2) {
                            AtBatResult.CAUGHT_STEALING_THIRD
                        } else {
                            AtBatResult.CAUGHT_STEALING_SECOND
                        }
                    }
                } else {
                    AtBatResult.NO_STEAL
                }

            } else {
                def currentAttemptRate = simRunner.getRate(simRunner.stolenBases, simRunner.walks + simRunner.hitByPitch + simRunner.atBats - simRunner.hits)
                if (currentAttemptRate < runnerAttemptRate) {
                    // Runner is due.  Give it a shot!
                    def currentSuccessRate = simRunner.getRate(simRunner.stolenBases, simRunner.stolenBases + simRunner.caughtStealing)
                    if (currentSuccessRate < runnerSuccessRate) {
                        // Base stolen!
                        simRunner.stolenBases += 1
                        gameRunner.stolenBases += 1
                        if (base == 2) {
                            AtBatResult.STOLE_THIRD
                        } else {
                            AtBatResult.STOLE_SECOND
                        }
                    } else {
                        //Caught stealing!
                        simRunner.caughtStealing += 1
                        gameRunner.caughtStealing += 1
                        if (base == 2) {
                            AtBatResult.CAUGHT_STEALING_THIRD
                        } else {
                            AtBatResult.CAUGHT_STEALING_SECOND
                        }
                    }
                } else {
                    AtBatResult.NO_STEAL
                }
            }
        } else {
            AtBatResult.NO_STEAL
        }
    }

}
