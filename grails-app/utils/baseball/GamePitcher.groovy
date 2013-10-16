package baseball

class GamePitcher
{
    def simPitcher

    int battersRetired = 0
    int order = 0
    int walks = 0
    int strikeouts = 0
    int hits = 0
    int homers = 0
    int hitByPitch = 0
    int whip = 0
    int balks = 0
    int runs = 0

    def getBattersFaced() {
        battersRetired + walks + hits + hitByPitch
    }

    def getRate(int num) {
        BigDecimal.valueOf(num / battersFaced)
    }

    def getRate(int num, int divisor) {
        BigDecimal.valueOf(num / divisor)
    }

    def reset() {
        battersRetired = 0
        order = 0
        walks = 0
        strikeouts = 0
        hits = 0
        homers = 0
        hitByPitch = 0
        whip = 0
        balks = 0
        runs = 0
    }
}
