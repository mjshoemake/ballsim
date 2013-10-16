package baseball

class BravesTeamLoader extends AbstractTeamLoader {

    def loadBraves2003() {
        def team = [:]
        def batters = []
        def rotation = []
        def bullpen = []
        // Batters                        "2003"                                  AB,  H,   2B  3B, HR, SB, CS, BB, SO, HBP
        batters << saveBatter("2003", "Braves", "Rafael Furcal", "SS", 1,   664, 194, 35, 10, 15, 25, 2,  60, 76, 3)
        batters << saveBatter("2003", "Braves", "Marcus Giles", "2B", 1,    551, 174, 49, 2,  21, 14, 4,  59, 80, 11)
        batters << saveBatter("2003", "Braves", "Julio Franco", "1B", 1,    197, 58,  12, 2,  5,  0,  1,  25, 43, 0)
        batters << saveBatter("2003", "Braves", "Gary Sheffield", "RF", 1,  576, 190, 37, 2,  39, 18, 4,  86, 55, 8)
        batters << saveBatter("2003", "Braves", "Javy Lopez", "C",    1,    457, 150, 29, 3,  43, 0,  1,  33, 90, 4)
        batters << saveBatter("2003", "Braves", "Chipper Jones", "LF", 1,   555, 169, 33, 2,  27, 2,  2,  94, 83, 1)
        batters << saveBatter("2003", "Braves", "Andruw Jones", "CF", 1,    595, 165, 28, 2,  36, 4,  3,  53, 125, 5)
        batters << saveBatter("2003", "Braves", "Vinny Castilla", "3B", 1,  542, 150, 28, 3,  22, 1,  2,  26, 86, 3)
        batters << saveBatter("2003", "Braves", "Mark Derosa", "DH", 1,     266, 70,  14, 0,  6,  1,  0,  16, 49, 5)

        // Pitchers                                                            BR,  H,   HR,  BB,  SO   HBP, WHIP,  BK
        rotation << savePitcher("2003", "Braves", "Greg Maddux", "SP", 1,      655, 225, 24,  33,  124, 8,   1.182, 0)
        rotation << savePitcher("2003", "Braves", "Russ Ortiz", "SP", 2,       637, 177, 17,  102, 149, 4,   1.314, 0)
        rotation << savePitcher("2003", "Braves", "Mike Hampton", "SP", 3,     570, 186, 14,  78,  110, 1,   1.389, 1)
        rotation << savePitcher("2003", "Braves", "Horacio Ramirez", "SP", 4,  547, 181, 21,  72,  100, 6,   1.388, 1)
        rotation << savePitcher("2003", "Braves", "Shane Reynolds", "SP", 5,   502, 191, 20,  59,  94,  8,   1.494, 1)

        team["batters"] = []
        team["lineup"] = batters
        team["rotation"] = rotation
        team["nextStarter"] = 0
        team["wins"] = 0
        team["losses"] = 0
        team["teamName"] = "Braves 2003"
        team["starterIndex"] = 0
        team
    }

    def loadBraves1997() {
        def team = [:]
        def batters = []
        def rotation = []
        def bullpen = []
        // Batters                                                          AB,  H,   2B  3B, HR, SB, CS, BB, SO,  HBP
        batters << saveBatter("1997", "Braves", "Kenny Lofton", "CF", 1,    493, 164, 20, 6,  5,  27, 20, 64, 83,  2)
        batters << saveBatter("1997", "Braves", "Jeff Blauser", "SS", 1,    519, 160, 31, 4,  17, 5,  1,  70, 101, 20)
        batters << saveBatter("1997", "Braves", "Chipper Jones", "3B", 1,   597, 176, 41, 3,  21, 20, 5,  76, 88,  0)
        batters << saveBatter("1997", "Braves", "Javy Lopez", "C", 1,       414, 122, 28, 1,  23, 1,  1,  40, 82,  5)
        batters << saveBatter("1997", "Braves", "Fred McGriff", "1B", 1,    564, 156, 25, 1,  22, 5,  0,  68, 112, 4)
        batters << saveBatter("1997", "Braves", "Ryan Klesko", "LF", 1,     467, 122, 23, 6,  24, 4,  4,  48, 130, 4)
        batters << saveBatter("1997", "Braves", "Michael Tucker", "RF", 1,  499, 141, 25, 7,  14, 12, 7,  44, 116, 6)
        batters << saveBatter("1997", "Braves", "Keith Lockhart", "DH", 1,  147, 41,  5,  3,  6,  0,  0,  14, 17,  1)
        batters << saveBatter("1997", "Braves", "Mark Lemke", "2B", 1,      351, 86,  17, 1,  2,  2,  0,  33, 51,  0)

        // Pitchers                       vv                                   BR,  H,   HR,  BB,  SO   HBP, WHIP,  BK
        rotation << savePitcher("1997", "Braves", "Greg Maddux", "SP", 1,      698, 200, 9,   20,  177, 6,   0.946, 0)
        rotation << savePitcher("1997", "Braves", "Denny Neagle", "SP", 2,     700, 204, 18,  49,  172, 6,   1.084, 0)
        rotation << savePitcher("1997", "Braves", "John Smoltz", "SP", 3,      768, 234, 21,  63,  241, 1,   1.160, 1)
        rotation << savePitcher("1997", "Braves", "Tom Glavine", "SP", 4,      720, 197, 20,  79,  152, 4,   1.150, 0)
        rotation << savePitcher("1997", "Braves", "Kevin Millwood", "SP", 5,   154, 55,  1,   21,  42,  2,   1.481, 0)

        team["batters"] = []
        team["lineup"] = batters
        team["rotation"] = rotation
        team["nextStarter"] = 0
        team["wins"] = 0
        team["losses"] = 0
        team["teamName"] = "Braves 1997"
        team["starterIndex"] = 0
        team
    }

    def loadBraves1995() {
        def team = [:]
        def batters = []
        def rotation = []
        def bullpen = []
        // Batters                                                        AB,  H,   2B  3B, HR, SB, CS, BB, SO,  HBP
        batters << saveBatter("1995", "Braves", "Javy Lopez", "C", 1,       333, 105, 11, 4,  14, 0,  1,  14, 57,  2)
        batters << saveBatter("1995", "Braves", "Fred McGriff", "1B", 1,    528, 148, 27, 1,  27, 3,  6,  65, 99,  5)
        batters << saveBatter("1995", "Braves", "Mark Lemke", "2B", 1,      399, 101, 16, 5,  5,  2,  2,  44, 40,  0)
        batters << saveBatter("1995", "Braves", "Jeff Blauser", "SS", 1,    431, 91,  16, 2,  12, 8,  5,  57, 107, 12)
        batters << saveBatter("1995", "Braves", "Chipper Jones", "3B", 1,   524, 139, 22, 3,  23, 8,  4,  73, 99,  0)
        batters << saveBatter("1995", "Braves", "Ryan Klesko", "LF", 1,     329, 102, 25, 2,  23, 5,  4,  47, 72,  2)
        batters << saveBatter("1995", "Braves", "Marquis Grissom", "CF", 1, 551, 142, 23, 3,  12, 29, 9,  47, 61,  3)
        batters << saveBatter("1995", "Braves", "David Justice", "RF", 1,   411, 104, 17, 2,  24, 4,  2,  73, 68,  2)
        batters << saveBatter("1995", "Braves", "Mike Mordecai", "DH", 1,   75,  21,  6,  0,  3,  0,  0,  9,  16,  0)

        // Pitchers                                                          BR,  H,   HR,  BB,  SO   HBP, WHIP,  BK
        rotation << savePitcher("1995", "Braves", "Greg Maddux", "SP", 1,      629, 147, 8,   23,  181, 4,   0.811, 0)
        rotation << savePitcher("1995", "Braves", "Tom Glavine", "SP", 2,      596, 182, 9,   66,  127, 5,   1.248, 0)
        rotation << savePitcher("1995", "Braves", "John Smoltz", "SP", 3,      578, 166, 15,  72,  193, 4,   1.235, 0)
        rotation << savePitcher("1995", "Braves", "Steve Avery", "SP", 4,      520, 165, 22,  52,  141, 6,   1.252, 0)
        rotation << savePitcher("1995", "Braves", "Kent Mercker", "SP", 5,     429, 143, 16,  61,  102, 3,   1.406, 2)

        team["batters"] = []
        team["lineup"] = batters
        team["rotation"] = rotation
        team["nextStarter"] = 0
        team["wins"] = 0
        team["losses"] = 0
        team["teamName"] = "Braves 1995"
        team["starterIndex"] = 0
        team
    }
}
