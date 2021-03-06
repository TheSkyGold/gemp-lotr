package com.gempukku.lotro.cards.set20.isengard;

import com.gempukku.lotro.cards.AbstractPermanent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.StackCardFromPlayEffect;
import com.gempukku.lotro.cards.effects.choose.ChooseAndPlayCardFromStackedEffect;
import com.gempukku.lotro.cards.modifiers.evaluator.CountStackedEvaluator;
import com.gempukku.lotro.cards.modifiers.evaluator.NegativeEvaluator;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.effects.ChooseActiveCardEffect;
import com.gempukku.lotro.logic.effects.RemoveThreatsEffect;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.modifiers.TwilightCostModifier;
import com.gempukku.lotro.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * 2
 * Throne of Orthanc
 * Artifact • Support Area
 * The twilight cost of each [Isengard] event is -1 for each minion stacked on this artifact.
 * Regroup: If Saruman is not exhausted, stack him here.
 * Shadow: Remove a threat to play Saruman stacked here as if from hand.
 * http://lotrtcg.org/coreset/isengard/throneoforthanc(r1).png
 */
public class Card20_240 extends AbstractPermanent {
    public Card20_240() {
        super(Side.SHADOW, 1, CardType.ARTIFACT, Culture.ISENGARD, Zone.SUPPORT, "Throne of Orthanc", null, true);
    }

    @Override
    public Modifier getAlwaysOnModifier(LotroGame game, PhysicalCard self) {
        return new TwilightCostModifier(self, Filters.and(Culture.ISENGARD, CardType.EVENT), null,
                new NegativeEvaluator(new CountStackedEvaluator(self, CardType.MINION)));
    }

    @Override
    protected List<? extends Action> getExtraPhaseActions(String playerId, LotroGame game, final PhysicalCard self) {
        if (PlayConditions.canUseShadowCardDuringPhase(game, Phase.REGROUP, self, 0)
                && PlayConditions.isActive(game, Filters.saruman, Filters.not(Filters.exhausted))) {
            final ActivateCardAction action = new ActivateCardAction(self);
            action.appendEffect(
                    new ChooseActiveCardEffect(self, playerId, "Choose Saruman", Filters.saruman, Filters.not(Filters.exhausted)) {
                        @Override
                        protected void cardSelected(LotroGame game, PhysicalCard card) {
                            action.appendEffect(
                                    new StackCardFromPlayEffect(card, self));
                        }
                    });
            return Collections.singletonList(action);
        }
        if (PlayConditions.canUseShadowCardDuringPhase(game, Phase.SHADOW, self, 0)
                && PlayConditions.canRemoveThreat(game, self, 1)) {
            ActivateCardAction action = new ActivateCardAction(self);
            action.appendCost(
                    new RemoveThreatsEffect(self, 1));
            action.appendEffect(
                    new ChooseAndPlayCardFromStackedEffect(playerId, self, Filters.saruman));
            return Collections.singletonList(action);
        }
        return null;
    }
}
