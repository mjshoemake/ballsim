package baseball


import baseball.domain.Simulation
import baseball.mongo.SimulationManager
import baseball.processing.HttpHistoricalDataManager
import baseball.processing.ScheduleLoader
import mjs.common.utils.LogUtils
import org.apache.log4j.Logger
import org.junit.After
import org.junit.Before
import org.junit.Before
import org.junit.Test

class ReplaySeasonTests {

    def C = "ReplaySeasonTests"
    def ballGame = null
    def loader = null
    def log = Logger.getLogger("Debug");
    def auditLog = Logger.getLogger('Audit')
    def highlightsLog = Logger.getLogger('highlights')
    String idPrefix = "Test"
    String simulationID = null
    SimulationManager simMgr = null
    HttpHistoricalDataManager dataMgr = null
    //DatabaseDriver dbDriver = null

    @Before
    void setUp() {
        if (! LogUtils.isLoggingConfigured()) {
            LogUtils.initializeLogging()
        }
        if (! simulationID) {
            //simulationID = Simulation.generateSimulationID(idPrefix)
            simulationID = "ReplaySeasonTests1"
        }
        // MySQL
        //Properties dbProps = new ConfigFileLoader("/Users/Dad/Config/").loadPropertiesFile("mysql.properties")
        //dbDriver = new DatabaseDriver(dbProps)
        //mongoDB.open("ballsim")
        simMgr = new SimulationManager()
        loader = new ScheduleLoader()
        dataMgr = new HttpHistoricalDataManager()
    }

    @After
    void tearDown() {
        ballGame == null
        loader = null
    }

    @Test
    void testSimulationSequence() {
        //deleteAllSimulations()
        testStartSimulation()
        //testPlaySimulationGame()
    }

    void deleteAllSimulations() {
        def m = "${C}.deleteAllSimulations() - "
        long deletedCount = simMgr.deleteAllSimulations()
        log.debug "$m Deleted simulations: $deletedCount"
    }

    void testStartSimulation() {
        def m = "${C}.testStartSimulation() - "
        // 2003 skipped... could only find 8 batters for lineup.
        def year = "1991"

        // Reload the simulation
        println "$m calling SimulationManager.findSimulation($simulationID)..."
        Simulation sim = simMgr.findSimulation(simulationID)
        println "$m calling SimulationManager.findSimulation($simulationID)... Done"

        if (! sim) {
            def teamMap = dataMgr.getTeamMapForSeason(year)
            // Start simulation
            log.debug "$m No saved simulation found. Starting new simulation..."
            log.debug "$m calling SimulationManager.startNewSimulation()... SimID: $simulationID"
            sim = simMgr.startNewSimulation(teamMap, idPrefix, "Test Sim", simulationID)
            log.debug "$m calling SimulationManager.startNewSimulation()... Done"
        } else {
            log.debug "$m Saved simulation found. Continuing simulation..."
        }

        log.debug "$m Simulation.logDivisions()..."
        sim.logDivisions(log)

        //LogUtils.println(reloaded, "   ", true)
        // Play Simulation Game
        println "$m calling SimulationManager.playSimGame()... SimID: $simulationID"
        simMgr.playSimGame(sim)
        simMgr.playSimGame(sim)
        simMgr.playSimGame(sim)
        simMgr.playSimGame(sim)
        simMgr.playSimGame(sim)
        simMgr.playSimGame(sim)
        simMgr.playSimGame(sim)
        simMgr.playSimGame(sim)
        simMgr.playSimGame(sim)
        simMgr.playSimGame(sim)
        simMgr.playSimGame(sim)
        simMgr.playSimGame(sim)
        simMgr.playSimGame(sim)
        simMgr.playSimGame(sim)
        simMgr.playSimGame(sim)
        simMgr.playSimGame(sim)
        simMgr.playSimGame(sim)
        simMgr.playSimGame(sim)
        simMgr.playSimGame(sim)
        simMgr.playSimGame(sim)
        simMgr.playSimGame(sim)
        simMgr.playSimGame(sim)
        simMgr.playSimGame(sim)
        simMgr.playSimGame(sim)
        simMgr.playSimGame(sim)
        println "$m calling SimulationManager.playSimGame()... Done"

        // Delete the Simulation
        println "$m calling SimulationManager.deleteSimulation()... SimID: $simulationID"
        simMgr.deleteSimulation(simulationID)
        println "$m calling SimulationManager.deleteSimulation()... Done"

    }

}
