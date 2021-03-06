package com.gempukku.lotro.cards.set1.shire;

import com.gempukku.lotro.cards.AbstractCompanion;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.AddUntilEndOfPhaseModifierEffect;
import com.gempukku.lotro.cards.effects.SelfExertEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.effects.ChooseActiveCardEffect;
import com.gempukku.lotro.logic.modifiers.StrengthModifier;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Free
 * Culture: Shire
 * Twilight Cost: 1
 * Type: Companion • Hobbit
 * Strength: 3
 * Vitality: 4
 * Resistance: 6
 * Signet: Frodo
 * Game Text: Skirmish: If Merry is not assigned to a skirmish, exert him twice to add his strength to another
 * companion.
 */
public class Card1_302 extends AbstractCompanion {
    public Card1_302() {
        super(1, 3, 4, 6, Culture.SHIRE, Race.HOBBIT, Signet.FRODO, "Merry", "Friend to Sam", true);
    }

    @Override
    protected List<ActivateCardAction> getExtraInPlayPhaseActions(String playerId, final LotroGame game, final PhysicalCard self) {
        if (PlayConditions.canUseFPCardDuringPhase(game, Phase.SKIRMISH, self)
                && PlayConditions.canExert(self, game, 2, self)
                && Filters.notAssignedToSkirmish.accepts(game.getGameState(), game.getModifiersQuerying(), self)) {
            final ActivateCardAction action = new ActivateCardAction(self);
            action.appendCost(
                    new SelfExertEffect(action, self));
            action.appendCost(
                    new SelfExertEffect(action, self));
            action.appendEffect(
                    new ChooseActiveCardEffect(self, playerId, "Choose another companion", CardType.COMPANION, Filters.not(self)) {
                        @Override
                        protected void cardSelected(LotroGame game, PhysicalCard anotherCompanion) {
                            int merryStrength = game.getModifiersQuerying().getStrength(game.getGameState(), self);
                            action.appendEffect(
                                    new AddUntilEndOfPhaseModifierEffect(
                                            new StrengthModifier(self, Filters.sameCard(anotherCompanion), merryStrength)));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

}
