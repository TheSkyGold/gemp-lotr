package com.gempukku.lotro.logic.actions;

import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.GameUtils;
import com.gempukku.lotro.logic.timing.Effect;

public class OptionalTriggerAction extends AbstractCostToEffectAction {
    private PhysicalCard _physicalCard;
    private PhysicalCard _actionAttachedToCard;

    private String _message;

    private boolean _sentMessage;
    private String _text;

    public OptionalTriggerAction(PhysicalCard physicalCard) {
        _physicalCard = physicalCard;
        _actionAttachedToCard = physicalCard;

        if (physicalCard != null) {
            _text = "Optional trigger from " + GameUtils.getCardLink(_physicalCard);
            _message = GameUtils.getCardLink(_physicalCard) + " optional triggered effect is used";
        }
    }

    public void setActionAttachedToCard(PhysicalCard actionAttachedToCard) {
        _actionAttachedToCard = actionAttachedToCard;
    }

    @Override
    public Type getType() {
        return Type.TRIGGER;
    }

    @Override
    public ActionSource getActionSource() {
        return new ActionSource(_physicalCard);
    }

    @Override
    public PhysicalCard getActionAttachedToCard() {
        return _actionAttachedToCard;
    }

    public void setText(String text) {
        _text = text;
    }

    @Override
    public String getText(LotroGame game) {
        return _text;
    }

    @Override
    public Effect nextEffect(LotroGame game) {
        if (!_sentMessage) {
            _sentMessage = true;
            if (_physicalCard != null)
                game.getGameState().activatedCard(getPerformingPlayer(), _physicalCard);
            if (_message != null)
                game.getGameState().sendMessage(_message);
        }

        if (!isCostFailed()) {
            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            Effect effect = getNextEffect();
            if (effect != null)
                return effect;
        }
        return null;
    }
}
