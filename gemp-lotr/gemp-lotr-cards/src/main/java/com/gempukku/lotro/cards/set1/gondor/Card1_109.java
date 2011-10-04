package com.gempukku.lotro.cards.set1.gondor;

import com.gempukku.lotro.cards.AbstractEvent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.effects.AddUntilStartOfPhaseModifierEffect;
import com.gempukku.lotro.cards.effects.CardAffectsCardEffect;
import com.gempukku.lotro.cards.effects.ExertCharactersEffect;
import com.gempukku.lotro.cards.modifiers.AllyOnCurrentSiteModifier;
import com.gempukku.lotro.cards.modifiers.StrengthModifier;
import com.gempukku.lotro.common.CardType;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Phase;
import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.effects.ChooseActiveCardEffect;

/**
 * Set: The Fellowship of the Ring
 * Side: Free
 * Culture: Gondor
 * Twilight Cost: 0
 * Type: Event
 * Game Text: Maneuver: Exert Aragorn to spot an ally. Until the regroup phase, that ally is strength +2 and
 * participates in archery fire and skirmishes.
 */
public class Card1_109 extends AbstractEvent {
    public Card1_109() {
        super(Side.FREE_PEOPLE, Culture.GONDOR, "One Whom Men Would Follow", Phase.MANEUVER);
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int twilightModifier) {
        return super.checkPlayRequirements(playerId, game, self, twilightModifier)
                && PlayConditions.canExert(self, game.getGameState(), game.getModifiersQuerying(), Filters.name("Aragorn"));
    }

    @Override
    public PlayEventAction getPlayCardAction(String playerId, LotroGame game, final PhysicalCard self, int twilightModifier) {
        PhysicalCard aragorn = Filters.findFirstActive(game.getGameState(), game.getModifiersQuerying(), Filters.name("Aragorn"));

        final PlayEventAction action = new PlayEventAction(self);
        action.appendCost(new ExertCharactersEffect(self, aragorn));
        action.appendEffect(
                new ChooseActiveCardEffect(playerId, "Choose an ally", Filters.type(CardType.ALLY)) {
                    @Override
                    protected void cardSelected(PhysicalCard ally) {
                        action.appendEffect(new CardAffectsCardEffect(self, ally));
                        action.appendEffect(
                                new AddUntilStartOfPhaseModifierEffect(
                                        new StrengthModifier(self, Filters.sameCard(ally), 2)
                                        , Phase.REGROUP));
                        action.appendEffect(
                                new AddUntilStartOfPhaseModifierEffect(
                                        new AllyOnCurrentSiteModifier(self, Filters.sameCard(ally))
                                        , Phase.REGROUP));
                    }
                }
        );
        return action;
    }

    @Override
    public int getTwilightCost() {
        return 0;
    }
}
