package com.gempukku.lotro.server;

import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.db.DeckDAO;
import com.gempukku.lotro.game.*;
import com.gempukku.lotro.hall.HallServer;
import com.gempukku.lotro.logic.vo.LotroDeck;
import com.sun.jersey.spi.resource.Singleton;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Singleton
@Path("/deck")
public class DeckResource extends AbstractResource {
    @Context
    private LotroServer _lotroServer;
    @Context
    private HallServer _hallServer;
    @Context
    private LotroCardBlueprintLibrary _library;
    @Context
    private DeckDAO _deckDao;

    private SortAndFilterCards _sortAndFilterCards = new SortAndFilterCards();

    @Path("/list")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Document listDecks(
            @QueryParam("participantId") String participantId,
            @Context HttpServletRequest request) throws ParserConfigurationException {
        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        List<String> names = new ArrayList<String>(_deckDao.getPlayerDeckNames(resourceOwner));
        Collections.sort(names);

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document doc = documentBuilder.newDocument();
        Element decksElem = doc.createElement("decks");
        for (String name : names) {
            Element deckElem = doc.createElement("deck");
            deckElem.appendChild(doc.createTextNode(name));
            decksElem.appendChild(deckElem);
        }
        doc.appendChild(decksElem);
        return doc;
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Document getDeck(
            @QueryParam("deckName") String deckName,
            @QueryParam("participantId") String participantId,
            @Context HttpServletRequest request) throws ParserConfigurationException {
        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        LotroDeck deck = _deckDao.getDeckForPlayer(resourceOwner, deckName);

        if (deck == null) {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document doc = documentBuilder.newDocument();
            Element deckElem = doc.createElement("deck");
            doc.appendChild(deckElem);
            return doc;
        }

        return serializeDeck(deck);
    }

    @POST
    @Produces(MediaType.APPLICATION_XML)
    public Document saveDeck(
            @FormParam("deckName") String deckName,
            @FormParam("participantId") String participantId,
            @FormParam("deckContents") String contents,
            @Context HttpServletRequest request) throws ParserConfigurationException {
        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        LotroDeck lotroDeck = _lotroServer.createDeckWithValidate(contents);
        if (lotroDeck == null)
            sendError(Response.Status.BAD_REQUEST);

        _deckDao.saveDeckForPlayer(resourceOwner, deckName, lotroDeck);

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        Document doc = documentBuilder.newDocument();
        Element deckElem = doc.createElement("ok");
        doc.appendChild(deckElem);

        return doc;
    }

    @Path("/rename")
    @POST
    @Produces(MediaType.APPLICATION_XML)
    public Document renameDeck(
            @FormParam("oldDeckName") String oldDeckName,
            @FormParam("deckName") String deckName,
            @FormParam("participantId") String participantId,
            @Context HttpServletRequest request) throws ParserConfigurationException {
        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        LotroDeck deck = _deckDao.renameDeck(resourceOwner, oldDeckName, deckName);
        if (deck == null)
            sendError(Response.Status.NOT_FOUND);

        return serializeDeck(deck);
    }

    @Path("/delete")
    @POST
    public void deleteDeck(
            @FormParam("deckName") String deckName,
            @FormParam("participantId") String participantId,
            @Context HttpServletRequest request) throws ParserConfigurationException {
        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        _deckDao.deleteDeckForPlayer(resourceOwner, deckName);
    }

    @Path("/stats")
    @POST
    @Produces("text/html")
    public String getDeckStats(
            @FormParam("participantId") String participantId,
            @FormParam("deckContents") String contents,
            @Context HttpServletRequest request) {
        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        LotroDeck deck = _lotroServer.createDeckWithValidate(contents);
        if (deck == null)
            sendError(Response.Status.BAD_REQUEST);

        int fpCount = 0;
        int shadowCount = 0;
        for (String card : deck.getAdventureCards()) {
            Side side = _library.getLotroCardBlueprint(card).getSide();
            if (side == Side.SHADOW)
                shadowCount++;
            else if (side == Side.FREE_PEOPLE)
                fpCount++;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<b>Free People</b>: " + fpCount + ", <b>Shadow</b>: " + shadowCount + "<br/>");

        for (LotroFormat format : _hallServer.getSupportedFormats().values()) {
            try {
                format.validateDeck(resourceOwner, deck);
                sb.append("<b>" + format.getName() + "</b>: <font color='green'>valid</font><br/>");
            } catch (DeckInvalidException exp) {
                sb.append("<b>" + format.getName() + "</b>: <font color='red'>" + exp.getMessage() + "</font><br/>");
            }
        }

        return sb.toString();
    }

    private Document serializeDeck(LotroDeck deck) throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        Document doc = documentBuilder.newDocument();
        Element deckElem = doc.createElement("deck");
        doc.appendChild(deckElem);

        if (deck.getRingBearer() != null) {
            Element ringBearer = doc.createElement("ringBearer");
            ringBearer.setAttribute("blueprintId", deck.getRingBearer());
            deckElem.appendChild(ringBearer);
        }

        if (deck.getRing() != null) {
            Element ring = doc.createElement("ring");
            ring.setAttribute("blueprintId", deck.getRing());
            deckElem.appendChild(ring);
        }

        for (CardItem cardItem : _sortAndFilterCards.process("sort:siteNumber,twilight", createCardItems(deck.getSites()), _library, null)) {
            Element site = doc.createElement("site");
            site.setAttribute("blueprintId", cardItem.getBlueprintId());
            deckElem.appendChild(site);
        }

        for (CardItem cardItem : _sortAndFilterCards.process("sort:cardType,culture,name", createCardItems(deck.getAdventureCards()), _library, null)) {
            Element card = doc.createElement("card");
            card.setAttribute("side", _library.getLotroCardBlueprint(cardItem.getBlueprintId()).getSide().toString());
            card.setAttribute("blueprintId", cardItem.getBlueprintId());
            deckElem.appendChild(card);
        }

        return doc;
    }

    private List<CardItem> createCardItems(List<String> blueprintIds) {
        List<CardItem> cardItems = new LinkedList<CardItem>();
        for (String blueprintId : blueprintIds)
            cardItems.add(new BasicCardItem(blueprintId));

        return cardItems;
    }

    private static class BasicCardItem implements CardItem {
        private String _blueprintId;

        private BasicCardItem(String blueprintId) {
            _blueprintId = blueprintId;
        }

        @Override
        public String getBlueprintId() {
            return _blueprintId;
        }
    }
}