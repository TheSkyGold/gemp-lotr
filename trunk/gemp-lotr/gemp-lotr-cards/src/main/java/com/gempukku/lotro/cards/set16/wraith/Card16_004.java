package com.gempukku.lotro.cards.set16.wraith;

import com.gempukku.lotro.cards.AbstractMinion;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.TriggerConditions;
import com.gempukku.lotro.cards.effects.RemoveTwilightEffect;
import com.gempukku.lotro.cards.effects.choose.ChooseAndDiscardCardsFromPlayEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.OptionalTriggerAction;
import com.gempukku.lotro.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Wraith Collection
 * Side: Shadow
 * Culture: Wraith
 * Twilight Cost: 2
 * Type: Minion • Wraith
 * Strength: 4
 * Vitality: 2
 * Site: 3
 * Game Text: Enduring. Each time the Free Peoples player assigns this minion to a companion, you may spot another
 * [WRAITH] Wraith and remove (2) to discard a condition.
 */
public class Card16_004 extends AbstractMinion {
    public Card16_004() {
        super(3, 8, 2, 3, Race.WRAITH, Culture.WRAITH, "Dead Faces");
        addKeyword(Keyword.ENDURING);
    }

    @Override
    public List<OptionalTriggerAction> getOptionalAfterTriggers(String playerId, LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (TriggerConditions.assignedAgainst(game, effectResult, Side.FREE_PEOPLE, CardType.COMPANION, self)
                && PlayConditions.canSpot(game, Filters.not(self), Culture.WRAITH, Race.WRAITH)
                && game.getGameState().getTwilightPool() >= 2) {
            OptionalTriggerAction action = new OptionalTriggerAction(self);
            action.appendCost(
                    new RemoveTwilightEffect(2));
            action.appendEffect(
                    new ChooseAndDiscardCardsFromPlayEffect(action, playerId, 1, 1, CardType.CONDITION));
            return Collections.singletonList(action);
        }
        return null;
    }
}
