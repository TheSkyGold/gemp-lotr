package com.gempukku.lotro.logic.effects;

import com.gempukku.lotro.common.Filterable;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.lotro.logic.decisions.DecisionResultInvalidException;
import com.gempukku.lotro.logic.timing.AbstractEffect;
import com.gempukku.lotro.logic.timing.Effect;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public abstract class ChooseArbitraryCardsEffect extends AbstractEffect {
    private String _playerId;
    private String _choiceText;
    private Collection<PhysicalCard> _cards;
    private Filterable _filter;
    private int _minimum;
    private int _maximum;

    public ChooseArbitraryCardsEffect(String playerId, String choiceText, Collection<? extends PhysicalCard> cards, int minimum, int maximum) {
        this(playerId, choiceText, cards, Filters.any, minimum, maximum);
    }

    public ChooseArbitraryCardsEffect(String playerId, String choiceText, Collection<? extends PhysicalCard> cards, Filterable filter, int minimum, int maximum) {
        _playerId = playerId;
        _choiceText = choiceText;
        _cards = new HashSet<PhysicalCard>(cards);
        _filter = filter;
        _minimum = minimum;
        _maximum = maximum;
    }

    @Override
    public boolean isPlayableInFull(LotroGame game) {
        return Filters.filter(_cards, game.getGameState(), game.getModifiersQuerying(), _filter).size() >= _minimum;
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
    protected FullEffectResult playEffectReturningResult(final LotroGame game) {
        Collection<PhysicalCard> possibleCards = Filters.filter(_cards, game.getGameState(), game.getModifiersQuerying(), _filter);

        boolean success = possibleCards.size() >= _minimum;

        int minimum = _minimum;

        if (possibleCards.size() < minimum)
            minimum = possibleCards.size();

        if (_maximum == 0) {
            cardsSelected(game, Collections.<PhysicalCard>emptySet());
        } else if (possibleCards.size() == minimum) {
            cardsSelected(game, possibleCards);
        } else {
            game.getUserFeedback().sendAwaitingDecision(_playerId,
                    new ArbitraryCardsSelectionDecision(1, _choiceText, _cards, possibleCards, _minimum, _maximum) {
                        @Override
                        public void decisionMade(String result) throws DecisionResultInvalidException {
                            cardsSelected(game, getSelectedCardsByResponse(result));
                        }
                    });
        }

        return new FullEffectResult(success);
    }

    protected abstract void cardsSelected(LotroGame game, Collection<PhysicalCard> selectedCards);
}
