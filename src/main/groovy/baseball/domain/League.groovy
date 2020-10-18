package baseball.domain

class League {
    String abbreviation = "AL"
    Map divisions = [:]
    List teams = []

    League(String abbreviation) {
        this.abbreviation = abbreviation
    }

    void addTeam(Team team) {
        teams << team.name
        Division division = null
        if (! divisions.containsKey(team.division)) {
            division = new Division(team.division, team.division_full)
            divisions[team.division] = division
        } else {
            division = divisions.get(team.division)
        }
        division.addTeam(team)
    }
}
