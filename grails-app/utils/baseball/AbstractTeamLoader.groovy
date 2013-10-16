package baseball

abstract class AbstractTeamLoader {

     def saveBatter(String pYear,
                   String pTeamName,
                   String pName,
                   String pPosition,
                   int pBattingPos,
                   int pAtBats,
                   int pHits,
                   int pDoubles,
                   int pTriples,
                   int pHomers,
                   int pStolenBases,
                   int pCaughtStealing,
                   int pWalks,
                   int pStrikeouts,
                   int pHitByPitch) {
        def existing = findBatter(pYear, pTeamName, pName)
        if (! existing) {
            // Not found.  Add.
            def batter = new Batter()
            def simStats = new SimBatter(batter: batter)
            simStats.battingPos = pBattingPos
            batter.addToSimBatter(simStats)

            batter.with {
                year = pYear
                teamName = pTeamName
                name = pName
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
            }

            SimBatter.withTransaction {
                batter.save(failOnError: true)
                simStats.save(failOnError: true)
            }

            new GameBatter(simBatter: simStats)
        } else {
            def simStats = new SimBatter(batter: existing)
            simStats.battingPos = pBattingPos
            existing.addToSimBatter(simStats)
            SimBatter.withTransaction {
                simStats.save(failOnError: true)
                batter.save(failOnError: true)
            }

            new GameBatter(simBatter: simStats)
        }
    }

    def savePitcher(String pYear,
                    String pTeamName,
                    String pName,
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
        def existing = findPitcher(pYear, pTeamName, pName)
        if (! existing) {
            // Not found.  Add.
            def pitcher = new Pitcher()
            def simStats = new SimPitcher(pitcher: pitcher)
            pitcher.addToSimPitcher(simStats)

            pitcher.with {
                year = pYear
                teamName = pTeamName
                name = pName
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

            SimPitcher.withTransaction {
                pitcher.save(failOnError: true)
                simStats.save(failOnError: true)
            }

            new GamePitcher(simPitcher: simStats)
        } else {
            def simStats = new SimPitcher(pitcher: existing)
            existing.addToSimPitcher(simStats)
            SimPitcher.withTransaction {
                simStats.save(failOnError: true)
                pitcher.save(failOnError: true)
            }

            new GamePitcher(simPitcher: simStats)
        }
    }

    def findBatter(def pYear, String pTeamName, String pName) {
        pYear += ""
        def c = Batter.createCriteria()
        def results = c.list {
            eq("year", pYear)
            eq("teamName", pTeamName)
            eq("name", pName)
            maxResults(1)
        }
        if (results.isEmpty()) {
            null
        } else {
            results.first()
        }
    }

    def findPitcher(def pYear, String pTeamName, String pName) {
        pYear += ""
        def c = Pitcher.createCriteria()
        def results = c.list {
            eq("year", pYear)
            eq("teamName", pTeamName)
            eq("name", pName)
            maxResults(1)
        }
        if (results.isEmpty()) {
            null
        } else {
            results.first()
        }
    }

    def fixTeamName(String teamName) {
        def result = teamName.replaceAll(' ', '')
        result = result.replaceAll('-', '')
        result
    }
}
