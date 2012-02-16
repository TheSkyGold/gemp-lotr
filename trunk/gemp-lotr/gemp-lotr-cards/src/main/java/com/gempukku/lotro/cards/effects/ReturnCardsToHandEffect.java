package com.gempukku.lotro.cards.effects;

import com.gempukku.lotro.common.Filterable;
import com.gempukku.lotro.common.Zone;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.GameUtils;
import com.gempukku.lotro.logic.modifiers.ModifiersQuerying;
import com.gempukku.lotro.logic.timing.AbstractEffect;
import com.gempukku.lotro.logic.timing.Effect;
import com.gempukku.lotro.logic.timing.results.DiscardCardsFromPlayResult;
import com.gempukku.lotro.logic.timing.results.ReturnCardsToHandResult;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReturnCardsToHandEffect extends AbstractEffect {
    private PhysicalCard _source;
    private Filterable _filter;

    public ReturnCardsToHandEffect(PhysicalCard source, Filterable filter) {
        _source = source;
        _filter = filter;
    }

    @Override
    public String getText(LotroGame game) {
        Collection<PhysicalCard> cards = Filters.filterActive(game.getGameState(), game.getModifiersQuerying(), _filter);
        return "Return " + getAppendedNames(cards) + " to hand";
    }

    @Override
    public Effect.Type getType() {
        return null;
    }

    @Override
    public boolean isPlayableInFull(LotroGame game) {
        return Filters.filterActive(game.getGameState(), game.getModifiersQuerying(), _filter,
                new Filter() {
                    @Override
                    public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                        return (_source == null || modifiersQuerying.canBeReturnedToHand(gameState, physicalCard, _source));
                    }
                }).size() > 0;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(LotroGame game) {
        Collection<PhysicalCard> cardsToReturnToHand = Filters.filterActive(game.getGameState(), game.getModifiersQuerying(), _filter);

        // Preparation, figure out, what's going where...
        Set<PhysicalCard> discardedFromPlay = new HashSet<PhysicalCard>();
        Set<PhysicalCard> removedFromZone = new HashSet<PhysicalCard>();

        for (PhysicalCard card : cardsToReturnToHand) {
            final List<PhysicalCard> attachedCards = game.getGameState().getAttachedCards(card);

            discardedFromPlay.addAll(attachedCards);

            removedFromZone.add(card);
            removedFromZone.addAll(attachedCards);
            removedFromZone.addAll(game.getGameState().getStackedCards(card));
        }

        discardedFromPlay.removeAll(cardsToReturnToHand);

        // Now do the actual things
        GameState gameState = game.getGameState();

        // Remove from their zone
        gameState.removeCardsFromZone(_source.getOwner(), removedFromZone);

        // Add cards to hand
        for (PhysicalCard card : cardsToReturnToHand)
            gameState.addCardToZone(game, card, Zone.HAND);

        // Add discarded to discard
        for (PhysicalCard card : discardedFromPlay)
            gameState.addCardToZone(game, card, Zone.DISCARD);

        if (_source != null && cardsToReturnToHand.size() > 0)
            game.getGameState().sendMessage(GameUtils.getCardLink(_source) + " returns " + getAppendedNames(cardsToReturnToHand) + " to hand");

        for (PhysicalCard discardedCard : discardedFromPlay)
            game.getActionsEnvironment().emitEffectResult(new DiscardCardsFromPlayResult(discardedCard));
        for (PhysicalCard cardReturned : cardsToReturnToHand)
            game.getActionsEnvironment().emitEffectResult(new ReturnCardsToHandResult(cardReturned));

        return new FullEffectResult(true, true);
    }
}