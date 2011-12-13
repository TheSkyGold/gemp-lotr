package com.gempukku.lotro.cards.set12.rohan;

import com.gempukku.lotro.cards.AbstractCompanion;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.SelfExertEffect;
import com.gempukku.lotro.cards.effects.choose.ChooseAndPlayCardFromDeckEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;

import java.util.Collections;
import java.util.List;

/**
 * Set: Black Rider
 * Side: Free
 * Culture: Rohan
 * Twilight Cost: 2
 * Type: Companion • Man
 * Strength: 6
 * Vitality: 3
 * Resistance: 6
 * Game Text: Skirmish: If Leofric is in a skirmish, exert him to play a [ROHAN] skirmish event from your draw deck.
 */
public class Card12_117 extends AbstractCompanion {
    public Card12_117() {
        super(2, 6, 3, 6, Culture.ROHAN, Race.MAN, null, "Leofric", true);
    }

    @Override
    protected List<ActivateCardAction> getExtraInPlayPhaseActions(String playerId, LotroGame game, PhysicalCard self) {
        if (PlayConditions.canUseFPCardDuringPhase(game, Phase.SKIRMISH, self)
                && PlayConditions.canSelfExert(self, game)
                && Filters.inSkirmish.accepts(game.getGameState(), game.getModifiersQuerying(), self)) {
            ActivateCardAction action = new ActivateCardAction(self);
            action.appendCost(
                    new SelfExertEffect(self));
            action.appendEffect(
                    new ChooseAndPlayCardFromDeckEffect(playerId, Culture.ROHAN, CardType.EVENT, Keyword.SKIRMISH));
            return Collections.singletonList(action);
        }
        return null;
    }
}
