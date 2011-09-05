package com.gempukku.lotro.cards.set1.elven;

import com.gempukku.lotro.cards.AbstractEvent;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.effects.ExertCharacterEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.effects.ChooseActiveCardEffect;
import com.gempukku.lotro.logic.effects.DiscardCardFromPlayEffect;
import com.gempukku.lotro.logic.timing.Action;

/**
 * Set: The Fellowship of the Ring
 * Side: Free
 * Culture: Elven
 * Twilight Cost: 0
 * Type: Event
 * Game Text: Fellowship: Exert 2 Elves to discard a condition.
 */
public class Card1_058 extends AbstractEvent {
    public Card1_058() {
        super(Side.FREE_PEOPLE, CardType.EVENT, Culture.ELVEN, "The Seen and the Unseen", Phase.FELLOWSHIP);
    }

    @Override
    public int getTwilightCost() {
        return 0;
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self) {
        return Filters.countActive(game.getGameState(), game.getModifiersQuerying(), Filters.keyword(Keyword.ELF), Filters.canExert()) >= 2;
    }

    @Override
    public Action getPlayCardAction(final String playerId, LotroGame game, PhysicalCard self, int twilightModifier) {
        final PlayEventAction action = new PlayEventAction(self);
        action.addCost(
                new ChooseActiveCardEffect(playerId, "Exert first Elf", Filters.keyword(Keyword.ELF)) {
                    @Override
                    protected void cardSelected(final PhysicalCard firstElf) {
                        action.addCost(new ExertCharacterEffect(firstElf));
                        action.addCost(
                                new ChooseActiveCardEffect(playerId, "Exert second Elf", Filters.keyword(Keyword.ELF), Filters.not(Filters.sameCard(firstElf))) {
                                    @Override
                                    protected void cardSelected(PhysicalCard secondElf) {
                                        action.addCost(new ExertCharacterEffect(secondElf));
                                    }
                                });
                    }
                });
        action.addEffect(
                new ChooseActiveCardEffect(playerId, "Choose condition", Filters.type(CardType.CONDITION)) {
                    @Override
                    protected void cardSelected(PhysicalCard condition) {
                        action.addEffect(new DiscardCardFromPlayEffect(condition));
                    }
                });
        return action;
    }
}
