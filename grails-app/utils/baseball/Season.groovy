package baseball

import org.apache.commons.logging.LogFactory
import org.apache.log4j.Logger

class Season {

    def league
    def schedule
    def scheduleIndex = 0
    def highlightsLog = Logger.getLogger('highlights')
    def seasonStatsLog = Logger.getLogger('seasonStats')
    def gameLogEnabled = true
    def highlightsLogEnabled = true
    def boxscoreLogEnabled = true

    void startSeason(def league, def schedule) {
        this.league = league;
        this.schedule = schedule;

        def teamList = []

        def it = league.keySet().iterator()
        while (it.hasNext()) {
            def div = league[it.next()]
            div.each { next ->
                teamList << next
            }
        }
    }

    void playSeason(def league, schedule) {
        startSeason(league, schedule)
        schedule.each() { game ->
            def ballGame = new BallGame()
            ballGame.gameLogEnabled = this.gameLogEnabled
            ballGame.highlightsLogEnabled = this.highlightsLogEnabled
            ballGame.boxscoreLogEnabled = this.boxscoreLogEnabled
            ballGame.homeTeam = teamList[game.homeTeamIndex]
            ballGame.awayTeam = teamList[game.awayTeamIndex]
            ballGame.start()
        }

        highlightsLog.debug("")
        highlightsLog.debug("")
        highlightsLog.debug("Standings:")
        highlightsLog.debug("")
        highlightsLog.debug "${format("Team", 25)}:    ${format("Wins", 4)}   ${format("Losses", 6)}"
        teamList.each {
            highlightsLog.debug "${format(it["teamName"], 25)}:    ${format(it["wins"], 4)}   ${format(it["losses"], 6)}"
        }

        seasonStatsLog.debug("")
        seasonStatsLog.debug("")
        seasonStatsLog.debug("Standings:")
        seasonStatsLog.debug("")
        seasonStatsLog.debug "${format("Team", 25)}:    ${format("Wins", 4)}   ${format("Losses", 6)}"
        teamList.each {
            seasonStatsLog.debug "${format(it["teamName"], 25)}:    ${format(it["wins"], 4)}   ${format(it["losses"], 6)}"
        }

        logTeamStats(teamList)
    }

    private def logTeamStats(def teamList) {
        seasonStatsLog.debug ""
        seasonStatsLog.debug "END OF SEASON STATS"

        teamList.each { team ->
            def teamName = team["teamName"]
            def lineup = team["lineup"]
            def rotation = team["rotation"]
            seasonStatsLog.debug ""
            seasonStatsLog.debug "${format(teamName, 25)}:    ${format(team["wins"], 4)}   ${format(team["losses"], 6)}"
            seasonStatsLog.debug ""
            seasonStatsLog.debug "$teamName:"
            seasonStatsLog.debug "   Lineup:"
            seasonStatsLog.debug "      ${format("Batter", 20)}  ${format("AB", 3)}  ${format("R", 3)}  ${format("H", 3)}  ${format("2B", 3)}  ${format("3B", 3)}  ${format("HR", 3)}  ${format("RBI", 4)}  ${format("BB", 3)}  ${format("SO", 3)}  ${format("SB", 3)}  ${format("CS", 3)}  ${format("AVG", 5)} "
            lineup.each {
                seasonStatsLog.debug "      ${format(it.simBatter.batter.name, 20)}  ${format(it.simBatter.atBats, 3)}  ${format(it.simBatter.runs, 3)}  ${format(it.simBatter.hits, 3)}  ${format(it.simBatter.doubles, 3)}  ${format(it.simBatter.triples, 3)}  ${format(it.simBatter.homers, 3)}  ${format(it.simBatter.rbi, 4)}  ${format(it.simBatter.walks, 3)}  ${format(it.simBatter.strikeouts, 3)}  ${format(it.simBatter.stolenBases, 3)}  ${format(it.simBatter.caughtStealing, 3)}  ${format(it.simBatter.battingAvg, 5)}"
            }
            seasonStatsLog.debug ""
            seasonStatsLog.debug "   Pitchers:"
            seasonStatsLog.debug "      ${format("Pitcher", 20)}  ${format("W", 3)}  ${format("L", 3)}  ${format("IP", 5)}  ${format("R", 3)}  ${format("ERA", 5)}  ${format("H", 3)}  ${format("HR", 3)}  ${format("BB", 3)}  ${format("SO", 3)}"
            rotation.each {
                seasonStatsLog.debug "      ${format(it.simPitcher.pitcher.name, 20)}  ${format(it.simPitcher.wins, 3)}  ${format(it.simPitcher.losses, 3)}  ${format(it.simPitcher.battersRetired/3, 5)}  ${format(it.simPitcher.runs, 3)}  ${it.simPitcher.era}  ${format(it.simPitcher.hits, 3)}  ${format(it.simPitcher.homers, 3)}  ${format(it.simPitcher.walks, 3)}  ${format(it.simPitcher.strikeouts, 3)}"
            }
            seasonStatsLog.debug ""
            seasonStatsLog.debug ""
        }

    }

    private def format(def text, int length) {
        text = text + ''
        if (text.length() < length) {
            int remainder = length - text.length()
            text + (" " * remainder)
        } else {
            text.substring(0, length)
        }
    }

}
