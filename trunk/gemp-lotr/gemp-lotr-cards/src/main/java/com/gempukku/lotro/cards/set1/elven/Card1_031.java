package com.gempukku.lotro.cards.set1.elven;

import com.gempukku.lotro.cards.AbstractAttachableFPPossession;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.SelfDiscardEffect;
import com.gempukku.lotro.cards.modifiers.conditions.LocationCondition;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.RequiredTriggerAction;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.modifiers.StrengthModifier;
import com.gempukku.lotro.logic.timing.EffectResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Set: The Fellowship of the Ring
 * Side: Free
 * Culture: Elven
 * Twilight Cost: 2
 * Type: Possession � Mount
 * Strength: +2
 * Game Text: Bearer must be an Elf. When played on Arwen, Asfaloth's twilight cost is -2. While at a plains site,
 * bearer is strength +2. Discard Asfaloth when at an underground site.
 */
public class Card1_031 extends AbstractAttachableFPPossession {
    public Card1_031() {
        super(2, 2, 0, Culture.ELVEN, PossessionClass.MOUNT, "Asfaloth", null, true);
    }

    @Override
    protected Filterable getValidTargetFilter(String playerId, LotroGame game, PhysicalCard self) {
        return Race.ELF;
    }

    @Override
    protected Map<Filter, Integer> getAttachCostModifiers(String playerId, LotroGame game, PhysicalCard self) {
        Map<Filter, Integer> costModifiers = new HashMap<Filter, Integer>();
        costModifiers.put(Filters.arwen, -2);
        return costModifiers;
    }

    @Override
    protected List<? extends Modifier> getNonBasicStatsModifiers(PhysicalCard self) {
        return Collections.singletonList(new StrengthModifier(self, Filters.hasAttached(self),
                new LocationCondition(Keyword.PLAINS), 2));
    }

    @Override
    public List<RequiredTriggerAction> getRequiredAfterTriggers(LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (PlayConditions.canSpot(game, Filters.hasAttached(self), CardType.COMPANION)
                && PlayConditions.location(game, Keyword.UNDERGROUND)) {
            RequiredTriggerAction action = new RequiredTriggerAction(self);
            action.appendEffect(new SelfDiscardEffect(self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
