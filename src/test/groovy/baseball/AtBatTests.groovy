package baseball

import baseball.domain.BallGame
import baseball.domain.Player
import baseball.domain.PitcherStats
import baseball.domain.GamePitcher
import baseball.domain.SimPitcher
import baseball.domain.GameBatter
import baseball.domain.SimBatter
import baseball.domain.SimStyle
import mjs.common.utils.LogUtils
import org.junit.After
import org.junit.Before
import org.junit.Test

class AtBatTests {

    def ballGame = null

    @Before
    void setUp() {
        if (! LogUtils.isLoggingConfigured()) {
            LogUtils.initializeLogging()
        }
        ballGame = new BallGame()
    }

    @After
    void tearDown() {
        ballGame = null
    }

    @Test
    void testPitchToBatter() {
        def batter = new Player()
        batter.with {
            name = "Javy Lopez"
            //position = "C"
            atBats = 457
            hits = 150
            walks = 33
            strikeouts = 90
            homers = 43
            caughtStealing = 1
            stolenBases = 0
            doubles = 29
            triples = 3
            hitByPitch = 4
        }
        def pitcher = new PitcherStats()
        pitcher.with {
            pitchingBattersRetired = 655
            pitchingHits = 225
            pitchingWalks = 33
            pitchingStrikeouts = 124
            pitchingHomers = 24
            pitchingHitBatter = 4
            pitchingWhip = BigDecimal.valueOf(1.18)
            pitchingBalks = 0
        }
        def gameBatter = new GameBatter()
        gameBatter.simBatter = new SimBatter(batter: batter)
        def gamePitcher = new GamePitcher()
        gamePitcher.simPitcher = new SimPitcher(pitcher: pitcher)


        for (int i=0; i <= batter.atBats - 1; i++) {
            ballGame.pitchToBatter(gameBatter, gamePitcher, 0, SimStyle.COMBINED)
            println "Sim Batter - Avg: ${gameBatter.battingAvg}  HR: ${gameBatter.homers}  Doubles: ${gameBatter.doubles}  Triples: ${gameBatter.triples}"
        }

        println "Sim Batter - Avg: ${gameBatter.battingAvg}  HR: ${gameBatter.homers}  Doubles: ${gameBatter.doubles}  Triples: ${gameBatter.triples}"
        println "Sim Pitcher - Faced: ${gamePitcher.battersFaced}  Retired: ${gamePitcher.battersRetired}  ERA: ${gamePitcher.getEra()}  Opp. Avg: ${gamePitcher.getOppAvg()}"
    }
}
