package baseball.processing

import baseball.domain.Batter
import baseball.domain.PitcherStats
import baseball.domain.Team
import groovy.json.JsonSlurper
import mjs.common.exceptions.ModelException
import mjs.common.utils.HttpClient
import mjs.common.utils.PerformanceEvent
import mjs.common.utils.PerformanceMetrics

class HttpHistoricalDataManager {
    String baseUrl = "http://lookup-service-prod.mlb.com"
    PerformanceMetrics perf = new PerformanceMetrics()

    def getTeamsForSeason(String year) {
        String json = HttpClient.requestGet("$baseUrl/json/named.team_all_season.bam?season=${year}&active_sw='Y'&all_star_sw='N'&sport_code='mlb'")
        JsonSlurper jsonSlurper = new JsonSlurper()
        def result = jsonSlurper.parseText(json)
        result["team_all_season"]["queryResults"]["row"]
    }

    def getTeamMapForSeason(String year) {
        String json = HttpClient.requestGet("$baseUrl/json/named.team_all_season.bam?season=${year}&active_sw='Y'&all_star_sw='N'&sport_code='mlb'")
        JsonSlurper jsonSlurper = new JsonSlurper()
        def result = jsonSlurper.parseText(json)
        def teams = result["team_all_season"]["queryResults"]["row"]
        def teamMap = [:]

        teams.each { next ->
            Team team = new Team()
            team.load(next).printTeam()
            teamMap[team.name] = team
        }
        teamMap
    }

    def get40ManRoster(String team_id) {
        String json = HttpClient.requestGet("$baseUrl/json/named.roster_40.bam?team_id=$team_id")
        JsonSlurper jsonSlurper = new JsonSlurper()
        def result = jsonSlurper.parseText(json)
        result["roster_40"]["queryResults"]["row"]
    }

    private def getTeamRoster(String team_id, String year) {
        String json = HttpClient.requestGet("$baseUrl/json/named.roster_team_alltime.bam?start_season=$year&end_season=$year&team_id=$team_id")
        JsonSlurper jsonSlurper = new JsonSlurper()
        def result = jsonSlurper.parseText(json)
        result["roster_team_alltime"]["queryResults"]["row"]
    }

    private def getPlayerBattingStats(String playerID, String team_id, String year) {
        String json = HttpClient.requestGet("$baseUrl/json/named.sport_hitting_tm.bam?league_list_id='mlb'&game_type='R'&season='$year'&player_id='$playerID'")
        JsonSlurper jsonSlurper = new JsonSlurper()
        def result = jsonSlurper.parseText(json)
        def battingStats = result["sport_hitting_tm"]["queryResults"]["row"]
        if (! (battingStats instanceof Map)) {
            // Not a Map, it's a list. Find the map in the list.
            battingStats.each() { next ->
                if (next.team_id == team_id) {
                    battingStats = next
                }
            }
        }
        battingStats
    }

    private def getPlayerPitchingStats(String playerID, String team_id, String year) {
        String json = HttpClient.requestGet("$baseUrl/json/named.sport_pitching_tm.bam?league_list_id='mlb'&game_type='R'&season='$year'&player_id='$playerID'")
        JsonSlurper jsonSlurper = new JsonSlurper()
        def result = jsonSlurper.parseText(json)
        def pitchingStats = result["sport_pitching_tm"]["queryResults"]["row"]
        if (! (pitchingStats instanceof Map)) {
            // Not a Map, it's a list. Find the map in the list.
            pitchingStats.each() { next ->
                if (next.team_id == team_id) {
                    pitchingStats = next
                }
            }
        }
        pitchingStats
    }

    def get40ManRoster(String team_id, String year) {
        def result = []
        perf.resetMetrics("LoadHistoricalData.get40ManRoster")
        perf.startEvent("LoadHistoricalData.get40ManRoster", "Main")
        perf.startEvent("LoadHistoricalData.get40ManRoster", "getTeamRoster")
        def playerList = getTeamRoster(team_id, year)
        perf.endEvent("LoadHistoricalData.get40ManRoster", "getTeamRoster")
        playerList.each { person ->
            // Get batting stats
            perf.startEvent("LoadHistoricalData.get40ManRoster", "getPlayerBattingStats")
            def battingStats = getPlayerBattingStats(person.player_id, team_id, year)
            perf.endEvent("LoadHistoricalData.get40ManRoster", "getPlayerBattingStats")

            def pitchingStats = null
            if (person.primary_position == "P") {
                // Player is a pitcher. Load pitching stats.
                perf.startEvent("LoadHistoricalData.get40ManRoster", "loadPitcherProperties[Map]")
                pitchingStats = getPlayerPitchingStats(person.player_id, team_id, year)
                perf.endEvent("LoadHistoricalData.get40ManRoster", "loadPitcherProperties[Map]")
            }

            perf.startEvent("LoadHistoricalData.get40ManRoster", "loadBatterProperties")
            Batter batter = new Batter()
            batter.loadFromMap(person, battingStats, pitchingStats, team_id, year)
            result << batter
            perf.endEvent("LoadHistoricalData.get40ManRoster", "loadBatterProperties")
        }
        perf.endEvent("LoadHistoricalData.get40ManRoster", "Main")
        result.each { next ->
            next.printPlayer()
        }

        perf.writeMetricsToLog()
        result
    }

}
