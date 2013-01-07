package com.gempukku.lotro.cards.effects;

import com.gempukku.lotro.common.Filterable;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.GameUtils;
import com.gempukku.lotro.logic.effects.AbstractPreventableCardEffect;
import com.gempukku.lotro.logic.modifiers.ModifiersQuerying;
import com.gempukku.lotro.logic.timing.Action;
import com.gempukku.lotro.logic.timing.Effect;
import com.gempukku.lotro.logic.timing.results.ExertResult;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ExertCharactersEffect extends AbstractPreventableCardEffect {
    private Action _action;
    private PhysicalCard _source;
    private Set<PhysicalCard> _placeNoWound = new HashSet<PhysicalCard>();

    public ExertCharactersEffect(Action action, PhysicalCard source, Filterable... filter) {
        super(filter);
        _action = action;
        _source = source;
    }

    public ExertCharactersEffect(Action action, PhysicalCard source, PhysicalCard... cards) {
        super(cards);
        _action = action;
        _source = source;
    }

    @Override
    protected Filter getExtraAffectableFilter() {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.canBeExerted(gameState, _source, physicalCard)
                        && modifiersQuerying.getVitality(gameState, physicalCard) > 1;
            }
        };
    }

    @Override
    public Effect.Type getType() {
        return Type.BEFORE_EXERT;
    }

    @Override
    public String getText(LotroGame game) {
        Collection<PhysicalCard> cards = getAffectedCardsMinusPrevented(game);
        return "Exert - " + getAppendedTextNames(cards);
    }

    @Override
    protected void playoutEffectOn(LotroGame game, Collection<PhysicalCard> cards) {
        if (cards.size() > 0)
            game.getGameState().sendMessage(getAppendedNames(cards) + " exert" + GameUtils.s(cards) + " due to " + GameUtils.getCardLink(_source));

        for (PhysicalCard woundedCard : cards) {
            if (!_placeNoWound.contains(woundedCard))
                game.getGameState().addWound(woundedCard);
            game.getActionsEnvironment().emitEffectResult(new ExertResult(_action, woundedCard));
            forEachExertedByEffect(woundedCard);
        }
    }

    protected void forEachExertedByEffect(PhysicalCard physicalCard) {

    }

    public void placeNoWoundOn(PhysicalCard physicalCard) {
        _placeNoWound.add(physicalCard);
    }
}
