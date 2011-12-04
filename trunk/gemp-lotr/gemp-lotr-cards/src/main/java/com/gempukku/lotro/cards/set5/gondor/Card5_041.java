package com.gempukku.lotro.cards.set5.gondor;

import com.gempukku.lotro.cards.AbstractAttachable;
import com.gempukku.lotro.cards.AbstractEvent;
import com.gempukku.lotro.cards.ExtraFilters;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.effects.choose.ChooseAndExertCharactersEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.SubAction;
import com.gempukku.lotro.logic.effects.ChooseArbitraryCardsEffect;
import com.gempukku.lotro.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.lotro.logic.timing.Effect;

import java.util.Collection;

/**
 * Set: Battle of Helm's Deep
 * Side: Free
 * Culture: Gondor
 * Twilight Cost: 3
 * Type: Event
 * Game Text: Fellowship: Exert Aragorn twice to play any number of possessions from your discard pile onto companions
 * with the Aragorn signet.
 */
public class Card5_041 extends AbstractEvent {
    public Card5_041() {
        super(Side.FREE_PEOPLE, 3, Culture.GONDOR, "These Are My People", Phase.FELLOWSHIP);
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int withTwilightRemoved, int twilightModifier, boolean ignoreRoamingPenalty, boolean ignoreCheckingDeadPile) {
        return super.checkPlayRequirements(playerId, game, self, withTwilightRemoved, twilightModifier, ignoreRoamingPenalty, ignoreCheckingDeadPile)
                && PlayConditions.canExert(self, game, 2, Filters.aragorn);
    }

    @Override
    public PlayEventAction getPlayCardAction(String playerId, LotroGame game, PhysicalCard self, int twilightModifier, boolean ignoreRoamingPenalty) {
        PlayEventAction action = new PlayEventAction(self);
        action.appendCost(
                new ChooseAndExertCharactersEffect(action, playerId, 1, 1, 2, Filters.aragorn));

        final SubAction subAction = new SubAction(action);
        subAction.appendEffect(
                new PlayAnyNumberOfPossessionsOnCharacters(subAction, playerId));
        action.appendEffect(
                new AbstractSuccessfulEffect() {
                    @Override
                    public String getText(LotroGame game) {
                        return null;
                    }

                    @Override
                    public Effect.Type getType() {
                        return null;
                    }

                    @Override
                    public void playEffect(LotroGame game) {
                        game.getActionsEnvironment().addActionToStack(subAction);
                    }
                }
        );

        return action;
    }

    private class PlayAnyNumberOfPossessionsOnCharacters extends AbstractSuccessfulEffect {
        private SubAction _subAction;
        private String _playerId;

        private PlayAnyNumberOfPossessionsOnCharacters(SubAction subAction, String playerId) {
            _subAction = subAction;
            _playerId = playerId;
        }

        @Override
        public String getText(LotroGame game) {
            return null;
        }

        @Override
        public Effect.Type getType() {
            return null;
        }

        @Override
        public void playEffect(final LotroGame game) {
            final Filter additionalAttachmentFilter = Filters.and(CardType.COMPANION, Signet.ARAGORN);

            _subAction.appendEffect(
                    new ChooseArbitraryCardsEffect(_playerId, "Choose card to play", game.getGameState().getDiscard(_playerId),
                            Filters.and(
                                    CardType.POSSESSION,
                                    ExtraFilters.attachableTo(game, additionalAttachmentFilter)), 1, 1) {
                        @Override
                        protected void cardsSelected(LotroGame game, Collection<PhysicalCard> selectedCards) {
                            if (selectedCards.size() > 0) {
                                PhysicalCard selectedCard = selectedCards.iterator().next();
                                game.getActionsEnvironment().addActionToStack(((AbstractAttachable) selectedCard.getBlueprint()).getPlayCardAction(_playerId, game, selectedCard, additionalAttachmentFilter, 0));
                                _subAction.appendEffect(new PlayAnyNumberOfPossessionsOnCharacters(_subAction, _playerId));
                            }
                        }
                    });
        }
    }
}
