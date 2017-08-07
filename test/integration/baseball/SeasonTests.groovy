package baseball

import org.junit.After
import org.junit.Before
import org.junit.Test

class SeasonTests {

    def league = [:]
    def season

    @Before
    void setUp() {
    }

    @After
    void tearDown() {
    }

    @Test
    void test2003Season() {
        def division = []
        // Create loaders
        def bravesTeamLoader = new BravesTeamLoader()
        def fileTeamLoader = new FileTeamLoader()

        // Load teams
        //division << bravesTeamLoader.loadBraves2003()
        division << fileTeamLoader.loadTeamFromFile("Phillies", 2003)
        division << fileTeamLoader.loadTeamFromFile("Mets", 2003)
        division << fileTeamLoader.loadTeamFromFile("Expos", 2003)
        division << fileTeamLoader.loadTeamFromFile("Marlins", 2003)
        league['East'] = division

        // Schedule
        def scheduleLoader = new ScheduleLoader()
        def schedule = scheduleLoader.loadRoundRobinScheduleFromFile(league)

        season = new Season(league: league, schedule:  schedule)
        season.playSeason()
    }

}
