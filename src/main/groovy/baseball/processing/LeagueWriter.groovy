package baseball.processing

import baseball.domain.League

class LeagueWriter {

    def basePath = "/ballsim/"

    LeagueWriter (String basePath) {
        if (basePath) {
            if (! basePath.endsWith("/")) {
                this.basePath = basePath + "/"
            } else {
                this.basePath = basePath
            }
        }
    }

    League getLeague(String leagueName) {
    }
}
