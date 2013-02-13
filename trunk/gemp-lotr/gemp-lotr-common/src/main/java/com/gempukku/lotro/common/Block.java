package com.gempukku.lotro.common;

public enum Block {
    FELLOWSHIP("Fellowship"), TWO_TOWERS("Towers"), KING("King"), SHADOWS("Shadows and onwards"), SPECIAL("Special"),
    SECOND_ED("2nd edition");

    private String _humanReadable;

    private Block(String humanReadable) {
        _humanReadable = humanReadable;
    }

    public String getHumanReadable() {
        return _humanReadable;
    }
}
