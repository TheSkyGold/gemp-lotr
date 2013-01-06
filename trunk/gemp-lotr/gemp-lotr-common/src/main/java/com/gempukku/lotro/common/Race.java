package com.gempukku.lotro.common;

public enum Race implements Filterable {
    BALROG("Balrog"), CREATURE("Creature"), WRAITH("Wraith"),
    ELF("Elf"), HOBBIT("Hobbit"), DWARF("Dwarf"), MAN("Man"), WIZARD("Wizard"), TREE("Tree"),
    URUK_HAI("Uruk-Hai"), NAZGUL("Nazgul"), ORC("Orc"), TROLL("Troll"), HALF_TROLL("Half-troll"), ENT("Ent"), SPIDER("Spider"), MAIA("Maia"),
    GOBLIN("Goblin");

    private String _humanReadable;

    private Race(String humanReadable) {
        _humanReadable = humanReadable;
    }

    public String getHumanReadable() {
        return _humanReadable;
    }

    public static Race findRaceByHumanReadable(String humanReadable) {
        for (Race race : values()) {
            if (race.getHumanReadable().equals(humanReadable))
                return race;
        }
        return null;
    }
}
