package baseball.mongo

import baseball.domain.*
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


    MongoManager mongoManager = new MongoManager()

    SimulationManager() {
        def m = "${C}.constructor() - "
        log.info "$m Opening Mongo connection..."
        mongoManager.open("ballsim")
        deleteAllSimulations()
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

    Simulation startNewSimulation(Map teamMap, String idPrefix, String simulationName) {
        String simulationID = Simulation.generateSimulationID(idPrefix)
        return startNewSimulation(teamMap, idPrefix, simulationName, simulationID)
    }

    Simulation startNewSimulation(Map teamMap, String idPrefix, String simulationName, String simulationID) {
        def m = "${C}.startNewSimulation() - "
        Simulation sim = new Simulation()
        sim.year = teamMap.year
        sim.simulationID = simulationID
        sim.simulationName = simulationName
        sim.teamMap = teamMap
        sim.teamMap.remove("year")
        sim.teamMap.each() { next ->
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

        log.debug "$m Preparing to save simulation (converting to Json)..."
        //Map jsonMap = sim.toJsonMap()
        Map jsonMap = sim.toMap()
        log.debug "$m Preparing to save simulation (converting to Json)... Done."

        // Save simulation.
        log.debug "$m Saving simulation to datastore..."
        mongoManager.addToCollection(collection, jsonMap)
        log.debug "$m Saving simulation to datastore... Done."


        // Return
        sim
    }

    Simulation playSimGame(String simulationID) {
        // Save simulation.
        Simulation sim = findSimulation(simulationID)
        return playSimGame(sim)
    }

    Simulation playSimGame(Simulation sim) {

        // Play the game.
        sim.playSimGame()
        // Return
        sim
    }

}
