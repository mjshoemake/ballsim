package baseball.domain

class Division {
    String abbreviation = "E"
    String name = "Eastern Division"
    String divisionKey = null
    List teams = []

    Division(String abbreviation, String fullName, divisionKey) {
        this.abbreviation = abbreviation
        this.name = fullName
        this.divisionKey = divisionKey
    }

    Division(Map map, Map<Team> teamMap) {
        this.abbreviation = map.abbreviation
        this.name = map.name
        this.divisionKey = map.divisionKey
        // List of team objects
        this.teams = map.teams
    }

    void addTeam(SimTeam simTeam) {
        this.teams << simTeam.teamName
    }

}
