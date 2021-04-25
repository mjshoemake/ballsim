package baseball.mongo

import baseball.domain.*
import baseball.processing.HttpHistoricalDataManager
import baseball.processing.ScheduleLoader
import mjs.common.utils.LogUtils
import org.apache.log4j.Logger

class SimulationManager {

    /**
     * The Log4J logger used by this object.
     */
    def C = "SimulationManager"
    protected Logger log = Logger.getLogger("Debug");
    def highlightsLog = Logger.getLogger('highlights')
    def seasonStatsLog = Logger.getLogger('seasonStats')
    final String collectionSimulations = "simulations"
    final String collectionSchedules = "schedules"
    final String collectionLeagues = "leagues"
    HttpHistoricalDataManager dataMgr = new HttpHistoricalDataManager()


    MongoManager mongoManager = new MongoManager()

    SimulationManager() {
        def m = "${C}.constructor() - "
        log.info "$m Opening Mongo connection..."
        mongoManager.open("ballsim")
        //deleteAllSimulations()
        log.info "$m Opening Mongo connection... Done!"
    }

    void close() {
        mongoManager.close()
    }

    long deleteAllSimulations() {
        long result = mongoManager.deleteAll(collectionSimulations)
        mongoManager.deleteAll(collectionSchedules)
        mongoManager.deleteAll(collectionLeagues)
        result
    }

    long deleteSimulation(String simulationID) {
        // Delete all simulations that match this simulationID.
        long result = mongoManager.deleteMany(collectionSimulations, ["simulationID":simulationID])
        mongoManager.deleteMany(collectionSchedules, ["simulationID":simulationID])
        mongoManager.deleteMany(collectionLeagues, ["simulationID":simulationID])
        result
    }

    Simulation findSimulation(String simulationID) {
        Simulation sim = null
        ScheduledSeason schedule = null
        def result = mongoManager.find(collectionSimulations, ["simulationID": simulationID])
        // Populate Simulation from JSON data.
        if (result.size() > 0) {
            sim = new Simulation(result[0])
        }
        // Reload schedule
        def scheduleMap = mongoManager.find(collectionSchedules, ["simulationID": simulationID])
        // Populate Schedule from JSON data.
        if (scheduleMap.size() > 0) {
            schedule = new ScheduledSeason(scheduleMap[0])
            sim.schedule = schedule
        }
/*
        // Reload Leagues
        sim.leagueKeys.each { nextKey ->
            def leagueMap = mongoManager.find(collectionLeagues, ["leagueName": "$simulationID-$nextKey"])
            if (leagueMap.size() > 0) {
                sim.leagues[nextKey] = new League(leagueMap[0])
            }
        }
 */
        sim
    }

    Simulation startNewSimulation(String year, String idPrefix, String simulationName) {
        String simulationID = Simulation.generateSimulationID(idPrefix)
        return startNewSimulation(teamMap, idPrefix, simulationName, simulationID)
    }

    Simulation startNewSimulation(String year, String idPrefix, String simulationName, String simulationID) {
        def m = "${C}.startNewSimulation() - "
        Simulation sim = new Simulation()
        sim.year = year
        sim.simulationID = simulationID
        sim.simulationName = simulationName
        Map teamMap = dataMgr.getTeamMapForSeason(year)
        teamMap.remove("year")
        Object nextTeam
        try {
            teamMap.each() { next ->
                nextTeam = next.value
                sim.addTeamToSeason(next.value)
            }
        } catch (Exception e) {
            log.error("Failed to add team ${nextTeam} to season.", e)
            e.printStackTrace()
        }
        log.debug "$m scheduleTeamLookup:"
        LogUtils.debug(log, sim.scheduleTeamLookup, "   ", true)

        // Schedule
        def scheduleLoader = new ScheduleLoader()
        String scheduleName = sim.getScheduleName()
        log.debug "$m Loading schedule from file..."
        sim.schedule = scheduleLoader.loadScheduleFromFile(scheduleName, sim.getTeamCount())
        sim.schedule.simulationID = sim.simulationID
        log.debug "$m Loading schedule from file... Done."

        // Save simulation
        saveSimulation(sim)

        // Return
        sim
    }

    void saveSimulation(Simulation sim) {
        def m = "${C}.saveSimulation() - "
        // Save simulation.
        log.debug "$m Saving simulation to datastore..."
        Map jsonMap = sim.toMap()
        mongoManager.addToCollection(collectionSimulations, jsonMap)
        log.debug "$m Saving schedule to datastore..."
        sim.schedule.simulationID = sim.simulationID
        jsonMap = sim.schedule.toMap()
        mongoManager.addToCollection(collectionSchedules, jsonMap)
        log.debug "$m Saving leagues to datastore..."
        sim.leagues.each { Map.Entry nextLeague ->
            jsonMap = nextLeague.value.toMap()
            mongoManager.addToCollection(collectionLeagues, jsonMap)
        }
        log.debug "$m Saving simulation to datastore... Done."
    }

    Simulation playSimGame(String simulationID) {
        playSimGames(simulationID, false)
    }

    Simulation playSimGame(String simulationID, boolean logStandings) {
        // Save simulation.
        Simulation sim = findSimulation(simulationID)
        return playSimGame(sim, logStandings)
    }

    Simulation playSimGame(Simulation sim) {
        playSimGame(false)
    }

    Simulation playSimGame(Simulation sim, boolean logStandings) {
        def m = "${C}.playSimGame() - "

        // Play the game.
        sim.playSimGame(logStandings)

        // Delete the currently saved simulation.
        deleteSimulation(sim.simulationID)

        // Simulation has now changed. Need to save current state.
        saveSimulation(sim)

        // Return Simulation object.
        sim
    }

    Simulation playSimRound(String simulationID, int numRounds) {
        playSimRound(simulationID, false, numRounds)
    }

    Simulation playSimRound(String simulationID, boolean logStandings, int numRounds) {
        // Save simulation.
        Simulation sim = findSimulation(simulationID, logStandings)
        return playSimRound(sim, logStandings, numRounds)
    }

    Simulation playSimRound(Simulation sim, int numRounds) {
        playSimRound(sim, false, numRounds)
    }

    Simulation playSimRound(Simulation sim, boolean logStandings, int numRounds) {
        def m = "${C}.playSimRound() - "

        // Play the game.
        for (int i=0; i <= numRounds-1; i++) {
            sim.playSimRound(logStandings, numRounds)
        }

        // Delete the currently saved simulation.
        deleteSimulation(sim.simulationID)

        // Simulation has now changed. Need to save current state.
        saveSimulation(sim)

        // Return Simulation object.
        sim
    }

    Simulation playSimWeek(Simulation sim, boolean logStandings) {
        def m = "${C}.playSimRound() - "

        // Play the game.
        sim.playSimWeek(logStandings)

        // Delete the currently saved simulation.
        deleteSimulation(sim.simulationID)

        // Simulation has now changed. Need to save current state.
        saveSimulation(sim)

        // Return Simulation object.
        sim
    }

    int countGamesLeftInRound(Simulation sim) {
        return sim.countGamesLeftInRound()
    }

    Simulation logStandings(Simulation sim) {
        def m = "${C}.logStandings() - "
        sim.logStandings()
        sim
    }

    Simulation logSchedule(Simulation sim) {
        def m = "${C}.logSchedules() - "
        sim.logSchedule()
        sim
    }

    Simulation logPitchingStatsAccuracy(Simulation sim) {
        def m = "${C}.logPitchingStasAccuracy() - "
        sim.logPitchingStatsAccuracy()
        sim
    }

    Simulation logBattingStatsAccuracy(Simulation sim) {
        def m = "${C}.logBattingStasAccuracy() - "
        sim.logBattingStatsAccuracy()
        sim
    }

}
