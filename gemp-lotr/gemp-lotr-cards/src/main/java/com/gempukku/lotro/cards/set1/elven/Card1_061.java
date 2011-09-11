package com.gempukku.lotro.cards.set1.elven;

import com.gempukku.lotro.cards.AbstractPermanent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.RemoveBurdenEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.DefaultCostToEffectAction;
import com.gempukku.lotro.logic.timing.Action;
import com.gempukku.lotro.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Free
 * Culture: Elven
 * Twilight Cost: 1
 * Type: Condition
 * Game Text: Plays to your support area. Each time you play an [ELVEN] tale, you may remove a burden.
 */
public class Card1_061 extends AbstractPermanent {
    public Card1_061() {
        super(Side.FREE_PEOPLE, 1, CardType.CONDITION, Culture.ELVEN, Zone.FREE_SUPPORT, "Songs of the Blessed Realm");
    }

    @Override
    public List<? extends Action> getOptionalAfterTriggers(String playerId, LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (PlayConditions.played(game.getGameState(), game.getModifiersQuerying(), effectResult, Filters.and(Filters.culture(Culture.ELVEN), Filters.keyword(Keyword.TALE)))) {
            DefaultCostToEffectAction action = new DefaultCostToEffectAction(self, null, "Remove a burder");
            action.addEffect(new RemoveBurdenEffect());
            return Collections.singletonList(action);
        }
        return null;
    }
}
