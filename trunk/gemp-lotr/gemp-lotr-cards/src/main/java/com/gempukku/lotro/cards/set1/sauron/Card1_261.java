package com.gempukku.lotro.cards.set1.sauron;

import com.gempukku.lotro.cards.AbstractMinion;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.TriggerConditions;
import com.gempukku.lotro.cards.effects.CancelEventEffect;
import com.gempukku.lotro.cards.effects.SelfExertEffect;
import com.gempukku.lotro.cards.modifiers.RoamingPenaltyModifier;
import com.gempukku.lotro.cards.results.PlayEventResult;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Shadow
 * Culture: Sauron
 * Twilight Cost: 1
 * Type: Minion • Orc
 * Strength: 5
 * Vitality: 2
 * Site: 6
 * Game Text: Tracker. The roaming penalty for each [SAURON] minion you play is -1. Response: If a Free Peoples regroup
 * event is played, exert this minion to cancel that event.
 */
public class Card1_261 extends AbstractMinion {
    public Card1_261() {
        super(1, 5, 2, 6, Race.ORC, Culture.SAURON, "Orc Ambusher");
        addKeyword(Keyword.TRACKER);
    }

    @Override
    public Modifier getAlwaysOnModifier(LotroGame game, PhysicalCard self) {
        return new RoamingPenaltyModifier(self, Filters.and(Filters.owner(self.getOwner()), Culture.SAURON, CardType.MINION), -1);
    }

    @Override
    public List<? extends ActivateCardAction> getOptionalInPlayAfterActions(String playerId, LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (TriggerConditions.played(game, effectResult, Side.FREE_PEOPLE, CardType.EVENT, Keyword.REGROUP)
                && PlayConditions.canExert(self, game, self)) {
            ActivateCardAction action = new ActivateCardAction(self);
            action.appendCost(
                    new SelfExertEffect(action, self));
            action.appendEffect(
                    new CancelEventEffect(self, (PlayEventResult) effectResult));
            return Collections.singletonList(action);
        }
        return null;
    }
}
