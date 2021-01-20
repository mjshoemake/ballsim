package baseball

import baseball.mongo.MongoManager
import baseball.processing.DatabankTeamLoader

import mjs.common.utils.ConfigFileLoader
import mjs.common.model.DatabaseDriver
import org.junit.After
import org.junit.Before
import org.junit.Test
import baseball.domain.Simulation
import baseball.processing.ScheduleLoader

class SeasonTests {

    def league = [:]
    def season
    MongoManager mongoDB = null
    DatabaseDriver dbDriver = null

    @Before
    void setUp() {
        // MySQL
        Properties dbProps = new ConfigFileLoader("/Users/Dad/Config/").loadPropertiesFile("mysql.properties")
        dbDriver = new DatabaseDriver(dbProps)

        // Mongo
        mongoDB = new MongoManager()
        mongoDB.open("ballsim")
    }

    @After
    void tearDown() {
        mongoDB.close()
        mongoDB = null
    }

    @Test
    void test2003Season() {
        def division1 = []
        def division2 = []
        def division3 = []
        def division4 = []
        def division5 = []
        def division6 = []

        def dbTeamLoader = new DatabankTeamLoader(mongoDB, dbDriver)
        division1 << dbTeamLoader.loadTeamFromMysql("Braves", 2003)
        division1 << dbTeamLoader.loadTeamFromMysql("Phillies", 2003)
        division1 << dbTeamLoader.loadTeamFromMysql("Mets", 2003)
        division1 << dbTeamLoader.loadTeamFromMysql("Expos", 2003)
        division1 << dbTeamLoader.loadTeamFromMysql("Marlins", 2003)
        division2 << dbTeamLoader.loadTeamFromMysql("Cubs", 2003)
        division2 << dbTeamLoader.loadTeamFromMysql("Cardinals", 2003)
        division2 << dbTeamLoader.loadTeamFromMysql("Astros", 2003)
        division2 << dbTeamLoader.loadTeamFromMysql("Pirates", 2003)
        division2 << dbTeamLoader.loadTeamFromMysql("Brewers", 2003)
        division2 << dbTeamLoader.loadTeamFromMysql("Reds", 2003)
        division3 << dbTeamLoader.loadTeamFromMysql("Diamondbacks", 2003)
        division3 << dbTeamLoader.loadTeamFromMysql("Giants", 2003)
        division3 << dbTeamLoader.loadTeamFromMysql("Padres", 2003)
        division3 << dbTeamLoader.loadTeamFromMysql("Dodgers", 2003)
        division3 << dbTeamLoader.loadTeamFromMysql("Rockies", 2003)
        division4 << dbTeamLoader.loadTeamFromMysql("Yankees", 2003)
        division4 << dbTeamLoader.loadTeamFromMysql("Red Sox", 2003)
        division4 << dbTeamLoader.loadTeamFromMysql("Blue Jays", 2003)
        division4 << dbTeamLoader.loadTeamFromMysql("Orioles", 2003)
        division4 << dbTeamLoader.loadTeamFromMysql("Rays", 2003)
        division5 << dbTeamLoader.loadTeamFromMysql("Twins", 2003)
        division5 << dbTeamLoader.loadTeamFromMysql("White Sox", 2003)
        division5 << dbTeamLoader.loadTeamFromMysql("Orioles", 2003)
        division5 << dbTeamLoader.loadTeamFromMysql("Indians", 2003)
        division5 << dbTeamLoader.loadTeamFromMysql("Tigers", 2003)
        division6 << dbTeamLoader.loadTeamFromMysql("Athletics", 2003)
        division6 << dbTeamLoader.loadTeamFromMysql("Mariners", 2003)
        division6 << dbTeamLoader.loadTeamFromMysql("Rangers", 2003)
        division6 << dbTeamLoader.loadTeamFromMysql("Angels", 2003)

        league['NL East'] = division1
        league['NL Central'] = division2
        league['NL West'] = division3
        league['AL East'] = division4
        league['AL Central'] = division5
        league['AL West'] = division6

        // Schedule
        def scheduleLoader = new ScheduleLoader()
        def schedule = scheduleLoader.loadRoundRobinScheduleFromFile(league)

        season = new Simulation(league, schedule)
        season.playSeason(5)
    }

}
