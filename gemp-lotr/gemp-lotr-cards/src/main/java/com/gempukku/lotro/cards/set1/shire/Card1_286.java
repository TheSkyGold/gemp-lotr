package com.gempukku.lotro.cards.set1.shire;

import com.gempukku.lotro.cards.AbstractAlly;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.AddUntilEndOfPhaseModifierEffect;
import com.gempukku.lotro.cards.effects.SelfExertEffect;
import com.gempukku.lotro.cards.modifiers.OverwhelmedByMultiplierModifier;
import com.gempukku.lotro.common.Block;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Phase;
import com.gempukku.lotro.common.Race;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.effects.ChooseActiveCardEffect;
import com.gempukku.lotro.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Free
 * Culture: Shire
 * Twilight Cost: 1
 * Type: Ally • Home 2 • Hobbit
 * Strength: 2
 * Vitality: 2
 * Site: 2
 * Game Text: Skirmish: Exert this ally to prevent a Hobbit from being overwhelmed unless that Hobbit's strength is
 * tripled.
 */
public class Card1_286 extends AbstractAlly {
    public Card1_286() {
        super(1, Block.FELLOWSHIP, 2, 2, 2, Race.HOBBIT, Culture.SHIRE, "Bounder");
    }

    @Override
    protected List<? extends Action> getExtraInPlayPhaseActions(String playerId, LotroGame game, final PhysicalCard self) {
        if (PlayConditions.canUseFPCardDuringPhase(game, Phase.SKIRMISH, self)
                && PlayConditions.canExert(self, game, self)) {
            final ActivateCardAction action = new ActivateCardAction(self);
            action.appendCost(new SelfExertEffect(action, self));
            action.appendEffect(
                    new ChooseActiveCardEffect(self, playerId, "Choose a Hobbit", Race.HOBBIT) {
                        @Override
                        protected void cardSelected(LotroGame game, PhysicalCard hobbit) {
                            action.appendEffect(
                                    new AddUntilEndOfPhaseModifierEffect(
                                            new OverwhelmedByMultiplierModifier(self, Filters.sameCard(hobbit), 3)));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
