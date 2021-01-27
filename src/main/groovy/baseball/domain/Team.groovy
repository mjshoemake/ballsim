package baseball.domain

import org.apache.log4j.Logger

class Team {

    def C = "Team"
    protected Logger log = Logger.getLogger("Debug");
    String name_display_full = ""
    String team_id = ""
    String league = ""
    String name_abbrev = ""
    String division = ""
    String division_full = ""
    String name = ""
    String city = ""
    String scheduleLookupKey
    String year

    Team(Map value, String year) {
        team_id = value.team_id
        name_display_full = value.name_display_full
        league = value.league
        name_abbrev = value.name_abbrev
        name = value.name
        city = value.city
        division = value.division
        division_full = value.division_full
        this.year = year
    }

    def printTeam() {
        def m = "${C}.printTeam() - "
        log.debug "$m ID: $team_id, Name: $name_display_full, League: $league, Abbr: $name_abbrev, Division: $division - $division_full, City: $city, Name: $name "
        this
    }

}
