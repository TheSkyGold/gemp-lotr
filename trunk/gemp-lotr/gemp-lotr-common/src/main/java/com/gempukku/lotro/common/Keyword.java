package com.gempukku.lotro.common;

public enum Keyword implements Filterable {
    SUPPORT_AREA("Support Area"),

    SKIRMISH("Skirmish"), FELLOWSHIP("Fellowship"), RESPONSE("Response"), MANEUVER("Maneuver"), ARCHERY("Archery"), SHADOW("Shadow"), ASSIGNMENT("Assignment"), REGROUP("Regroup"),

    CAN_START_WITH_RING("Can-bear-ring", false),

    RING_BOUND("Ring-Bound", true),

    ENDURING("Enduring", true), ROAMING("Roaming", true), MOUNTED("Mounted", false), TWILIGHT("Twilight", true), HUNTER("Hunter", true, true),

    WEATHER("Weather", true), TALE("Tale", true), SPELL("Spell", true), SEARCH("Search", true), STEALTH("Stealth", true), TENTACLE("Tentacle", true),

    RIVER("River", true), PLAINS("Plains", true), UNDERGROUND("Underground", true), SANCTUARY("Sanctuary", true), FOREST("Forest", true), MARSH("Marsh", true), MOUNTAIN("Mountain", true),
    BATTLEGROUND("Battleground", true), DWELLING("Dwelling", true),

    PIPEWEED("Pipeweed"),

    DAMAGE("Damage", true, true), DEFENDER("Defender", true, true), AMBUSH("Ambush", true, true), FIERCE("Fierce", true), ARCHER("Archer", true),
    UNHASTY("Unhasty", true), MUSTER("Muster", true), TOIL("Toil", true, true), LURKER("Lurker", true),

    RANGER("Ranger", true), TRACKER("Tracker", true), VILLAGER("Villager", true), MACHINE("Machine", true), ENGINE("Engine", true),
    SOUTHRON("Southron", true), EASTERLING("Easterling", true), VALIANT("Valiant", true), KNIGHT("Knight", true), FORTIFICATION("Fortification", true),
    WARG_RIDER("Warg-rider", true), BESIEGER("Besieger", true), CORSAIR("Corsair", true);

    private String _humanReadable;
    private boolean _infoDisplayable;
    private boolean _multiples;

    private Keyword(String humanReadable) {
        this(humanReadable, false);
    }

    private Keyword(String humanReadable, boolean infoDisplayable) {
        this(humanReadable, infoDisplayable, false);
    }

    private Keyword(String humanReadable, boolean infoDisplayable, boolean multiples) {
        _humanReadable = humanReadable;
        _infoDisplayable = infoDisplayable;
        _multiples = multiples;
    }

    public String getHumanReadable() {
        return _humanReadable;
    }

    public boolean isInfoDisplayable() {
        return _infoDisplayable;
    }

    public boolean isMultiples() {
        return _multiples;
    }
}
