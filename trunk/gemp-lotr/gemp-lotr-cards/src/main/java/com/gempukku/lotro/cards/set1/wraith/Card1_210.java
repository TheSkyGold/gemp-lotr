package com.gempukku.lotro.cards.set1.wraith;

import com.gempukku.lotro.cards.AbstractOldEvent;
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
 * Set: The Fellowship of the Ring
 * Side: Shadow
 * Culture: Wraith
 * Twilight Cost: 0
 * Type: Event
 * Game Text: Skirmish: Spot 3 burdens to make a Nazgul strength +1 and damage +1, or spot 6 burdens to make a Nazgul
 * strength +3 and damage +2.
 */
public class Card1_210 extends AbstractOldEvent {
    public Card1_210() {
        super(Side.SHADOW, Culture.WRAITH, "Dark Whispers", Phase.SKIRMISH);
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int twilightModifier, boolean ignoreRoamingPenalty, boolean ignoreCheckingDeadPile) {
        return super.checkPlayRequirements(playerId, game, self, twilightModifier, ignoreRoamingPenalty, ignoreCheckingDeadPile)
                && game.getGameState().getBurdens() >= 3;
    }

    @Override
    public PlayEventAction getPlayCardAction(String playerId, final LotroGame game, final PhysicalCard self, int twilightModifier, boolean ignoreRoamingPenalty) {
        final PlayEventAction action = new PlayEventAction(self);
        action.appendEffect(
                new ChooseActiveCardEffect(self, playerId, "Choose a Nazgul", Race.NAZGUL) {
                    @Override
                    protected void cardSelected(LotroGame game, PhysicalCard nazgul) {
                        int burdens = game.getGameState().getBurdens();

                        action.appendEffect(
                                new AddUntilEndOfPhaseModifierEffect(
                                        new StrengthModifier(self, Filters.sameCard(nazgul), (burdens >= 6) ? 3 : 1), Phase.SKIRMISH));
                        action.appendEffect(
                                new AddUntilEndOfPhaseModifierEffect(
                                        new KeywordModifier(self, Filters.sameCard(nazgul), Keyword.DAMAGE, (burdens >= 6) ? 2 : 1), Phase.SKIRMISH));
                    }
                });
        return action;
    }

    @Override
    public int getTwilightCost() {
        return 0;
    }
}
