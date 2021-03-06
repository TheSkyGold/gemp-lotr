package com.gempukku.lotro.cards.set4.dunland;

import com.gempukku.lotro.cards.AbstractMinion;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.choose.ChooseAndAssignCharacterToMinionEffect;
import com.gempukku.lotro.common.CardType;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Phase;
import com.gempukku.lotro.common.Race;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Two Towers
 * Side: Shadow
 * Culture: Dunland
 * Twilight Cost: 3
 * Type: Minion • Man
 * Strength: 9
 * Vitality: 1
 * Site: 3
 * Game Text: Assignment: Spot an ally to make that ally participate in skirmishes and assign this minion to skirmish
 * that ally.
 */
public class Card4_018 extends AbstractMinion {
    public Card4_018() {
        super(3, 9, 1, 3, Race.MAN, Culture.DUNLAND, "Dunlending Warrior");
    }

    @Override
    protected List<? extends Action> getExtraPhaseActions(final String playerId, LotroGame game, final PhysicalCard self) {
        if (PlayConditions.canUseShadowCardDuringPhase(game, Phase.ASSIGNMENT, self, 0)
                && PlayConditions.canSpot(game, CardType.ALLY)) {
            final ActivateCardAction action = new ActivateCardAction(self);
            action.appendEffect(
                    new ChooseAndAssignCharacterToMinionEffect(action, playerId, self, true, CardType.ALLY));
            return Collections.singletonList(action);
        }
        return null;
    }
}
