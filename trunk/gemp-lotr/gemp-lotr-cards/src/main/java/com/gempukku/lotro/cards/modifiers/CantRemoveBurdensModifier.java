package com.gempukku.lotro.cards.modifiers;

import com.gempukku.lotro.common.Filterable;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.logic.modifiers.AbstractModifier;
import com.gempukku.lotro.logic.modifiers.Condition;
import com.gempukku.lotro.logic.modifiers.ModifierEffect;
import com.gempukku.lotro.logic.modifiers.ModifiersQuerying;

public class CantRemoveBurdensModifier extends AbstractModifier {
    private Filter _sourceFilters;

    public CantRemoveBurdensModifier(PhysicalCard source, Condition condition, Filterable... sourceFilters) {
        super(source, "Can't remove burdens", null, condition, ModifierEffect.BURDEN_MODIFIER);
        _sourceFilters = Filters.and(sourceFilters);
    }

    @Override
    public boolean canRemoveBurden(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard source) {
        if (_sourceFilters.accepts(gameState, modifiersQuerying, source))
            return false;
        return true;
    }
}
