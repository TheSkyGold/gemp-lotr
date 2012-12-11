package com.gempukku.lotro.cards.effects;

import com.gempukku.lotro.common.Zone;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.GameUtils;
import com.gempukku.lotro.logic.timing.AbstractEffect;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class RemoveCardsFromDeadPileEffect extends AbstractEffect {
    private String _playerPerforming;
    private PhysicalCard _source;
    private Collection<PhysicalCard> _cardsToRemove;

    public RemoveCardsFromDeadPileEffect(String playerPerforming, PhysicalCard source, Collection<PhysicalCard> cardsToRemove) {
        _playerPerforming = playerPerforming;
        _source = source;
        _cardsToRemove = cardsToRemove;
    }

    @Override
    public String getText(LotroGame game) {
        return null;
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public boolean isPlayableInFull(LotroGame game) {
        for (PhysicalCard physicalCard : _cardsToRemove) {
            if (physicalCard.getZone() != Zone.DEAD)
                return false;
        }

        return true;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(LotroGame game) {
        Set<PhysicalCard> removedCards = new HashSet<PhysicalCard>();
        for (PhysicalCard physicalCard : _cardsToRemove)
            if (physicalCard.getZone() == Zone.DEAD)
                removedCards.add(physicalCard);

        game.getGameState().removeCardsFromZone(_playerPerforming, removedCards);
        for (PhysicalCard removedCard : removedCards)
            game.getGameState().addCardToZone(game, removedCard, Zone.REMOVED);

        game.getGameState().sendMessage(_playerPerforming + " removed " + GameUtils.getAppendedNames(removedCards) + " from dead pile using " + GameUtils.getCardLink(_source));

        return new FullEffectResult(_cardsToRemove.size() == removedCards.size());
    }
}
