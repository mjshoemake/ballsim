package baseball

import org.junit.After
import org.junit.Before
import org.junit.Test

class AtBatTests {

    def ballGame = null

    @Before
    void setUp() {
        ballGame = new BallGame()
    }

    @After
    void tearDown() {
        ballGame = null
    }

    @Test
    void testPitchToBatter() {
        def batter = new Batter()
        batter.with {
            name = "Javy Lopez"
            position = "C"
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
        def pitcher = new Pitcher()
        pitcher.with {
            name = "Greg Maddux"
            position = "SP"
            battersRetired = 655
            hits = 225
            walks = 33
            strikeouts = 124
            homers = 24
            hitByPitch = 4
            whip = BigDecimal.valueOf(1.18)
            balks = 0
        }
        def gameBatter = new GameBatter()
        gameBatter.simBatter = new SimBatter(batter: batter)
        def gamePitcher = new GamePitcher()
        gamePitcher.simPitcher = new SimPitcher(pitcher: pitcher)


        for (int i=0; i <= batter.atBats - 1; i++) {
            ballGame.pitchToBatter(gameBatter, gamePitcher, SimStyle.COMBINED)
            println "Sim Batter - Avg: ${gameBatter.battingAvg}  HR: ${gameBatter.homers}  Doubles: ${gameBatter.doubles}  Triples: ${gameBatter.triples}"
        }

        println "Sim Batter - Avg: ${gameBatter.battingAvg}  HR: ${gameBatter.homers}  Doubles: ${gameBatter.doubles}  Triples: ${gameBatter.triples}"
    }
}
