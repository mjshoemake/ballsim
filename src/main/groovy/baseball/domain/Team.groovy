package baseball.domain

class Team {

    String name_display_full = ""
    String team_id = ""
    String league = ""
    String name_abbrev = ""
    String division = ""
    String division_full = ""
    String name = ""
    String city = ""

    Team(Map value) {
        team_id = value.team_id
        name_display_full = value.name_display_full
        league = value.league
        name_abbrev = value.name_abbrev
        name = value.name
        city = value.city
        division = value.division
        division_full = value.division_full
    }

    def printTeam() {
        println "ID: $team_id, Name: $name_display_full, League: $league, Abbr: $name_abbrev, Division: $division - $division_full, City: $city, Name: $name "
        this
    }

}
