package com.gempukku.lotro.cards.set2.dwarven;

import com.gempukku.lotro.cards.AbstractAttachable;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.AddUntilEndOfPhaseModifierEffect;
import com.gempukku.lotro.cards.effects.SelfDiscardEffect;
import com.gempukku.lotro.cards.modifiers.VitalityModifier;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.modifiers.StrengthModifier;
import com.gempukku.lotro.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Mines of Moria
 * Side: Free
 * Culture: Dwarven
 * Twilight Cost: 1
 * Type: Condition
 * Vitality: +1
 * Game Text: Bearer must be a Dwarf. Skirmish: Discard this condition to make bearer strength +1.
 */
public class Card2_004 extends AbstractAttachable {
    public Card2_004() {
        super(Side.FREE_PEOPLE, CardType.CONDITION, 1, Culture.DWARVEN, null, "Endurance of Dwarves");
    }

    @Override
    protected Filterable getValidTargetFilter(String playerId, LotroGame game, PhysicalCard self) {
        return Race.DWARF;
    }

    @Override
    public Modifier getAlwaysOnModifier(LotroGame game, PhysicalCard self) {
        return new VitalityModifier(self, Filters.hasAttached(self), 1);
    }

    @Override
    protected List<? extends Action> getExtraPhaseActions(String playerId, LotroGame game, PhysicalCard self) {
        if (PlayConditions.canUseFPCardDuringPhase(game, Phase.SKIRMISH, self)) {
            ActivateCardAction action = new ActivateCardAction(self);
            action.appendCost(
                    new SelfDiscardEffect(self));
            action.appendEffect(
                    new AddUntilEndOfPhaseModifierEffect(
                            new StrengthModifier(self, Filters.sameCard(self.getAttachedTo()), 1)));
            return Collections.singletonList(action);
        }
        return null;
    }
}
