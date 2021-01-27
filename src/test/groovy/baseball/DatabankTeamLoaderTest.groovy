package baseball

import baseball.mongo.MongoManager
import baseball.processing.DatabankTeamLoader
import mjs.common.model.DatabaseDriver
import mjs.common.utils.ConfigFileLoader
import mjs.common.utils.LogUtils
import org.junit.After
import org.junit.Before
import org.junit.Test

class DatabankTeamLoaderTest {

    def league = [:]
    def season
    MongoManager mongoDB = null
    DatabaseDriver dbDriver = null

    @Before
    void setUp() {
        // MySQL
        Properties dbProps = new ConfigFileLoader("/Config/").loadPropertiesFile("mysql.properties")
        dbDriver = new DatabaseDriver(dbProps)

        // Mongo
        //mongoDB = new MongoManager()
        //mongoDB.open("ballsim")
    }

    @After
    void tearDown() {
        mongoDB?.close()
        mongoDB = null
    }

    @Test
    void testLoad() {
        def division = []
        // Create loaders
        def dbTeamLoader = new DatabankTeamLoader(mongoDB, dbDriver)

        // Load teams
        def team = dbTeamLoader.loadTeamFromMysql("Braves", 2003)
        //LogUtils.println(team, "   ", true)


/*
        def json = groovy.json.JsonOutput.toJson(team)
        println "$json"

        def slurper = new groovy.json.JsonSlurper()
        def result = slurper.parseText(json)

        LogUtils.println(result, "   ", true)
*/
    }

}
