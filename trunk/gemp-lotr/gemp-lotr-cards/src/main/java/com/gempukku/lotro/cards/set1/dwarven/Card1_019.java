package com.gempukku.lotro.cards.set1.dwarven;

import com.gempukku.lotro.cards.AbstractLotroCardBlueprint;
import com.gempukku.lotro.cards.actions.PlayEventFromHandAction;
import com.gempukku.lotro.cards.effects.ExertCharacterEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.effects.ChooseActiveCardEffect;
import com.gempukku.lotro.logic.effects.ChooseActiveCardsEffect;
import com.gempukku.lotro.logic.effects.WoundEffect;
import com.gempukku.lotro.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Free
 * Culture: Dwarven
 * Twilight Cost: 0
 * Type: Event
 * Game Text: Maneuver: Exert a Dwarf to wound 2 Orcs or to wound 1 Orc twice.
 */
public class Card1_019 extends AbstractLotroCardBlueprint {
    public Card1_019() {
        super(Side.FREE_PEOPLE, CardType.EVENT, Culture.DWARVEN, "Here Lies Balin, Son of Fundin", "1_19");
        addKeyword(Keyword.MANEUVER);
    }

    @Override
    public int getTwilightCost() {
        return 0;
    }

    @Override
    public List<? extends Action> getPlayablePhaseActions(String playerId, LotroGame game, PhysicalCard self) {
        if (game.getGameState().getCurrentPhase() == Phase.MANEUVER
                && Filters.canSpot(game.getGameState(), game.getModifiersQuerying(), Filters.keyword(Keyword.DWARF), Filters.canExert())) {
            final PlayEventFromHandAction action = new PlayEventFromHandAction(self);
            action.addCost(
                    new ChooseActiveCardEffect(playerId, "Choose Dwarf to exert", Filters.canExert(), Filters.keyword(Keyword.DWARF)) {
                        @Override
                        protected void cardSelected(LotroGame game, PhysicalCard dwarf) {
                            action.addCost(new ExertCharacterEffect(dwarf));
                        }
                    }
            );
            action.addEffect(
                    new ChooseActiveCardsEffect(playerId, "Choose Orc(s) to wound", 2, Filters.keyword(Keyword.ORC)) {
                        @Override
                        protected void cardsSelected(List<PhysicalCard> cards) {
                            if (cards.size() == 2) {
                                action.addEffect(new WoundEffect(cards.get(0)));
                                action.addEffect(new WoundEffect(cards.get(1)));
                            } else {
                                action.addEffect(new WoundEffect(cards.get(0)));
                                action.addEffect(new WoundEffect(cards.get(0)));
                            }
                        }
                    }
            );

            return Collections.<Action>singletonList(action);
        }

        return null;
    }
}
