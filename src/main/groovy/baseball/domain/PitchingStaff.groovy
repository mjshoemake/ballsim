package baseball.domain

class PitchingStaff extends Comparable {
    def C = "ScheduledRound"
    def rotation = []
    def bullpen = []
    def closer = null

    // Is the specified object equal to this one?
    boolean equals(PitchingStaff target) {
        boolean result = true
        def m = "${C}.equals() - "
        if (! compareObject("closer", closer, target.closer)) { result = false }
        else if (! bullpen.equals(target.bullpen)) { result = false }
        else if (! rotation.equals(target.rotation)) { result = false }

        return result
    }

    // Is the specified object equal to this one?
    boolean equals(PitchingStaff target, List builder) {
        boolean result = true
        def m = "${C}.equals() - "
        if (! compareObject("closer", closer, target.closer, builder)) { result = false }
        else if (! bullpen.equals(target.bullpen, builder)) { result = false }
        else if (! rotation.equals(target.rotation, builder)) { result = false }

        return result
    }

}
