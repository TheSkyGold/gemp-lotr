package com.gempukku.lotro.cards.modifiers;

import com.gempukku.lotro.cards.modifiers.conditions.AndCondition;
import com.gempukku.lotro.common.Filterable;
import com.gempukku.lotro.common.Phase;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.logic.modifiers.AbstractModifier;
import com.gempukku.lotro.logic.modifiers.Condition;
import com.gempukku.lotro.logic.modifiers.ModifierEffect;
import com.gempukku.lotro.logic.modifiers.ModifiersQuerying;
import com.gempukku.lotro.logic.modifiers.condition.PhaseCondition;

import java.util.Collection;

public class CantTakeMoreThanXWoundsModifier extends AbstractModifier {
    private int _count;

    public CantTakeMoreThanXWoundsModifier(PhysicalCard source, Phase phase, int count, Filterable... affectFilters) {
        this(source, phase, count, null, affectFilters);
    }

    public CantTakeMoreThanXWoundsModifier(PhysicalCard source, final Phase phase, int count, Condition condition, Filterable... affectFilters) {
        super(source, "Can't take more than " + count + " wound(s)", Filters.and(affectFilters),
                (condition == null ? new PhaseCondition(phase) : new AndCondition(new PhaseCondition(phase), condition)), ModifierEffect.WOUND_MODIFIER);
        _count = count;
    }

    @Override
    public boolean canTakeWounds(GameState gameState, ModifiersQuerying modifiersQuerying, Collection<PhysicalCard> woundSources, PhysicalCard physicalCard, int woundsAlreadyTaken, int woundsToTake) {
        return woundsAlreadyTaken + woundsToTake <= _count;
    }
}
