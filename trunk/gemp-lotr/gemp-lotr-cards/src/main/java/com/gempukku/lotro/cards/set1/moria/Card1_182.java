package com.gempukku.lotro.cards.set1.moria;

import com.gempukku.lotro.cards.AbstractAttachable;
import com.gempukku.lotro.cards.TriggerConditions;
import com.gempukku.lotro.cards.effects.AddBurdenEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.RequiredTriggerAction;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.modifiers.StrengthModifier;
import com.gempukku.lotro.logic.timing.EffectResult;
import com.gempukku.lotro.logic.timing.results.ForEachKilledResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Shadow
 * Culture: Moria
 * Twilight Cost: 1
 * Type: Possession • Hand Weapon
 * Strength: +2
 * Game Text: Bearer must be a [MORIA] minion. If bearer kills a companion in a skirmish, add 1 burden (or 2 burdens if
 * that companion was a Hobbit).
 */
public class Card1_182 extends AbstractAttachable {
    public Card1_182() {
        super(Side.SHADOW, CardType.POSSESSION, 1, Culture.MORIA, PossessionClass.HAND_WEAPON, "Goblin Spear");
    }

    @Override
    protected Filter getValidTargetFilter(String playerId, LotroGame game, PhysicalCard self) {
        return Filters.and(Culture.MORIA, CardType.MINION);
    }

    @Override
    public Modifier getAlwaysOnModifier(PhysicalCard self) {
        return new StrengthModifier(self, Filters.hasAttached(self), 2);
    }

    @Override
    public List<RequiredTriggerAction> getRequiredAfterTriggers(LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (TriggerConditions.forEachKilledInASkirmish(game, effectResult, self.getAttachedTo(), CardType.COMPANION)) {
            ForEachKilledResult killResult = (ForEachKilledResult) effectResult;
            Race killedRace = killResult.getKilledCard().getBlueprint().getRace();
            int burdens = (killedRace == Race.HOBBIT) ? 2 : 1;
            RequiredTriggerAction action = new RequiredTriggerAction(self);
            action.appendEffect(
                    new AddBurdenEffect(self.getOwner(), self, burdens));
            return Collections.singletonList(action);
        }
        return null;
    }
}
