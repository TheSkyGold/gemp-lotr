package com.gempukku.lotro.cards.effects;

import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.common.Zone;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.GameUtils;
import com.gempukku.lotro.logic.timing.AbstractEffect;

public class PutPlayedEventOnBottomOfDeckEffect extends AbstractEffect {
    private PlayEventAction _action;

    public PutPlayedEventOnBottomOfDeckEffect(PlayEventAction action) {
        _action = action;
    }

    @Override
    public String getText(LotroGame game) {
        return "Put " + GameUtils.getCardLink(_action.getEventPlayed()) + " on bottom of your deck";
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public boolean isPlayableInFull(LotroGame game) {
        return _action.getEventPlayed().getZone() == Zone.VOID;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(LotroGame game) {
        if (isPlayableInFull(game)) {
            game.getGameState().sendMessage(_action.getPerformingPlayer() + " puts " + GameUtils.getCardLink(_action.getEventPlayed()) + " on bottom of his/her deck");
            _action.skipDiscardPart();
            game.getGameState().putCardOnBottomOfDeck(_action.getEventPlayed());
            return new FullEffectResult(true);
        }
        return new FullEffectResult(false);
    }
}