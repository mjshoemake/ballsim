
package baseball.exec

import baseball.domain.Batter
import baseball.processing.FileTeamLoader
import baseball.mongo.MongoManager
import mjs.common.utils.LogUtils
import org.bson.types.ObjectId

//import org.junit.After
//import org.junit.Before
//import org.junit.Test

class FileTeamLoaderTest {

    static void main(String[] args) {
        MongoManager mongoDB = new MongoManager()
        mongoDB.open("ballsim")

        Batter batter = new Batter(atBats: 57, hits: 19, homers: 5, caughtStealing: 0, doubles: 2, hitByPitch: 1, name: "Bob Horner", position: "3B", stolenBases: 0, strikeouts: 6, teamName: "Braves", year: 1982, triples: 2, walks: 3)

        // Delete all
        mongoDB.deleteMany("batter", [position:"3B"])

        // Add one.
        ObjectId id = mongoDB.addToCollection("batter", batter)

        // Count batters.
        long count = mongoDB.getCount("batter")
        println "Count: $count   ID: ${id.toString()}"

        // Re-add batter.
        def newBatterList = mongoDB.find("batter", [position:"3B"])
        def mapBatter = newBatterList[0]
        id = mongoDB.addToCollection("batter", mapBatter)

        // Search for batters.
        def finalBatterList = mongoDB.find("batter", [position:"3B"])
        println "Final Result:"

        // Log batters (both should be Batter objects with valid ObjectId).
        LogUtils.println(finalBatterList, "   ", true)
        FileTeamLoader loader = new FileTeamLoader(mongoDB)
        //loader.loadTeamFromFile("Phillies", 2003)
    }
}