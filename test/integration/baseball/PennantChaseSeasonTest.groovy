package baseball

import org.junit.After
import org.junit.Before
import org.junit.Test

class PennantChaseSeasonTest {

    def league = [:]
    def season

    @Before
    void setUp() {
    }

    @After
    void tearDown() {
    }

    @Test
    void testPennantChase() {
        def division1 = []
        def division2 = []
        def division3 = []
        // Create loaders
        def bravesTeamLoader = new BravesTeamLoader()
        def fileTeamLoader = new FileTeamLoader()

        // Load teams
        division1 << fileTeamLoader.loadTeamFromFile("Ducks", "PennantChase")
        division1 << fileTeamLoader.loadTeamFromFile("First Fire Ants", "PennantChase")
        division1 << fileTeamLoader.loadTeamFromFile("First Hammers", "PennantChase")
        division1 << fileTeamLoader.loadTeamFromFile("Last Blue Jays", "PennantChase")
        division1 << fileTeamLoader.loadTeamFromFile("Last STL 76", "PennantChase")
        division2 << fileTeamLoader.loadTeamFromFile("Dark Knights", "PennantChase")
        division2 << fileTeamLoader.loadTeamFromFile("First Yankees", "PennantChase")
        division2 << fileTeamLoader.loadTeamFromFile("First Twins", "PennantChase")
        division2 << fileTeamLoader.loadTeamFromFile("Last Yankees", "PennantChase")
        division2 << fileTeamLoader.loadTeamFromFile("Last Gas House Gang", "PennantChase")
        division3 << fileTeamLoader.loadTeamFromFile("Devils", "PennantChase")
        league['East'] = division1
        league['Central'] = division2
        league['West'] = division3

        // Schedule
        def scheduleLoader = new ScheduleLoader()
        def schedule = scheduleLoader.loadRoundRobinScheduleFromFile(league)

        season = new Season(league: league, schedule:  schedule)
        season.playSeason()
    }

}
