/*
package baseball

import baseball.domain.Batter

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
    void testConversion() {
        Batter batter = new Batter(teamID: "1", playerID: "5", nameFirst: "Bob", nameLast: "Horner", atBats: 57, hits: 19, homers: 5, caughtStealing: 0, doubles: 2, hitByPitch: 1, name: "Bob Horner", position: "3B", stolenBases: 0, strikeouts: 6, teamName: "Braves", year: 1982, triples: 2, walks: 3)
        BsonValue result = converter.objectToBson(batter)
        LogUtils.println(result)
    }
}
*/
