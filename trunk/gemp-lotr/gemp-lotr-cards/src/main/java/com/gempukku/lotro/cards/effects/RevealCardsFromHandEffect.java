package com.gempukku.lotro.cards.effects;

import com.gempukku.lotro.common.Zone;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.GameUtils;
import com.gempukku.lotro.logic.timing.AbstractEffect;
import com.gempukku.lotro.logic.timing.Effect;
import com.gempukku.lotro.logic.timing.results.RevealCardFromHandResult;

import java.util.Collection;

public class RevealCardsFromHandEffect extends AbstractEffect {
    private PhysicalCard _source;
    private String _handPlayerId;
    private Collection<PhysicalCard> _cards;

    public RevealCardsFromHandEffect(PhysicalCard source, String handPlayerId, Collection<PhysicalCard> cards) {
        _source = source;
        _handPlayerId = handPlayerId;
        _cards = cards;
    }

    @Override
    public String getText(LotroGame game) {
        return "Reveal cards from hand";
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

        return true;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(LotroGame game) {
        game.getGameState().sendMessage(GameUtils.getCardLink(_source) + " revealed " + _handPlayerId + " cards in hand - " + getAppendedNames(_cards));

        for (PhysicalCard card : _cards) {
            game.getActionsEnvironment().emitEffectResult(new RevealCardFromHandResult(_source, _handPlayerId, card));
        }

        return new FullEffectResult(true, true);
    }
}
