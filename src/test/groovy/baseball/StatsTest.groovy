package baseball

import baseball.domain.Player
import baseball.mongo.MongoManager
import baseball.processing.BravesTeamLoader
import org.junit.After
import org.junit.Before
import org.junit.Test

class StatsTest {

    MongoManager mongoDB = null

    @Before
    void setUp() {
        mongoDB = new MongoManager()
        mongoDB.open("ballsim")
    }

    @After
    void tearDown() {
        mongoDB.close()
        mongoDB = null
    }

    @Test
    void testStatistics() {
        def bravesTeamLoader = new BravesTeamLoader(mongoDB)
        def braves2003 = bravesTeamLoader.loadBraves2003()

        braves2003.lineup.each() {
            Player batter = it.simBatter.batter
            println "${batter.nameFirst} ${batter.nameLast} ${batter.battingAvg}  ${batter.fieldingPercentage}  ${batter.sluggingPercentage}  ${batter.onBasePercentage}  ${batter.ops}   ${batter.rank}"
        }
    }
}
