package com.gempukku.lotro.cards.set2.dwarven;

import com.gempukku.lotro.cards.AbstractAttachableFPPossession;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.costs.DiscardCardsFromPlayCost;
import com.gempukku.lotro.cards.effects.AddUntilEndOfPhaseModifierEffect;
import com.gempukku.lotro.cards.modifiers.ArcheryTotalModifier;
import com.gempukku.lotro.cards.modifiers.StrengthModifier;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Mines of Moria
 * Side: Free
 * Culture: Dwarven
 * Twilight Cost: 0
 * Type: Possession • Hand Weapon
 * Strength: +1
 * Game Text: Bearer must be a Dwarf. This weapon may be borne in addition to 1 other hand weapon. Archery: Discard
 * this weapon to make the fellowship archery total +1.
 */
public class Card2_010 extends AbstractAttachableFPPossession {
    public Card2_010() {
        super(0, Culture.DWARVEN, Keyword.HAND_WEAPON, "Hand Axe");
    }

    @Override
    public boolean isExtraPossessionClass() {
        return true;
    }

    @Override
    protected Filter getValidTargetFilter(String playerId, LotroGame game, PhysicalCard self) {
        return Filters.race(Race.DWARF);
    }

    @Override
    public Modifier getAlwaysOnEffect(PhysicalCard self) {
        return new StrengthModifier(self, Filters.hasAttached(self), 1);
    }

    @Override
    protected List<? extends Action> getExtraInPlayPhaseActions(String playerId, LotroGame game, PhysicalCard self) {
        if (PlayConditions.canPlayCardDuringPhase(game, Phase.ARCHERY, self)) {
            ActivateCardAction action = new ActivateCardAction(self, Keyword.ARCHERY, "Discard this weapon to make the fellowship archery total +1");
            action.appendCost(
                    new DiscardCardsFromPlayCost(self));
            action.appendEffect(
                    new AddUntilEndOfPhaseModifierEffect(
                            new ArcheryTotalModifier(self, Side.FREE_PEOPLE, 1), Phase.ARCHERY));
            return Collections.singletonList(action);
        }
        return null;
    }
}
