package com.gempukku.lotro.cards.set1.gandalf;

import com.gempukku.lotro.cards.AbstractPermanent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.ChoiceEffect;
import com.gempukku.lotro.cards.effects.StackCardFromHandEffect;
import com.gempukku.lotro.cards.effects.choose.ChooseCardsFromHandEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.effects.ChooseArbitraryCardsEffect;
import com.gempukku.lotro.logic.timing.Action;
import com.gempukku.lotro.logic.timing.Effect;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Free
 * Culture: Gandalf
 * Twilight Cost: 1
 * Type: Possession
 * Game Text: Plays to your support area. Fellowship: Stack a Free Peoples artifact (or possession) from hand on this
 * card, or play a card stacked here as if played from hand.
 */
public class Card1_073 extends AbstractPermanent {
    public Card1_073() {
        super(Side.FREE_PEOPLE, 1, CardType.POSSESSION, Culture.GANDALF, Zone.SUPPORT, "Gandalf's Cart", null, true);
    }

    @Override
    public List<? extends Action> getExtraPhaseActions(final String playerId, final LotroGame game, final PhysicalCard self) {
        if (PlayConditions.canUseFPCardDuringPhase(game, Phase.FELLOWSHIP, self)
                && (
                Filters.filter(game.getGameState().getStackedCards(self), game.getGameState(), game.getModifiersQuerying(), Filters.playable(game)).size() > 0
                        || Filters.filter(game.getGameState().getHand(playerId), game.getGameState(), game.getModifiersQuerying(), Side.FREE_PEOPLE, Filters.or(CardType.ARTIFACT, CardType.POSSESSION)).size() > 0)) {
            final ActivateCardAction action = new ActivateCardAction(self);

            List<Effect> possibleChoices = new LinkedList<Effect>();
            possibleChoices.add(
                    new ChooseCardsFromHandEffect(playerId, 1, 1, Side.FREE_PEOPLE, Filters.or(CardType.ARTIFACT, CardType.POSSESSION)) {
                        @Override
                        public String getText(LotroGame game) {
                            return "Stack a card from hand on this";
                        }

                        @Override
                        protected void cardsSelected(LotroGame game, Collection<PhysicalCard> selectedCards) {
                            for (PhysicalCard card : selectedCards)
                                action.appendEffect(new StackCardFromHandEffect(card, self));
                        }
                    });
            possibleChoices.add(
                    new ChooseArbitraryCardsEffect(playerId, "Choose artifact or possession to play", game.getGameState().getStackedCards(self), Filters.playable(game), 1, 1) {
                        @Override
                        public String getText(LotroGame game) {
                            return "Play stacked artifact or possession";
                        }

                        @Override
                        protected void cardsSelected(LotroGame game, Collection<PhysicalCard> selectedCards) {
                            PhysicalCard selectedCard = selectedCards.iterator().next();
                            game.getActionsEnvironment().addActionToStack(selectedCard.getBlueprint().getPlayCardAction(playerId, game, selectedCard, 0, false));
                        }
                    });

            action.appendEffect(
                    new ChoiceEffect(action, playerId, possibleChoices));

            return Collections.singletonList(action);
        }
        return null;
    }
}
