package com.gempukku.lotro.cards.set1.isengard;

import com.gempukku.lotro.cards.AbstractOldEvent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.effects.AddBurdenEffect;
import com.gempukku.lotro.cards.effects.ChoiceEffect;
import com.gempukku.lotro.cards.effects.ExertCharactersEffect;
import com.gempukku.lotro.cards.effects.choose.ChooseAndExertCharactersEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.timing.Effect;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Shadow
 * Culture: Isengard
 * Twilight Cost: 2
 * Type: Event
 * Game Text: Spell. Weather. Maneuver: Exert a [ISENGARD] minion to make the opponent choose to either exert the
 * Ring-bearer or add a burden.
 */
public class Card1_124 extends AbstractOldEvent {
    public Card1_124() {
        super(Side.SHADOW, Culture.ISENGARD, "Cruel Caradhras", Phase.MANEUVER);
        addKeyword(Keyword.SPELL);
        addKeyword(Keyword.WEATHER);
    }

    @Override
    public int getTwilightCost() {
        return 2;
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int withTwilightRemoved, int twilightModifier, boolean ignoreRoamingPenalty, boolean ignoreCheckingDeadPile) {
        return super.checkPlayRequirements(playerId, game, self, withTwilightRemoved, twilightModifier, ignoreRoamingPenalty, ignoreCheckingDeadPile)
                && PlayConditions.canExert(self, game, Culture.ISENGARD, CardType.MINION);
    }

    @Override
    public PlayEventAction getPlayCardAction(String playerId, LotroGame game, PhysicalCard self, int twilightModifier, boolean ignoreRoamingPenalty) {
        final PlayEventAction action = new PlayEventAction(self);
        String fpPlayer = game.getGameState().getCurrentPlayerId();

        action.appendCost(
                new ChooseAndExertCharactersEffect(action, playerId, 1, 1, Culture.ISENGARD, CardType.MINION));

        List<Effect> possibleEffects = new LinkedList<Effect>();
        possibleEffects.add(new ExertCharactersEffect(action, self, game.getGameState().getRingBearer(fpPlayer)) {
            @Override
            public String getText(LotroGame game) {
                return "Exert Ring-bearer";
            }
        });
        possibleEffects.add(
                new AddBurdenEffect(fpPlayer, self, 1));

        action.appendEffect(
                new ChoiceEffect(action, fpPlayer, possibleEffects));
        return action;
    }
}
