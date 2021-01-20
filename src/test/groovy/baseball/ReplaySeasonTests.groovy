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

    def ballGame = null
    def loader = null
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
            simulationID = Simulation.generateSimulationID(idPrefix)
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
        deleteAllSimulations()
        testStartSimulation()
        //testPlaySimulationGame()
    }

    void deleteAllSimulations() {
        long deletedCount = simMgr.deleteAllSimulations()
        println "Deleted simulations: $deletedCount"
    }

    void testStartSimulation() {
        // 2003 skipped... could only find 8 batters for lineup.
        def year = "1991"
        def teamMap = dataMgr.getTeamMapForSeason(year)

        // Start simulation
        println "SimulationManager.startNewSimulation()... SimID: $simulationID"
        Simulation sim = simMgr.startNewSimulation(teamMap, idPrefix, "Test Sim", simulationID)
        println "SimulationManager.startNewSimulation()... Done"

        sim.logDivisions(auditLog)

        // Reload the simulation
        println "SimulationManager.findSimulation($sim.simulationID)..."
        Simulation reloaded = simMgr.findSimulation(sim.simulationID)
        println "SimulationManager.findSimulation($sim.simulationID)... Done"
        println ""
        println "RELOADED!!!!!!!!!!!!!"
        println ""
        //LogUtils.println(reloaded, "   ", true)

        sim.logDivisions(auditLog)

        // Play Simulation Game
        println "SimulationManager.playSimGame()... SimID: $simulationID"
        simMgr.playSimGame(sim)
        println "SimulationManager.playSimGame()... Done"

        // Delete the Simulation
        println "SimulationManager.deleteSimulation()... SimID: $simulationID"
        simMgr.deleteSimulation(simulationID)
        println "SimulationManager.deleteSimulation()... Done"

    }

}
