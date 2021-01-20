package baseball.mongo

import baseball.domain.*
import baseball.mongo.MongoManager
import baseball.processing.ScheduleLoader
import mjs.common.utils.TransactionIdGen
import mjs.common.utils.LogUtils
import org.apache.log4j.Logger

class SimulationManager {

    /**
     * The Log4J logger used by this object.
     */
    protected Logger log = Logger.getLogger("Core");
    def highlightsLog = Logger.getLogger('highlights')
    def seasonStatsLog = Logger.getLogger('seasonStats')
    final String collection = "simulations"


    MongoManager mongoManager = new MongoManager()

    SimulationManager() {
        log.info "Opening Mongo connection..."
        mongoManager.open("ballsim")
        deleteAllSimulations()
        log.info "Opening Mongo connection... Done!"
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
        def result = mongoManager.find(collection, ["simulationID": simulationID])
        // Populate Simulation from JSON data.
        Simulation sim = new Simulation(result[0])
        sim
    }

    Simulation startNewSimulation(Map teamMap, String idPrefix, String simulationName) {
        String simulationID = Simulation.generateSimulationID(idPrefix)
        return startNewSimulation(teamMap, idPrefix, simulationName, simulationID)
    }

    Simulation startNewSimulation(Map teamMap, String idPrefix, String simulationName, String simulationID) {
        Simulation sim = new Simulation()
        sim.year = teamMap.year
        sim.simulationID = simulationID
        sim.simulationName = simulationName
        sim.teamMap = teamMap
        sim.teamMap.remove("year")
        sim.teamMap.each() { next ->
            sim.addTeamToSeason(next.value)
        }
        LogUtils.println(sim.scheduleTeamLookup, "   ", true)

        // Schedule
        def scheduleLoader = new ScheduleLoader()
        String scheduleName = sim.getScheduleName()
        sim.schedule = scheduleLoader.loadScheduleFromFile(scheduleName, sim.getTeamCount())

        // Save simulation.
        mongoManager.addToCollection(collection, sim)


        // Return
        sim
    }

    Simulation playSimGame(String simulationID) {
        // Save simulation.
        Simulation sim = findSimulation(simulationID)
        return playSimGame(sim)
    }

    Simulation playSimGame(Simulation sim) {
        // Get the next game to play.
        ScheduledGame game = sim.getNextGame()
        LogUtils.println(game, "   ", true)

        // Get the next two teams.
        Team homeTeam = sim.scheduleTeamLookup[game.homeTeam]
        Team awayTeam = sim.scheduleTeamLookup[game.awayTeam]
        println "Next game:  Home: ${homeTeam.name}  Away: ${awayTeam.name}"

        // Return
        sim
    }

}
