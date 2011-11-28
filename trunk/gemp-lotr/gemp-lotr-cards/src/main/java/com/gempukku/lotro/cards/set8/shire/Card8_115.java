package com.gempukku.lotro.cards.set8.shire;

import com.gempukku.lotro.cards.AbstractResponseEvent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.TriggerConditions;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.effects.choose.ChooseAndExertCharactersEffect;
import com.gempukku.lotro.common.CardType;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Race;
import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.GameUtils;
import com.gempukku.lotro.logic.effects.ChooseAndWoundCharactersEffect;
import com.gempukku.lotro.logic.timing.EffectResult;
import com.gempukku.lotro.logic.timing.results.ExertResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Siege of Gondor
 * Side: Free
 * Culture: Shire
 * Twilight Cost: 0
 * Type: Event • Response
 * Game Text: If a minion exerts, exert an unbound Hobbit to wound that minion.
 */
public class Card8_115 extends AbstractResponseEvent {
    public Card8_115() {
        super(Side.FREE_PEOPLE, 0, Culture.SHIRE, "Unheeded");
    }

    @Override
    public List<PlayEventAction> getOptionalAfterActions(String playerId, LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (TriggerConditions.forEachExerted(game, effectResult, CardType.MINION)
                && PlayConditions.canExert(self, game, Filters.unboundCompanion, Race.HOBBIT)
                && checkPlayRequirements(playerId, game, self, 0, false, false)) {
            ExertResult exertResult = (ExertResult) effectResult;
            PlayEventAction action = new PlayEventAction(self);
            final PhysicalCard exertedCard = exertResult.getExertedCard();
            action.setText("Wound " + GameUtils.getCardLink(exertedCard));
            action.appendCost(
                    new ChooseAndExertCharactersEffect(action, playerId, 1, 1, Filters.unboundCompanion, Race.HOBBIT));
            action.appendEffect(
                    new ChooseAndWoundCharactersEffect(action, playerId, 1, 1, exertedCard));
            return Collections.singletonList(action);
        }
        return null;
    }
}
