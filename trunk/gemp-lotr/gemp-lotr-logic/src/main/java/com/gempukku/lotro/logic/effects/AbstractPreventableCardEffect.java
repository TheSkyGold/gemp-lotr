package com.gempukku.lotro.logic.effects;

import com.gempukku.lotro.common.Filterable;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.timing.AbstractEffect;

import java.util.*;

public abstract class AbstractPreventableCardEffect extends AbstractEffect {
    private Filter _filter;
    private Set<PhysicalCard> _preventedTargets = new HashSet<PhysicalCard>();
    private int _requiredTargets;

    public AbstractPreventableCardEffect(PhysicalCard... cards) {
        List<PhysicalCard> affectedCards = Arrays.asList(cards);
        _requiredTargets = affectedCards.size();
        _filter = Filters.in(affectedCards);
    }

    public AbstractPreventableCardEffect(Filterable... filter) {
        _filter = Filters.and(filter);
    }

    protected abstract Filter getExtraAffectableFilter();

    protected final Collection<PhysicalCard> getAffectedCards(LotroGame game) {
        return Filters.filterActive(game.getGameState(), game.getModifiersQuerying(), _filter, getExtraAffectableFilter());
    }

    public final Collection<PhysicalCard> getAffectedCardsMinusPrevented(LotroGame game) {
        Collection<PhysicalCard> affectedCards = getAffectedCards(game);
        affectedCards.removeAll(_preventedTargets);
        return affectedCards;
    }

    @Override
    public boolean isPlayableInFull(LotroGame game) {
        return getAffectedCardsMinusPrevented(game).size() >= _requiredTargets;
    }

    protected abstract void playoutEffectOn(LotroGame game, Collection<PhysicalCard> cards);

    @Override
    protected FullEffectResult playEffectReturningResult(LotroGame game) {
        Collection<PhysicalCard> affectedMinusPreventedCards = getAffectedCardsMinusPrevented(game);
        playoutEffectOn(game, affectedMinusPreventedCards);
        return new FullEffectResult(affectedMinusPreventedCards.size() >= _requiredTargets);
    }

    public void preventEffect(LotroGame game, PhysicalCard card) {
        _preventedTargets.add(card);
        _prevented = true;
    }
}
