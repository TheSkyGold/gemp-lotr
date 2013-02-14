package com.gempukku.lotro.logic.modifiers;

import com.gempukku.lotro.common.Filterable;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;

public class CantDiscardFromPlayModifier extends AbstractModifier {
    private Filter _sourceFilter;

    public CantDiscardFromPlayModifier(PhysicalCard source, String text, Filterable affectFilter, Filterable sourceFilter) {
        this(source, text, null, affectFilter, sourceFilter);
    }

    public CantDiscardFromPlayModifier(PhysicalCard source, String text, Condition condition, Filterable affectFilter, Filterable sourceFilter) {
        super(source, text, affectFilter, condition, ModifierEffect.DISCARD_FROM_PLAY_MODIFIER);
        _sourceFilter = Filters.and(sourceFilter);
    }

    @Override
    public boolean canBeDiscardedFromPlay(GameState gameState, ModifiersQuerying modifiersQuerying, String performingPlayer, PhysicalCard card, PhysicalCard source) {
        if (_sourceFilter.accepts(gameState, modifiersQuerying, source))
            return false;
        return true;
    }
}
