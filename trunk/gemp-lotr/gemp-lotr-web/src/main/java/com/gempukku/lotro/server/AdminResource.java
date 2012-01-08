package com.gempukku.lotro.server;

import com.gempukku.lotro.db.CollectionDAO;
import com.gempukku.lotro.db.DeckDAO;
import com.gempukku.lotro.db.LeagueDAO;
import com.gempukku.lotro.db.LeagueSerieDAO;
import com.gempukku.lotro.db.vo.League;
import com.gempukku.lotro.game.DefaultCardCollection;
import com.gempukku.lotro.game.LotroCardBlueprintLibrary;
import com.gempukku.lotro.game.MutableCardCollection;
import com.gempukku.lotro.game.Player;
import com.gempukku.lotro.hall.HallServer;
import com.sun.jersey.spi.resource.Singleton;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Singleton
@Path("/admin")
public class AdminResource extends AbstractResource {
    @Context
    private CollectionDAO _collectionDao;
    @Context
    private DeckDAO _deckDao;
    @Context
    private LeagueDAO _leagueDao;
    @Context
    private LeagueSerieDAO _leagueSeasonDao;
    @Context
    private LotroCardBlueprintLibrary _library;
    @Context
    private HallServer _hallServer;

    @Path("/clearCache")
    @GET
    public String clearCache(@Context HttpServletRequest request) throws Exception {
        validateAdmin(request);

        _playerDao.clearCache();
        _collectionDao.clearCache();
        _deckDao.clearCache();
        _leagueDao.clearCache();

        return "OK";
    }

    @Path("/setMotd")
    @POST
    public String setMotd(
            @FormParam("motd") String motd,
            @Context HttpServletRequest request) throws Exception {
        validateAdmin(request);

        _hallServer.setMOTD(motd);

        return "OK";
    }

    @Path("/addLeague")
    @POST
    public String addLeague(
            @FormParam("name") String name,
            @FormParam("type") String type,
            @FormParam("start") int start,
            @FormParam("end") int end,
            @FormParam("product") String product,
            @Context HttpServletRequest request) throws Exception {
        validateAdmin(request);

        DefaultCardCollection leagueCollection = new DefaultCardCollection();
        Map<String, Integer> productItems = getProductItems(product);
        for (Map.Entry<String, Integer> productItem : productItems.entrySet())
            leagueCollection.addItem(productItem.getKey(), productItem.getValue());

        _leagueDao.addLeague(name, type, leagueCollection, start, end);

        return "OK";
    }


    @Path("/addLeagueSeason")
    @POST
    public String addLeagueSeason(
            @FormParam("leagueType") String leagueType,
            @FormParam("type") String type,
            @FormParam("format") String format,
            @FormParam("start") int start,
            @FormParam("end") int end,
            @FormParam("maxMatches") int maxMatches,
            @Context HttpServletRequest request) throws Exception {
        validateAdmin(request);

        _leagueSeasonDao.addSerie(leagueType, type, format, start, end, maxMatches);

        return "OK";
    }

    @Path("/addLeagueProduct")
    @POST
    public String addLeagueProduct(
            @FormParam("leagueType") String leagueType,
            @FormParam("product") String product,
            @FormParam("skipBaseCollection") String skipBaseCollection,
            @Context HttpServletRequest request) throws Exception {
        validateAdmin(request);

        League league = getLeagueByType(leagueType);
        if (league == null)
            throw new WebApplicationException(Response.Status.NOT_FOUND);

        DefaultCardCollection items = new DefaultCardCollection();

        Map<String, Integer> productItems = getProductItems(product);
        for (Map.Entry<String, Integer> productItem : productItems.entrySet())
            items.addItem(productItem.getKey(), productItem.getValue());

        if (skipBaseCollection == null || !skipBaseCollection.equals("true")) {
            MutableCardCollection baseCollection = league.getBaseCollection();

            for (Map.Entry<String, Integer> productItem : productItems.entrySet())
                baseCollection.addItem(productItem.getKey(), productItem.getValue());
            _leagueDao.setBaseCollectionForLeague(league, baseCollection);
        }

        Map<Integer, MutableCardCollection> playerCollections = _collectionDao.getPlayerCollectionsByType(leagueType);
        for (Map.Entry<Integer, MutableCardCollection> playerCollection : playerCollections.entrySet()) {
            int playerId = playerCollection.getKey();
            MutableCardCollection collection = playerCollection.getValue();
            for (Map.Entry<String, Integer> productItem : productItems.entrySet())
                collection.addItem(productItem.getKey(), productItem.getValue());
            Player player = _playerDao.getPlayer(playerId);
            _collectionDao.setCollectionForPlayer(player, leagueType, collection);
            _deliveryService.addPackage(player, league.getName(), items);
        }

        return "OK";
    }

    @Path("/addItems")
    @POST
    public String addItems(
            @FormParam("collectionType") String collectionType,
            @FormParam("product") String product,
            @FormParam("players") String players,
            @Context HttpServletRequest request) throws Exception {
        validateAdmin(request);

        String packageName = getPackageNameByCollectionType(collectionType);

        DefaultCardCollection items = new DefaultCardCollection();

        Map<String, Integer> productItems = getProductItems(product);
        for (Map.Entry<String, Integer> productItem : productItems.entrySet())
            items.addItem(productItem.getKey(), productItem.getValue());

        List<String> playerNames = getItems(players);

        for (String playerName : playerNames) {
            Player player = _playerDao.getPlayer(playerName);

            MutableCardCollection collection = getPlayerCollection(player, collectionType);
            for (Map.Entry<String, Integer> productItem : productItems.entrySet())
                collection.addItem(productItem.getKey(), productItem.getValue());
            _collectionDao.setCollectionForPlayer(player, collectionType, collection);
            _deliveryService.addPackage(player, packageName, items);
        }

        return "OK";
    }

    private String getPackageNameByCollectionType(String collectionType) {
        String packageName;
        if (collectionType.equals("permanent"))
            packageName = "My cards";
        else {
            League league = getLeagueByType(collectionType);
            if (league == null)
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            packageName = league.getName();
        }
        return packageName;
    }

    private MutableCardCollection getPlayerCollection(Player player, String collectionType) {
        MutableCardCollection collection = _collectionDao.getCollectionForPlayer(player, collectionType);
        if (collection == null && collectionType.equals("permanent"))
            collection = new DefaultCardCollection();
        return collection;
    }

    private List<String> getItems(String values) {
        List<String> result = new LinkedList<String>();
        for (String pack : values.split("\n")) {
            String blueprint = pack.trim();
            if (blueprint.length() > 0)
                result.add(blueprint);
        }
        return result;
    }

    private Map<String, Integer> getProductItems(String values) {
        Map<String, Integer> result = new HashMap<String, Integer>();
        for (String item : values.split("\n")) {
            item = item.trim();
            if (item.length() > 0) {
                final String[] itemSplit = item.split("x", 2);
                if (itemSplit.length != 2)
                    throw new RuntimeException("Unable to parse the items");
                result.put(itemSplit[1].trim(), Integer.parseInt(itemSplit[0].trim()));
            }
        }
        return result;
    }

    private League getLeagueByType(String leagueType) {
        for (League league : _leagueDao.getActiveLeagues())
            if (league.getType().equals(leagueType))
                return league;
        return null;
    }

    private void validateAdmin(HttpServletRequest request) {
        Player player = getResourceOwnerSafely(request, null);

        if (!player.getType().equals("a"))
            sendError(Response.Status.FORBIDDEN);
    }
}
