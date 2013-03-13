package com.gempukku.lotro.cards.set20.moria;

import com.gempukku.lotro.cards.AbstractMinion;
import com.gempukku.lotro.cards.modifiers.evaluator.CountStackedEvaluator;
import com.gempukku.lotro.common.CardType;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Race;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.logic.modifiers.ModifiersQuerying;

/**
 * 5
 * Goblin Patroller
 * Moria	Minion • Goblin
 * 11	3	4
 * This minion's twilight cost is -1 for each card stacked on a [Moria] condition.
 */
public class Card20_263 extends AbstractMinion {
    public Card20_263() {
        super(5, 11, 3, 4, Race.GOBLIN, Culture.MORIA, "Goblin Patrol");
    }

    @Override
    public int getTwilightCostModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return -(new CountStackedEvaluator(Filters.and(Culture.MORIA, CardType.CONDITION), Race.GOBLIN).evaluateExpression(gameState, modifiersQuerying, self));
    }
}
