package baseball.domain

class BatterComparable implements Comparable {

    String sortBy = "rank"

    BatterComparable() {
    }

    int compareTo(Object arg) {
        switch(sortBy) {
            case "rank": return compareRankTo(arg)
            case "obp": return compareObpTo(arg)
            case "slugging": return compareSluggingTo(arg)
            case "ops": return compareOpsTo(arg)
            case "homers": return compareOpsTo(arg)
        }
    }

    private int compareRankTo(Object arg) {
        Batter reference = (Batter)arg
        BigDecimal myRank = this.rank
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
        Batter reference = (Batter)arg
        BigDecimal myValue = this.onBasePercentage
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
        Batter reference = (Batter)arg
        BigDecimal myValue = this.onBasePercentage
        BigDecimal refValue = reference.onBasePercentage

        if (myValue == refValue) {
            0
        } else if (myValue > refValue) {
            -1
        } else {
            1
        }
    }

    private int compareSluggingTo(Object arg) {
        Batter reference = (Batter)arg
        BigDecimal myValue = this.sluggingPercentage
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
        Batter reference = (Batter)arg
        int myValue = this.homers
        int refValue = reference.homers

        if (myValue == refValue) {
            0
        } else if (myValue > refValue) {
            -1
        } else {
            1
        }
    }

}
