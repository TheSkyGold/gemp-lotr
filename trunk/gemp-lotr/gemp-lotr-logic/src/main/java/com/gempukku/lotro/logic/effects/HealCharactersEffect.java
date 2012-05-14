package com.gempukku.lotro.logic.effects;

import com.gempukku.lotro.common.Filterable;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.GameUtils;
import com.gempukku.lotro.logic.modifiers.ModifiersQuerying;
import com.gempukku.lotro.logic.timing.Effect;
import com.gempukku.lotro.logic.timing.results.HealResult;

import java.util.Collection;

public class HealCharactersEffect extends AbstractPreventableCardEffect {
    private PhysicalCard _source;

    public HealCharactersEffect(PhysicalCard source, PhysicalCard... cards) {
        super(cards);
        _source = source;
    }

    public HealCharactersEffect(PhysicalCard source, Filterable... filter) {
        super(filter);
        _source = source;
    }

    @Override
    protected Filter getExtraAffectableFilter() {
        return Filters.and(
                Filters.wounded,
                new Filter() {
                    @Override
                    public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                        return modifiersQuerying.canBeHealed(gameState, physicalCard);
                    }
                });
    }

    @Override
    public Effect.Type getType() {
        return Type.BEFORE_HEALED;
    }

    @Override
    public String getText(LotroGame game) {
        Collection<PhysicalCard> cards = getAffectedCardsMinusPrevented(game);
        return "Heal - " + getAppendedTextNames(cards);
    }

    @Override
    protected void playoutEffectOn(LotroGame game, Collection<PhysicalCard> cards) {
        Collection<PhysicalCard> cardsToHeal = getAffectedCardsMinusPrevented(game);

        if (cardsToHeal.size() > 0) {
            game.getGameState().sendMessage(GameUtils.getCardLink(_source) + " heals " + getAppendedNames(cardsToHeal));
            for (PhysicalCard cardToHeal : cardsToHeal) {
                game.getGameState().removeWound(cardToHeal);
                game.getActionsEnvironment().emitEffectResult(new HealResult(cardToHeal));
            }
        }
    }
}
