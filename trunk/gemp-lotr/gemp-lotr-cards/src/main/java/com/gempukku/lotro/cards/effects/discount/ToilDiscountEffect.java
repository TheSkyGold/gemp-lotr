package com.gempukku.lotro.cards.effects.discount;

import com.gempukku.lotro.cards.effects.DiscountEffect;
import com.gempukku.lotro.cards.effects.choose.ChooseAndExertCharactersEffect;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.AbstractCostToEffectAction;
import com.gempukku.lotro.logic.actions.SubAction;
import com.gempukku.lotro.logic.timing.AbstractSubActionEffect;
import com.gempukku.lotro.logic.timing.Action;

public class ToilDiscountEffect extends AbstractSubActionEffect implements DiscountEffect {
    private Action _action;
    private PhysicalCard _payingFor;
    private String _ownerId;
    private Culture _culture;
    private int _toilCount;
    private int _minimalDiscount;

    private int _exertedCount;
    private int _paidToil;

    public ToilDiscountEffect(Action action, PhysicalCard payingFor, String ownerId, Culture culture, int toilCount) {
        _action = action;
        _payingFor = payingFor;
        _ownerId = ownerId;
        _culture = culture;
        _toilCount = toilCount;
    }

    @Override
    public String getText(LotroGame game) {
        return null;
    }

    public void incrementToil() {
        _paidToil++;
    }

    @Override
    public Type getType() {
        return Type.BEFORE_TOIL;
    }

    public PhysicalCard getPayingFor() {
        return _payingFor;
    }

    @Override
    public void setMinimalRequiredDiscount(int minimalDiscount) {
        _minimalDiscount = minimalDiscount;
    }

    @Override
    public boolean isPlayableInFull(LotroGame game) {
        return _minimalDiscount <= _toilCount * (_paidToil + Filters.countActive(game.getGameState(), game.getModifiersQuerying(), Filters.owner(_ownerId), _culture, Filters.character, Filters.canExert(_payingFor)));
    }

    @Override
    public void playEffect(final LotroGame game) {
        if (isPlayableInFull(game)) {
            int minimalExerts;
            if (_minimalDiscount == 0) {
                minimalExerts = 0;
            } else {
                minimalExerts = _minimalDiscount / _toilCount + ((_minimalDiscount % _toilCount > 0) ? 1 : 0);
            }

            SubAction subAction = new SubAction(_action);
            final ChooseAndExertCharactersEffect effect = new ChooseAndExertCharactersEffect(subAction, _ownerId, minimalExerts, Integer.MAX_VALUE, Filters.owner(_ownerId), _culture, Filters.character) {
                @Override
                protected void forEachCardExertedCallback(PhysicalCard character) {
                    _exertedCount++;
                }
            };
            effect.setForToil(true);
            subAction.appendEffect(effect);
            processSubAction(game, subAction);
        }
    }

    @Override
    public int getDiscountPaidFor() {
        return (_exertedCount + _paidToil) * _toilCount;
    }

    @Override
    public void afterDiscountCallback(AbstractCostToEffectAction action) {
        if (_exertedCount>0)
            action.setPaidToil(true);
    }
}
