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

    def load(def teamMap) {
        team_id = teamMap["team_id"]
        name_display_full = teamMap["name_display_full"]
        league = teamMap["league"]
        name_abbrev = teamMap["name_abbrev"]
        name = teamMap["name"]
        city = teamMap["city"]
        division = teamMap["division"]
        division_full = teamMap["division_full"]
        this
    }

    def printTeam() {
        println "ID: $team_id, Name: $name_display_full, League: $league, Abbr: $name_abbrev, Division: $division - $division_full, City: $city, Name: $name "
        this
    }

}
