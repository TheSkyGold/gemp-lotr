package com.gempukku.lotro.cards.effects.choose;

import com.gempukku.lotro.cards.effects.PutCardFromHandOnTopOfDeckEffect;
import com.gempukku.lotro.common.Filterable;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.SubAction;
import com.gempukku.lotro.logic.effects.ChooseArbitraryCardsEffect;
import com.gempukku.lotro.logic.timing.AbstractSubActionEffect;
import com.gempukku.lotro.logic.timing.Action;

import java.util.Collection;

public class ChooseAndPutCardsFromHandOnTopOfDrawDeckEffect extends AbstractSubActionEffect {
    private Action _action;
    private String _playerId;
    private int _minimum;
    private int _maximum;
    private Filterable[] _filters;

    public ChooseAndPutCardsFromHandOnTopOfDrawDeckEffect(Action action, String playerId, int minimum, int maximum, Filterable... filters) {
        _action = action;
        _playerId = playerId;
        _minimum = minimum;
        _maximum = maximum;
        _filters = filters;
    }

    @Override
    public String getText(LotroGame game) {
        return null;
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public boolean isPlayableInFull(LotroGame game) {
        return Filters.filter(game.getGameState().getHand(_playerId), game.getGameState(), game.getModifiersQuerying(), _filters).size() >= _minimum;
    }

    @Override
    public void playEffect(LotroGame game) {
        final Collection<PhysicalCard> cards = Filters.filter(game.getGameState().getHand(_playerId), game.getGameState(), game.getModifiersQuerying(), _filters);
        SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new ChooseAndPutNextCardFromHandOnTopOfLibrary(subAction, Math.min(_minimum, cards.size()), Math.min(_maximum, cards.size()), cards));
        processSubAction(game, subAction);
    }

    private class ChooseAndPutNextCardFromHandOnTopOfLibrary extends ChooseArbitraryCardsEffect {
        private Collection<PhysicalCard> _remainingCards;
        private SubAction _subAction;
        private int _remainingMinCount;
        private int _remainingMaxCount;

        public ChooseAndPutNextCardFromHandOnTopOfLibrary(SubAction subAction, int remainingMinCount, int remainingMaxCount, Collection<PhysicalCard> remainingCards) {
            super(_playerId, "Choose a card to put on top of your deck", remainingCards, (remainingMinCount > 0) ? 1 : 0, 1);
            _subAction = subAction;
            _remainingMinCount = remainingMinCount;
            _remainingMaxCount = remainingMaxCount;
            _remainingCards = remainingCards;
        }

        @Override
        protected void cardsSelected(LotroGame game, Collection<PhysicalCard> selectedCards) {
            for (PhysicalCard selectedCard : selectedCards) {
                _subAction.appendEffect(
                        new PutCardFromHandOnTopOfDeckEffect(selectedCard));
                _remainingCards.remove(selectedCard);
                if (_remainingMaxCount - 1 > 0)
                    _subAction.appendEffect(
                            new ChooseAndPutNextCardFromHandOnTopOfLibrary(_subAction, _remainingMinCount-1, _remainingMaxCount - 1, _remainingCards));
            }
        }
    }
}
