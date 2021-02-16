package baseball.actions

import baseball.domain.Simulation
import baseball.mongo.SimulationManager
import baseball.processing.HttpHistoricalDataManager
import baseball.processing.ScheduleLoader
import mjs.common.utils.LogUtils
import mjs.common.utils.PerformanceMetrics
import org.apache.log4j.Logger
import org.junit.After
import org.junit.Before
import org.junit.Test

class LogSchedule {

    def C = "LogSchedule"
    def ballGame = null
    def loader = null
    def log = Logger.getLogger("Debug");
    def auditLog = Logger.getLogger('Audit')
    def highlightsLog = Logger.getLogger('highlights')
    String idPrefix = "Test"
    String simulationID = null
    SimulationManager simMgr = null
    HttpHistoricalDataManager dataMgr = null

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
        dataMgr = new HttpHistoricalDataManager()
    }

    @After
    void tearDown() {
        ballGame == null
        loader = null
    }

    @Test
    void run() {
        def m = "${C}.run() - "
        PerformanceMetrics perf = new PerformanceMetrics()
        perf.resetMetrics("LogSchedule.run")
        perf.startEvent("LogSchedule.run", "Main")

        // Reload the simulation
        println "$m calling SimulationManager.findSimulation($simulationID)..."
        perf.startEvent("LogSchedule.run", "LoadSim")
        Simulation sim = simMgr.findSimulation(simulationID)
        perf.endEvent("LogSchedule.run", "LoadSim")
        println "$m calling SimulationManager.findSimulation($simulationID)... Done"

        // Play Simulation Game
        println "$m calling SimulationManager.logSchedule()... SimID: $simulationID"
        perf.startEvent("LogSchedule.run", "LogSchedule")
        simMgr.logSchedule(sim)
        perf.endEvent("LogSchedule.run", "LogSchedule")
        println "$m calling SimulationManager.logSchedule()... Done"

        println "$m Log Standings Completed Successfully!"

        perf.endEvent("LogSchedule.run", "Main")
        perf.writeMetricsToLog()

    }

}
