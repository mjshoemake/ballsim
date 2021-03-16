package baseball.domain

class LeagueComparable extends baseball.domain.Comparable implements java.lang.Comparable {

    LeagueComparable() {
    }

    static String sortBy = "abbreviation"

    int compareTo(Object arg) {
        //switch(sortBy) {
        //    case "abbreviation": return compareAbbreviationTo(arg)
        //    case "teamName": return compareTeamNameTo(arg)
        //}
        return compareAbbreviationTo(arg)
    }

    private int compareAbbreviationTo(Object arg) {
        League reference = (League)arg
        String myValue = this["abbreviation"]
        String refValue = reference["abbreviation"]

        if (myValue == refValue) {
            0
        } else if (myValue > refValue) {
            -1
        } else {
            1
        }
    }

}
