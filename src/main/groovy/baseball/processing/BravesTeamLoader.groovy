package baseball.processing

import baseball.domain.FieldingPosition
import baseball.mongo.MongoManager

class BravesTeamLoader extends AbstractTeamLoader {

    BravesTeamLoader(MongoManager mongoDB) {
        super(mongoDB)
    }

    def loadBraves2003() {
        def team = [:]
        def batters = []
        def rotation = []
        def bullpen = []
        def fielding

        // Batters                        "2003"                                AB,  H,   2B  3B, HR, SB, CS, BB, SO, HBP
        fielding = ["SS": new FieldingPosition(position: "SS", errors: 1, assists: 125, putouts: 75)]
        batters << saveBatter("2003", "Braves", "Rafael", "Furcal", fielding,   664, 194, 35, 10, 15, 25, 2,  60, 76, 3, "1", 1973, "R", "R", 162, 75, 125, 1, 0, 0)
        fielding = ["2B": new FieldingPosition(position: "2B", errors: 1, assists: 125, putouts: 75)]
        batters << saveBatter("2003", "Braves", "Marcus", "Giles", fielding,    551, 174, 49, 2,  21, 14, 4,  59, 80, 11, "2", 1973, "R", "R", 162, 75, 125, 1, 0, 0)
        fielding = ["1B": new FieldingPosition(position: "1B", errors: 1, assists: 125, putouts: 75)]
        batters << saveBatter("2003", "Braves", "Julio", "Franco", fielding,    197, 58,  12, 2,  5,  0,  1,  25, 43, 0, "3", 1973, "R", "R", 162, 75, 125, 1, 0, 0)
        fielding = ["RF": new FieldingPosition(position: "RF", errors: 1, assists: 125, putouts: 75)]
        batters << saveBatter("2003", "Braves", "Gary", "Sheffield", fielding,  576, 190, 37, 2,  39, 18, 4,  86, 55, 8, "4", 1973, "R", "R", 162, 75, 125, 1, 0, 0)
        fielding = ["C": new FieldingPosition(position: "C", errors: 1, assists: 125, putouts: 75)]
        batters << saveBatter("2003", "Braves", "Javy", "Lopez", fielding,      457, 150, 29, 3,  43, 0,  1,  33, 90, 4, "5", 1973, "R", "R", 162, 75, 125, 1, 0, 0)
        fielding = ["LF": new FieldingPosition(position: "LF", errors: 1, assists: 125, putouts: 75)]
        batters << saveBatter("2003", "Braves", "Chipper", "Jones", fielding,   555, 169, 33, 2,  27, 2,  2,  94, 83, 1, "6", 1973, "R", "R", 162, 75, 125, 1, 0, 0)
        fielding = ["CF": new FieldingPosition(position: "CF", errors: 1, assists: 125, putouts: 75)]
        batters << saveBatter("2003", "Braves", "Andruw", "Jones", fielding,    595, 165, 28, 2,  36, 4,  3,  53, 125, 5, "7", 1973, "R", "R", 162, 75, 125, 1, 0, 0)
        fielding = ["3B": new FieldingPosition(position: "3B", errors: 1, assists: 125, putouts: 75)]
        batters << saveBatter("2003", "Braves", "Vinny", "Castilla", fielding,  542, 150, 28, 3,  22, 1,  2,  26, 86, 3, "8", 1973, "R", "R", 162, 75, 125, 1, 0, 0)
        fielding = ["SS": new FieldingPosition(position: "SS", errors: 1, assists: 125, putouts: 75)]
        batters << saveBatter("2003", "Braves", "Mark", "Derosa", fielding,     266, 70,  14, 0,  6,  1,  0,  16, 49, 5, "9", 1973, "R", "R", 162, 75, 125, 1, 0, 0)

        // Pitchers                                                               BR,  H,   HR,  BB,  SO   HBP, WHIP,  BK
        rotation << savePitcher("2003", "Braves", "Greg", "Maddux", "SP",  1,     655, 225, 24,  33,  124, 8,   1.182, 0)
        rotation << savePitcher("2003", "Braves", "Russ", "Ortiz", "SP", 2,       637, 177, 17,  102, 149, 4,   1.314, 0)
        rotation << savePitcher("2003", "Braves", "Mike", "Hampton", "SP", 3,     570, 186, 14,  78,  110, 1,   1.389, 1)
        rotation << savePitcher("2003", "Braves", "Horacio", "Ramirez", "SP", 4,  547, 181, 21,  72,  100, 6,   1.388, 1)
        rotation << savePitcher("2003", "Braves", "Shane", "Reynolds", "SP", 5,   502, 191, 20,  59,  94,  8,   1.494, 1)
        rotation << savePitcher("2003", "Braves", "Reliever", "Fred", "RP", 5,    502, 191, 20,  59,  94,  8,   1.494, 1)
        rotation << savePitcher("2003", "Braves", "Reliever", "Jim", "RP", 5,     502, 191, 20,  59,  94,  8,   1.494, 1)
        rotation << savePitcher("2003", "Braves", "Reliever", "Harold", "RP", 5,  502, 191, 20,  59,  94,  8,   1.494, 1)
        rotation << savePitcher("2003", "Braves", "Reliever", "Bill", "RP", 5,    502, 191, 20,  59,  94,  8,   1.494, 1)
        rotation << savePitcher("2003", "Braves", "Reliever", "Mark", "RP", 5,    502, 191, 20,  59,  94,  8,   1.494, 1)

        team["batters"] = batters
        team["lineup"] = []
        team["rotation"] = rotation
        team["nextStarter"] = 0
        team["wins"] = 0
        team["losses"] = 0
        team["winDiff"] = 0
        team["gamesBack"] = ""
        team["teamName"] = "Braves 2003"
        team["starterIndex"] = 0
        team
    }

}
