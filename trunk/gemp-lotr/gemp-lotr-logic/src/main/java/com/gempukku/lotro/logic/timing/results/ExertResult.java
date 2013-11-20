package com.gempukku.lotro.logic.timing.results;

import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.logic.timing.Action;
import com.gempukku.lotro.logic.timing.EffectResult;

public class ExertResult extends EffectResult {
    private Action _action;
    private PhysicalCard _card;
    private boolean _forToil;

    public ExertResult(Action action, PhysicalCard card, boolean forToil) {
        super(Type.FOR_EACH_EXERTED);
        _action = action;
        _card = card;
        _forToil = forToil;
    }

    public PhysicalCard getExertedCard() {
        return _card;
    }

    public Action getAction() {
        return _action;
    }

    public boolean isForToil() {
        return _forToil;
    }
}
