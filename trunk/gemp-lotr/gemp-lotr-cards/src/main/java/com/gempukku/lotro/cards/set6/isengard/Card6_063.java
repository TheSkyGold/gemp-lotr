package com.gempukku.lotro.cards.set6.isengard;

import com.gempukku.lotro.cards.AbstractPermanent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.TriggerConditions;
import com.gempukku.lotro.cards.effects.RemoveTwilightEffect;
import com.gempukku.lotro.cards.effects.StackCardFromDiscardEffect;
import com.gempukku.lotro.cards.effects.choose.ChooseAndDiscardStackedCardsEffect;
import com.gempukku.lotro.cards.effects.choose.ChooseAndPlayCardFromDiscardEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.GameUtils;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.actions.OptionalTriggerAction;
import com.gempukku.lotro.logic.timing.Action;
import com.gempukku.lotro.logic.timing.EffectResult;
import com.gempukku.lotro.logic.timing.results.DiscardCardsFromPlayResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Ents of Fangorn
 * Side: Shadow
 * Culture: Isengard
 * Twilight Cost: 2
 * Type: Condition
 * Game Text: Plays to your support area. Each time a regroup action discards an [ISENGARD] Orc, you may stack that Orc
 * on this card. Shadow: Discard 2 cards stacked here and remove (1) to play an [ISENGARD] Orc from your discard pile.
 */
public class Card6_063 extends AbstractPermanent {
    public Card6_063() {
        super(Side.SHADOW, 2, CardType.CONDITION, Culture.ISENGARD, Zone.SUPPORT, "Gnawing, Biting, Hacking, Burning");
    }

    @Override
    public List<OptionalTriggerAction> getOptionalAfterTriggers(String playerId, LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (TriggerConditions.forEachDiscardedFromPlay(game, effectResult, Culture.ISENGARD, Race.ORC)) {
            DiscardCardsFromPlayResult discardResult = (DiscardCardsFromPlayResult) effectResult;
            if (game.getGameState().getCurrentPhase() == Phase.REGROUP
                    && discardResult.getSource() != null) {
                PhysicalCard discardedOrc = discardResult.getDiscardedCard();
                OptionalTriggerAction action = new OptionalTriggerAction(self);
                action.setTriggerIdentifier(self.getCardId()+"-"+discardedOrc.getCardId());
                action.setText("Stack " + GameUtils.getCardLink(discardedOrc));
                action.appendEffect(
                        new StackCardFromDiscardEffect(discardedOrc, self));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<? extends Action> getExtraPhaseActions(String playerId, LotroGame game, PhysicalCard self) {
        if (PlayConditions.canUseShadowCardDuringPhase(game, Phase.SHADOW, self, 1)
                && game.getGameState().getStackedCards(self).size() >= 2
                && PlayConditions.canPlayFromDiscard(playerId, game, 1, 0, Culture.ISENGARD, Race.ORC)) {
            ActivateCardAction action = new ActivateCardAction(self);
            action.appendCost(
                    new ChooseAndDiscardStackedCardsEffect(action, playerId, 2, 2, self, Filters.any));
            action.appendCost(
                    new RemoveTwilightEffect(1));
            action.appendEffect(
                    new ChooseAndPlayCardFromDiscardEffect(playerId, game, Culture.ISENGARD, Race.ORC));
            return Collections.singletonList(action);
        }
        return null;
    }
}
