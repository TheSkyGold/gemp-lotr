package com.gempukku.lotro.cards.effects;

import com.gempukku.lotro.cards.TriggerConditions;
import com.gempukku.lotro.common.Filterable;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.AbstractActionProxy;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.RequiredTriggerAction;
import com.gempukku.lotro.logic.effects.WoundCharactersEffect;
import com.gempukku.lotro.logic.timing.Effect;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PreventAllWoundsActionProxy extends AbstractActionProxy {
    private PhysicalCard _source;
    private Filter _filters;

    public PreventAllWoundsActionProxy(PhysicalCard source, Filterable... filters) {
        _source = source;
        _filters = Filters.and(filters);
    }

    @Override
    public List<? extends RequiredTriggerAction> getRequiredBeforeTriggers(LotroGame game, Effect effect) {
        if (TriggerConditions.isGettingWounded(effect, game, _filters)) {
            WoundCharactersEffect woundEffect = (WoundCharactersEffect) effect;
            Collection<PhysicalCard> toPreventOn = Filters.filter(woundEffect.getAffectedCardsMinusPrevented(game), game.getGameState(), game.getModifiersQuerying(), _filters);
            RequiredTriggerAction action = new RequiredTriggerAction(_source);
            action.appendEffect(
                    new PreventCardEffect(woundEffect, toPreventOn));
            return Collections.singletonList(action);
        }
        return null;
    }
}
