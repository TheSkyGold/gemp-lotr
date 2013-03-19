package com.gempukku.lotro.cards.set20.isengard;

import com.gempukku.lotro.cards.AbstractResponseEvent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.TriggerConditions;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.effects.choose.ChooseAndExertCharactersEffect;
import com.gempukku.lotro.common.CardType;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.GameUtils;
import com.gempukku.lotro.logic.effects.WoundCharactersEffect;
import com.gempukku.lotro.logic.timing.EffectResult;
import com.gempukku.lotro.logic.timing.results.WoundResult;

import java.util.Collections;
import java.util.List;

/**
 * 1
 * Treachery Runs Deep
 * Event • Response
 * Spell.
 * If a companion takes a wound, exert Saruman to wound that companion again.
 * http://lotrtcg.org/coreset/isengard/treacheryrunsdeep(r1).png
 */
public class Card20_244 extends AbstractResponseEvent {
    public Card20_244() {
        super(Side.SHADOW, 1, Culture.ISENGARD, "Treachery Runs Deep");
    }

    @Override
    public List<PlayEventAction> getOptionalAfterActions(String playerId, LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (TriggerConditions.forEachWounded(game, effectResult, CardType.COMPANION)
                && PlayConditions.canExert(self, game, Filters.saruman)
                && checkPlayRequirements(playerId, game, self, 0, 0, false, false)) {
            PlayEventAction action = new PlayEventAction(self);
            action.appendCost(
                    new ChooseAndExertCharactersEffect(action, playerId, 1, 1, Filters.saruman));
            PhysicalCard woundedCompanion = ((WoundResult) effectResult).getWoundedCard();
            action.setText("Wound " + GameUtils.getFullName(woundedCompanion));
            action.appendEffect(
                    new WoundCharactersEffect(self, woundedCompanion));
            return Collections.singletonList(action);
        }
        return null;
    }
}
