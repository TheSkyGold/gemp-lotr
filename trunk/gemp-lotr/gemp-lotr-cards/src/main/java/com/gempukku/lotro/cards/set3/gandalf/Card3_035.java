package com.gempukku.lotro.cards.set3.gandalf;

import com.gempukku.lotro.cards.AbstractPermanent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.AddUntilEndOfPhaseModifierEffect;
import com.gempukku.lotro.cards.effects.choose.ChooseAndExertCharactersEffect;
import com.gempukku.lotro.cards.modifiers.evaluator.CardPhaseLimitEvaluator;
import com.gempukku.lotro.cards.modifiers.evaluator.CountActiveEvaluator;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.effects.ChooseActiveCardEffect;
import com.gempukku.lotro.logic.modifiers.StrengthModifier;
import com.gempukku.lotro.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Realms of Elf-lords
 * Side: Free
 * Culture: Gandalf
 * Twilight Cost: 2
 * Type: Condition
 * Game Text: Plays to your support area. Skirmish: Exert Gandalf to make a companion strength +1 for each companion
 * with the Gandalf signet you can spot (limit +3).
 */
public class Card3_035 extends AbstractPermanent {
    public Card3_035() {
        super(Side.FREE_PEOPLE, 2, CardType.CONDITION, Culture.GANDALF, Zone.SUPPORT, "Trust Me as You Once Did");
    }

    @Override
    protected List<? extends Action> getExtraPhaseActions(String playerId, final LotroGame game, final PhysicalCard self) {
        if (PlayConditions.canUseFPCardDuringPhase(game, Phase.SKIRMISH, self)
                && PlayConditions.canExert(self, game, Filters.gandalf)) {
            final ActivateCardAction action = new ActivateCardAction(self);
            action.appendCost(
                    new ChooseAndExertCharactersEffect(action, playerId, 1, 1, Filters.gandalf));
            action.appendEffect(
                    new ChooseActiveCardEffect(self, playerId, "Choose a companion", CardType.COMPANION) {
                        @Override
                        protected void cardSelected(LotroGame game, PhysicalCard card) {
                            action.insertEffect(
                                    new AddUntilEndOfPhaseModifierEffect(
                                            new StrengthModifier(self, Filters.sameCard(card), null,
                                                    new CardPhaseLimitEvaluator(game, self, Phase.SKIRMISH, 3,
                                                            new CountActiveEvaluator(CardType.COMPANION, Signet.GANDALF)))));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
