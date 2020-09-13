package baseball.domain

/**
 * Created by Dad on 6/25/16.
 */
class GameBatterComparable implements Comparable {

    GameBatterComparable() {
    }

    String sortBy = "rank"

    int compareTo(Object arg) {
        switch(sortBy) {
            case "rank": return compareRankTo(arg)
            case "obp": return compareObpTo(arg)
            case "slugging": return compareSluggingTo(arg)
            case "ops": return compareOpsTo(arg)
            case "homers": return compareOpsTo(arg)
            case "leadoff": return compareLeadOffTo(arg)
        }
    }

    private int compareRankTo(Object arg) {
        Player reference = (Player)arg.simBatter.batter
        BigDecimal myRank = this.simBatter.batter.rank
        BigDecimal refRank = reference.rank

        if (myRank == refRank) {
            0
        } else if (myRank > refRank) {
            -1
        } else {
            1
        }
    }

    private int compareObpTo(Object arg) {
        Player reference = (Player)arg.simBatter.batter
        BigDecimal myValue = this.simBatter.batter.onBasePercentage
        BigDecimal refValue = reference.onBasePercentage

        if (myValue == refValue) {
            0
        } else if (myValue > refValue) {
            -1
        } else {
            1
        }
    }

    private int compareOpsTo(Object arg) {
        Player reference = (Player)arg.simBatter.batter
        BigDecimal myValue = this.simBatter.batter.ops
        BigDecimal refValue = reference.ops

        if (myValue == refValue) {
            0
        } else if (myValue > refValue) {
            -1
        } else {
            1
        }
    }

    private int compareSluggingTo(Object arg) {
        Player reference = (Player)arg.simBatter.batter
        BigDecimal myValue = this.simBatter.batter.sluggingPercentage
        BigDecimal refValue = reference.sluggingPercentage

        if (myValue == refValue) {
            0
        } else if (myValue > refValue) {
            -1
        } else {
            1
        }
    }

    private int compareHomersTo(Object arg) {
        Player reference = (Player)arg.simBatter.batter
        int myValue = this.simBatter.batter.homers
        int refValue = reference.homers

        if (myValue == refValue) {
            0
        } else if (myValue > refValue) {
            -1
        } else {
            1
        }
    }

    private int compareLeadOffTo(Object arg) {
        Player reference = (Player)arg.simBatter.batter
        BigDecimal myValue = BigDecimal.valueOf(this.simBatter.batter.stolenBases / 50) + this.simBatter.batter.onBasePercentage
        int refValue = BigDecimal.valueOf(reference.stolenBases / 50) + reference.onBasePercentage

        if (myValue == refValue) {
            0
        } else if (myValue > refValue) {
            -1
        } else {
            1
        }
    }

}
