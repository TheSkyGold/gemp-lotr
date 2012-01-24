package com.gempukku.lotro.logic.timing.rules;

import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.logic.modifiers.CantDiscardFromPlayModifier;
import com.gempukku.lotro.logic.modifiers.CantReturnToHandModifier;
import com.gempukku.lotro.logic.modifiers.ModifiersLogic;

public class RingBearerRule {
    private ModifiersLogic _modifiersLogic;

    public RingBearerRule(ModifiersLogic modifiersLogic) {
        _modifiersLogic = modifiersLogic;
    }

    public void applyRule() {
        _modifiersLogic.addAlwaysOnModifier(
                new CantDiscardFromPlayModifier(null, "Can't be discarded from play", Filters.ringBearer, Filters.any));
        _modifiersLogic.addAlwaysOnModifier(
                new CantReturnToHandModifier(null, "Can't be returned to hand", Filters.ringBearer, Filters.any));
    }
}
