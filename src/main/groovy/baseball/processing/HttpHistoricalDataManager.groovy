package baseball.processing

import baseball.domain.Player
import baseball.domain.Team
import baseball.mongo.MongoManager
import groovy.json.JsonSlurper
import mjs.common.utils.HttpClient
import mjs.common.utils.LogUtils
import mjs.common.utils.PerformanceMetrics

class HttpHistoricalDataManager {
    String baseUrl = "http://lookup-service-prod.mlb.com"
    PerformanceMetrics perf = new PerformanceMetrics()
    MongoManager mongoManager = new MongoManager()
    String TABLE_TEAMS_FOR_SEASON = "teamsForSeason"
    String TABLE_TEAM_MAP_FOR_SEASON = "teamMapForSeason"
    String TABLE_TEAM_ROSTER_FOR_SEASON = "teamRosterForSeason"

    HttpHistoricalDataManager() {
        mongoManager.open("ballsim")
        //deleteAllSavedData()
    }

    void deleteAllSavedData() {
        mongoManager.deleteAll(TABLE_TEAM_MAP_FOR_SEASON)
        mongoManager.deleteAll(TABLE_TEAM_ROSTER_FOR_SEASON)
        mongoManager.deleteAll(TABLE_TEAMS_FOR_SEASON)
    }

    void close() {
        mongoManager.close()
    }

    def getTeamsForSeason(String year) {
        def teamsForSeason = mongoManager.find(TABLE_TEAMS_FOR_SEASON, ["year": year])
        if (! teamsForSeason) {
            String json = HttpClient.requestGet("$baseUrl/json/named.team_all_season.bam?season=${year}&active_sw='Y'&all_star_sw='N'&sport_code='mlb'")
            JsonSlurper jsonSlurper = new JsonSlurper()
            def response = jsonSlurper.parseText(json)
            def result = response["team_all_season"]["queryResults"]["row"]
            mongoManager.addToCollection(TABLE_TEAMS_FOR_SEASON, ["year": year, "value": result])
            result["value"]
        } else {
            teamsForSeason[0]
        }
    }

    def getTeamMapForSeason(String year, boolean forceLoadFromWeb) {
        boolean loadedFromAPI = false
        def teamMapForSeason = null

        // Force load from web?  If so, skip load from Mongo.
        if (! forceLoadFromWeb)
            teamMapForSeason = mongoManager.find(TABLE_TEAM_MAP_FOR_SEASON, ["year": year])
        def teams = []
        if (! teamMapForSeason) {
            String json = HttpClient.requestGet("$baseUrl/json/named.team_all_season.bam?season=${year}&active_sw='Y'&all_star_sw='N'&sport_code='mlb'")
            loadedFromAPI = true
            JsonSlurper jsonSlurper = new JsonSlurper()
            def result = jsonSlurper.parseText(json)
            teams = result["team_all_season"]["queryResults"]["row"]
        } else {
            def oldMap = teamMapForSeason[0]
            oldMap.each() { nextTeam ->
                if (nextTeam.key != "_id" && nextTeam.key != "year") {
                    teams << nextTeam["value"]
                }
            }
        }
        def teamMap = ["year": year]
        teams.each { next ->
            Team team = new Team(next, year)
            if (! team.league.isEmpty()) {
                team.printTeam()
                teamMap[team.name] = team
            }
        }
        if (loadedFromAPI) {
            mongoManager.addToCollection(TABLE_TEAM_MAP_FOR_SEASON, teamMap)
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
        if (!(battingStats instanceof Map)) {
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
        if (!(pitchingStats instanceof Map)) {
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
        perf.resetMetrics("LoadHistoricalData.get40ManRoster")
        perf.startEvent("LoadHistoricalData.get40ManRoster", "Main")
        def teamRoster = mongoManager.find(TABLE_TEAM_ROSTER_FOR_SEASON, ["year": year, "team": team_id])
        def result
        if (teamRoster?.size() > 0) {
            result = teamRoster[0]["value"]
        } else {
            result = []
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
                    //if (pitchingStats)
                    perf.endEvent("LoadHistoricalData.get40ManRoster", "loadPitcherProperties[Map]")
                }

                perf.startEvent("LoadHistoricalData.get40ManRoster", "loadBatterProperties")
                Player batter = new Player()
                batter.loadFromMap(person, battingStats, pitchingStats, team_id, year)
                result << batter
                perf.endEvent("LoadHistoricalData.get40ManRoster", "loadBatterProperties")
            }
            mongoManager.addToCollection(TABLE_TEAM_ROSTER_FOR_SEASON, ["year": year, "team": team_id, "value": result])
        }

        perf.endEvent("LoadHistoricalData.get40ManRoster", "Main")
        //result.each { next ->
        //    next.printPlayer()
        //}

        perf.writeMetricsToLog()
        result
    }

}
