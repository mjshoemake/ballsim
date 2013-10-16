package baseball

class PhilliesTeamLoader extends AbstractTeamLoader {

    // 1915, 1980, 2007, 2011
    def loadPennantChase_Dalmatians() {
        def team = [:]
        def batters = []
        def rotation = []
        def bullpen = []
        // Batters                                                                       AB,  H,   2B  3B, HR, SB, CS, BB,  SO,  HBP
        batters << saveBatter(1, "PennantChase Dalmatians", "Keith Moreland", "C", 1,    159, 50,  8,  0,  4,  3,  1,  8,   48,  0)  // 1980
        batters << saveBatter(1, "PennantChase Dalmatians", "Ryan Howard", "1B", 1,      529, 142, 26, 0,  47, 1,  1,  107, 199, 5)  // 2007
        batters << saveBatter(1, "PennantChase Dalmatians", "Chase Utley", "2B", 1,      613, 212, 34, 11, 17, 23, 1,  50,  25,  6)  // 2007
        batters << saveBatter(1, "PennantChase Dalmatians", "Mike Schmidt", "3B", 1,     548, 157, 25, 8,  48, 12, 1,  89,  119, 2)  // 1980
        batters << saveBatter(1, "PennantChase Dalmatians", "Jimmy Rollins", "SS", 1,    716, 212, 38, 20, 30, 41, 1,  49,  85,  7)  // 2007
        batters << saveBatter(1, "PennantChase Dalmatians", "Hunter Pence", "LF", 1,     606, 190, 38, 5,  22, 8,  1,  56,  124, 1)  // 2011
        batters << saveBatter(1, "PennantChase Dalmatians", "Lonnie Smith", "RF", 1,     298, 101, 14, 4,  3,  33, 1,  26,  48,  4)  // 1980
        batters << saveBatter(1, "PennantChase Dalmatians", "Aaron Rowand", "CF", 1,     612, 189, 45, 0,  27, 6,  1,  47,  119, 19) // 2007
        batters << saveBatter(1, "PennantChase Dalmatians", "Fred Luderus", "DH", 1,     499, 157, 36, 7,  7,  9,  1,  42,  36,  7)  // 1915

        // Pitchers                                                                          BR,   H,   HR,  BB,  SO   HBP, WHIP,  BK
        rotation << savePitcher(1, "PennantChase Dalmatians", "Cliff Lee", "SP", 2,          696,  197, 0,   42,  238, 0,   1.030, 0) // 2011
        rotation << savePitcher(1, "PennantChase Dalmatians", "Steve Carlton", "SP", 5,      912,  243, 0,   90,  286, 0,   1.100, 0) // 1980
        rotation << savePitcher(1, "PennantChase Dalmatians", "Roy Halladay", "SP", 4,       699,  208, 0,   35,  220, 0,   1.040, 0) // 2011
        rotation << savePitcher(1, "PennantChase Dalmatians", "Pete Alexander", "SP", 1,     1128, 253, 0,   64,  241, 0,   0.840, 0) // 1915
        rotation << savePitcher(1, "PennantChase Dalmatians", "Cole Hamels", "SP", 3,        648,  169, 0,   44,  194, 0,   0.990, 0) // 2011

        team["batters"] = []
        team["lineup"] = batters
        team["rotation"] = rotation
        team["nextStarter"] = 0
        team["wins"] = 0
        team["losses"] = 0
        team["teamName"] = "PennantChase Dalmatians"
        team["starterIndex"] = 0
        team
    }

}
