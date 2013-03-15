package com.gempukku.lotro.cards.set20.dwarven;

import com.gempukku.lotro.cards.AbstractEvent;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.effects.choose.ChooseAndAddUntilEOPStrengthBonusEffect;
import com.gempukku.lotro.cards.modifiers.evaluator.ConditionEvaluator;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.modifiers.Condition;
import com.gempukku.lotro.logic.modifiers.ModifiersQuerying;

/**
 * 0
 * Crushing Blow
 * Event • Skirmish
 * Make a Dwarf strength +2 (or strength +3 if you can spot a card stacked on a [Dwarven] condition).
 * http://lotrtcg.org/coreset/dwarven/crushingblow(r1).png
 */
public class Card20_043 extends AbstractEvent {
    public Card20_043() {
        super(Side.FREE_PEOPLE, 0, Culture.DWARVEN, "Crushing Blow", Phase.SKIRMISH);
    }

    @Override
    public PlayEventAction getPlayCardAction(String playerId, LotroGame game, PhysicalCard self, int twilightModifier, boolean ignoreRoamingPenalty) {
        PlayEventAction action = new PlayEventAction(self);
        action.appendEffect(
                new ChooseAndAddUntilEOPStrengthBonusEffect(action, self, playerId,
                        new ConditionEvaluator(2, 3,
                                new Condition() {
                                    @Override
                                    public boolean isFullfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                                        for (PhysicalCard physicalCard : Filters.filterActive(gameState, modifiersQuerying, Culture.DWARVEN, CardType.CONDITION)) {
                                            if (gameState.getStackedCards(physicalCard).size()>0)
                                                return true;
                                        }
                                        return false;
                                    }
                                }), Race.DWARF));
        return action;
    }
}
