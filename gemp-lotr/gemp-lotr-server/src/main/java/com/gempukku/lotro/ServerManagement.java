package com.gempukku.lotro;

import com.gempukku.lotro.db.DbAccess;
import com.gempukku.lotro.game.LotroCardBlueprintLibrary;

public class ServerManagement {
    public static void main(String[] args) throws Exception {
        DbAccess dbAccess = new DbAccess();
        LotroCardBlueprintLibrary library = new LotroCardBlueprintLibrary();

//        addLeague(dbAccess);
//        League league = getLeague(dbAccess, library, "league_test");
//        CardCollection leagueCollection = league.getBaseCollection();
//        List<CardCollection.Item> items = leagueCollection.getItems("");
//        System.out.println(items.size());
    }

//    private static void addLeague(DbAccess dbAccess) throws IOException, SQLException {
//        LeagueDAO leagueDao = new LeagueDAO(dbAccess, new CollectionSerializer());
//        DefaultCardCollection collection = new DefaultCardCollection();
//        collection.addItem("FotR - League Starter", 1);
//
//        leagueDao.addLeague("Test league", "league_test", collection, 20111122, 20121123);
//    }
//
//    private static League getLeague(DbAccess dbAccess, String leagueType) {
//        LeagueDAO leagueDao = new LeagueDAO(dbAccess, new CollectionSerializer());
//        for (League league : leagueDao.getActiveLeagues()) {
//            if (league.getType().equals(leagueType))
//                return league;
//        }
//        return null;
//    }
}