package com.gempukku.lotro.db.vo;

import com.gempukku.lotro.league.LeagueData;

import java.lang.reflect.Constructor;

public class League {
    private int _id;
    private String _name;
    private String _type;
    private String _clazz;
    private String _parameters;
    private int _start;
    private int _end;
    private int _status;

    public League(int id, String name, String type, String clazz, String parameters, int start, int end, int status) {
        _id = id;
        _name = name;
        _type = type;
        _clazz = clazz;
        _parameters = parameters;
        _start = start;
        _end = end;
        _status = status;
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

    public LeagueData getLeagueData() {
        try {
            Class<?> aClass = Class.forName(_clazz);
            Constructor<?> constructor = aClass.getConstructor(String.class);
            return (LeagueData) constructor.newInstance(_parameters);
        } catch (Exception exp) {
            throw new RuntimeException("Unable to create LeagueData", exp);
        }
    }

    public int getStart() {
        return _start;
    }

    public int getEnd() {
        return _end;
    }

    public int getStatus() {
        return _status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        League league = (League) o;

        if (_id != league._id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return _id;
    }
}