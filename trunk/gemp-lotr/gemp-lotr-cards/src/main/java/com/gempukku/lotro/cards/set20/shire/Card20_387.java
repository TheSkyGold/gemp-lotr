package com.gempukku.lotro.cards.set20.shire;

import com.gempukku.lotro.cards.AbstractCompanion;
import com.gempukku.lotro.cards.TriggerConditions;
import com.gempukku.lotro.cards.modifiers.OverwhelmedByMultiplierModifier;
import com.gempukku.lotro.cards.modifiers.conditions.LocationCondition;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Keyword;
import com.gempukku.lotro.common.Race;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.RequiredTriggerAction;
import com.gempukku.lotro.logic.effects.HealCharactersEffect;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * 0
 * •Frodo, Bane of the Sackville Bagginses
 * Shire	Companion • Hobbit
 * 3	4	10R
 * Ring-bound.
 * While Frodo is in region 1, he is not overwhelmed unless his strength is tripled:
 * Each time you play a [Shire] tale, heal Frodo.
 */
public class Card20_387 extends AbstractCompanion {
    public Card20_387() {
        super(0, 3, 4, 10, Culture.SHIRE, Race.HOBBIT, null, "Frodo", "Bane of the Sackville Bagginses", true);
        addKeyword(Keyword.RING_BOUND);
        addKeyword(Keyword.CAN_START_WITH_RING);
    }

    @Override
    public Modifier getAlwaysOnModifier(PhysicalCard self) {
        return new OverwhelmedByMultiplierModifier(self, self,
                new LocationCondition(Filters.region(1)), 3);
    }

    @Override
    public List<RequiredTriggerAction> getRequiredAfterTriggers(LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (TriggerConditions.played(game, effectResult, Culture.SHIRE, Keyword.TALE)) {
            RequiredTriggerAction action = new RequiredTriggerAction(self);
            action.appendEffect(
                    new HealCharactersEffect(self, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
