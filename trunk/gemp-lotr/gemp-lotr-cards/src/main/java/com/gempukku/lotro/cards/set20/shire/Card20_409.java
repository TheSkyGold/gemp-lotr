package com.gempukku.lotro.cards.set20.shire;

import com.gempukku.lotro.cards.AbstractAttachableFPPossession;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.CheckPhaseLimitEffect;
import com.gempukku.lotro.cards.effects.ForEachYouSpotEffect;
import com.gempukku.lotro.cards.effects.RemoveBurdenEffect;
import com.gempukku.lotro.cards.effects.choose.ChooseAndDiscardCardsFromPlayEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * 1
 * •Sam's Pipe
 * Possession • Pipe
 * Bearer must be a Hobbit.
 * Fellowship: If bearer is Sam, you may discard a pipeweed possession and spot X pipes to remove X burdens (limit once per phase).
 * http://lotrtcg.org/coreset/shire/samspipe(r2).jpg
 */
public class Card20_409 extends AbstractAttachableFPPossession {
    public Card20_409() {
        super(1, 0, 0, Culture.SHIRE, PossessionClass.PIPE, "Sam's Pipe", null, true);
    }

    @Override
    protected Filterable getValidTargetFilter(String playerId, LotroGame game, PhysicalCard self) {
        return Race.HOBBIT;
    }

    @Override
    protected List<? extends Action> getExtraInPlayPhaseActions(final String playerId, LotroGame game, final PhysicalCard self) {
        if (PlayConditions.canUseFPCardDuringPhase(game, Phase.FELLOWSHIP, self)
                && PlayConditions.isActive(game, Filters.name("Sam"), Filters.hasAttached(self))
                && PlayConditions.canDiscardFromPlay(self, game, CardType.POSSESSION, Keyword.PIPEWEED)) {
            final ActivateCardAction action = new ActivateCardAction(self);
            action.appendCost(
                    new ChooseAndDiscardCardsFromPlayEffect(action, playerId, 1, 1, CardType.POSSESSION, Keyword.PIPEWEED));
            action.appendCost(
                    new ForEachYouSpotEffect(playerId, PossessionClass.PIPE) {
                        @Override
                        protected void spottedCards(int spotCount) {
                            action.appendEffect(
                                    new CheckPhaseLimitEffect(action, self, 1,
                                            new RemoveBurdenEffect(playerId, self, spotCount)));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
