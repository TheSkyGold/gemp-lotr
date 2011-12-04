package com.gempukku.lotro.cards.set1.dwarven;

import com.gempukku.lotro.cards.AbstractOldEvent;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.effects.DiscardCardFromDeckEffect;
import com.gempukku.lotro.cards.effects.PutCardFromDeckIntoHandOrDiscardEffect;
import com.gempukku.lotro.cards.effects.RevealTopCardsOfDrawDeckEffect;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Phase;
import com.gempukku.lotro.common.Race;
import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;

import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Free
 * Culture: Dwarven
 * Twilight Cost: 2
 * Type: Event
 * Game Text: Fellowship: Spot a Dwarf to reveal the top 3 cards of your draw deck. Take all Free Peoples cards
 * revealed into hand and discard the rest.
 */
public class Card1_028 extends AbstractOldEvent {
    public Card1_028() {
        super(Side.FREE_PEOPLE, Culture.DWARVEN, "Wealth of Moria", Phase.FELLOWSHIP);
    }

    @Override
    public int getTwilightCost() {
        return 2;
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int withTwilightRemoved, int twilightModifier, boolean ignoreRoamingPenalty, boolean ignoreCheckingDeadPile) {
        return super.checkPlayRequirements(playerId, game, self, withTwilightRemoved, twilightModifier, ignoreRoamingPenalty, ignoreCheckingDeadPile)
                && Filters.canSpot(game.getGameState(), game.getModifiersQuerying(), Race.DWARF);
    }

    @Override
    public PlayEventAction getPlayCardAction(final String playerId, LotroGame game, PhysicalCard self, int twilightModifier, boolean ignoreRoamingPenalty) {
        final PlayEventAction action = new PlayEventAction(self);
        action.appendEffect(
                new RevealTopCardsOfDrawDeckEffect(self, playerId, 3) {
                    @Override
                    protected void cardsRevealed(List<PhysicalCard> cards) {
                        for (PhysicalCard card : cards) {
                            if (card.getBlueprint().getSide() == Side.FREE_PEOPLE)
                                action.appendEffect(new PutCardFromDeckIntoHandOrDiscardEffect(card));
                            else
                                action.appendEffect(new DiscardCardFromDeckEffect(card));
                        }
                    }
                });
        return action;
    }
}
