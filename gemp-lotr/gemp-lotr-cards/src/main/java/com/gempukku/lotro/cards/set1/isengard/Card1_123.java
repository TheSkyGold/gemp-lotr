package com.gempukku.lotro.cards.set1.isengard;

import com.gempukku.lotro.cards.AbstractOldEvent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.effects.choose.ChooseAndExertCharactersEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.effects.ChooseActiveCardEffect;
import com.gempukku.lotro.logic.effects.DiscardCardsFromPlayEffect;

/**
 * Set: The Fellowship of the Ring
 * Side: Shadow
 * Culture: Isengard
 * Twilight Cost: 3
 * Type: Event
 * Game Text: Spell. Weather. Maneuver: Exert a [ISENGARD] minion and spot 5 companions to discard an exhausted
 * companion (except the Ring-bearer).
 */
public class Card1_123 extends AbstractOldEvent {
    public Card1_123() {
        super(Side.SHADOW, Culture.ISENGARD, "Caradhras Has Not Forgiven Us", Phase.MANEUVER);
        addKeyword(Keyword.WEATHER);
        addKeyword(Keyword.SPELL);
    }

    @Override
    public int getTwilightCost() {
        return 3;
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int withTwilightRemoved, int twilightModifier, boolean ignoreRoamingPenalty, boolean ignoreCheckingDeadPile) {
        return super.checkPlayRequirements(playerId, game, self, withTwilightRemoved, twilightModifier, ignoreRoamingPenalty, ignoreCheckingDeadPile)
                && PlayConditions.canExert(self, game, Culture.ISENGARD, CardType.MINION)
                && PlayConditions.canSpot(game, 5, CardType.COMPANION);
    }

    @Override
    public PlayEventAction getPlayCardAction(String playerId, LotroGame game, final PhysicalCard self, int twilightModifier, boolean ignoreRoamingPenalty) {
        final PlayEventAction action = new PlayEventAction(self);
        action.appendCost(
                new ChooseAndExertCharactersEffect(action, playerId, 1, 1, Culture.ISENGARD, CardType.MINION));
        action.appendEffect(
                new ChooseActiveCardEffect(self, playerId, "Choose non Ring-bearer exhausted companion", CardType.COMPANION, Filters.not(Filters.ringBearer), Filters.exhausted) {
                    @Override
                    protected void cardSelected(LotroGame game, PhysicalCard exhaustedCompanion) {
                        action.appendEffect(new DiscardCardsFromPlayEffect(self, exhaustedCompanion));
                    }
                }
        );
        return action;
    }
}
