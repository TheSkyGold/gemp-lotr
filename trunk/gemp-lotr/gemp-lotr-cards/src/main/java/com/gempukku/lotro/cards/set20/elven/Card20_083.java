package com.gempukku.lotro.cards.set20.elven;

import com.gempukku.lotro.cards.AbstractPermanent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.actions.SubCostToEffectAction;
import com.gempukku.lotro.cards.effects.DiscardCardFromDeckEffect;
import com.gempukku.lotro.cards.effects.SelfDiscardEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.lotro.logic.decisions.DecisionResultInvalidException;
import com.gempukku.lotro.logic.decisions.YesNoDecision;
import com.gempukku.lotro.logic.effects.AddTwilightEffect;
import com.gempukku.lotro.logic.effects.PlayoutDecisionEffect;
import com.gempukku.lotro.logic.timing.Action;
import com.gempukku.lotro.logic.timing.UnrespondableEffect;

import java.util.Collections;
import java.util.List;

/**
 * 0
 * Forearmed
 * Elven	Condition • Support Area
 * To play spot two elves.
 * Fellowship: Add (1) to look at the top card of your draw deck. You may discard this condition to discard that card.
 */
public class Card20_083 extends AbstractPermanent {
    public Card20_083() {
        super(Side.FREE_PEOPLE, 0, CardType.CONDITION, Culture.ELVEN, Zone.SUPPORT, "Forearmed");
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int withTwilightRemoved, int twilightModifier, boolean ignoreRoamingPenalty, boolean ignoreCheckingDeadPile) {
        return super.checkPlayRequirements(playerId, game, self, withTwilightRemoved, twilightModifier, ignoreRoamingPenalty, ignoreCheckingDeadPile)
                && PlayConditions.canSpot(game, 2, Race.ELF);
    }

    @Override
    protected List<? extends Action> getExtraPhaseActions(final String playerId, LotroGame game, final PhysicalCard self) {
        if (PlayConditions.canUseFPCardDuringPhase(game, Phase.FELLOWSHIP, self)) {
            final ActivateCardAction action = new ActivateCardAction(self);
            action.appendCost(
                    new AddTwilightEffect(self, 1));
            action.appendEffect(
                    new UnrespondableEffect() {
                        @Override
                        protected void doPlayEffect(LotroGame game) {
                            if (game.getGameState().getDeck(playerId).size()>0) {
                                final PhysicalCard topCard = game.getGameState().getDeck(playerId).get(0);
                                action.appendEffect(
                                        new PlayoutDecisionEffect(playerId,
                                                        new ArbitraryCardsSelectionDecision(1, "Your top card", Collections.singleton(topCard), Collections.<PhysicalCard>emptyList(), 0, 0) {
                                                            @Override
                                                            public void decisionMade(String result) throws DecisionResultInvalidException {
                                                            }
                                                        }));
                                action.appendEffect(
                                        new PlayoutDecisionEffect(playerId,
                                                new YesNoDecision("Do you want to discard this condition to discard that card?") {
                                                    @Override
                                                    protected void yes() {
                                                        SubCostToEffectAction subAction = new SubCostToEffectAction(action);
                                                        subAction.appendCost(
                                                                new SelfDiscardEffect(self));
                                                        subAction.appendEffect(
                                                                new DiscardCardFromDeckEffect(topCard));
                                                    }
                                                }));
                            }
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
