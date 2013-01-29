package com.gempukku.lotro.common;

public enum PossessionClass implements Filterable {
    HAND_WEAPON("Hand Weapon"), ARMOR("Armor"), HELM("Helm"), MOUNT("Mount"), RANGED_WEAPON("Ranged Weapon"),
    CLOAK("Cloak"), PIPE("Pipe"), PIPEWEED("Pipeweed"), SHIELD("Shield"), BRACERS("Bracers"), STAFF("Staff"), RING("Ring"),
    BROOCH("Brooch"), GAUNTLETS("Gauntlets"), BOX("Box"), PALANTIR("Palantir"), PHIAL("Phial"), HORN("Horn");

    private String _humanReadable;

    private PossessionClass(String humanReadable) {
        _humanReadable = humanReadable;
    }

    public String getHumanReadable() {
        return _humanReadable;
    }
}
