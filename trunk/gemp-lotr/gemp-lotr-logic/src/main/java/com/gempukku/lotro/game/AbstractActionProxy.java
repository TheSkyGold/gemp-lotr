package com.gempukku.lotro.game;

import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.timing.Action;
import com.gempukku.lotro.logic.timing.Effect;
import com.gempukku.lotro.logic.timing.EffectResult;

import java.util.List;

public abstract class AbstractActionProxy implements ActionProxy {
    @Override
    public List<? extends Action> getRequiredIsAboutToActions(LotroGame lotroGame, Effect effect, EffectResult effectResult) {
        return null;
    }

    @Override
    public List<? extends Action> getRequiredOneTimeActions(LotroGame lotroGame, EffectResult effectResult) {
        return null;
    }

    @Override
    public List<? extends Action> getPhaseActions(String playerId, LotroGame lotroGame) {
        return null;
    }

    @Override
    public List<? extends Action> getOptionalIsAboutToActions(String playerId, LotroGame lotroGame, Effect effect, EffectResult effectResult) {
        return null;
    }

    @Override
    public List<? extends Action> getOptionalOneTimeActions(String playerId, LotroGame lotroGame, EffectResult effectResult) {
        return null;
    }

    @Override
    public List<? extends Action> getOptionalActions(String playerId, LotroGame lotroGame, EffectResult effectResult) {
        return null;
    }
}
