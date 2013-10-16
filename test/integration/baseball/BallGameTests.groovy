package baseball

import org.junit.*

class BallGameTests {

    def ballGame = null

    @Before
    void setUp() {
        ballGame = new BallGame()

        def bravesTeamLoader = new BravesTeamLoader()
        def fileTeamLoader = new FileTeamLoader()
        def braves2003 = bravesTeamLoader.loadBraves2003()
        def phillies2003 = fileTeamLoader.loadTeamFromFile("Phillies", 2003)

        ballGame.with {
            awayTeam = braves2003
            homeTeam = phillies2003
        }
    }

    @After
    void tearDown() {
        ballGame == null
    }

    @Test
    void testPlayGame() {
        ballGame.start()
    }

}
