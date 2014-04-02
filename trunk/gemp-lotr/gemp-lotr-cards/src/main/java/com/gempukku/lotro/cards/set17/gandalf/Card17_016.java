package com.gempukku.lotro.cards.set17.gandalf;

import com.gempukku.lotro.cards.AbstractCompanion;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.DiscardCardFromDeckEffect;
import com.gempukku.lotro.cards.effects.PutCardFromDeckIntoHandOrDiscardEffect;
import com.gempukku.lotro.cards.effects.RevealTopCardsOfDrawDeckEffect;
import com.gempukku.lotro.cards.effects.SelfExertEffect;
import com.gempukku.lotro.cards.modifiers.evaluator.CountActiveEvaluator;
import com.gempukku.lotro.common.CardType;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Phase;
import com.gempukku.lotro.common.Race;
import com.gempukku.lotro.common.Zone;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.modifiers.StrengthModifier;

import java.util.Collections;
import java.util.List;

/**
 * Set: Rise of Saruman
 * Side: Free
 * Culture: Gandalf
 * Twilight Cost: 2
 * Type: Companion • Man
 * Strength: 4
 * Vitality: 3
 * Resistance: 6
 * Game Text: Barliman Butterbur is strength +1 for each attached [GANDALF] follower. Maneuver: Exert Barliman Butterbur
 * to reveal the top 2 cards of your deck. Take any [GANDALF] cards into hand and discard the rest.
 */
public class Card17_016 extends AbstractCompanion {
    public Card17_016() {
        super(2, 4, 3, 6, Culture.GANDALF, Race.MAN, null, "Barliman Butterbur", "Red-Faced Landlord", true);
    }

    @Override
    public Modifier getAlwaysOnModifier(LotroGame game, PhysicalCard self) {
        return new StrengthModifier(self, self, null, new CountActiveEvaluator(Zone.ATTACHED, Culture.GANDALF, CardType.FOLLOWER));
    }

    @Override
    protected List<ActivateCardAction> getExtraInPlayPhaseActions(String playerId, LotroGame game, PhysicalCard self) {
        if (PlayConditions.canUseFPCardDuringPhase(game, Phase.MANEUVER, self)
                && PlayConditions.canSelfExert(self, game)) {
            final ActivateCardAction action = new ActivateCardAction(self);
            action.appendCost(
                    new SelfExertEffect(action, self));
            action.appendEffect(
                    new RevealTopCardsOfDrawDeckEffect(self, playerId, 2) {
                        @Override
                        protected void cardsRevealed(List<PhysicalCard> revealedCards) {
                            for (PhysicalCard card : revealedCards) {
                                if (card.getBlueprint().getCulture() == Culture.GANDALF) {
                                    action.appendEffect(
                                            new PutCardFromDeckIntoHandOrDiscardEffect(card));
                                } else {
                                    action.appendEffect(
                                            new DiscardCardFromDeckEffect(card));
                                }
                            }
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
