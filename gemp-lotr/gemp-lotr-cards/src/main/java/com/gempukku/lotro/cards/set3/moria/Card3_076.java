package com.gempukku.lotro.cards.set3.moria;

import com.gempukku.lotro.cards.AbstractOldEvent;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.effects.RevealRandomCardsFromHandEffect;
import com.gempukku.lotro.common.CardType;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Phase;
import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.effects.AddTwilightEffect;

import java.util.List;

/**
 * Set: Realms of Elf-lords
 * Side: Shadow
 * Culture: Moria
 * Twilight Cost: 2
 * Type: Event
 * Game Text: Shadow: Spot a [MORIA] minion to reveal a card at random from the Free Peoples player's hand. Add (X),
 * where X is the twilight cost of the card revealed.
 */
public class Card3_076 extends AbstractOldEvent {
    public Card3_076() {
        super(Side.SHADOW, Culture.MORIA, "Dangerous Gamble", Phase.SHADOW);
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int withTwilightRemoved, int twilightModifier, boolean ignoreRoamingPenalty, boolean ignoreCheckingDeadPile) {
        return super.checkPlayRequirements(playerId, game, self, withTwilightRemoved, twilightModifier, ignoreRoamingPenalty, ignoreCheckingDeadPile)
                && Filters.canSpot(game.getGameState(), game.getModifiersQuerying(), Culture.MORIA, CardType.MINION);
    }

    @Override
    public int getTwilightCost() {
        return 2;
    }

    @Override
    public PlayEventAction getPlayCardAction(String playerId, LotroGame game, final PhysicalCard self, int twilightModifier, boolean ignoreRoamingPenalty) {
        final PlayEventAction action = new PlayEventAction(self);
        action.appendEffect(
                new RevealRandomCardsFromHandEffect(playerId, game.getGameState().getCurrentPlayerId(), self, 1) {
                    @Override
                    protected void cardsRevealed(List<PhysicalCard> revealedCards) {
                        if (revealedCards.size() > 0) {
                            PhysicalCard revealedCard = revealedCards.get(0);
                            int twilightCost = revealedCard.getBlueprint().getTwilightCost();
                            action.appendEffect(
                                    new AddTwilightEffect(self, twilightCost));
                        }
                    }
                });
        return action;
    }
}
