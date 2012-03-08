package com.gempukku.lotro.game;

public class Player {
    private int _id;
    private String _name;
    private String _type;

    public Player(int id, String name, String type) {
        _id = id;
        _name = name;
        _type = type;
    }

    public int getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public String getType() {
        return _type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        if (_name != null ? !_name.equals(player._name) : player._name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return _name != null ? _name.hashCode() : 0;
    }
}