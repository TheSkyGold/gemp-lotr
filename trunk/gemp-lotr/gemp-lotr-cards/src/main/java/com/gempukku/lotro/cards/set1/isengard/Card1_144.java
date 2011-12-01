package com.gempukku.lotro.cards.set1.isengard;

import com.gempukku.lotro.cards.AbstractPermanent;
import com.gempukku.lotro.cards.TriggerConditions;
import com.gempukku.lotro.cards.effects.RemoveTwilightEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.GameUtils;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.effects.HealCharactersEffect;
import com.gempukku.lotro.logic.timing.EffectResult;
import com.gempukku.lotro.logic.timing.results.CharacterWonSkirmishResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Shadow
 * Culture: Isengard
 * Twilight Cost: 3
 * Type: Condition
 * Game Text: Plays to your support area. Response: If your Uruk-hai wins a skirmish, remove (1) to heal him.
 */
public class Card1_144 extends AbstractPermanent {
    public Card1_144() {
        super(Side.SHADOW, 3, CardType.CONDITION, Culture.ISENGARD, Zone.SUPPORT, "Uruk Bloodlust");
    }

    @Override
    public List<? extends ActivateCardAction> getOptionalInPlayAfterActions(final String playerId, LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (TriggerConditions.winsSkirmish(game, effectResult, Filters.and(Race.URUK_HAI, Filters.owner(playerId)))
                && game.getGameState().getTwilightPool() >= 1) {
            CharacterWonSkirmishResult skirmishResult = ((CharacterWonSkirmishResult) effectResult);
            final ActivateCardAction action = new ActivateCardAction(self);
            action.setText("Heal " + GameUtils.getCardLink(skirmishResult.getWinner()));
            action.appendCost(new RemoveTwilightEffect(1));
            action.appendEffect(
                    new HealCharactersEffect(self, skirmishResult.getWinner()));
            return Collections.singletonList(action);
        }
        return null;
    }
}
