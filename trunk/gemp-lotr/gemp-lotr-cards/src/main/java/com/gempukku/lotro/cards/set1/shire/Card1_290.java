package com.gempukku.lotro.cards.set1.shire;

import com.gempukku.lotro.cards.AbstractCompanion;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.choose.ChooseAndExertCharactersEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.effects.HealCharactersEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Free
 * Culture: Shire
 * Twilight Cost: 0
 * Type: Companion � Hobbit
 * Strength: 3
 * Vitality: 4
 * Resistance: 10
 * Signet: Frodo
 * Game Text: Ring-bearer (resistance 10). Fellowship: Exert another companion who has the Frodo signet to heal Frodo.
 */
public class Card1_290 extends AbstractCompanion {
    public Card1_290() {
        super(0, 3, 4, 10, Culture.SHIRE, Race.HOBBIT, Signet.FRODO, "Frodo", "Son of Drogo", true);
        addKeyword(Keyword.CAN_START_WITH_RING);
    }

    @Override
    protected List<ActivateCardAction> getExtraInPlayPhaseActions(String playerId, LotroGame game, PhysicalCard self) {
        if (PlayConditions.canUseFPCardDuringPhase(game, Phase.FELLOWSHIP, self)
                && PlayConditions.canExert(self, game, CardType.COMPANION, Signet.FRODO, Filters.not(self))) {
            final ActivateCardAction action = new ActivateCardAction(self);
            action.appendCost(
                    new ChooseAndExertCharactersEffect(action, playerId, 1, 1, CardType.COMPANION, Signet.FRODO, Filters.not(self)));
            action.appendEffect(
                    new HealCharactersEffect(self, self));

            return Collections.singletonList(action);
        }
        return null;
    }
}
