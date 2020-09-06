package baseball.processing

import groovy.json.JsonSlurper
import mjs.common.utils.HttpClient

class HttpHistoricalDataManager {
    String baseUrl = "http://lookup-service-prod.mlb.com"

   def getTeamsForSeason(String year) {
       String json = HttpClient.requestGet("$baseUrl/json/named.team_all_season.bam?season=${year}&active_sw='Y'&all_star_sw='N'&sport_code='mlb'")
       JsonSlurper jsonSlurper = new JsonSlurper()
       def result = jsonSlurper.parseText(json)
       result["team_all_season"]["queryResults"]["row"]
   }

   def get40ManRoster(String team_id) {
       String json = HttpClient.requestGet("$baseUrl/json/named.roster_40.bam?team_id=$team_id")
       JsonSlurper jsonSlurper = new JsonSlurper()
       def result = jsonSlurper.parseText(json)
       result["roster_40"]["queryResults"]["row"]
   }

   def get40ManRoster(String team_id, String year) {
       String json = HttpClient.requestGet("$baseUrl/json/named.roster_team_alltime.bam?start_season=$year&end_season=$year&team_id=$team_id")
       JsonSlurper jsonSlurper = new JsonSlurper()
       def result = jsonSlurper.parseText(json)
       result["roster_team_alltime"]["queryResults"]["row"]
   }

}
