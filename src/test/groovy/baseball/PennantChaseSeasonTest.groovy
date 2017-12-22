package baseball

import baseball.mongo.MongoManager
import org.junit.After
import org.junit.Before
import org.junit.Test
import baseball.domain.Season
import baseball.processing.BravesTeamLoader
import baseball.processing.FileTeamLoader
import baseball.processing.ScheduleLoader

class PennantChaseSeasonTest {

    def league = [:]
    def season

    MongoManager mongoDB = new MongoManager()

    @Before
    void setUp() {
        mongoDB.open("ballsim")
    }

    @After
    void tearDown() {
        mongoDB.close()
    }

    @Test
    void testPennantChase() {
        def division1 = []
        def division2 = []
        def division3 = []
        // Create loaders
        def bravesTeamLoader = new BravesTeamLoader(mongoDB)
        def fileTeamLoader = new FileTeamLoader(mongoDB)

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
        division3 << bravesTeamLoader.loadBraves2003()
        division3 << fileTeamLoader.loadTeamFromFile("Phillies", 2003)
        division3 << fileTeamLoader.loadTeamFromFile("Mets", 2003)
        division3 << fileTeamLoader.loadTeamFromFile("Expos", 2003)
        division3 << fileTeamLoader.loadTeamFromFile("Marlins", 2003)

        league['East'] = division1
        league['Central'] = division2
        league['West'] = division3

        // Schedule
        def scheduleLoader = new ScheduleLoader()
        def schedule = scheduleLoader.loadRoundRobinScheduleFromFile(league)

        season = new Season(league, schedule)
        season.playSeason(10)
    }

}
