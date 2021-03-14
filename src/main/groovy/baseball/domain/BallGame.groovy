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
        homeTeam.starter.simPitcher.gamesStarted += 1
        awayTeam.starter.simPitcher.gamesStarted += 1
        homeTeam.starter.simPitcher.games += 1
        awayTeam.starter.simPitcher.games += 1
        if (homeTeam.starter.simPitcher.gamesStarted >= homeTeam.starter.simPitcher.pitcher.pitcherStats.pitchingGamesStarted) {
            // This starter is maxed out. Pull a new starter out of the reserved pitchers list.
            int next = 0
            while (homeTeam.reservePitchers.get(next).simPitcher.pitcher.pitcherStats.pitchingGamesStarted == 0 && next < homeTeam.reservePitchers.size()-1) {
                next++
            }
            if (homeTeam.reservePitchers.get(next).simPitcher.pitcher.pitcherStats.pitchingGamesStarted > 0) {
                // Found a starter!
                def newStarter = homeTeam.reservePitchers.get(next)
                homeTeam.rotation[homeTeam.nextStartingPitcher] = newStarter
                homeTeam.doneStarters << homeTeam.starter
            }
        }
        if (awayTeam.starter.simPitcher.gamesStarted >= awayTeam.starter.simPitcher.pitcher.pitcherStats.pitchingGamesStarted) {
            // This starter is maxed out. Pull a new starter out of the reserved pitchers list.
            int next = 0
            while (awayTeam.reservePitchers.get(next).simPitcher.pitcher.pitcherStats.pitchingGamesStarted == 0 && next < awayTeam.reservePitchers.size()-1) {
                next++
            }
            if (awayTeam.reservePitchers.get(next).simPitcher.pitcher.pitcherStats.pitchingGamesStarted > 0) {
                // Found a starter!
                def newStarter = awayTeam.reservePitchers.get(next)
                awayTeam.rotation[awayTeam.nextStartingPitcher] = newStarter
                awayTeam.doneStarters << awayTeam.starter
            }
        }

        awayTeam.lineup.each {
            it.reset()
        }
        homeTeam.lineup.each {
            it.reset()
        }

        awayTeam.rotation.each {
            it.reset()
        }
        homeTeam.rotation.each {
            it.reset()
        }

        awayTeam.bullpen.each {
            it.reset()
        }
        homeTeam.bullpen.each {
            it.reset()
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

    private def getNewPitcher() {
        if (side == HalfInning.TOP) {
            // New home pitcher.
            if (homeTeam.bullpen.size() >= homeTeam.nextReliefPitcher+1) {
                homeTeam.currentPitcher = homeTeam.bullpen[homeTeam.nextReliefPitcher]
                auditLog.info "New pitcher for home team ($homeTeam.nextReliefPitcher). Bullpen size = $homeTeam.bullpen.size() ${homeTeam.currentPitcher.simPitcher.pitcher.name}"
                homeTeam.pitchersUsed << homeTeam.currentPitcher
                homeTeam.currentPitcher.simPitcher.games += 1
                homeTeam.nextReliefPitcher++
                return homeTeam.currentPitcher
            } else if (homeTeam.nextReliefPitcher-5 < homeTeam.reservePitchers.size()) {
                // Bullpen empty. Use reserve pitchers.
                auditLog.info "New pitcher for home team ($homeTeam.nextReliefPitcher). Bullpen size = $homeTeam.bullpen.size() "
                homeTeam.currentPitcher = homeTeam.reservePitchers[homeTeam.nextReliefPitcher-5]
                homeTeam.pitchersUsed << homeTeam.currentPitcher
                homeTeam.currentPitcher.simPitcher.games += 1
                homeTeam.nextReliefPitcher++
                return homeTeam.currentPitcher
            } else {
                // Keep the current pitcher. Out of pitchers.
                auditLog.info "Out of pitchers. Going back to the starter."
                homeTeam.currentPitcher = homeTeam.starter
            }
        } else {
            // New away pitcher.
            auditLog.info("New pitcher for away team ($awayTeam.nextReliefPitcher). Bullpen size = $awayTeam.bullpen.size() ")
            if (awayTeam.bullpen.size() >= awayTeam.nextReliefPitcher+1) {
                awayTeam.currentPitcher = awayTeam.bullpen[awayTeam.nextReliefPitcher]
                awayTeam.pitchersUsed << awayTeam.currentPitcher
                awayTeam.currentPitcher.simPitcher.games += 1
                awayTeam.nextReliefPitcher++
                return awayTeam.currentPitcher
            } else if (awayTeam.nextReliefPitcher-5 < awayTeam.reservePitchers.size()) {
                // Bullpen empty. Use reserve pitchers.
                auditLog.info("New pitcher for away team ($awayTeam.nextReliefPitcher). Bullpen size = $awayTeam.bullpen.size() ")
                awayTeam.currentPitcher = awayTeam.reservePitchers[awayTeam.nextReliefPitcher-5]
                awayTeam.pitchersUsed << awayTeam.currentPitcher
                awayTeam.currentPitcher.simPitcher.games += 1
                awayTeam.nextReliefPitcher++
                return awayTeam.currentPitcher
            } else {
                // Keep the current pitcher. Out of pitchers.
                auditLog.info "Out of pitchers. Going back to the starter."
                awayTeam.currentPitcher = awayTeam.starter
            }
        }
    }

    private def playHalfInning(SimStyle simStyle, int doublePlayCount) {
        boolean readyToStart = isReadyToStart()
        int pitcherCount
        def pitcher
        if (side == HalfInning.TOP) {
            pitcher = homeTeam.currentPitcher
        } else {
            pitcher = awayTeam.currentPitcher
        }
        outs = 0
        bases = Bases.EMPTY
        runnerFirst = null
        runnerSecond = null
        runnerThird = null
        while (outs < 3) {
            if (pitcher.pitcherExhausted()) {
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
            throw new Exception("Unable to start game.  The away lineup does not contain 9 batters.")
        }
        if (homeTeam.lineup.size() != 9) {
            throw new Exception("Unable to start game.  The home lineup does not contain 9 batters.")
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
        auditLog.info "Checking isReadyToStart()... Result: Yes"

    }

    private def getNextBatter() {
        def batter
        if (side == HalfInning.TOP) {
            batter = awayTeam.lineup[awayTeam.nextBatter]
        } else {
            batter = homeTeam.lineup[homeTeam.nextBatter]
        }
        batter
    }

    private def inningOver() {
        if (side == HalfInning.TOP) {
            if (inning == 9 && homeScore > awayScore) {
                isGameOver = true
            } else {
                def homeStarter = homeTeam.currentPitcher
                auditLog.info("End of top of inning $inning. Current pitcher: $homeTeam.currentPitcher.simPitcher.pitcher.name")
                side = HalfInning.BOTTOM
                gameLog.debug "${format("Pitcher", 20)}  ${format("W", 3)}  ${format("L", 3)}  ${format("IP", 5)}  ${format("R", 3)}  ${format("ERA", 5)}  ${format("H", 3)}  ${format("HR", 3)}  ${format("BB", 3)}  ${format("SO", 3)}"
                gameLog.debug "${format(homeTeam.currentPitcher.simPitcher.pitcher.name, 20)}  ${format(homeTeam.currentPitcher.simPitcher.wins, 3)}  ${format(homeTeam.currentPitcher.simPitcher.losses, 3)}  ${format(homeTeam.currentPitcher.battersRetired/3, 5)}  ${format(homeTeam.currentPitcher.runs, 3)}  ${homeTeam.currentPitcher.simPitcher.era}  ${format(homeTeam.currentPitcher.hits, 3)}  ${format(homeTeam.currentPitcher.homers, 3)}  ${format(homeTeam.currentPitcher.walks, 3)}  ${format(homeTeam.currentPitcher.strikeouts, 3)}"
                printTeamHittingStats(awayTeam.lineup)
            }
        } else {
            if (inning >= 9 && awayScore != homeScore) {
                isGameOver = true
            } else {
                auditLog.info("End of bottom of inning $inning. Current pitcher: $awayTeam.currentPitcher.simPitcher.pitcher.name")
                side = HalfInning.TOP
                gameLog.debug "${format("Pitcher", 20)}  ${format("W", 3)}  ${format("L", 3)}  ${format("IP", 5)}  ${format("R", 3)}  ${format("ERA", 5)}  ${format("H", 3)}  ${format("HR", 3)}  ${format("BB", 3)}  ${format("SO", 3)}"
                gameLog.debug "${format(awayTeam.currentPitcher.simPitcher.pitcher.name, 20)}  ${format(awayTeam.currentPitcher.simPitcher.wins, 3)}  ${format(awayTeam.currentPitcher.simPitcher.losses, 3)}  ${format(awayTeam.currentPitcher.battersRetired/3, 5)}  ${format(awayTeam.currentPitcher.runs, 3)}  ${awayTeam.currentPitcher.simPitcher.era}  ${format(awayTeam.currentPitcher.hits, 3)}  ${format(awayTeam.currentPitcher.homers, 3)}  ${format(awayTeam.currentPitcher.walks, 3)}  ${format(awayTeam.currentPitcher.strikeouts, 3)}"
                printTeamHittingStats(homeTeam.lineup)
                inning++
                awayInnings.add(0)
                homeInnings.add(0)
            }
        }

        if (isGameOver) {
            if (homeScore > awayScore) {
                homeTeam.wins++
                awayTeam.losses++
                homeTeam.winDiff = homeTeam.wins - homeTeam.losses
                awayTeam.winDiff = awayTeam.wins - awayTeam.losses
                homeTeam.starter.simPitcher.wins += 1
                awayTeam.starter.simPitcher.losses += 1
            } else {
                awayTeam.wins++
                homeTeam.losses++
                homeTeam.winDiff = homeTeam.wins - homeTeam.losses
                awayTeam.winDiff = awayTeam.wins - awayTeam.losses
                awayTeam.starter.simPitcher.wins += 1
                homeTeam.starter.simPitcher.losses += 1
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
                boxscoreLog.debug "${format(it.simBatter.batter.name, 20)}  ${format(it.atBats, 3)}  ${format(it.runs, 3)}  ${format(it.hits, 3)}  ${format(it.doubles, 3)}  ${format(it.triples, 3)}  ${format(it.homers, 3)}  ${format(it.rbi, 4)}  ${format(it.walks, 3)}  ${format(it.strikeouts, 3)}  ${format(it.stolenBases, 3)}  ${format(it.caughtStealing, 3)}  ${format(it.simBatter.battingAvg + "0000", 5)}"
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
                boxscoreLog.debug "${format(next.simPitcher.pitcher.name, 20)}  ${format(next.simPitcher.wins, 3)}  ${format(next.simPitcher.losses, 3)}  ${format(next.battersRetired/3, 5)}  ${format(next.runs, 3)}  ${next.simPitcher.era}  ${format(next.hits, 3)}  ${format(next.homers, 3)}  ${format(next.walks, 3)}  ${format(next.strikeouts, 3)}"
            }
            boxscoreLog.debug ""
            boxscoreLog.debug ""
            boxscoreLog.debug "${homeTeam.year} ${homeTeam.teamName}:"
            boxscoreLog.debug "${format("Batter", 20)}  ${format("AB", 3)}  ${format("R", 3)}  ${format("H", 3)}  ${format("2B", 3)}  ${format("3B", 3)}  ${format("HR", 3)}  ${format("RBI", 4)}  ${format("BB", 3)}  ${format("SO", 3)}  ${format("SB", 3)}  ${format("CS", 3)}  ${format("AVG", 5)} "
            homeTeam.lineup.each {
                boxscoreLog.debug "${format(it.simBatter.batter.name, 20)}  ${format(it.atBats, 3)}  ${format(it.runs, 3)}  ${format(it.hits, 3)}  ${format(it.doubles, 3)}  ${format(it.triples, 3)}  ${format(it.homers, 3)}  ${format(it.rbi, 4)}  ${format(it.walks, 3)}  ${format(it.strikeouts, 3)}  ${format(it.stolenBases, 3)}  ${format(it.caughtStealing, 3)}  ${format(it.simBatter.battingAvg + "0000", 5)}"
            }
            boxscoreLog.debug ""
            boxscoreLog.debug "${format("Pitcher", 20)}  ${format("W", 3)}  ${format("L", 3)}  ${format("IP", 5)}  ${format("R", 3)}  ${format("ERA", 5)}  ${format("H", 3)}  ${format("HR", 3)}  ${format("BB", 3)}  ${format("SO", 3)}"
            homeTeam.pitchersUsed.each() { next ->
                boxscoreLog.debug "${format(next.simPitcher.pitcher.name, 20)}  ${format(next.simPitcher.wins, 3)}  ${format(next.simPitcher.losses, 3)}  ${format(next.battersRetired/3, 5)}  ${format(next.runs, 3)}  ${next.simPitcher.era}  ${format(next.hits, 3)}  ${format(next.homers, 3)}  ${format(next.walks, 3)}  ${format(next.strikeouts, 3)}"
            }
        }
    }

    private void printTeamHittingStats(def lineup) {
        int atBats = 0, runs = 0, hits = 0, doubles = 0, triples = 0, homers = 0, rbi = 0, bb = 0, so = 0, sb = 0, cs = 0
        lineup.each {
            atBats += it.atBats
            runs += it.runs
            hits += it.hits
            doubles += it.doubles
            triples += it.triples
            homers += it.homers
            rbi += it.rbi
            bb += it.walks
            so += it.strikeouts
            sb += it.stolenBases
            cs += it.caughtStealing
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
            if (runsScored > 0) {
                auditLog.info "AwayInnings[$inning] += ${awayInnings[inning]}"
            }
            awayHits += hitOnPlay
        } else {
            homeScore += runsScored
            homeInnings[inning-1] += runsScored
            if (runsScored > 0) {
                auditLog.info "HomeInnings[$inning] += $runsScored"
            }
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
