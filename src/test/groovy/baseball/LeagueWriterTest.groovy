package baseball

import baseball.domain.*
import baseball.processing.LeagueWriter
import org.junit.After
import org.junit.Before
import org.junit.Test

class LeagueWriterTest {

    def leagueWriter = null

    @Before
    void setUp() {
       leagueWriter = new LeagueWriter()
    }

    @After
    void tearDown() {
        leagueWriter = null
    }

    @Test
    void testGetLeague() {
        String
    }
}
