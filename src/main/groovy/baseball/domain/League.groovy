package baseball.domain

class League {
    String abbreviation = "AL"
    String leagueKey = null
    // Map of Division objects, for easy lookup access.
    Map divisions = [:]
    // List of Division objects, to preserve order.
    List divisionsList = []
    // Map of Team objects, with team name as the key.
    Map teamMap = [:]
    // List of team names (not actual Team objects).
    List<String> teams = []
    // Letters to use for divisionKey.
    List<String> keyTokens = ["A","B","C","D","E","F","G","H","I","J","K","L"]

    League(String abbreviation, String leagueKey) {
        this.leagueKey = leagueKey
        this.abbreviation = abbreviation
    }

    League(Map map, Map teamMap, String leagueKey) {
        // Populate real object model from Map/List objects loaded from a
        // datastore.
        this.leagueKey = map.leagueKey
        this.abbreviation = map.abbreviation

        // Map of division objects
        map.divisions.keySet().each { key ->
            divisions[key] = new Division(map.divisions[key], teamMap)
        }
        // Map of team objects
        map.teamMap.keySet().each { teamKey ->
            this.teamMap[teamKey] = teamMap[teamKey]
        }
        // List of team names (not actual Team objects).
        teams = map.teams
    }

    void addTeam(Team team, Map scheduleTeamLookup) {
        teams << team.name
        teamMap[team.name] = team
        Division division = null
        if (! divisions.containsKey(team.division)) {
            String divisionKey = leagueKey + keyTokens[divisions.size()]
            division = new Division(team.division, team.division_full, divisionKey)
            divisions[team.division] = division
        } else {
            division = divisions.get(team.division)
        }
        team.scheduleLookupKey = division.divisionKey + (division.teams.size() + 1)
        division.addTeam(team)
        scheduleTeamLookup[team.scheduleLookupKey, team]
    }
}
