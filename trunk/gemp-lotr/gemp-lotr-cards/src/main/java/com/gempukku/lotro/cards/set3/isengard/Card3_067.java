package com.gempukku.lotro.cards.set3.isengard;

import com.gempukku.lotro.cards.AbstractPermanent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.PutCardFromHandOnTopOfDeckEffect;
import com.gempukku.lotro.cards.effects.RemoveTwilightEffect;
import com.gempukku.lotro.cards.effects.RevealRandomCardsFromHandEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Realms of Elf-lords
 * Side: Shadow
 * Culture: Isengard
 * Twilight Cost: 0
 * Type: Artifact
 * Game Text: To play, spot an [ISENGARD] minion. Plays to your support area. Shadow: Spot an [ISENGARD] minion and
 * remove (1) to reveal a card at random from the Free Peoples player's hand. Place that card on top of that player's draw deck.
 */
public class Card3_067 extends AbstractPermanent {
    public Card3_067() {
        super(Side.SHADOW, 0, CardType.ARTIFACT, Culture.ISENGARD, Zone.SUPPORT, "The Palanthir of Orthanc", null, true);
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int withTwilightRemoved, int twilightModifier, boolean ignoreRoamingPenalty, boolean ignoreCheckingDeadPile) {
        return super.checkPlayRequirements(playerId, game, self, withTwilightRemoved, twilightModifier, ignoreRoamingPenalty, ignoreCheckingDeadPile)
                && Filters.canSpot(game.getGameState(), game.getModifiersQuerying(), Culture.ISENGARD, CardType.MINION);
    }

    @Override
    protected List<? extends Action> getExtraPhaseActions(String playerId, LotroGame game, PhysicalCard self) {
        if (PlayConditions.canUseShadowCardDuringPhase(game, Phase.SHADOW, self, 1)
                && PlayConditions.canSpot(game, Culture.ISENGARD, CardType.MINION)) {
            final ActivateCardAction action = new ActivateCardAction(self);
            action.appendCost(
                    new RemoveTwilightEffect(1));
            action.appendEffect(
                    new RevealRandomCardsFromHandEffect(playerId, game.getGameState().getCurrentPlayerId(), self, 1) {
                        @Override
                        protected void cardsRevealed(List<PhysicalCard> revealedCards) {
                            for (PhysicalCard revealedCard : revealedCards) {
                                action.appendEffect(
                                        new PutCardFromHandOnTopOfDeckEffect(revealedCard));
                            }
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
