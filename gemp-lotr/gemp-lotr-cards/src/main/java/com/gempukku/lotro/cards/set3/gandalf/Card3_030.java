package com.gempukku.lotro.cards.set3.gandalf;

import com.gempukku.lotro.cards.AbstractOldEvent;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.effects.DiscardCardsFromPlayEffect;

/**
 * Set: Realms of Elf-lords
 * Side: Free
 * Culture: Gandalf
 * Twilight Cost: 4
 * Type: Event
 * Game Text: Spell. Maneuver: Spot Gandalf and 4 twilight tokens to discard all Shadow conditions.
 */
public class Card3_030 extends AbstractOldEvent {
    public Card3_030() {
        super(Side.FREE_PEOPLE, Culture.GANDALF, "Deep in Thought", Phase.MANEUVER);
        addKeyword(Keyword.SPELL);
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int withTwilightRemoved, int twilightModifier, boolean ignoreRoamingPenalty, boolean ignoreCheckingDeadPile) {
        return super.checkPlayRequirements(playerId, game, self, withTwilightRemoved, twilightModifier, ignoreRoamingPenalty, ignoreCheckingDeadPile)
                && Filters.canSpot(game.getGameState(), game.getModifiersQuerying(), Filters.gandalf)
                && game.getGameState().getTwilightPool() >= 4;
    }

    @Override
    public int getTwilightCost() {
        return 4;
    }

    @Override
    public PlayEventAction getPlayCardAction(String playerId, LotroGame game, PhysicalCard self, int twilightModifier, boolean ignoreRoamingPenalty) {
        PlayEventAction action = new PlayEventAction(self);
        action.appendEffect(
                new DiscardCardsFromPlayEffect(self, Filters.and(Side.SHADOW, CardType.CONDITION)));
        return action;
    }
}
