package baseball.processing

class ScheduleLoader {


    def loadRoundRobinScheduleFromFile(def league) {
        return loadRoundRobinScheduleFromFile(league, false)
    }

    def loadRoundRobinScheduleFromFile(def league, boolean oneGameOnly) {
        def schedule = []
        def teamList = []
        int gameCount = 0

        def it = league.keySet().iterator()
        while (it.hasNext()) {
            def div = league[it.next()]
            div.each { next ->
                teamList << next
            }
        }
        int numTeams = teamList.size()

        def scheduleText = this.class.getResource("/schedules/${numTeams}-team-schedule.txt")?.text
        def gameNum = 0
        scheduleText.eachLine {
            def columns = it.split("\t")
            if (! (columns[0] == "Round" && columns[1] == "Game #" && columns[2] == "Home Team")) {
                def round = columns[0]
                def seriesNum = columns[1]
                def homeTeam = (Integer.parseInt(columns[2].substring(5)) - 1)
                def awayTeam = (Integer.parseInt(columns[3].substring(5)) - 1)
                for (int i=0; i <= 2; i++) {
                    gameNum++
                    def game = [:]
                    game.round = round
                    game.seriesNum = seriesNum
                    game.gameNum = gameNum
                    game.homeTeamIndex = homeTeam
                    game.awayTeamIndex = awayTeam
                    gameCount++
                    if (! oneGameOnly || gameCount == 1) {
                        schedule << game
                    }
                }
            }
        }
        schedule
    }

}
