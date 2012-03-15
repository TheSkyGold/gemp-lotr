package com.gempukku.lotro.cards.effects;

import com.gempukku.lotro.common.Zone;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.timing.AbstractEffect;
import com.gempukku.lotro.logic.timing.Effect;
import com.gempukku.lotro.logic.timing.results.CardTransferredResult;

import java.util.Collections;

public class TransferToSupportEffect extends AbstractEffect {
    private PhysicalCard _card;

    public TransferToSupportEffect(PhysicalCard card) {
        _card = card;
    }

    @Override
    public boolean isPlayableInFull(LotroGame game) {
        return _card.getZone().isInPlay();
    }

    @Override
    public String getText(LotroGame game) {
        return null;
    }

    @Override
    public Effect.Type getType() {
        return null;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(LotroGame game) {
        if (isPlayableInFull(game)) {
            PhysicalCard transferredFrom = _card.getAttachedTo();
            game.getGameState().removeCardsFromZone(_card.getOwner(), Collections.singleton(_card));
            game.getGameState().addCardToZone(game, _card, Zone.SUPPORT);

            game.getActionsEnvironment().emitEffectResult(
                    new CardTransferredResult(_card, transferredFrom, null));

            return new FullEffectResult(true, true);
        }
        return new FullEffectResult(false, false);
    }
}