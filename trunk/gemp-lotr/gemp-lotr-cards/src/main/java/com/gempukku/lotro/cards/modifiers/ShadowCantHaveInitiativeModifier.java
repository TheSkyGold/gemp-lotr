package com.gempukku.lotro.cards.modifiers;

import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.logic.modifiers.AbstractModifier;
import com.gempukku.lotro.logic.modifiers.Condition;
import com.gempukku.lotro.logic.modifiers.ModifierEffect;
import com.gempukku.lotro.logic.modifiers.ModifiersQuerying;

public class ShadowCantHaveInitiativeModifier extends AbstractModifier {
    public ShadowCantHaveInitiativeModifier(PhysicalCard source, Condition condition) {
        super(source, "Shadow can't have initiative", null, condition, ModifierEffect.INITIATIVE_MODIFIER);
    }

    @Override
    public boolean shadowCanHaveInitiative(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return false;
    }
}
