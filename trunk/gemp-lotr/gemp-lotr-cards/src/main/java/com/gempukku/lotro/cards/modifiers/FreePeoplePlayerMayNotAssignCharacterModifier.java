package com.gempukku.lotro.cards.modifiers;

import com.gempukku.lotro.common.Filterable;
import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.logic.modifiers.AbstractModifier;
import com.gempukku.lotro.logic.modifiers.Condition;
import com.gempukku.lotro.logic.modifiers.ModifierEffect;
import com.gempukku.lotro.logic.modifiers.ModifiersQuerying;

public class FreePeoplePlayerMayNotAssignCharacterModifier extends AbstractModifier {
    public FreePeoplePlayerMayNotAssignCharacterModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, null, affectFilter);
    }

    public FreePeoplePlayerMayNotAssignCharacterModifier(PhysicalCard source, Condition condition, Filterable affectFilter) {
        super(source, "Free people player may not assign character to skirmish", affectFilter, condition, ModifierEffect.ASSIGNMENT_MODIFIER);
    }

    @Override
    public boolean isPreventedFromBeingAssignedToSkirmish(GameState gameState, Side sidePlayer, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        if (sidePlayer == Side.FREE_PEOPLE)
            return true;
        return false;
    }
}
