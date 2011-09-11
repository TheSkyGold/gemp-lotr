package com.gempukku.lotro.cards.set1.wraith;

import com.gempukku.lotro.cards.AbstractPermanent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.PlaySiteEffect;
import com.gempukku.lotro.cards.effects.RemoveTwilightEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.DefaultCostToEffectAction;
import com.gempukku.lotro.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Shadow
 * Culture: Wraith
 * Twilight Cost: 1
 * Type: Condition
 * Game Text: Search. To play, spot a Nazgul. Plays to your support area. Shadow: Remove (3) to replace the fellowship's
 * site with your version of the same site.
 */
public class Card1_222 extends AbstractPermanent {
    public Card1_222() {
        super(Side.SHADOW, 1, CardType.CONDITION, Culture.WRAITH, Zone.SHADOW_SUPPORT, "Paths Seldom Trodden");
        addKeyword(Keyword.SEARCH);
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int twilightModifier) {
        return super.checkPlayRequirements(playerId, game, self, twilightModifier)
                && Filters.canSpot(game.getGameState(), game.getModifiersQuerying(), Filters.keyword(Keyword.NAZGUL));
    }

    @Override
    public List<? extends Action> getExtraPhaseActions(String playerId, LotroGame game, PhysicalCard self) {
        if (PlayConditions.canUseShadowCardDuringPhase(game.getGameState(), Phase.SHADOW, self, 3)) {
            DefaultCostToEffectAction action = new DefaultCostToEffectAction(self, Keyword.SHADOW, "Remove (3) to replace the fellowship's site with your version of the same site.");
            action.addCost(new RemoveTwilightEffect(3));
            if (!game.getGameState().getCurrentSite().getOwner().equals(playerId))
                action.addEffect(
                        new PlaySiteEffect(playerId, game.getGameState().getCurrentSiteNumber()));
            return Collections.singletonList(action);
        }
        return null;
    }
}
