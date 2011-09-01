package com.gempukku.lotro.cards.set1.elven;

import com.gempukku.lotro.cards.AbstractLotroCardBlueprint;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.actions.PlayPermanentAction;
import com.gempukku.lotro.cards.effects.RemoveBurderEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.CostToEffectAction;
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
public class Card1_061 extends AbstractLotroCardBlueprint {
    public Card1_061() {
        super(Side.FREE_PEOPLE, CardType.CONDITION, Culture.ELVEN, "Songs of the Blessed Realm");
    }

    @Override
    public int getTwilightCost() {
        return 1;
    }

    @Override
    public List<? extends Action> getPhaseActions(String playerId, LotroGame game, PhysicalCard self) {
        if (PlayConditions.canPlayFPCardDuringPhase(game, Phase.FELLOWSHIP, self)) {
            PlayPermanentAction action = new PlayPermanentAction(self, Zone.FREE_SUPPORT);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    public List<? extends Action> getOptionalOneTimeActions(String playerId, LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (PlayConditions.played(game.getGameState(), game.getModifiersQuerying(), effectResult, Filters.and(Filters.culture(Culture.ELVEN), Filters.keyword(Keyword.TALE)))) {
            CostToEffectAction action = new CostToEffectAction(self, null, "Remove a burder");
            action.addEffect(new RemoveBurderEffect(playerId));
            return Collections.singletonList(action);
        }
        return null;
    }
}
