package com.gempukku.lotro.cards.set1.isengard;

import com.gempukku.lotro.cards.AbstractMinion;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.AddUntilStartOfPhaseModifierEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.modifiers.KeywordModifier;
import com.gempukku.lotro.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Shadow
 * Culture: Isengard
 * Twilight Cost: 3
 * Type: Minion � Uruk-Hai
 * Strength: 8
 * Vitality: 2
 * Site: 5
 * Game Text: Damage +1. Maneuver: Spot 5 companions to make this minion fierce until the regroup phase.
 */
public class Card1_146 extends AbstractMinion {
    public Card1_146() {
        super(3, 8, 2, 5, Race.URUK_HAI, Culture.ISENGARD, "Uruk Fighter");
        addKeyword(Keyword.DAMAGE, 1);
    }

    @Override
    protected List<? extends Action> getExtraPhaseActions(String playerId, LotroGame game, PhysicalCard self) {
        if (PlayConditions.canUseShadowCardDuringPhase(game, Phase.MANEUVER, self, 0)
                && PlayConditions.canSpot(game, 5, CardType.COMPANION)) {

            ActivateCardAction action = new ActivateCardAction(self);
            action.appendEffect(new AddUntilStartOfPhaseModifierEffect(new KeywordModifier(self, self, Keyword.FIERCE), Phase.REGROUP));

            return Collections.singletonList(action);
        }
        return null;
    }
}
