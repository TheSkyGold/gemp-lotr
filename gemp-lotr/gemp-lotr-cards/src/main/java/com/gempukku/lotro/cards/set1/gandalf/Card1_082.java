package com.gempukku.lotro.cards.set1.gandalf;

import com.gempukku.lotro.cards.AbstractOldEvent;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.effects.PutCardsFromDeckIntoHandDiscardRestEffect;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Keyword;
import com.gempukku.lotro.common.Phase;
import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Free
 * Culture: Gandalf
 * Twilight Cost: 1
 * Type: Event
 * Game Text: Spell. Fellowship: If the twilight pool has fewer than 2 twilight tokens, spot Gandalf to look at the top
 * 2 cards of your draw deck. Take one into hand and discard the other.
 */
public class Card1_082 extends AbstractOldEvent {
    public Card1_082() {
        super(Side.FREE_PEOPLE, Culture.GANDALF, "Risk a Little Light", Phase.FELLOWSHIP);
        addKeyword(Keyword.SPELL);
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int withTwilightRemoved, int twilightModifier, boolean ignoreRoamingPenalty, boolean ignoreCheckingDeadPile) {
        return super.checkPlayRequirements(playerId, game, self, withTwilightRemoved, twilightModifier, ignoreRoamingPenalty, ignoreCheckingDeadPile)
                && game.getGameState().getTwilightPool() < 2
                && Filters.canSpot(game.getGameState(), game.getModifiersQuerying(), Filters.gandalf);
    }

    @Override
    public PlayEventAction getPlayCardAction(final String playerId, LotroGame game, PhysicalCard self, int twilightModifier, boolean ignoreRoamingPenalty) {
        final PlayEventAction action = new PlayEventAction(self);
        List<? extends PhysicalCard> deck = game.getGameState().getDeck(playerId);
        final List<PhysicalCard> cards = new LinkedList<PhysicalCard>(deck.subList(0, Math.min(deck.size(), 2)));
        action.appendEffect(
                new PutCardsFromDeckIntoHandDiscardRestEffect(action, self, playerId, cards, 1, Filters.any));
        return action;
    }

    @Override
    public int getTwilightCost() {
        return 1;
    }
}
