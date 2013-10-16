package baseball

import static org.junit.Assert.*
import org.junit.*

class BravesTeamLoaderTests {

    BravesTeamLoader loader = null

    @Before
    void setUp() {
        loader = new BravesTeamLoader()
    }

    @After
    void tearDown() {
        loader = null
    }

    @Test
    void testDBAccess() {
        loader.loadBraves2003()
        def marcusGiles = loader.findBatter(2003, "Braves", "Marcus Giles")
        println "Name: ${marcusGiles.name}"
    }
}
