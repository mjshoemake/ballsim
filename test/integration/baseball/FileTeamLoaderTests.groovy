package baseball

import org.junit.After
import org.junit.Before
import org.junit.Test

class FileTeamLoaderTests {

    FileTeamLoader loader = null

    @Before
    void setUp() {
        loader = new FileTeamLoader()
    }

    @After
    void tearDown() {
        loader = null
    }

    @Test
    void testDBAccess() {
        loader.loadTeamFromFile("Phillies", 2003)
    }
}
