package com.gempukku.lotro.cards.effects;

import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.lotro.logic.timing.Effect;

public class SetUpConsecutiveActionEffect extends AbstractSuccessfulEffect {
    @Override
    public String getText(LotroGame game) {
        return null;
    }

    @Override
    public Effect.Type getType() {
        return null;
    }

    @Override
    public void playEffect(LotroGame game) {
        game.getGameState().setConsecutiveAction(true);
    }
}
