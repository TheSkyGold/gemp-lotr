package com.gempukku.lotro.cards.set20.shire;

import com.gempukku.lotro.cards.AbstractAttachableFPPossession;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.decisions.ForEachYouSpotDecision;
import com.gempukku.lotro.cards.effects.ShuffleCardsFromDiscardIntoDeckEffect;
import com.gempukku.lotro.cards.effects.choose.ChooseAndDiscardCardsFromPlayEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.decisions.DecisionResultInvalidException;
import com.gempukku.lotro.logic.effects.ChooseArbitraryCardsEffect;
import com.gempukku.lotro.logic.effects.PlayoutDecisionEffect;
import com.gempukku.lotro.logic.timing.Action;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 1
 * •Bilbo's Pipe
 * Shire	Possession • Pipe
 * Bearer must be a Hobbit.
 * Fellowship: Discard a pipeweed and spot X pipes to shuffle X tales from your discard pile into your draw deck.
 */
public class Card20_383 extends AbstractAttachableFPPossession {
    public Card20_383() {
        super(1, 0, 0, Culture.SHIRE, PossessionClass.PIPE, "Bilbo's Pipe", null, true);
    }

    @Override
    protected Filterable getValidTargetFilter(String playerId, LotroGame game, PhysicalCard self) {
        return Race.HOBBIT;
    }

    @Override
    protected List<? extends Action> getExtraInPlayPhaseActions(final String playerId, final LotroGame game, final PhysicalCard self) {
        if (PlayConditions.canUseFPCardDuringPhase(game, Phase.FELLOWSHIP, self)
                && PlayConditions.canDiscardFromPlay(self, game, Keyword.PIPEWEED)) {
            final ActivateCardAction action = new ActivateCardAction(self);
            action.appendCost(
                    new ChooseAndDiscardCardsFromPlayEffect(action, playerId, 1, 1, Keyword.PIPEWEED, CardType.POSSESSION));
            action.appendEffect(
                    new PlayoutDecisionEffect(playerId,
                            new ForEachYouSpotDecision(1, "Choose number of pipes you wish to spot", game, PossessionClass.PIPE, Integer.MAX_VALUE) {
                                @Override
                                public void decisionMade(String result) throws DecisionResultInvalidException {
                                    int spotCount = getValidatedResult(result);
                                    Collection<PhysicalCard> talesInDiscard = Filters.filter(game.getGameState().getDiscard(playerId), game.getGameState(), game.getModifiersQuerying(), Keyword.TALE);

                                    int shufflableTales = Math.min(spotCount, talesInDiscard.size());
                                    action.appendEffect(
                                            new ChooseArbitraryCardsEffect(playerId, "Choose tales", new LinkedList<PhysicalCard>(talesInDiscard), shufflableTales, shufflableTales) {
                                                @Override
                                                protected void cardsSelected(LotroGame game, Collection<PhysicalCard> selectedCards) {
                                                    action.insertEffect(
                                                            new ShuffleCardsFromDiscardIntoDeckEffect(self, playerId, selectedCards));
                                                }
                                            });
                                }
                            }));
            return Collections.singletonList(action);
        }
        return null;
    }
}
