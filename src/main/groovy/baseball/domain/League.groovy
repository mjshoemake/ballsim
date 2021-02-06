package baseball.domain

class League extends Comparable {
    def C = "League"
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

    League(Map map, Map teamMap, String leagueKey, Map scheduleTeamLookup) {
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
            SimTeam newSimTeam = new SimTeam(teamMap[teamKey])
            this.teamMap[teamKey] = newSimTeam
            scheduleTeamLookup[newSimTeam.team.scheduleLookupKey] = newSimTeam
        }
        // List of team names (not actual Team objects).
        teams = map.teams
    }

    // Is the specified object equal to this one?
    boolean equals(League target) {
        boolean result = true
        def m = "${C}.equals() - "

        if (! compareString("abbreviation", abbreviation, target.abbreviation)) { result = false }
        if (! compareString("leagueKey", leagueKey, target.leagueKey)) { result = false }
        if (! compareMap("divisions", divisions, target.divisions)) { result = false }
        if (! compareList("divisionsList", divisionsList, target.divisionsList)) { result = false }
        if (! compareMap("teamMap", teamMap, target.teamMap)) { result = false }
        if (! compareList("teams", teams, target.teams)) { result = false }
        if (result) {
            log.debug("$m Leagues match?  OK")
        } else {
            log.debug("$m Leagues match?  NO MATCH")
        }
        return result
    }

    // Is the specified object equal to this one?
    boolean equals(League target, List builder) {
        boolean result = true
        def m = "${C}.equals() - "

        if (! compareString("abbreviation", abbreviation, target.abbreviation, builder)) { result = false }
        if (! compareString("leagueKey", leagueKey, target.leagueKey, builder)) { result = false }
        if (! compareMap("divisions", divisions, target.divisions, builder)) { result = false }
        if (! compareList("divisionsList", divisionsList, target.divisionsList, builder)) { result = false }
        if (! compareMap("teamMap", teamMap, target.teamMap, builder)) { result = false }
        if (! compareList("teams", teams, target.teams, builder)) { result = false }
        if (result) {
            builder << "$m Leagues match?  OK"
        } else {
            builder << "$m Leagues match?  NO MATCH"
        }
        return result
    }

    void addTeam(Team team, Map scheduleTeamLookup) {
        // Create SimTeam instance for this team.
        SimTeam simTeam = new SimTeam(team)
        teams << team.name
        teamMap[team.name] = simTeam
        Division division = null
        if (! divisions.containsKey(team.division)) {
            String divisionKey = leagueKey + keyTokens[divisions.size()]
            division = new Division(team.division, team.division_full, divisionKey)
            divisions[team.division] = division
        } else {
            division = divisions.get(team.division)
        }
        team.scheduleLookupKey = division.divisionKey + (division.teams.size() + 1)
        division.addTeam(simTeam)
        scheduleTeamLookup[team.scheduleLookupKey] = simTeam
    }
}
