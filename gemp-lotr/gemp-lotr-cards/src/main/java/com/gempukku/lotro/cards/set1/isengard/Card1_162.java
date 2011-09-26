package com.gempukku.lotro.cards.set1.isengard;

import com.gempukku.lotro.cards.AbstractPermanent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.actions.PlayPermanentAction;
import com.gempukku.lotro.cards.costs.ChooseAndExertCharactersCost;
import com.gempukku.lotro.cards.effects.AddBurdenEffect;
import com.gempukku.lotro.cards.effects.ChoiceEffect;
import com.gempukku.lotro.cards.effects.ExertCharacterEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.RequiredTriggerAction;
import com.gempukku.lotro.logic.timing.ChooseableEffect;
import com.gempukku.lotro.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Shadow
 * Culture: Isengard
 * Twilight Cost: 2
 * Type: Condition
 * Game Text: To play, exert an Uruk-hai. Plays to your support area. Each time a companion or ally loses a skirmish
 * involving an Uruk-hai, the opponent must choose to either exert the Ring-bearer or add a burden.
 */
public class Card1_162 extends AbstractPermanent {
    public Card1_162() {
        super(Side.SHADOW, 2, CardType.CONDITION, Culture.ISENGARD, Zone.SHADOW_SUPPORT, "Worry", true);
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int twilightModifier) {
        return super.checkPlayRequirements(playerId, game, self, twilightModifier)
                && Filters.canSpot(game.getGameState(), game.getModifiersQuerying(), Filters.race(Race.URUK_HAI), Filters.canExert());
    }

    @Override
    public PlayPermanentAction getPlayCardAction(String playerId, LotroGame game, PhysicalCard self, int twilightModifier) {
        final PlayPermanentAction action = super.getPlayCardAction(playerId, game, self, twilightModifier);
        action.appendCost(
                new ChooseAndExertCharactersCost(action, playerId, 1, 1, Filters.race(Race.URUK_HAI), Filters.canExert()));
        return action;
    }

    @Override
    public List<RequiredTriggerAction> getRequiredAfterTriggers(LotroGame game, EffectResult effectResult, PhysicalCard self) {
        GameState gameState = game.getGameState();
        if (PlayConditions.winsSkirmish(gameState, game.getModifiersQuerying(), effectResult, Filters.race(Race.URUK_HAI))) {
            RequiredTriggerAction action = new RequiredTriggerAction(self, null, "Each time a companion or ally loses a skirmish involving an Uruk-hai, the opponent must choose to either exert the Ring-bearer or add a burden.");
            List<ChooseableEffect> possibleEffects = new LinkedList<ChooseableEffect>();
            possibleEffects.add(new ExertCharacterEffect(gameState.getCurrentPlayerId(), gameState.getRingBearer(gameState.getCurrentPlayerId())));
            possibleEffects.add(new AddBurdenEffect(gameState.getCurrentPlayerId()));

            action.appendEffect(
                    new ChoiceEffect(action, game.getGameState().getCurrentPlayerId(), possibleEffects));
            return Collections.singletonList(action);
        }
        return null;
    }
}
