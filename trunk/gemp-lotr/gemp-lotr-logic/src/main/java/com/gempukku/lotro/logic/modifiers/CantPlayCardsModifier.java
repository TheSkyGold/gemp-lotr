package com.gempukku.lotro.logic.modifiers;

import com.gempukku.lotro.common.Filterable;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.logic.timing.Action;

public class CantPlayCardsModifier extends AbstractModifier {
    private Filter _filters;

    public CantPlayCardsModifier(PhysicalCard source, Filterable... filters) {
        this(source, null, filters);
    }

    public CantPlayCardsModifier(PhysicalCard source, Condition condition, Filterable... filters) {
        super(source, null, null, condition, ModifierEffect.ACTION_MODIFIER);
        _filters = Filters.and(filters);
    }

    @Override
    public boolean canPlayAction(GameState gameState, ModifiersQuerying modifiersQuerying, String performingPlayer, Action action) {
        final PhysicalCard actionSource = action.getActionSource();
        if (actionSource != null)
            if (action.getType() == Action.Type.PLAY_CARD)
                if (_filters.accepts(gameState, modifiersQuerying, actionSource))
                    return false;
        return true;
    }
}
