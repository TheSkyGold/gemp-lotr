package com.gempukku.lotro.cards.set20.fallenRealms;

import com.gempukku.lotro.cards.AbstractPermanent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.TriggerConditions;
import com.gempukku.lotro.cards.effects.RemoveTwilightEffect;
import com.gempukku.lotro.cards.effects.StackCardFromDiscardEffect;
import com.gempukku.lotro.cards.effects.choose.ChooseAndPlayCardFromStackedEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.actions.OptionalTriggerAction;
import com.gempukku.lotro.logic.timing.Action;
import com.gempukku.lotro.logic.timing.EffectResult;
import com.gempukku.lotro.logic.timing.results.DiscardCardsFromPlayResult;

import java.util.Collections;
import java.util.List;

/**
 * ❶ Weapons of the East [Fal]
 * Condition • Support Area
 * To play, spot an Easterling.
 * Each time a [Fal] possession is discarded, you may spot an Easterling stack it here instead.
 * Shadow: Remove ❷ to play a [Fal] possession stacked here as if from hand.
 * <p/>
 * http://lotrtcg.org/coreset/fallenrealms/weaponsoftheeast(r3).jpg
 */
public class Card20_485 extends AbstractPermanent {
    public Card20_485() {
        super(Side.SHADOW, 1, CardType.CONDITION, Culture.FALLEN_REALMS, Zone.SUPPORT, "Weapons of the East");
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int withTwilightRemoved, int twilightModifier, boolean ignoreRoamingPenalty, boolean ignoreCheckingDeadPile) {
        return super.checkPlayRequirements(playerId, game, self, withTwilightRemoved, twilightModifier, ignoreRoamingPenalty, ignoreCheckingDeadPile)
                && PlayConditions.canSpot(game, Keyword.EASTERLING);
    }

    @Override
    public List<OptionalTriggerAction> getOptionalAfterTriggers(String playerId, LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (TriggerConditions.forEachDiscardedFromPlay(game, effectResult, Culture.FALLEN_REALMS, CardType.POSSESSION)
                && PlayConditions.canSpot(game, Keyword.EASTERLING)) {
            OptionalTriggerAction action = new OptionalTriggerAction(self);
            action.appendEffect(
                    new StackCardFromDiscardEffect(((DiscardCardsFromPlayResult) effectResult).getDiscardedCard(), self));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<? extends Action> getExtraPhaseActions(String playerId, LotroGame game, PhysicalCard self) {
        if (PlayConditions.canUseShadowCardDuringPhase(game, Phase.SHADOW, self, 2)
                && PlayConditions.canPlayFromStacked(playerId, game, 2, self, Culture.FALLEN_REALMS, CardType.POSSESSION)) {
            ActivateCardAction action = new ActivateCardAction(self);
            action.appendCost(new RemoveTwilightEffect(3));
            action.appendEffect(
                    new ChooseAndPlayCardFromStackedEffect(playerId, self, Culture.FALLEN_REALMS, CardType.POSSESSION));
            return Collections.singletonList(action);
        }
        return null;
    }
}
