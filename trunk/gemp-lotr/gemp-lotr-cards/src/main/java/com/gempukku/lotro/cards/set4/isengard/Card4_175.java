package com.gempukku.lotro.cards.set4.isengard;

import com.gempukku.lotro.cards.AbstractOldEvent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.effects.AddUntilEndOfPhaseModifierEffect;
import com.gempukku.lotro.cards.effects.choose.ChooseAndExertCharactersEffect;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Phase;
import com.gempukku.lotro.common.Race;
import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.effects.ChooseActiveCardEffect;
import com.gempukku.lotro.logic.modifiers.StrengthModifier;

/**
 * Set: The Two Towers
 * Side: Shadow
 * Culture: Isengard
 * Twilight Cost: 1
 * Type: Event
 * Game Text: Skirmish: Exert an Uruk-hai who is not assigned to a skirmish to make another Uruk-hai strength +3.
 */
public class Card4_175 extends AbstractOldEvent {
    public Card4_175() {
        super(Side.SHADOW, Culture.ISENGARD, "Still They Came", Phase.SKIRMISH);
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int twilightModifier, boolean ignoreRoamingPenalty, boolean ignoreCheckingDeadPile) {
        return super.checkPlayRequirements(playerId, game, self, twilightModifier, ignoreRoamingPenalty, ignoreCheckingDeadPile)
                && PlayConditions.canExert(self, game, Race.URUK_HAI, Filters.notAssignedToSkirmish);
    }

    @Override
    public int getTwilightCost() {
        return 1;
    }

    @Override
    public PlayEventAction getPlayCardAction(final String playerId, LotroGame game, final PhysicalCard self, int twilightModifier, boolean ignoreRoamingPenalty) {
        final PlayEventAction action = new PlayEventAction(self);
        action.appendCost(
                new ChooseAndExertCharactersEffect(action, playerId, 1, 1, Race.URUK_HAI, Filters.notAssignedToSkirmish) {
                    @Override
                    protected void forEachCardExertedCallback(PhysicalCard character) {
                        action.appendEffect(
                                new ChooseActiveCardEffect(self, playerId, "Choose Uruk-hai", Filters.not(Filters.sameCard(character)), Race.URUK_HAI) {
                                    @Override
                                    protected void cardSelected(LotroGame game, PhysicalCard card) {
                                        action.insertEffect(
                                                new AddUntilEndOfPhaseModifierEffect(
                                                        new StrengthModifier(self, Filters.sameCard(card), 3), Phase.SKIRMISH));
                                    }
                                });
                    }
                });
        return action;
    }
}
