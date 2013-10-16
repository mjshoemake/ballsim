package baseball

class SimPitcher {

    int wins = 0
    int losses = 0
    int saves = 0
    int blownSaves = 0
    int battersRetired = 0
    int orderPos = 0
    int walks = 0
    int strikeouts = 0
    int hits = 0
    int homers = 0
    int hitByPitch = 0
    double whip = 0
    int balks = 0
    int runs = 0
    int ipGame1 = 0
    int ipGame2 = 0
    int ipGame3 = 0
    int ipGame4 = 0
    int ipGame5 = 0

    static hasOne = [pitcher: Pitcher]

    static constraints = {
        wins(nullable: false)
        losses(nullable: false)
        saves(nullable: false)
        blownSaves(nullable: false)
        battersRetired(nullable: false)
        orderPos(nullable: false)
        walks(nullable: false)
        strikeouts(nullable: false)
        hits(nullable: false)
        homers(nullable: false)
        hitByPitch(nullable: false)
        whip(nullable: false)
        balks(nullable: false)
        runs(nullable: false)
        ipGame1(nullable: false)
        ipGame2(nullable: false)
        ipGame3(nullable: false)
        ipGame4(nullable: false)
        ipGame5(nullable: false)
    }

    def getBattersFaced() {
        battersRetired + walks + hits + hitByPitch
    }

    def getRate(int num) {
        BigDecimal.valueOf(num / battersFaced)
    }

    def getRate(int num, int divisor) {
        BigDecimal.valueOf(num / divisor)
    }

    def getOppBattingAvg() {
        int oppAtBats = battersRetired + hits
        if (oppAtBats == 0) {
            BigDecimal.valueOf(0)
        }  else {
            BigDecimal.valueOf(hits / oppAtBats)
        }
    }

    def getEra() {
        def era = runs / (battersRetired/27)
        def padded = era + "000"
        if (era >= 10) {
            format(padded, 5)
        } else {
            format(padded, 4) + ' '
        }
    }

    private def format(def text, int length) {
        text = text + ''
        if (text.length() < length) {
            int remainder = length - text.length()
            text + (" " * remainder)
        } else {
            text.substring(0, length)
        }
    }
}
