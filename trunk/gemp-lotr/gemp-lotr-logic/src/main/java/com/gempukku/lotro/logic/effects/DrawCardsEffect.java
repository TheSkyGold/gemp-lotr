package com.gempukku.lotro.logic.effects;

import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.timing.AbstractEffect;
import com.gempukku.lotro.logic.timing.Effect;
import com.gempukku.lotro.logic.timing.results.DrawCardOrPutIntoHandResult;

public class DrawCardsEffect extends AbstractEffect {
    private String _playerId;
    private final int _count;

    public DrawCardsEffect(String playerId, int count) {
        _playerId = playerId;
        _count = count;
    }

    @Override
    public String getText(LotroGame game) {
        return "Draw " + _count + " card" + ((_count > 1) ? "s" : "");
    }

    @Override
    public Effect.Type getType() {
        return null;
    }

    @Override
    public boolean isPlayableInFull(LotroGame game) {
        return game.getGameState().getDeck(_playerId).size() >= _count;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(LotroGame game) {
        int drawn = 0;
        for (int i = 0; i < _count; i++) {
            if (game.getGameState().getDeck(_playerId).size() > 0 && game.getModifiersQuerying().canDrawCardAndIncrement(game.getGameState(), _playerId)) {
                game.getGameState().playerDrawsCard(_playerId);
                drawn++;
            }
        }
        if (drawn > 0)
            game.getGameState().sendMessage(_playerId + " draws " + drawn + " card" + ((drawn > 1) ? "s" : ""));

        if (drawn > 0) {
            for (int i = 0; i < drawn; i++)
                game.getActionsEnvironment().emitEffectResult(new DrawCardOrPutIntoHandResult(_playerId));
            return new FullEffectResult(_count == drawn, _count == drawn);
        } else
            return new FullEffectResult(false, false);
    }
}
