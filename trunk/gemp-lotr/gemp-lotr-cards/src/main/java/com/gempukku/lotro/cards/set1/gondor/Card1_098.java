package com.gempukku.lotro.cards.set1.gondor;

import com.gempukku.lotro.cards.AbstractAttachableFPPossession;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.ExertCharactersEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.effects.ChooseActiveCardEffect;
import com.gempukku.lotro.logic.effects.DiscardCardsFromPlayEffect;
import com.gempukku.lotro.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Free
 * Culture: Gondor
 * Twilight Cost: 0
 * Type: Possession • Cloak
 * Game Text: Bearer must be Boromir. Maneuver: Exert Boromir to discard a weather condition.
 */
public class Card1_098 extends AbstractAttachableFPPossession {
    public Card1_098() {
        super(0, 0, 0, Culture.GONDOR, PossessionClass.CLOAK, "Boromir's Cloak", null, true);
    }

    @Override
    protected Filter getValidTargetFilter(String playerId, LotroGame game, PhysicalCard self) {
        return Filters.boromir;
    }

    @Override
    protected List<? extends Action> getExtraInPlayPhaseActions(String playerId, LotroGame game, final PhysicalCard self) {
        if (PlayConditions.canUseFPCardDuringPhase(game, Phase.MANEUVER, self)
                && PlayConditions.canExert(self, game, self.getAttachedTo())) {
            final ActivateCardAction action = new ActivateCardAction(self);
            action.appendCost(new ExertCharactersEffect(self, self.getAttachedTo()));
            action.appendEffect(
                    new ChooseActiveCardEffect(self, playerId, "Choose a Weather condition", Keyword.WEATHER, CardType.CONDITION) {
                        @Override
                        protected void cardSelected(LotroGame game, PhysicalCard weatherCondition) {
                            action.appendEffect(new DiscardCardsFromPlayEffect(self, weatherCondition));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
