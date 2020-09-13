package baseball

import baseball.domain.Team
import baseball.processing.HttpHistoricalDataManager
import mjs.common.utils.HttpClient
import groovy.json.JsonSlurper
import org.junit.After
import org.junit.Before
import org.junit.Test

class GetTeamsHttpTests {

    @Before
    void setUp() {
    }

    @After
    void tearDown() {
    }

    @Test
    void testGetTeamForSeason() {
        HttpHistoricalDataManager dataMgr = new HttpHistoricalDataManager()
        String year = "1995"
        def teams = dataMgr.getTeamsForSeason(year)
        def teamList = []

        teams.each { next ->
            Team team = new Team()
            teamList << team.load(next).printTeam()

            // Get data for team.
            def teamRoster = dataMgr.get40ManRoster(team.team_id, year)
            teamRoster.each { person ->
                if (team.name_abbrev == "ATL") {
                    println("Name: ${person.name_first_last}, Position: ${person.primary_position} ")
                }
            }
        }
    }
}