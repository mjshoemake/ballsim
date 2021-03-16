package baseball.domain

class Division extends Comparable {
    def C = "Division"
    String abbreviation = "E"
    String name = "Eastern Division"
    String divisionKey = null
    List<SimTeam> teams = []

    Division(String abbreviation, String fullName, divisionKey) {
        this.abbreviation = abbreviation
        this.name = fullName
        this.divisionKey = divisionKey
    }

    Division(Map map) {
        this.abbreviation = map.abbreviation
        this.name = map.name
        this.divisionKey = map.divisionKey
        // List of team objects
        map.teams.each { simTeamMap ->
            SimTeam nextSimTeam = new SimTeam(simTeamMap)
            teams << nextSimTeam
        }
    }

    // Is the specified object equal to this one?
    boolean equals(Division target) {
        boolean result = true
        def m = "${C}.equals() - "

        if (! compareString("abbreviation", abbreviation, target.abbreviation)) { result = false }
        if (! compareString("name", name, target.name)) { result = false }
        if (! compareString("divisionKey", divisionKey, target.divisionKey)) { result = false }
        if (! compareList("teams", teams, target.teams)) { result = false }
        if (result) {
            log.debug("$m Divisions match?  OK")
        } else {
            log.debug("$m Divisions match?  NO MATCH")
        }
        if (teams.size() > 10) {
            throw new Exception("Error: Division teams (${teams.size()}) should never be greater than 10.")
        }
        return result
    }

    // Is the specified object equal to this one?
    boolean equals(Division target, List builder) {
        boolean result = true
        def m = "${C}.equals() - "

        if (! compareString("abbreviation", abbreviation, target.abbreviation, builder)) { result = false }
        if (! compareString("name", name, target.name, builder)) { result = false }
        if (! compareString("divisionKey", divisionKey, target.divisionKey, builder)) { result = false }
        if (! compareList("teams", teams.sort(), target.teams.sort(), builder)) { result = false }
        if (result) {
            builder << "$m Divisions match?  OK"
        } else {
            builder << "$m Divisions match?  NO MATCH"
        }
        if (teams.size() > 10) {
            throw new Exception("Error: Division teams (${teams.size()}) should never be greater than 10.")
        }
        return result
    }

    String toString() {
        return "$name (${teams.size()})"
    }

    void addTeam(SimTeam simTeam) {
        this.teams << simTeam
    }

    boolean compareList(String fieldName, def source, def target, List builder) {
        boolean result = super.compareList(fieldName, source, target, builder)
        if (result) {
            return result
        } else {
            return result
        }
    }


}
