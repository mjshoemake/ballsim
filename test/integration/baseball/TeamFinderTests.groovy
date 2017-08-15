package baseball

import org.junit.After
import org.junit.Before
import org.junit.Test

class TeamFinderTests {

    def teamFinder = null

    @Before
    void setUp() {
        teamFinder = new TeamFinder()
    }

    @After
    void tearDown() {
        teamFinder = null
    }

    @Test
    void testFindYearsAndTeams() {
        def result = teamFinder.findYears()
        result.each() { year ->
            println "   Year: ${year}"
            def teams = teamFinder.findTeamsForYear(year);
            teams.each() { team ->
                println "      Team: ${team}"
            }
        }
    }
}
