package com.gempukku.lotro.cards.set6.gandalf;

import com.gempukku.lotro.cards.AbstractEvent;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.effects.AddUntilEndOfPhaseModifierEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.effects.ChooseActiveCardEffect;
import com.gempukku.lotro.logic.modifiers.KeywordModifier;
import com.gempukku.lotro.logic.modifiers.StrengthModifier;

/**
 * Set: Ents of Fangorn
 * Side: Free
 * Culture: Gandalf
 * Twilight Cost: 0
 * Type: Event
 * Game Text: Skirmish: Make an Ent strength +X and damage +X, where X is the number of unbound Hobbits you can spot.
 */
public class Card6_024 extends AbstractEvent {
    public Card6_024() {
        super(Side.FREE_PEOPLE, 0, Culture.GANDALF, "Boomed and Trumpeted", Phase.SKIRMISH);
    }

    @Override
    public PlayEventAction getPlayCardAction(String playerId, LotroGame game, final PhysicalCard self, int twilightModifier, boolean ignoreRoamingPenalty) {
        final PlayEventAction action = new PlayEventAction(self);
        action.appendEffect(
                new ChooseActiveCardEffect(self, playerId, "Choose an Ent", Race.ENT) {
                    @Override
                    protected void cardSelected(LotroGame game, PhysicalCard card) {
                        int unboundHobbit = Filters.countActive(game.getGameState(), game.getModifiersQuerying(), Race.HOBBIT, Filters.unboundCompanion);
                        action.appendEffect(
                                new AddUntilEndOfPhaseModifierEffect(
                                        new StrengthModifier(self, card, unboundHobbit)));
                        action.appendEffect(
                                new AddUntilEndOfPhaseModifierEffect(
                                        new KeywordModifier(self, card, Keyword.DAMAGE, unboundHobbit)));
                    }
                });
        return action;
    }
}
