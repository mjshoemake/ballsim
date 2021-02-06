package baseball.domain

import org.apache.log4j.Logger

class Team extends Comparable {

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
        this.scheduleLookupKey = value.scheduleLookupKey
    }

    Team(Map map) {
        team_id = map.team_id
        name_display_full = map.name_display_full
        league = map.league
        name_abbrev = map.name_abbrev
        name = map.name
        city = map.city
        division = map.division
        division_full = map.division_full
        year = map.year
        scheduleLookupKey = map.scheduleLookupKey
    }

    // Is the specified object equal to this one?
    boolean equals(Team target) {
        boolean result = true
        def m = "${C}.equals() - "

        if (! compareString("name_display_full", name_display_full, target.name_display_full)) { result = false }
        if (! compareString("team_id", team_id, target.team_id)) { result = false }
        if (! compareString("league", league, target.league)) { result = false }
        if (! compareString("name_abbrev", name_abbrev, target.name_abbrev)) { result = false }
        if (! compareString("division", division, target.division)) { result = false }
        if (! compareString("division_full", division_full, target.division_full)) { result = false }
        if (! compareString("name", name, target.name)) { result = false }
        if (! compareString("city", city, target.city)) { result = false }
        if (! compareString("year", year, target.year)) { result = false }
        if (! compareString("scheduleLookupKey", scheduleLookupKey, target.scheduleLookupKey)) { result = false }
        if (result) {
            log.debug("$m Teams match?  OK")
        } else {
            log.debug("$m Teams match?  NO MATCH")
        }

        return result
    }

    // Is the specified object equal to this one?
    boolean equals(Team target, List builder) {
        boolean result = true
        def m = "${C}.equals() - "

        if (! compareString("name_display_full", name_display_full, target.name_display_full, builder)) { result = false }
        if (! compareString("team_id", team_id, target.team_id, builder)) { result = false }
        if (! compareString("league", league, target.league, builder)) { result = false }
        if (! compareString("name_abbrev", name_abbrev, target.name_abbrev, builder)) { result = false }
        if (! compareString("division", division, target.division, builder)) { result = false }
        if (! compareString("division_full", division_full, target.division_full, builder)) { result = false }
        if (! compareString("name", name, target.name, builder)) { result = false }
        if (! compareString("city", city, target.city, builder)) { result = false }
        if (! compareString("year", year, target.year, builder)) { result = false }
        if (! compareString("scheduleLookupKey", scheduleLookupKey, target.scheduleLookupKey, builder)) { result = false }
        if (result) {
            builder << "$m Teams match?  OK"
        } else {
            builder << "$m Teams match?  NO MATCH"
        }

        return result
    }

    def printTeam() {
        def m = "${C}.printTeam() - "
        log.debug "$m ID: $team_id, Name: $name_display_full, League: $league, Abbr: $name_abbrev, Division: $division - $division_full, City: $city, Name: $name "
        this
    }

}
