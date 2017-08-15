package baseball

class TeamFinder {

    public String[] findYears() {
        def result = []
        ClassLoader loader = Thread.currentThread().getContextClassLoader()
        String path = loader.getResource("seasonStats").path
        File[] children = new File(path).listFiles()
        children.each() {
            String name = it.name
            if (! name.contains("PennantChase")) {
                result << name
            }
        }
        result
    }

    String[] findTeamsForYear(String year) {
        def result = []
        ClassLoader loader = Thread.currentThread().getContextClassLoader()
        String path = loader.getResource("seasonStats/$year").path
        File[] children = new File(path).listFiles()
        children.each() {
            if (it.name.contains("Batters.txt")) {
                int pos = it.name.indexOf("Batters.txt")
                String team = it.name.substring(0, pos)
                result << team
            }
        }
        result
    }
}
