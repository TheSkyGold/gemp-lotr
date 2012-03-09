package com.gempukku.lotro.cards.set4.site;

import com.gempukku.lotro.cards.AbstractSite;
import com.gempukku.lotro.cards.TriggerConditions;
import com.gempukku.lotro.common.Block;
import com.gempukku.lotro.common.Phase;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.RequiredTriggerAction;
import com.gempukku.lotro.logic.effects.AddTwilightEffect;
import com.gempukku.lotro.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Two Towers
 * Twilight Cost: 6
 * Type: Site
 * Site: 8T
 * Game Text: At the start of the first Shadow phase, if the twilight pool has fewer than 9 twilight tokens, add (4) (limit once per turn).
 */
public class Card4_359 extends AbstractSite {
    public Card4_359() {
        super("Wizard's Vale", Block.TWO_TOWERS, 8, 6, Direction.RIGHT);
    }

    @Override
    public List<RequiredTriggerAction> getRequiredAfterTriggers(LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (TriggerConditions.startOfPhase(game, effectResult, Phase.SHADOW)
                && self.getData() == null
                && game.getGameState().getTwilightPool() < 9) {
            RequiredTriggerAction action = new RequiredTriggerAction(self);
            action.appendEffect(
                    new AddTwilightEffect(self, 4));
            return Collections.singletonList(action);
        }
        if (TriggerConditions.endOfPhase(game, effectResult, Phase.SHADOW)) {
            self.storeData(new Object());
        }
        return null;
    }
}