package com.gempukku.lotro.cards.modifiers;

import com.gempukku.lotro.common.Filterable;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.logic.modifiers.AbstractModifier;
import com.gempukku.lotro.logic.modifiers.Condition;
import com.gempukku.lotro.logic.modifiers.ModifierEffect;
import com.gempukku.lotro.logic.modifiers.ModifiersQuerying;
import com.gempukku.lotro.logic.timing.Action;

public class CantPlayCardsModifier extends AbstractModifier {
    private Filterable[] _filters;

    public CantPlayCardsModifier(PhysicalCard source, Filterable... filters) {
        this(source, null, filters);
    }

    public CantPlayCardsModifier(PhysicalCard source, Condition condition, Filterable... filters) {
        super(source, null, null, condition, ModifierEffect.ACTION_MODIFIER);
        _filters = filters;
    }

    @Override
    public boolean canPlayAction(GameState gameState, ModifiersQuerying modifiersQuerying, String performingPlayer, Action action) {
        final PhysicalCard actionSource = action.getActionSource();
        if (actionSource != null)
            if (action.getType() == Action.Type.PLAY_CARD)
                if (Filters.and(_filters).accepts(gameState, modifiersQuerying, actionSource))
                    return false;
        return true;
    }
}
