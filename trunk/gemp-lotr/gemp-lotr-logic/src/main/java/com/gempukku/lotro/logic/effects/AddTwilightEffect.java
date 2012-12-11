package com.gempukku.lotro.logic.effects;

import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.GameUtils;
import com.gempukku.lotro.logic.timing.AbstractEffect;
import com.gempukku.lotro.logic.timing.Effect;
import com.gempukku.lotro.logic.timing.Preventable;

public class AddTwilightEffect extends AbstractEffect implements Preventable {
    private PhysicalCard _source;
    private int _twilight;
    private int _prevented;
    private String _sourceText;

    public AddTwilightEffect(PhysicalCard source, int twilight) {
        _source = source;
        _twilight = twilight;
        if (source != null)
            _sourceText = GameUtils.getCardLink(source);
    }

    public void setSourceText(String sourceText) {
        _sourceText = sourceText;
    }

    public PhysicalCard getSource() {
        return _source;
    }

    @Override
    public String getText(LotroGame game) {
        return "Add (" + _twilight + ")";
    }

    @Override
    public Effect.Type getType() {
        return Effect.Type.BEFORE_ADD_TWILIGHT;
    }

    @Override
    public boolean isPrevented() {
        return _prevented == _twilight;
    }

    @Override
    public void prevent() {
        _prevented = _twilight;
    }

    @Override
    public boolean isPlayableInFull(LotroGame game) {
        return true;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(LotroGame game) {
        if (!isPrevented()) {
            game.getGameState().sendMessage(_sourceText + " added " + _twilight + " twilight");
            game.getGameState().addTwilight(_twilight);
            return new FullEffectResult(_prevented == 0);
        }
        return new FullEffectResult(false);
    }
}
