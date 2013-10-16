package baseball

import org.apache.log4j.Logger

class Pitcher
{
    def gameLog = Logger.getLogger('gamelog')

    String name
    String teamName
    String year
    String position
    int battersRetired
    int orderPos
    int walks
    int strikeouts
    int hits
    int homers
    int hitByPitch
    double whip
    int balks

    static hasMany = [simPitcher: SimPitcher]


    static constraints = {
        teamName(blank:false, maxSize:40)
        year(blank: false, maxSize: 35)
        name(blank:false, maxSize:35)
        position(blank:false, maxSize:2)
        battersRetired(nullable: false)
        orderPos(nullable: false)
        walks(nullable: false)
        strikeouts(nullable: false)
        hits(nullable: false)
        homers(nullable: false)
        hitByPitch(nullable: false)
        whip(nullable: false)
        balks(nullable: false)
    }

    public def getBattersFaced() {
        battersRetired + walks + hits + hitByPitch
    }

    def getOppBattingAvg() {
        int oppAtBats = battersRetired + hits
        if (oppAtBats == 0) {
            BigDecimal.valueOf(0)
        }  else {
            BigDecimal.valueOf(hits / oppAtBats)
        }
    }

    public def getRate(int num) {
        def rate = BigDecimal.valueOf(num / battersFaced)
        rate
    }

    public def getRate(int num, int divisor) {
        def rate = BigDecimal.valueOf(num / divisor)
        rate
    }

}
