package baseball.domain

class Division {
    String abbreviation = "E"
    String name = "Eastern Division"
    List teams = []

    Division(String abbreviation, String fullName) {
        this.abbreviation = abbreviation
        this.name = fullName
    }

    void addTeam(Team team) {
        teams << team.name
    }

}
