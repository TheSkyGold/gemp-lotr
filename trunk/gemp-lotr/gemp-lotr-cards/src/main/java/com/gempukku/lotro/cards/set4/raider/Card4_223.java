package com.gempukku.lotro.cards.set4.raider;

import com.gempukku.lotro.cards.AbstractOldEvent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.effects.choose.ChooseAndDiscardCardsFromPlayEffect;
import com.gempukku.lotro.cards.effects.choose.ChooseAndExertCharactersEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;

/**
 * Set: The Two Towers
 * Side: Shadow
 * Culture: Raider
 * Twilight Cost: 1
 * Type: Event
 * Game Text: Maneuver: Exert a [RAIDER] Man to discard a Free Peoples condition (or 2 Free Peoples conditions if
 * you spot 3 burdens).
 */
public class Card4_223 extends AbstractOldEvent {
    public Card4_223() {
        super(Side.SHADOW, Culture.RAIDER, "Discovered", Phase.MANEUVER);
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int withTwilightRemoved, int twilightModifier, boolean ignoreRoamingPenalty, boolean ignoreCheckingDeadPile) {
        return super.checkPlayRequirements(playerId, game, self, withTwilightRemoved, twilightModifier, ignoreRoamingPenalty, ignoreCheckingDeadPile)
                && PlayConditions.canExert(self, game, Culture.RAIDER, Race.MAN);
    }

    @Override
    public PlayEventAction getPlayCardAction(String playerId, LotroGame game, PhysicalCard self, int twilightModifier, boolean ignoreRoamingPenalty) {
        PlayEventAction action = new PlayEventAction(self);
        action.appendCost(
                new ChooseAndExertCharactersEffect(action, playerId, 1, 1, Culture.RAIDER, Race.MAN));
        int maxDiscard = (game.getGameState().getBurdens() >= 3) ? 2 : 1;
        action.appendEffect(
                new ChooseAndDiscardCardsFromPlayEffect(action, playerId, 1, maxDiscard, Side.FREE_PEOPLE, CardType.CONDITION));
        return action;
    }

    @Override
    public int getTwilightCost() {
        return 1;
    }
}
