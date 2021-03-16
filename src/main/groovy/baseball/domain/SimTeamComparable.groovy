package baseball.domain

class SimTeamComparable extends baseball.domain.Comparable implements java.lang.Comparable {

    SimTeamComparable() {
    }

    static String sortBy = "winDiff"

    int compareTo(Object arg) {
        switch(sortBy) {
            case "winDiff": return compareWinDiffTo(arg)
            case "teamName": return compareTeamNameTo(arg)
        }
    }

    private int compareTeamNameTo(Object arg) {
        SimTeam reference = (SimTeam)arg
        String myValue = this["teamName"]
        String refValue = reference["teamName"]

        if (myValue == refValue) {
            0
        } else if (myValue > refValue) {
            -1
        } else {
            1
        }
    }
    int compareWinDiffTo(Object arg) {
        SimTeam reference = (SimTeam)arg
        int myDiff = this["winDiff"]
        int compDiff = reference["winDiff"]
        if (myDiff == compDiff) {
            0
        } else if (myDiff > compDiff) {
            1
        } else {
            -1
        }
    }

}
