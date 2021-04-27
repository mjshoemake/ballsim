package baseball

import baseball.domain.BidirectionalHashMap
import baseball.domain.Simulation
import baseball.mongo.SimulationManager
import baseball.processing.HttpHistoricalDataManager
import baseball.processing.ScheduleLoader
import mjs.common.utils.LogUtils
import org.apache.log4j.Logger
import org.junit.After
import org.junit.Before
import org.junit.Test

class BidirectionalHashMapTests {

    def C = "BidirectionalHashMapTests"
    def log = Logger.getLogger("Debug");
    def auditLog = Logger.getLogger('Audit')
    def highlightsLog = Logger.getLogger('highlights')

    @Before
    void setUp() {
    }

    @After
    void tearDown() {
    }

    @Test
    void testMap() {
        BidirectionalHashMap map = new BidirectionalHashMap()
        map["abc"] = 5
        map["efg"] = 9
        map["hij"] = 7
        map["klm"] = 8
        map["nop"] = 4
        map["qrs"] = 3
        map["tuv"] = 6
        map["wxy"] = 2

        assert map.getKeyForValue(2).equals("wxy")
        assert map.getKeyForValue(7).equals("hij")

        map["hij"] = 1
        assert map.getKeyForValue(7) == null
    }


}
