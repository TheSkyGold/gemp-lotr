package com.gempukku.lotro.cards.set3.isengard;

import com.gempukku.lotro.cards.AbstractMinion;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.ExhaustCharacterEffect;
import com.gempukku.lotro.cards.effects.SelfExertEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.effects.ChooseActiveCardEffect;
import com.gempukku.lotro.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Realms of Elf-lords
 * Side: Shadow
 * Culture: Isengard
 * Twilight Cost: 5
 * Type: Minion • Uruk-Hai
 * Strength: 11
 * Vitality: 3
 * Site: 5
 * Game Text: Damage +1. Maneuver: Spot 5 burdens and exert Orthanc Berserker twice to exhaust a companion (except
 * the Ring-bearer).
 */
public class Card3_066 extends AbstractMinion {
    public Card3_066() {
        super(5, 11, 3, 5, Race.URUK_HAI, Culture.ISENGARD, "Orthanc Berserker", true);
        addKeyword(Keyword.DAMAGE);
    }

    @Override
    protected List<? extends Action> getExtraPhaseActions(final String playerId, LotroGame game, final PhysicalCard self) {
        if (PlayConditions.canUseShadowCardDuringPhase(game, Phase.MANEUVER, self, 0)
                && PlayConditions.canExert(self, game, 2, self)
                && game.getGameState().getBurdens() >= 5) {
            final ActivateCardAction action = new ActivateCardAction(self);
            action.appendCost(
                    new SelfExertEffect(self));
            action.appendCost(
                    new SelfExertEffect(self));
            action.appendEffect(
                    new ChooseActiveCardEffect(self, playerId, "Choose non Ring-bearer companion", CardType.COMPANION, Filters.not(Filters.ringBearer)) {
                        @Override
                        protected void cardSelected(LotroGame game, PhysicalCard card) {
                            action.insertEffect(
                                    new ExhaustCharacterEffect(self, action, card));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
