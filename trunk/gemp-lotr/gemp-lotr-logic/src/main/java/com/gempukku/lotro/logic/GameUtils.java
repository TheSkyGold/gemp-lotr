package com.gempukku.lotro.logic;

import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Filterable;
import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.common.Token;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.LotroCardBlueprint;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.modifiers.ModifiersQuerying;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class GameUtils {
    public static boolean isSide(GameState gameState, Side side, String playerId) {
        if (side == Side.FREE_PEOPLE)
            return gameState.getCurrentPlayerId().equals(playerId);
        else
            return !gameState.getCurrentPlayerId().equals(playerId);
    }

    public static boolean isFP(LotroGame game, String playerId) {
        return game.getGameState().getCurrentPlayerId().equals(playerId);
    }

    public static boolean isShadow(LotroGame game, String playerId) {
        return !game.getGameState().getCurrentPlayerId().equals(playerId);
    }

    public static String getFullName(PhysicalCard card) {
        LotroCardBlueprint blueprint = card.getBlueprint();
        return getFullName(blueprint);
    }

    public static String getFullName(LotroCardBlueprint blueprint) {
        if (blueprint.getSubtitle() != null)
            return blueprint.getName() + ", " + blueprint.getSubtitle();
        return blueprint.getName();
    }

    public static String getFirstShadowPlayer(LotroGame game) {
        final String fpPlayer = game.getGameState().getCurrentPlayerId();
        final PlayOrder counterClockwisePlayOrder = game.getGameState().getPlayerOrder().getCounterClockwisePlayOrder(fpPlayer, false);
        // Skip FP player
        counterClockwisePlayOrder.getNextPlayer();
        return counterClockwisePlayOrder.getNextPlayer();
    }

    public static String[] getShadowPlayers(LotroGame game) {
        final String fpPlayer = game.getGameState().getCurrentPlayerId();
        List<String> shadowPlayers = new LinkedList<String>(game.getGameState().getPlayerOrder().getAllPlayers());
        shadowPlayers.remove(fpPlayer);
        return shadowPlayers.toArray(new String[shadowPlayers.size()]);
    }

    public static String getFreePeoplePlayer(LotroGame game) {
        return game.getGameState().getCurrentPlayerId();
    }

    public static String[] getOpponents(LotroGame game, String playerId) {
        List<String> shadowPlayers = new LinkedList<String>(game.getGameState().getPlayerOrder().getAllPlayers());
        shadowPlayers.remove(playerId);
        return shadowPlayers.toArray(new String[shadowPlayers.size()]);
    }

    public static String[] getAllPlayers(LotroGame game) {
        return game.getGameState().getPlayerOrder().getAllPlayers().toArray(new String[0]);
    }

    public static List<PhysicalCard> getRandomCards(List<? extends PhysicalCard> cards, int count) {
        List<PhysicalCard> randomizedCards = new ArrayList<PhysicalCard>(cards);
        Collections.shuffle(randomizedCards);

        return new LinkedList<PhysicalCard>(randomizedCards.subList(0, Math.min(count, randomizedCards.size())));
    }

    public static String s(Collection<PhysicalCard> cards) {
        if (cards.size() > 1)
            return "s";
        return "";
    }

    public static String be(Collection<PhysicalCard> cards) {
        if (cards.size() > 1)
            return "are";
        return "is";
    }

    public static String getCardLink(PhysicalCard card) {
        LotroCardBlueprint blueprint = card.getBlueprint();
        return getCardLink(card.getBlueprintId(), blueprint);
    }

    public static String getCardLink(String blueprintId, LotroCardBlueprint blueprint) {
        return "<div class='cardHint' value='" + blueprintId + "'>" + (blueprint.isUnique() ? "•" : "") + GameUtils.getFullName(blueprint) + "</div>";
    }

    public static String getAppendedTextNames(Collection<PhysicalCard> cards) {
        StringBuilder sb = new StringBuilder();
        for (PhysicalCard card : cards)
            sb.append(GameUtils.getFullName(card) + ", ");

        if (sb.length() == 0)
            return "none";
        else
            return sb.substring(0, sb.length() - 2);
    }

    public static String getAppendedNames(Collection<PhysicalCard> cards) {
        StringBuilder sb = new StringBuilder();
        for (PhysicalCard card : cards)
            sb.append(GameUtils.getCardLink(card) + ", ");

        if (sb.length() == 0)
            return "none";
        else
            return sb.substring(0, sb.length() - 2);
    }

    public static int getSpottableTokensTotal(GameState gameState, ModifiersQuerying modifiersQuerying, Token token) {
        int tokensTotal = 0;

        for (PhysicalCard physicalCard : Filters.filterActive(gameState, modifiersQuerying, Filters.hasToken(token)))
            tokensTotal += gameState.getTokenCount(physicalCard, token);

        return tokensTotal;
    }

    public static int getSpottableCulturesCount(GameState gameState, ModifiersQuerying modifiersQuerying, Filterable... filters) {
        Set<Culture> cultures = new HashSet<Culture>();
        for (PhysicalCard physicalCard : Filters.filterActive(gameState, modifiersQuerying, filters))
            cultures.add(physicalCard.getBlueprint().getCulture());
        return cultures.size();
    }

    public static String formatNumber(int effective, int requested) {
        if (effective != requested)
            return effective + "(out of " + requested + ")";
        else
            return String.valueOf(effective);
    }

    public static int getRegion(GameState gameState) {
        return getRegion(gameState.getCurrentSiteNumber());
    }

    public static int getRegion(int siteNumber) {
        return 1 + ((siteNumber - 1) / 3);
    }

    public static int getSpottableFPCulturesCount(GameState gameState, ModifiersQuerying modifiersQuerying, String playerId) {
        return modifiersQuerying.getNumberOfSpottableFPCultures(gameState, playerId);
    }

    public static int getSpottableShadowCulturesCount(GameState gameState, ModifiersQuerying modifiersQuerying, String playerId) {
        return modifiersQuerying.getNumberOfSpottableShadowCultures(gameState, playerId);
    }
}
