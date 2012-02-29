package com.gempukku.lotro.server;

import com.gempukku.lotro.cards.packs.RarityReader;
import com.gempukku.lotro.cards.packs.SetRarity;
import com.gempukku.lotro.collection.CollectionsManager;
import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.db.LeagueDAO;
import com.gempukku.lotro.db.vo.CollectionType;
import com.gempukku.lotro.db.vo.League;
import com.gempukku.lotro.game.CardCollection;
import com.gempukku.lotro.game.LotroCardBlueprintLibrary;
import com.gempukku.lotro.game.LotroServer;
import com.gempukku.lotro.game.Player;
import com.gempukku.lotro.league.LeagueService;
import com.gempukku.lotro.packs.PacksStorage;
import com.sun.jersey.spi.resource.Singleton;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
@Path("/collection")
public class CollectionResource extends AbstractResource {
    private static final Logger _logger = Logger.getLogger(CollectionResource.class);

    @Context
    private LotroServer _lotroServer;
    @Context
    private CollectionsManager _collectionsManager;
    @Context
    private LeagueDAO _leagueDao;
    @Context
    private LotroCardBlueprintLibrary _library;
    @Context
    private LeagueService _leagueService;
    @Context
    private PacksStorage _packStorage;

    private Map<String, SetRarity> _rarities;

    public CollectionResource() {
        _rarities = new HashMap<String, SetRarity>();
        RarityReader reader = new RarityReader();
        _rarities.put("0", reader.getSetRarity("0"));
        _rarities.put("1", reader.getSetRarity("1"));
        _rarities.put("2", reader.getSetRarity("2"));
        _rarities.put("3", reader.getSetRarity("3"));
        _rarities.put("4", reader.getSetRarity("4"));
        _rarities.put("5", reader.getSetRarity("5"));
        _rarities.put("6", reader.getSetRarity("6"));
    }

    @Path("/{collectionType}")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Document getCollection(
            @PathParam("collectionType") String collectionType,
            @QueryParam("participantId") String participantId,
            @QueryParam("filter") String filter,
            @QueryParam("start") int start,
            @QueryParam("count") int count,
            @Context HttpServletRequest request,
            @Context HttpServletResponse response) throws ParserConfigurationException {
        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        final League league = _leagueService.getLeagueByType(collectionType);
        if (league != null)
            _leagueService.ensurePlayerIsInLeague(resourceOwner, league);

        CardCollection collection = getCollection(resourceOwner, collectionType);
        if (collection == null)
            sendError(Response.Status.NOT_FOUND);

        List<CardCollection.Item> filteredResult = collection.getItems(filter, _library, _rarities);

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        Document doc = documentBuilder.newDocument();

        Element collectionElem = doc.createElement("collection");
        collectionElem.setAttribute("count", String.valueOf(filteredResult.size()));
        doc.appendChild(collectionElem);

        int index = 0;
        for (CardCollection.Item item : filteredResult) {
            if (index >= start && index < start + count) {
                String blueprintId = item.getBlueprintId();
                if (item.getType() == CardCollection.Item.Type.CARD) {
                    Element card = doc.createElement("card");
                    card.setAttribute("count", String.valueOf(item.getCount()));
                    card.setAttribute("blueprintId", blueprintId);
                    Side side = _library.getLotroCardBlueprint(blueprintId).getSide();
                    if (side != null)
                        card.setAttribute("side", side.toString());
                    collectionElem.appendChild(card);
                } else {
                    Element pack = doc.createElement("pack");
                    pack.setAttribute("count", String.valueOf(item.getCount()));
                    pack.setAttribute("blueprintId", blueprintId);
                    if (blueprintId.startsWith("(S)")) {
                        List<CardCollection.Item> contents = _packStorage.openPack(blueprintId);
                        StringBuilder contentsStr = new StringBuilder();
                        for (CardCollection.Item content : contents)
                            contentsStr.append(content.getBlueprintId()).append("|");
                        contentsStr.delete(contentsStr.length() - 1, contentsStr.length());
                        pack.setAttribute("contents", contentsStr.toString());
                    }
                    collectionElem.appendChild(pack);
                }
            }
            index++;
        }

        processDeliveryServiceNotification(request, response);

        return doc;
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Document getCollectionTypes(
            @QueryParam("participantId") String participantId,
            @Context HttpServletRequest request) throws ParserConfigurationException {
        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        Document doc = documentBuilder.newDocument();

        Element collectionsElem = doc.createElement("collections");

        for (League league : _leagueService.getActiveLeagues()) {
            Element collectionElem = doc.createElement("collection");
            collectionElem.setAttribute("type", league.getType());
            collectionElem.setAttribute("name", league.getName());
            collectionsElem.appendChild(collectionElem);
        }

        doc.appendChild(collectionsElem);
        return doc;
    }

    private CardCollection getCollection(Player player, String collectionType) {
        if (collectionType.equals("default"))
            return _lotroServer.getDefaultCollection();
        else
            return _collectionsManager.getPlayerCollection(player, collectionType);
    }

    @Path("/{collectionType}")
    @POST
    @Produces(MediaType.APPLICATION_XML)
    public Document openPack(
            @PathParam("collectionType") String collectionType,
            @FormParam("participantId") String participantId,
            @FormParam("pack") String packId,
            @FormParam("selection") String selection,
            @Context HttpServletRequest request,
            @Context HttpServletResponse response) throws ParserConfigurationException {
        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        CollectionType collectionTypeObj = createCollectionType(collectionType);
        CardCollection packContents = _collectionsManager.openPackInPlayerCollection(resourceOwner, collectionTypeObj, selection, _packStorage, packId);

        if (packContents == null)
            sendError(Response.Status.NOT_FOUND);

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        Document doc = documentBuilder.newDocument();

        Element collectionElem = doc.createElement("pack");
        doc.appendChild(collectionElem);

        for (Map.Entry<String, Integer> itemCount : packContents.getAll().entrySet()) {
            String blueprintId = itemCount.getKey();
            if (blueprintId.contains("_")) {
                Element card = doc.createElement("card");
                card.setAttribute("count", String.valueOf(itemCount.getValue()));
                card.setAttribute("blueprintId", blueprintId);
                Side side = _library.getLotroCardBlueprint(blueprintId).getSide();
                if (side != null)
                    card.setAttribute("side", side.toString());
                collectionElem.appendChild(card);
            } else {
                Element pack = doc.createElement("pack");
                pack.setAttribute("count", String.valueOf(itemCount.getValue()));
                pack.setAttribute("blueprintId", blueprintId);
                collectionElem.appendChild(pack);
            }
        }

        processDeliveryServiceNotification(request, response);

        return doc;
    }

    private CollectionType createCollectionType(String collectionType) {
        if (collectionType.equals("permanent"))
            return new CollectionType("permanent", "My cards");

        return _leagueService.getLeagueByType(collectionType).getCollectionType();
    }
}