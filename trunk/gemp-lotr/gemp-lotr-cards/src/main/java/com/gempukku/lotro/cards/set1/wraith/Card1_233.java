package com.gempukku.lotro.cards.set1.wraith;

import com.gempukku.lotro.cards.AbstractMinion;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.SelfExertEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.effects.ChooseActiveCardEffect;
import com.gempukku.lotro.logic.effects.PlaySiteEffect;
import com.gempukku.lotro.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Shadow
 * Culture: Wraith
 * Twilight Cost: 5
 * Type: Minion • Nazgul
 * Strength: 10
 * Vitality: 3
 * Site: 2
 * Game Text: Fierce. Shadow: Exert Úlairë Nelya and spot an opponent's site to replace it with your site of the same
 * number.
 */
public class Card1_233 extends AbstractMinion {
    public Card1_233() {
        super(5, 10, 3, 2, Race.NAZGUL, Culture.WRAITH, Names.nelya, "Lieutenant of Morgul", true);
        addKeyword(Keyword.FIERCE);
    }

    @Override
    protected List<? extends Action> getExtraPhaseActions(final String playerId, LotroGame game, PhysicalCard self) {
        if (PlayConditions.canUseShadowCardDuringPhase(game, Phase.SHADOW, self, 0)
                && PlayConditions.canExert(self, game, self)
                && Filters.canSpot(game.getGameState(), game.getModifiersQuerying(), CardType.SITE, Filters.not(Filters.owner(playerId)))) {
            final ActivateCardAction action = new ActivateCardAction(self);
            action.appendCost(
                    new SelfExertEffect(self));
            action.appendEffect(
                    new ChooseActiveCardEffect(self, playerId, "Choose opponent's site", CardType.SITE, Filters.not(Filters.owner(playerId))) {
                        @Override
                        protected void cardSelected(LotroGame game, PhysicalCard site) {
                            action.appendEffect(new PlaySiteEffect(action, playerId, Block.FELLOWSHIP, site.getSiteNumber()));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
