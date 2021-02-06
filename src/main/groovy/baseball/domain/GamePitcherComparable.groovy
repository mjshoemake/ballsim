package baseball.domain

/**
 * Created by Dad on 6/25/16.
 */
class GamePitcherComparable extends baseball.domain.Comparable implements java.lang.Comparable {

    GamePitcherComparable() {
    }

    String sortBy = "rank"

    int compareTo(Object arg) {
        switch(sortBy) {
            case "rank": return compareRankTo(arg)
            case "gs": return compareGamesStartedTo(arg)
            case "era": return compareEraTo(arg)
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

    private int compareGamesStartedTo(Object arg) {
        PitcherStats reference = (PitcherStats)arg.simPitcher.pitcher
        BigDecimal myValue = this.simPitcher.pitcher.gamesStarted
        BigDecimal refValue = reference.gamesStarted

        if (myValue == refValue) {
            0
        } else if (myValue > refValue) {
            -1
        } else {
            1
        }
    }

    private int compareEraTo(Object arg) {
        PitcherStats reference = (PitcherStats)arg.simPitcher.pitcher
        BigDecimal myValue = this.simBatter.batter.era
        BigDecimal refValue = reference.era

        if (myValue == refValue) {
            0
        } else if (myValue > refValue) {
            -1
        } else {
            1
        }
    }

}
