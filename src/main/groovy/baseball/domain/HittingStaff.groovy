package baseball.domain

class HittingStaff extends Comparable {
    def lineup = []
    def bench = []
    def pitchers = []

    // Is the specified object equal to this one?
    boolean equals(HittingStaff target) {
        boolean result = true
        def m = "${C}.equals() - "
        if (! bench.equals(target.bench)) { result = false }
        else if (! lineup.equals(target.lineup)) { result = false }
        else if (! pitchers.equals(target.pitchers)) { result = false }

        return result
    }

    // Is the specified object equal to this one?
    boolean equals(HittingStaff target, List builder) {
        boolean result = true
        def m = "${C}.equals() - "
        if (! bench.equals(target.bench, builder)) { result = false }
        else if (! lineup.equals(target.lineup, builder)) { result = false }
        else if (! pitchers.equals(target.pitchers, builder)) { result = false }

        return result
    }
}
