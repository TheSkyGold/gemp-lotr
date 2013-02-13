package com.gempukku.lotro.cards.effects;

import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.results.PlayEventResult;
import com.gempukku.lotro.common.Zone;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.effects.PlayCardEffect;

public class PlayEventEffect extends PlayCardEffect {
    private PhysicalCard _cardPlayed;
    private PlayEventResult _playEventResult;

    public PlayEventEffect(PlayEventAction action, Zone playedFrom, PhysicalCard cardPlayed, boolean requiresRanger, boolean paidToil) {
        super(playedFrom, cardPlayed, (Zone) null, null, paidToil);
        _cardPlayed = cardPlayed;
        _playEventResult = new PlayEventResult(action, playedFrom, getPlayedCard(), requiresRanger, paidToil);
    }

    public PlayEventResult getPlayEventResult() {
        return _playEventResult;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(LotroGame game) {
        game.getActionsEnvironment().emitEffectResult(_playEventResult);
        return new FullEffectResult(true);
    }
}
