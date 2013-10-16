package baseball

class FileTeamLoader extends AbstractTeamLoader {

    def loadTeamFromFile(def teamName, year) {
        def team = [:]
        def batters = []
        def lineup = []
        def rotation = []
        def bullpen = []

        year += ""
        println "Loading $teamName..."
        def fileName = "/seasonStats/${year}/${fixTeamName(teamName)}Batters.txt"
        def battersText = this.class.getResource(fileName)?.text
        def lineNum = 0
        def statsSource = StatsSource.BASEBALL_REFERENCE
        battersText.eachLine {
            lineNum++
            def columns = it.split("\t")
            if (lineNum == 1) {
                if (columns[0] == "Rk" && columns[1] == "Pos" && columns[3] == "Age") {
                    statsSource = StatsSource.BASEBALL_REFERENCE
                } else if (columns[0] == "POS" && columns[1] == "Name" && columns[2] == "Age") {
                    statsSource = StatsSource.PENNANT_CHASE
                } else {
                    throw new Exception("The import source file was unrecognized.  Expected a file either from ${StatsSource.BASEBALL_REFERENCE.value} or ${StatsSource.PENNANT_CHASE.value}.")
                }
            } else {
                if (statsSource == StatsSource.BASEBALL_REFERENCE) {
                    if (! (columns[0] == "Rk" && columns[1] == "Pos" && columns[3] == "Age")) {
                        def hitter = saveBatter(year,
                                teamName,
                                columns[2],
                                columns[1],
                                lineNum,
                                Integer.parseInt(columns[6]),
                                Integer.parseInt(columns[8]),
                                Integer.parseInt(columns[9]),
                                Integer.parseInt(columns[10]),
                                Integer.parseInt(columns[11]),
                                Integer.parseInt(columns[13]),
                                Integer.parseInt(columns[14]),
                                Integer.parseInt(columns[15]),
                                Integer.parseInt(columns[16]),
                                Integer.parseInt(columns[24]))
                        if (lineup.size() < 9) {
                            lineup << hitter
                        } else {
                            batters << hitter
                        }
                    }
                } else { // pennantchase.com
                    if (! (columns[0] == "POS" && columns[1] == "Name" && columns[2] == "Age")) {
                        def hitter = saveBatter(year,
                                teamName,
                                columns[1],
                                columns[0],
                                lineNum,
                                Integer.parseInt(columns[10]),
                                Integer.parseInt(columns[12]),
                                Integer.parseInt(columns[13]),
                                Integer.parseInt(columns[14]),
                                Integer.parseInt(columns[15]),
                                Integer.parseInt(columns[20]),
                                1,
                                Integer.parseInt(columns[17]),
                                Integer.parseInt(columns[18]),
                                Integer.parseInt(columns[19]))
                        if (lineup.size() < 9) {
                            lineup << hitter
                        } else {
                            batters << hitter
                        }
                    }
                }
            }
        }

        def pitchersText = this.class.getResource("/seasonStats/${year}/${fixTeamName(teamName)}Pitchers.txt")?.text
        lineNum = 0
        int numStarters = 0
        pitchersText.eachLine {
            lineNum++
            def columns = it.split("\t")
            if (lineNum == 1) {
                if (columns[0] == "Rk" && columns[1] == "Pos" && columns[3] == "Age") {
                    statsSource = StatsSource.BASEBALL_REFERENCE
                } else if (columns[0] == "Role" && columns[1] == "Name" && columns[2] == "Age") {
                    statsSource = StatsSource.PENNANT_CHASE
                } else {
                    throw new Exception("The import source file was unrecognized.  Expected a file either from ${StatsSource.BASEBALL_REFERENCE.value} or ${StatsSource.PENNANT_CHASE.value}.")
                }
            } else {
                if (statsSource == StatsSource.BASEBALL_REFERENCE) {
                    if (! (columns[0] == "Rk" && columns[1] == "Pos" && columns[3] == "Age")) {
                        def ipSplit = columns[14].split('\\.')
                        def battersRetired
                        if (ipSplit.length > 1) {
                            battersRetired = (Integer.parseInt(ipSplit[0]) * 3) + Integer.parseInt(ipSplit[1])
                        } else {
                            battersRetired = Integer.parseInt(ipSplit[0]) * 3
                        }
                        def pitcher = savePitcher(year,
                                teamName,
                                columns[2],
                                columns[1],
                                lineNum,
                                battersRetired,
                                Integer.parseInt(columns[15]),
                                Integer.parseInt(columns[18]),
                                Integer.parseInt(columns[19]),
                                Integer.parseInt(columns[21]),
                                Integer.parseInt(columns[22]),
                                Float.parseFloat(columns[27]),
                                Integer.parseInt(columns[23]))
                        if (columns[1] == "SP") {
                            rotation << pitcher
                        } else {
                            bullpen << pitcher
                        }
                    }
                } else { // pennantchase.com
                    if (! (columns[0] == "Rk" && columns[1] == "Pos" && columns[3] == "Age")) {
                        def ipSplit = columns[8].split('\\.')
                        def battersRetired
                        if (ipSplit.length > 1) {
                            battersRetired = (Integer.parseInt(ipSplit[0]) * 3) + Integer.parseInt(ipSplit[1])
                        } else {
                            battersRetired = Integer.parseInt(ipSplit[0]) * 3
                        }
                        def pitcher = savePitcher(year,
                                teamName,
                                columns[1],
                                columns[0],
                                lineNum,
                                battersRetired,
                                Integer.parseInt(columns[9]),
                                -1,
                                Integer.parseInt(columns[10]),
                                Integer.parseInt(columns[11]),
                                -1,
                                Float.parseFloat(columns[15]),
                                0)
                        if (columns[0] == "SP" && numStarters < 5) {
                            rotation << pitcher
                            numStarters++
                        } else {
                            bullpen << pitcher
                        }
                    }
                }
            }
        }

        println "Loading $teamName...  DONE."

        team["batters"] = batters
        team["lineup"] = lineup
        team["rotation"] = rotation
        team["bullpen"] = bullpen
        team["nextStarter"] = 0
        team["wins"] = 0
        team["losses"] = 0
        team["teamName"] = "$teamName $year"
        team["starterIndex"] = 0
        team
    }


}
