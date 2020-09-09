package baseball.processing

import baseball.domain.Batter
import baseball.domain.PitcherStats
import baseball.domain.GameBatter
import baseball.domain.GamePitcher
import baseball.domain.SimBatter
import baseball.domain.SimPitcher
import baseball.mongo.MongoManager
import mjs.common.model.DatabaseDriver
import mjs.common.model.DataManager

import org.apache.log4j.Logger

/**
 * This class is built to load team data from a MySQL database downloaded from
 * baseball-databank.org.
 */
class DatabankTeamLoader extends AbstractTeamLoader {

    /**
     * The Log4J logger used by this object.
     */
    protected Logger log = Logger.getLogger("Core");

    final DatabaseDriver dbDriver

    DatabankTeamLoader(MongoManager mongoDB, DatabaseDriver driver) {
        super(mongoDB)
        this.dbDriver = driver
    }

    def loadTeamFromMysql(def teamName, year) throws Exception {
        def team = [:]
        def batters = []
        def pitchers = []
        def lineup = []
        def rotation = []
        def bullpen = []
        def tempBatters
        def tempPitchers

        year += ""
        println "Loading $teamName..."
        DataManager dataManager = new DataManager(dbDriver)
        String sql = """select t.yearID year, t.teamID, b.playerID, m.nameFirst, m.nameLast, m.nameNick,
                        m.birthYear, m.bats armBats, m.throws as armThrows, b.G games,
                        f.POS position, b.AB atBats, b.BB walks, b.SO strikeouts, b.SF sacrificeFlies,
                        b.H hits, b.doubles, b.triples, b.HR homers, f.PO putouts,
                        f.A assists, f.E errors, b.SB stolenbases, b.CS caughtstealing,
                        f.CS catcherCaught, f.SB catcherSteals
                        from teams t, batting b, master m, fielding f
                        where t.yearID=${year}
                          and b.yearID=t.yearID
                          and b.yearID=f.yearID
                          and t.teamname=\"${teamName}\"
                          and t.teamID = b.teamID
                          and f.teamID = b.teamID
                          and m.playerID = b.playerID
                          and f.playerID = b.playerID"""
        String mappingFile = "/mapping/BatterMap.xml"
        try {
            dataManager.open()
            tempBatters = dataManager.loadList(sql, Batter.class, mappingFile, 1000, 1000)
        } finally {
            dataManager.close()
        }

        // Create Sim/Game wrapper objects.
        int num = 1
        tempBatters.entireList.each() {
            def simStats = new SimBatter(batter: it)
            simStats.nameLast = it.nameLast
            simStats.nameFirst = it.nameFirst
            if (num <= 8) {
                simStats.battingPos = num
            }
            def gameBatter = new GameBatter(simBatter: simStats)
            gameBatter.nameLast = it.nameLast
            gameBatter.nameFirst = it.nameFirst
            batters << gameBatter
            num++
        }

        // Set the batting order (required to play a game)
        def hittingStaff = createLineup(batters)

        sql = """select t.yearID year, t.teamID, p.playerID, m.nameFirst, m.nameLast, m.nameNick,
                        m.birthYear, m.bats armBats, m.throws armThrows, p.G games, p.R runs,
                        p.GS gamesStarted, p.BB walks, p.SO strikeouts, p.H hits, p.SV saves,
                        p.HR homers, p.IPouts battersRetired, p.HBP hitByPitch, p.BK balks, p.WP wildPitch
                 from teams t, pitching p, master m
                 where t.yearID=${year}
                   and p.yearID=t.yearID
                   and t.teamname=\"${teamName}\"
                   and t.teamID = p.teamID
                   and m.playerID = p.playerID"""

        mappingFile = "/mapping/PitcherMap.xml"
        try {
            dataManager.open()
            tempPitchers = dataManager.loadList(sql, PitcherStats.class, mappingFile, 1000, 1000)
        } finally {
            dataManager.close()
        }

        // Create Sim/Game wrapper objects.
        num = 1
        tempPitchers.entireList.each() {
            def simStats = new SimPitcher(pitcher: it)
            simStats.nameLast = it.nameLast
            simStats.nameFirst = it.nameFirst
            if (num <= 5) {
                simStats.orderPos = num
            }
            def gamePitcher = new GamePitcher(simPitcher: simStats)
            gamePitcher.nameLast = it.nameLast
            gamePitcher.nameFirst = it.nameFirst
            pitchers << gamePitcher
            num++
        }

        // Set the rotation order
        def pitchingStaff = createPitchingStaff(pitchers)

        println "Loading $teamName...  DONE."

        team["bench"] = hittingStaff.bench
        team["originalBench"] = hittingStaff.bench.clone()
        team["battingpitchers"] = hittingStaff.pitchers
        //team["batters"] = batters
        team["originalLineup"] = hittingStaff.lineup.clone()
        team["lineup"] = hittingStaff.lineup
        team["rotation"] = pitchingStaff.rotation
        team["bullpen"] = pitchingStaff.bullpen
        team["nextStarter"] = 0
        team["wins"] = 0
        team["losses"] = 0
        team["winDiff"] = 0
        team["teamName"] = "$teamName $year"
        team["starterIndex"] = 0
        team
    }

}
