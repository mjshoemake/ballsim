package baseball.domain

class League {
    String basePath = "/ballsim/"
    File dir = null

    League(String basePath, leagueName) {
        if (basePath) {
            if (! basePath.endsWith("/")) {
                this.basePath = basePath + "/"
            } else {
                this.basePath = basePath
            }
        }
        dir = new File("${basePath}${leagueName}")
        dir.mkdirs()
    }



}
