package baseball.domain

import java.math.RoundingMode

class GamePitcher extends GamePitcherComparable
{
    def simPitcher

    String nameFirst, nameLast
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
    int pitches = 0

    GamePitcher() {
    }

    GamePitcher(Player player) {
        simPitcher = new SimPitcher()
        simPitcher.pitcher = player
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

    def getEra() {
        BigDecimal games = new BigDecimal(battersRetired)
        games = games.divide(27, 5, RoundingMode.HALF_UP)
        BigDecimal result = new BigDecimal(runs)
        result = result.divide(games, 3, RoundingMode.HALF_UP)
        String padded = result.toString() + "000"
        if (result.intValue() >= 10) {
            format(padded, 5)
        } else {
            format(padded, 4) + ' '
        }
        result
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
    def getOppAvg() {
        hits / (battersRetired + hits)
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
        pitches = 0
    }

    public boolean pitcherExhausted() {
        battersRetired > avgBattersRetiredPerGame
    }

    public def getAvgBattersRetiredPerGame() {
        simPitcher.avgBattersRetiredPerGame
    }

}
