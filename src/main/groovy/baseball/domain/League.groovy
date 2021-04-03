package baseball.domain

class League extends LeagueComparable {
    def C = "League"
    String abbreviation = "AL"
    String leagueKey = null
    String simulationID = null
    // Map of Division objects, for easy lookup access.
    //Map divisions = [:]
    // List of Division objects, to preserve order.
    List divisionsList = []
    // Map of Team objects, with team name as the key.
    //Map teamMap = [:]
    // List of team SimTeam objects.
    //List<SimTeam> teams = []
    // Letters to use for divisionKey.
    List<String> keyTokens = ["A","B","C","D","E","F","G","H","I","J","K","L"]

    League(String abbreviation, String leagueKey, String simulationID) {
        this.leagueKey = leagueKey
        this.abbreviation = abbreviation
        this.simulationID = simulationID
    }

    Map toMap() {
        Map result = [:]
        result["abbreviation"] = abbreviation
        result["simulationID"] = simulationID
        result["leagueKey"] = leagueKey
        result["leagueName"] = leagueName
        result["divisionsList"] = toListOfMaps(divisionsList)
        result
    }

    //League(Map map, Map teamMap, String leagueKey, Map scheduleTeamLookup) {
    League(Map map) {
        // Populate real object model from Map/List objects loaded from a
        // datastore.
        this.leagueKey = map.leagueKey
        this.abbreviation = map.abbreviation

        // Map of division objects
        map.divisionsList.each() { Map divMap ->
            divisionsList << new Division(divMap)
        }
        // Map of team objects
        //map.teamMap.keySet().each { teamKey ->
        //    SimTeam newSimTeam = new SimTeam(teamMap[teamKey])
        //    this.teamMap[teamKey] = newSimTeam
        //    scheduleTeamLookup[newSimTeam.team.scheduleLookupKey] = newSimTeam
        //}
        // List of team names (not actual Team objects).
        //teams = map.teams
    }

    String toString() {
        "$abbreviation (${divisionsList.size()}/${teams.size()})"
    }
    String getLeagueName() {
        return "$simulationID-$leagueKey"
    }

    // Is the specified object equal to this one?
    boolean equals(League target) {
        boolean result = true
        def m = "${C}.equals() - "

        if (! compareString("abbreviation", abbreviation, target.abbreviation)) { result = false }
        if (! compareString("leagueKey", leagueKey, target.leagueKey)) { result = false }
        //if (! compareMap("divisions", divisions, target.divisions)) { result = false }
        if (! compareList("divisionsList", divisionsList, target.divisionsList)) { result = false }
        //if (! compareMap("teamMap", teamMap, target.teamMap)) { result = false }
        //if (! compareList("teams", teams, target.teams)) { result = false }
        if (result) {
            log.debug("$m Leagues match?  OK")
        } else {
            log.debug("$m Leagues match?  NO MATCH   ${abbreviation} != ${target.abbreviation}")
            new Exception("Leagues don't match!!!").printStackTrace()
        }
        if (divisions.size() > 5) {
            throw new Exception("Error: League divisions (${divisions.size()}) should never be greater than 5.")
        }
        if (divisionsList.size() > 5) {
            throw new Exception("Error: League divisions list (${divisionsList.size()}) should never be greater than 5.")
        }
        return result
    }

    // Is the specified object equal to this one?
    boolean equals(League target, List builder) {
        boolean result = true
        def m = "${C}.equals() - "

        if (! compareString("abbreviation", abbreviation, target.abbreviation, builder)) { result = false }
        if (! compareString("leagueKey", leagueKey, target.leagueKey, builder)) { result = false }
        //if (! compareMap("divisions", divisions, target.divisions, builder)) { result = false }
        if (! compareList("divisionsList", divisionsList, target.divisionsList, builder)) { result = false }
        //if (! compareMap("teamMap", teamMap, target.teamMap, builder)) { result = false }
        //if (! compareList("teams", teams, target.teams, builder)) { result = false }
        if (result) {
            builder << "$m Leagues match?  OK"
        } else {
            builder << "$m Leagues match?  NO MATCH   ${abbreviation} != ${target.abbreviation}"
            new Exception("Leagues don't match!!!").printStackTrace()
        }
        if (divisions.size() > 5) {
            throw new Exception("Error: League divisions (${divisions.size()}) should never be greater than 5.")
        }
        if (divisionsList.size() > 5) {
            throw new Exception("Error: League divisions list (${divisionsList.size()}) should never be greater than 5.")
        }
        return result
    }

    void addTeam(Team team) {
        // Create SimTeam instance for this team.
        SimTeam simTeam = new SimTeam(team)
        //teams << team.name
        //teamMap[team.name] = simTeam
        Division division = null
        if (! divisions.containsKey(team.division)) {
            String divisionKey = leagueKey + keyTokens[divisions.size()]
            division = new Division(team.division, team.division_full, divisionKey)
            divisionsList << division
        } else {
            division = divisions.get(team.division)
        }
        team.scheduleLookupKey = division.divisionKey + (division.teams.size() + 1)
        division.addTeam(simTeam)
        //scheduleTeamLookup[team.scheduleLookupKey] = simTeam
    }

    void setDivisions(Map map) {
        // Do nothing.
    }

    Map getDivisions() {
        //Map result = [:]
        Map result = new HashMap()
        //divisionsList.each() { Division next ->
        for (int i=0; i <= divisionsList.size()-1; i++) {
            Division next = divisionsList.get(i)
            result[next.abbreviation] = next
        }
        result
    }

    List<SimTeam> getTeams() {
        List<SimTeam> teamsList = []
        divisionsList.each() { Division nextDivision ->
            teamsList << nextDivision.teams
        }
        teamsList.flatten()
    }

    Map getTeamMap() {
        Map result = [:]
        teams.each() { SimTeam next ->
            result[next.teamName] = next
        }
        return result
    }
}
