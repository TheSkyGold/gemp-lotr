package com.gempukku.lotro.logic.effects;

import com.gempukku.lotro.common.Zone;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.GameUtils;
import com.gempukku.lotro.logic.timing.AbstractEffect;
import com.gempukku.lotro.logic.timing.Effect;
import com.gempukku.lotro.logic.timing.results.DiscardCardFromHandResult;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DiscardCardsFromHandEffect extends AbstractEffect {
    private PhysicalCard _source;
    private String _playerId;
    private Collection<PhysicalCard> _cards;
    private boolean _forced;

    public DiscardCardsFromHandEffect(PhysicalCard source, String playerId, Collection<PhysicalCard> cards, boolean forced) {
        _source = source;
        _playerId = playerId;
        _cards = cards;
        _forced = forced;
    }

    @Override
    public String getText(LotroGame game) {
        return "Discard from hand - " + getAppendedTextNames(_cards);
    }

    @Override
    public Effect.Type getType() {
        return null;
    }

    @Override
    public boolean isPlayableInFull(LotroGame game) {
        for (PhysicalCard card : _cards) {
            if (card.getZone() != Zone.HAND)
                return false;
        }
        if (_forced && !game.getModifiersQuerying().canDiscardCardsFromHand(game.getGameState(), _playerId, _source))
            return false;
        return true;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(LotroGame game) {
        if (!_forced || game.getModifiersQuerying().canDiscardCardsFromHand(game.getGameState(), _playerId, _source)) {
            GameState gameState = game.getGameState();

            Set<PhysicalCard> discardedCards = new HashSet<PhysicalCard>();
            for (PhysicalCard card : _cards)
                if (card.getZone() == Zone.HAND)
                    discardedCards.add(card);

            if (discardedCards.size() > 0)
                gameState.sendMessage(getAppendedNames(discardedCards) + " " + GameUtils.be(discardedCards) + " discarded from hand");
            String sourcePlayer = null;
            if (_source != null)
                sourcePlayer = _source.getOwner();
            gameState.removeCardsFromZone(sourcePlayer, discardedCards);
            for (PhysicalCard card : discardedCards) {
                gameState.addCardToZone(game, card, Zone.DISCARD);
                game.getActionsEnvironment().emitEffectResult(new DiscardCardFromHandResult(_source, card, _playerId, _forced));
            }

            return new FullEffectResult(discardedCards.size() == _cards.size());
        }

        return new FullEffectResult(false);
    }
}
