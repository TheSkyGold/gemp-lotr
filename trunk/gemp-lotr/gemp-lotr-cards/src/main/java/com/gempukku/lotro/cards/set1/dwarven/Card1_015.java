package com.gempukku.lotro.cards.set1.dwarven;

import com.gempukku.lotro.cards.AbstractAttachableFPPossession;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.AddUntilEndOfPhaseModifierEffect;
import com.gempukku.lotro.cards.modifiers.CantTakeWoundsModifier;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Keyword;
import com.gempukku.lotro.common.Phase;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.DefaultCostToEffectAction;
import com.gempukku.lotro.logic.effects.DiscardCardFromPlayEffect;
import com.gempukku.lotro.logic.timing.Action;
import com.gempukku.lotro.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Free
 * Culture: Dwarven
 * Twilight Cost: 0
 * Type: Possession � Helm
 * Game Text: Bearer must be Gimli. He takes no more than 1 wound during each skirmish phase. Skirmish: Discard Gimli's
 * Helm to prevent all wounds to him.
 */
public class Card1_015 extends AbstractAttachableFPPossession {
    public Card1_015() {
        super(0, Culture.DWARVEN, Keyword.HELM, "Gimli's Helm", true);
    }

    @Override
    protected Filter getValidTargetFilter(String playerId, LotroGame game, PhysicalCard self) {
        return Filters.and(Filters.name("Gimli"), Filters.not(Filters.hasAttached(Filters.keyword(Keyword.HELM))));
    }

    @Override
    protected List<? extends Action> getExtraPhaseActions(String playerId, LotroGame game, PhysicalCard self) {
        if (PlayConditions.canUseFPCardDuringPhase(game.getGameState(), Phase.SKIRMISH, self)) {
            DefaultCostToEffectAction action = new DefaultCostToEffectAction(self, Keyword.SKIRMISH, "Discard Gimli's Helm to prevent all wounds to him");
            action.addCost(new DiscardCardFromPlayEffect(self, self));
            action.addEffect(
                    new AddUntilEndOfPhaseModifierEffect(
                            new CantTakeWoundsModifier(self, Filters.sameCard(self.getAttachedTo())), Phase.SKIRMISH));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    public List<? extends Action> getRequiredAfterTriggers(LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (PlayConditions.isWounded(effectResult, self.getAttachedTo())) {
            DefaultCostToEffectAction action = new DefaultCostToEffectAction(self, null, "Apply damage prevention");
            action.addEffect(
                    new AddUntilEndOfPhaseModifierEffect(
                            new CantTakeWoundsModifier(self, Filters.sameCard(self.getAttachedTo())), Phase.SKIRMISH));
            return Collections.<Action>singletonList(action);
        }
        return null;
    }
}
