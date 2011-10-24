package com.gempukku.lotro.logic.effects;

import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.common.Zone;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.GameUtils;
import com.gempukku.lotro.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.lotro.logic.timing.Effect;
import com.gempukku.lotro.logic.timing.EffectResult;
import com.gempukku.lotro.logic.timing.results.DiscardCardsFromPlayResult;
import com.gempukku.lotro.logic.timing.results.KillResult;

import java.util.*;

public class KillEffect extends AbstractSuccessfulEffect {
    private List<PhysicalCard> _cards;

    public KillEffect(List<PhysicalCard> cards) {
        _cards = cards;
    }

    @Override
    public Effect.Type getType() {
        return Effect.Type.BEFORE_KILLED;
    }

    public List<PhysicalCard> getCharactersToBeKilled() {
        return _cards;
    }

    @Override
    public String getText(LotroGame game) {
        List<PhysicalCard> cards = getCharactersToBeKilled();
        return "Kill - " + getAppendedTextNames(cards);
    }

    @Override
    public Collection<? extends EffectResult> playEffect(LotroGame game) {
        GameState gameState = game.getGameState();

        for (PhysicalCard card : _cards)
            gameState.sendMessage(GameUtils.getCardLink(card) + " gets killed");

        // For result
        Set<PhysicalCard> discardedCards = new HashSet<PhysicalCard>();
        Set<PhysicalCard> killedCards = new HashSet<PhysicalCard>();

        // Prepare the moves
        Set<PhysicalCard> toRemoveFromZone = new HashSet<PhysicalCard>();
        Set<PhysicalCard> toAddToDeadPile = new HashSet<PhysicalCard>();
        Set<PhysicalCard> toAddToDiscard = new HashSet<PhysicalCard>();

        for (PhysicalCard card : _cards) {
            toRemoveFromZone.add(card);

            if (card.getBlueprint().getSide() == Side.FREE_PEOPLE) {
                killedCards.add(card);
                toAddToDeadPile.add(card);
            } else {
                killedCards.add(card);
                discardedCards.add(card);
                toAddToDiscard.add(card);
            }

            List<PhysicalCard> attachedCards = gameState.getAttachedCards(card);
            discardedCards.addAll(attachedCards);
            toRemoveFromZone.addAll(attachedCards);
            toAddToDiscard.addAll(attachedCards);

            List<PhysicalCard> stackedCards = gameState.getStackedCards(card);
            toRemoveFromZone.addAll(stackedCards);
            toAddToDiscard.addAll(stackedCards);
        }

        gameState.removeCardsFromZone(toRemoveFromZone);

        for (PhysicalCard deadCard : toAddToDeadPile)
            gameState.addCardToZone(game, deadCard, Zone.DEAD);

        for (PhysicalCard discardedCard : toAddToDiscard)
            gameState.addCardToZone(game, discardedCard, Zone.DISCARD);

        if (killedCards.size() > 0 && discardedCards.size() > 0) {
            return Arrays.asList(new KillResult(killedCards), new DiscardCardsFromPlayResult(discardedCards));
        } else if (killedCards.size() > 0) {
            return Arrays.asList(new KillResult(killedCards));
        } else if (discardedCards.size() > 0) {
            return Arrays.asList(new DiscardCardsFromPlayResult(discardedCards));
        } else {
            return null;
        }
    }
}
