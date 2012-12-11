package com.gempukku.lotro.cards.effects;

import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.timing.AbstractEffect;
import com.gempukku.lotro.logic.timing.Effect;

public class PayTwilightCostEffect extends AbstractEffect {
    private PhysicalCard _physicalCard;
    private int _twilightModifier;
    private boolean _ignoreRoamingPenalty;

    public PayTwilightCostEffect(PhysicalCard physicalCard) {
        this(physicalCard, 0);
    }

    public PayTwilightCostEffect(PhysicalCard physicalCard, int twilightModifier) {
        this(physicalCard, twilightModifier, false);
    }

    public PayTwilightCostEffect(PhysicalCard physicalCard, int twilightModifier, boolean ignoreRoamingPenalty) {
        _physicalCard = physicalCard;
        _twilightModifier = twilightModifier;
        _ignoreRoamingPenalty = ignoreRoamingPenalty;
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
    public boolean isPlayableInFull(LotroGame game) {
        int twilightCost = _twilightModifier + game.getModifiersQuerying().getTwilightCost(game.getGameState(), _physicalCard, _ignoreRoamingPenalty);

        String currentPlayerId = game.getGameState().getCurrentPlayerId();
        if (!currentPlayerId.equals(_physicalCard.getOwner())) {
            int twilightPool = game.getGameState().getTwilightPool();
            if (twilightPool < twilightCost)
                return false;
        }
        return true;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(LotroGame game) {
        int twilightCost = _twilightModifier + game.getModifiersQuerying().getTwilightCost(game.getGameState(), _physicalCard, _ignoreRoamingPenalty);

        String currentPlayerId = game.getGameState().getCurrentPlayerId();
        if (currentPlayerId.equals(_physicalCard.getOwner())) {
            game.getGameState().addTwilight(twilightCost);
            if (twilightCost > 0)
                game.getGameState().sendMessage(_physicalCard.getOwner() + " adds " + twilightCost + " to twilight pool");
            return new FullEffectResult(true);
        } else {
            boolean success = game.getGameState().getTwilightPool() >= twilightCost;
            twilightCost = Math.min(twilightCost, game.getGameState().getTwilightPool());
            if (twilightCost > 0) {
                game.getGameState().removeTwilight(twilightCost);
                game.getGameState().sendMessage(_physicalCard.getOwner() + " removes " + twilightCost + " from twilight pool");
            }
            return new FullEffectResult(success);
        }
    }
}
