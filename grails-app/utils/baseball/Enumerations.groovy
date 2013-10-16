package baseball

enum AtBatResult {
    SINGLE("Single"),
    DOUBLE("Double"),
    TRIPLE("Triple"),
    HOMER("Homer"),
    POP_FLY("PopFly"),
    GROUND_OUT("GroundOut"),
    WALK("Walk"),
    HIT_BY_PITCH("Walk"),
    STRIKEOUT("Strikeout"),
    STOLE_SECOND("Stole Second"),
    STOLE_THIRD("Stole Third"),
    CAUGHT_STEALING_SECOND("Caught Stealing Second"),
    CAUGHT_STEALING_THIRD("Caught Stealing Third"),
    NO_STEAL("NoSteal")

    final String value
    AtBatResult(String newValue) { this.value = newValue }
}

enum HalfInning {
    TOP("Top"),
    BOTTOM("Bottom")

    final String value
    HalfInning(String newValue) { this.value = newValue }
}

enum StatsSource {
    BASEBALL_REFERENCE("baseball-reference.com"),
    PENNANT_CHASE("pennantchase.com")

    final String value
    StatsSource(String newValue) { this.value = newValue }
}

enum SimType {
    RANDOM("Random"),
    ACCURATE("Accurate")

    final String value
    SimType(String newValue) { this.value = newValue }
}

enum SimStyle {
    BATTER_FOCUSED("BatterFocused"),
    PITCHER_FOCUSED("PitcherFocused"),
    COMBINED("Combined")

    final String value
    SimStyle(String newValue) { this.value = newValue }
}

enum Bases {
    EMPTY("Bases empty."),
    FIRST("Runner on first."),
    SECOND("Runner on second."),
    THIRD("Runner on third."),
    FIRST_AND_SECOND("Runners on first and second."),
    SECOND_AND_THIRD("Runners on second and third."),
    CORNERS("Runners on the corners."),
    LOADED("Bases loaded.")

    final String value
    Bases(String newValue) { this.value = newValue }
}

class Enumerations {
}

