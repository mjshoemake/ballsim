package baseball.processing

import baseball.domain.Batter
import baseball.domain.GameBatter
import baseball.domain.GamePitcher
import baseball.domain.HittingStaff
import baseball.domain.Pitcher
import baseball.domain.PitchingStaff
import baseball.domain.SimBatter
import baseball.domain.SimPitcher
import baseball.mongo.MongoManager
import org.apache.log4j.Logger

abstract class AbstractTeamLoader {

    /**
     * The Log4J logger used by this object.
     */
    protected Logger log = Logger.getLogger("Core");

    final MongoManager mongoDB

    AbstractTeamLoader(MongoManager mongoMgr) {
        this.mongoDB = mongoMgr
    }

    def saveBatter(String pYear,
                   String pTeamID,
                   String pNameFirst,
                   String pNameLast,
                   Map pPosition,
                   int pAtBats,
                   int pHits,
                   int pDoubles,
                   int pTriples,
                   int pHomers,
                   int pStolenBases,
                   int pCaughtStealing,
                   int pWalks,
                   int pStrikeouts,
                   int pHitByPitch,
                   String pPlayerID,
                   int pBirthYear,
                   String pArmBats,
                   String pArmThrows,
                   int pGames,
                   int pPutouts,
                   int pAssists,
                   int pErrors,
                   int pCatcherSteals,
                   int pCatcherCaught) {
        def existing = findBatter(pYear, pTeamID, pNameFirst, pNameLast)
        if (! existing) {
            // Not found.  Add.
            def batter = new Batter()
            def simStats = new SimBatter(batter: batter)

            batter.with {
                year = pYear
                playerID = pPlayerID
                teamID = pTeamID
                nameFirst = pNameFirst
                nameLast = pNameLast
                position = pPosition
                atBats = pAtBats
                hits = pHits
                walks = pWalks
                strikeouts = pStrikeouts
                homers = pHomers
                caughtStealing = pCaughtStealing
                stolenBases = pStolenBases
                doubles = pDoubles
                triples = pTriples
                hitByPitch = pHitByPitch
                birthYear = pBirthYear
                armBats = pArmBats
                armThrows = pArmThrows
                games = pGames
                putouts = pPutouts
                assists = pAssists
                errors = pErrors
                catcherSteals = pCatcherSteals
                catcherCaught = pCatcherCaught
            }
/*
        int birthYear
        String armBats
        String armThrows
        int games = 0
        int putouts = 0
        int assists = 0
        int errors = 0
        int catcherSteals = 0
        int catcherCaught = 0
*/

            mongoDB.addToCollection("batter", batter)
            new GameBatter(simBatter: simStats)
        } else {
            def simStats = new SimBatter(batter: existing)
            new GameBatter(simBatter: simStats)
        }
    }

    def savePitcher(String pYear,
                    String pTeamName,
                    String pNameFirst,
                    String pNameLast,
                    String pPosition,
                    int pOrderPos,
                    int pBattersRetired,
                    int pHits,
                    int pHomers,
                    int pWalks,
                    int pStrikeouts,
                    int pHitByPitch,
                    def pWhip,
                    int pBalks) {
        def existing = findPitcher(pYear, pTeamName, pNameFirst, pNameLast)
        if (! existing) {
            // Not found.  Add.
            def pitcher = new Pitcher()
            def simStats = new SimPitcher(pitcher: pitcher)

            pitcher.with {
                year = pYear
                teamID = pTeamName
                nameFirst = pNameFirst
                nameLast = pNameLast
                position = pPosition
                orderPos = pOrderPos
                battersRetired = pBattersRetired
                walks = pWalks
                strikeouts = pStrikeouts
                hits = pHits
                homers = pHomers
                hitByPitch = pHitByPitch
                whip = pWhip
                balks = pBalks
            }

            mongoDB.addToCollection("pitcher", pitcher)
            new GamePitcher(simPitcher: simStats)
        } else {
            def simStats = new SimPitcher(pitcher: existing)
            new GamePitcher(simPitcher: simStats)
        }
    }

    def findBatter(def pYear, String pTeamName, String pNameFirst, String pNameLast) {
        def newBatterList = mongoDB.find("batter", [year:pYear, teamName: pTeamName, nameFirst: pNameFirst, nameLast: pNameLast])
        if (newBatterList?.isEmpty()) {
            null
        } else {
            newBatterList[0]
        }
    }

    def findPitcher(def pYear, String pTeamName, String pNameFirst, String pNameLast) {
        def newPitcherList = mongoDB.find("pitcher", [year:pYear, teamName: pTeamName, nameFirst: pNameFirst, nameLast: pNameLast])
        if (newPitcherList.isEmpty()) {
            null
        } else {
            newPitcherList[0]
        }
    }

    def fixTeamName(String teamName) {
        def result = teamName.replaceAll(' ', '')
        result = result.replaceAll('-', '')
        result
    }
    def createPitchingStaff(List pitchers) {
        def pitchingStaff = new PitchingStaff()
        pitchers.each {item -> item.sortBy = "gs"}
        Collections.sort(pitchers)

        for (int i=0; i <= 4; i++) {
            pitchingStaff.rotation << pitchers[i]
        }
        for (int i=5; i <= pitchers.size()-1; i++) {
            pitchingStaff.bullpen << pitchers[i]
        }
        log.debug("Pitching Staff:  Rotation- ${pitchingStaff.rotation.size()}   Bullpen- ${pitchingStaff.bullpen.size()}")
        log.debug("Rotation:")
        pitchingStaff.rotation.each {
            log.debug("   ${it.simPitcher.pitcher.nameFirst} ${it.simPitcher.pitcher.nameLast} (${it.simPitcher.pitcher.era})")
        }
        log.debug("Bullpen:")
        pitchingStaff.bullpen.each {
            log.debug("   ${it.simPitcher.pitcher.nameFirst} ${it.simPitcher.pitcher.nameLast} (${it.simPitcher.pitcher.era})")
        }
        pitchingStaff
    }

    def createLineup(List batters) {
        def hittingStaff = new HittingStaff()
        def selected = []
        def lineup = hittingStaff.lineup
        def sortedBatters = batters.clone()
        Collections.sort(sortedBatters)
        def done = []
        def tentative = []
        def P = []
        def C = []
        def first = []
        def second = []
        def third = []
        def shortstop = []
        def LF = []
        def RF = []
        def CF = []
        def toProcess = [C, first, second, third, shortstop, LF, RF, CF]
        sortedBatters.each() { nextBatter ->
            Map positions = nextBatter.simBatter.batter.position
            positions.keySet().each() { next ->
                switch (next) {
                    case "P":
                        P << nextBatter
                        nextBatter.position = "P"
                        break
                    case "C":
                        C << nextBatter
                        break
                    case "1B":
                        first << nextBatter
                        break
                    case "2B":
                        second << nextBatter
                        break
                    case "3B":
                        third << nextBatter
                        break
                    case "SS":
                        shortstop << nextBatter
                        break
                    case "LF":
                        LF << nextBatter
                        break
                    case "RF":
                        RF << nextBatter
                        break
                    case "CF":
                        CF << nextBatter
                        break
                }
            }
        }

        // Auto-select if only one option.
        toProcess.clone().each() { nextList ->
            if (nextList.size() == 1) {
                sortedBatters.remove(nextList[0])
                done << nextList
                toProcess.remove(nextList)
            }
        }

        // Process remaining
        toProcess.clone().each() { nextList ->
            nextList.clone().each() { batter ->
                if (! sortedBatters.contains(batter)) {
                    nextList.remove(batter)
                }

            }
            if (nextList.size() == 1) {
                sortedBatters.remove(nextList[0])
                done << nextList
                toProcess.remove(nextList)
            } else if (nextList.size() == 0) {
                throw new Exception("No remaining options for position list.  No batters available.")
            } else {
                // Go with first person tentatively.
                sortedBatters.remove(nextList[0])
                tentative << nextList
                toProcess.remove(nextList)
            }
        }

        if (toProcess.size() == 0) {
            // C
            C[0].position = "C"
            selected << C[0]
            Batter batter = C[0].simBatter.batter
            log.debug("Assigned ${batter.nameFirst} ${batter.nameLast} -> C.")

            // 1B
            first[0].position = "1B"
            selected << first[0]
            batter = first[0].simBatter.batter
            log.debug("Assigned ${batter.nameFirst} ${batter.nameLast} -> 1B.")

            // 2B
            second[0].position = "2B"
            selected << second[0]
            batter = second[0].simBatter.batter
            log.debug("Assigned ${batter.nameFirst} ${batter.nameLast} -> 2B.")

            // 3B
            third[0].position = "3B"
            selected << third[0]
            batter = third[0].simBatter.batter
            log.debug("Assigned ${batter.nameFirst} ${batter.nameLast} -> 3B.")

            // SS
            shortstop[0].position = "SS"
            selected << shortstop[0]
            batter = shortstop[0].simBatter.batter
            log.debug("Assigned ${batter.nameFirst} ${batter.nameLast} -> SS.")

            // LF
            LF[0].position = "LF"
            selected << LF[0]
            batter = LF[0].simBatter.batter
            log.debug("Assigned ${batter.nameFirst} ${batter.nameLast} -> LF.")

            // RF
            RF[0].position = "RF"
            selected << RF[0]
            batter = RF[0].simBatter.batter
            log.debug("Assigned ${batter.nameFirst} ${batter.nameLast} -> RF.")

            // CF
            CF[0].position = "CF"
            selected << CF[0]
            batter = CF[0].simBatter.batter
            log.debug("Assigned ${batter.nameFirst} ${batter.nameLast} -> CF.")

            log.debug("Assigning hitting slots...")
            def power = []

            selected.each {item -> item.sortBy = "homers"}
            Collections.sort(selected)

            // Pick out the two players with the best homers (4 and 5 in the order)
            power << selected[0]
            selected.remove(0)
            power << selected[0]
            selected.remove(0)

            selected.each {item -> item.sortBy = "obp"}
            Collections.sort(selected)

            // Pick out the three players with the best OBP (1, 2, and 3 in the order)
            lineup << selected[0]
            selected.remove(0)
            lineup << selected[0]
            selected.remove(0)
            lineup << selected[0]
            selected.remove(0)

            // Sort remaining players by OPS
            selected.each() {item -> item.sortBy = "ops"}
            Collections.sort(selected)

            // Add power hitters to lineup
            power.each {item -> lineup << item}
            // Add remaining players to lineup
            selected.each {item -> lineup << item}

            // Split bench and pitchers
            log.debug("Hitters remaining: ${sortedBatters.size()}   Original: ${batters.size()}")
            sortedBatters.each {
                if (it.position == "P") {
                    hittingStaff.pitchers << it
                } else {
                    hittingStaff.bench << it
                }
            }
            log.debug("Bench: ${hittingStaff.bench.size()}   Pitchers: ${hittingStaff.pitchers.size()}")


            int index = 1
            log.debug("Final Lineup:")
            lineup.each {
                log.debug("   $index: ${it.simBatter.batter.nameFirst} ${it.simBatter.batter.nameLast} (${it.position})")
                index++
            }

            hittingStaff
        } else {
            throw new Exception("The toProcess list is not empty and it should be.")
        }
    }

}
