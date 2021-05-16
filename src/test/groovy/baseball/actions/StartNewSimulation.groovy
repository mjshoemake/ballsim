package baseball.actions

import baseball.domain.Simulation
import baseball.mongo.SimulationManager
import baseball.processing.HttpHistoricalDataManager
import baseball.processing.ScheduleLoader
import mjs.common.utils.LogUtils
import org.apache.log4j.Logger
import org.junit.After
import org.junit.Before
import org.junit.Test

class StartNewSimulation {

    def C = "StartNewSimulation"
    def ballGame = null
    def loader = null
    def log = Logger.getLogger("Debug");
    def auditLog = Logger.getLogger('Audit')
    def highlightsLog = Logger.getLogger('highlights')
    String idPrefix = "Test"
    String simulationID = null
    SimulationManager simMgr = null

    @Before
    void setUp() {
        if (! LogUtils.isLoggingConfigured()) {
            LogUtils.initializeLogging()
        }
        if (! simulationID) {
            simulationID = "ShoemakeSim1"
        }
        simMgr = new SimulationManager()
        loader = new ScheduleLoader()
    }

    @After
    void tearDown() {
        ballGame == null
        loader = null
    }

    @Test
    void testSimulationSequence() {
        createNewSimulation()
    }

    void deleteAllSimulations() {
        def m = "${C}.deleteAllSimulations() - "
        long deletedCount = simMgr.deleteAllSimulations()
        log.debug "$m Deleted simulations: $deletedCount"
    }

    void createNewSimulation() {
        def m = "${C}.createNewSimulation() - "
        def year = "1990"

        // Delete the simulation, just in case a sim with this ID exists.
        println "$m calling SimulationManager.deleteSimulation()... SimID: $simulationID"
        simMgr.deleteSimulation(simulationID)
        println "$m calling SimulationManager.deleteSimulation()... Done"

        // Start simulation
        log.debug "$m No saved simulation found. Starting new simulation..."
        log.debug "$m calling SimulationManager.startNewSimulation()... SimID: $simulationID"
        Simulation sim = simMgr.startNewSimulation(year, idPrefix, "Shoemake Sim 1", simulationID)
        log.debug "$m calling SimulationManager.startNewSimulation()... Done"

        // Reload the simulation
        println "$m calling SimulationManager.findSimulation($simulationID)..."
        Simulation reloaded = simMgr.findSimulation(simulationID)
        println "$m calling SimulationManager.findSimulation($simulationID)... Done"

        assert sim.equals(reloaded): "Validating simulation saved properly."
        println "$m New simulation created successfully!"
    }

}
