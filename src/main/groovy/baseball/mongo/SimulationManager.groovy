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
    final String collection = "simulations"
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
        long result = mongoManager.deleteAll(collection)
        result
    }

    long deleteSimulation(String simulationID) {
        // Delete all simulations that match this simulationID.
        long result = mongoManager.deleteMany(collection, ["simulationID":simulationID])
        result
    }

    Simulation findSimulation(String simulationID) {
        Simulation sim = null
        def result = mongoManager.find(collection, ["simulationID": simulationID])
        // Populate Simulation from JSON data.
        if (result.size() > 0) {
            sim = new Simulation(result[0])
        }
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
        teamMap.each() { next ->
            sim.addTeamToSeason(next.value)
        }
        log.debug "$m scheduleTeamLookup:"
        LogUtils.debug(log, sim.scheduleTeamLookup, "   ", true)

        // Schedule
        def scheduleLoader = new ScheduleLoader()
        String scheduleName = sim.getScheduleName()
        log.debug "$m Loading schedule from file..."
        sim.schedule = scheduleLoader.loadScheduleFromFile(scheduleName, sim.getTeamCount())
        log.debug "$m Loading schedule from file... Done."

        // Save simulation.
        log.debug "$m Saving simulation to datastore..."
        Map jsonMap = sim.toMap()
        mongoManager.addToCollection(collection, jsonMap)
        log.debug "$m Saving simulation to datastore... Done."

        // Return
        sim
    }

    void saveSimulation(Simulation sim) {
        def m = "${C}.saveSimulation() - "
        // Save simulation.
        log.debug "$m Saving simulation to datastore..."
        Map jsonMap = sim.toMap()
        mongoManager.addToCollection(collection, jsonMap)
        log.debug "$m Saving simulation to datastore... Done."
    }

    Simulation playSimGame(String simulationID) {
        playSimGames(simulationID, false)
    }

    Simulation playSimGame(String simulationID, boolean logStandings) {
        // Save simulation.
        Simulation sim = findSimulation(simulationID, logStandings)
        return playSimGame(sim)
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

    Simulation playSimRound(String simulationID) {
        playSimRound(simulationID, false)
    }

    Simulation playSimRound(String simulationID, boolean logStandings) {
        // Save simulation.
        Simulation sim = findSimulation(simulationID, logStandings)
        return playSimRound(sim)
    }

    Simulation playSimRound(Simulation sim) {
        playSimRound(false)
    }

    Simulation playSimRound(Simulation sim, boolean logStandings) {
        def m = "${C}.playSimRound() - "

        // Play the game.
        sim.playSimRound(logStandings)

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

        // Play the game.
        sim.logStandings()

        // Return Simulation object.
        sim
    }

}
