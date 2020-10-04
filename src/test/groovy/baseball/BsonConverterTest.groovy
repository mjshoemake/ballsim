
package baseball

import baseball.domain.Player
import baseball.domain.FieldingPosition
import baseball.processing.HttpHistoricalDataManager
import mjs.common.utils.BsonConverter
import mjs.common.utils.LogUtils
import org.bson.BsonValue
import org.junit.After
import org.junit.Before
import org.junit.Test

class BsonConverterTest {

    BsonConverter converter = null

    @Before
    void setUp() {
        converter = new BsonConverter()
    }

    @After
    void tearDown() {
        converter = null
    }

    @Test
    void testConversionTeam() {
        HttpHistoricalDataManager dataMgr = new HttpHistoricalDataManager()
        def team_id = "144"
        def year = "1991"
        def roster = dataMgr.get40ManRoster(team_id, year)
        println "Roster loaded (1991 Braves)."

    }
}

