package baseball.domain

/**
 * Created by Dad on 6/25/16.
 */
class TeamComparable implements Comparable {

    final Map myteam;

    TeamComparable(Map team) {
        myteam = team
    }

    int compareTo(Object arg) {
        Map team = (Map)arg
        int myDiff = myteam["winDiff"]
        int compDiff = team["winDiff"]
        if (myDiff == compDiff) {
            0
        } else if (myDiff > compDiff) {
            1
        } else {
            -1
        }
    }

}
