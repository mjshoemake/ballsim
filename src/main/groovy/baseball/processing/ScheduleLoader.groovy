package baseball.processing

import baseball.domain.ScheduledGame
import baseball.domain.ScheduledRound
import baseball.domain.ScheduledSeason
import mjs.common.utils.LogUtils
import org.apache.log4j.Logger

class ScheduleLoader {

    def Logger log = Logger.getLogger("Core");
    def highlightsLog = Logger.getLogger('highlights')
    /*
    def loadRoundRobinScheduleFromFile(def league) {
        return loadRoundRobinScheduleFromFile(league, false)
    }

    def loadRoundRobinScheduleFromFile(def league, boolean oneGameOnly) {
        def schedule = []
        def teamList = []
        int gameCount = 0

        def it = league.keySet().iterator()
        while (it.hasNext()) {
            def div = league[it.next()]
            div.each { next ->
                teamList << next
            }
        }
        int numTeams = teamList.size()

        def scheduleText = this.class.getResource("/schedules/${numTeams}-team-schedule.txt")?.text
        def gameNum = 0
        scheduleText.eachLine {
            def columns = it.split("\t")
            if (! (columns[0] == "Round" && columns[1] == "Game #" && columns[2] == "Home Team")) {
                def round = columns[0]
                def seriesNum = columns[1]
                def homeTeam = (Integer.parseInt(columns[2].substring(5)) - 1)
                def awayTeam = (Integer.parseInt(columns[3].substring(5)) - 1)
                for (int i=0; i <= 2; i++) {
                    gameNum++
                    def game = [:]
                    game.round = round
                    game.seriesNum = seriesNum
                    game.gameNum = gameNum
                    game.homeTeamIndex = homeTeam
                    game.awayTeamIndex = awayTeam
                    gameCount++
                    if (! oneGameOnly || gameCount == 1) {
                        schedule << game
                    }
                }
            }
        }
        schedule
    }
*/
    def loadScheduleFromFile(String scheduleName, int teamCount) {
        println "loadScheduleFromFile() START"
        int gameCount = 0
        boolean trimTeamName = false

        def scheduleText = this.class.getResource("/schedules/${scheduleName}-team-schedule.txt")?.text
        if (! scheduleText) {
            trimTeamName = true
            scheduleText = this.class.getResource("/schedules/${teamCount}-team-schedule.txt")?.text
        }
        ScheduledSeason scheduledSeason = new ScheduledSeason()

        int roundNum = 0
        int gameNum = 0
        int lastRoundNum = 1
        ScheduledRound currentRound = new ScheduledRound(1)

        scheduleText.eachLine() {
            def columns = it.split("\t")

            if ((! (columns[0] == "Round" && columns[1] == "Game #" && columns[2] == "Home Team")) && (columns[0] != "")) {
                roundNum = Integer.parseInt(columns[0])
                gameNum = Integer.parseInt(columns[1])
                //println("$roundNum - $gameNum")
                def homeTeam
                def awayTeam
                if (trimTeamName) {
                    homeTeam = (Integer.parseInt(columns[2].substring(5)) - 1)
                    awayTeam = (Integer.parseInt(columns[3].substring(5)) - 1)
                } else {
                    homeTeam = columns[2]
                    awayTeam = columns[3]
                }
                if (roundNum != lastRoundNum) {
                    //println "Adding round: ${currentRound.roundNum}  home: ${currentRound.games[0].homeTeam}  away: ${currentRound.games[0].awayTeam}"
                    scheduledSeason.addRound(currentRound)
                    for (int i=1; i <= roundNum - lastRoundNum - 1; i++) {
                        // Complete existing round.
                        currentRound = currentRound.copy()
                        scheduledSeason.addRound(currentRound)
                        //println "Adding round: ${currentRound.roundNum} Size: ${scheduledSeason.rounds.size()}  home: ${currentRound.games[0].homeTeam}  away: ${currentRound.games[0].awayTeam}"
                    }
                    lastRoundNum = roundNum
                    currentRound = new ScheduledRound(roundNum)
                }
                def game = new ScheduledGame()
                game.roundNum = roundNum
                game.gameNum = gameNum++
                game.homeTeam = homeTeam
                game.awayTeam = awayTeam
                currentRound.addGame(game)
            }
        }

        //LogUtils.info(log, scheduledSeason, "   ", true)
        //LogUtils.println(scheduledSeason, "   ", true)
        scheduledSeason
    }

}
