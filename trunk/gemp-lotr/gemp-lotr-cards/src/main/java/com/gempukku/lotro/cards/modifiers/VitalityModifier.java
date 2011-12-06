package com.gempukku.lotro.cards.modifiers;

import com.gempukku.lotro.common.Filterable;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.logic.modifiers.AbstractModifier;
import com.gempukku.lotro.logic.modifiers.ModifierEffect;
import com.gempukku.lotro.logic.modifiers.ModifiersQuerying;

public class VitalityModifier extends AbstractModifier {
    private int _modifier;

    public VitalityModifier(PhysicalCard source, Filterable affectFilter, int modifier) {
        super(source, "Vitality " + ((modifier < 0) ? modifier : ("+" + modifier)), affectFilter, ModifierEffect.VITALITY_MODIFIER);
        _modifier = modifier;
    }

    @Override
    public int getVitalityModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _modifier;
    }
}
