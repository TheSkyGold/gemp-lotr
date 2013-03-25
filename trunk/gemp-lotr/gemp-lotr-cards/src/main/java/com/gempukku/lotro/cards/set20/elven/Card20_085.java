package com.gempukku.lotro.cards.set20.elven;

import com.gempukku.lotro.cards.AbstractEvent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.effects.RevealTopCardsOfDrawDeckEffect;
import com.gempukku.lotro.cards.effects.choose.ChooseAndDiscardCardsFromPlayEffect;
import com.gempukku.lotro.cards.effects.choose.ChooseAndExertCharactersEffect;
import com.gempukku.lotro.common.CardType;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Phase;
import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;

import java.util.List;

/**
 * 2
 * Galadriel's Wisdom
 * Event • Regroup
 * Exert Galadriel and reveal the top card of your draw deck to discard a Shadow condition (or 2 Shadow conditions
 * if you reveal an [Elven] card).
 * http://www.lotrtcg.org/coreset/elven/galadrielswisdom(r2).jpg
 */
public class Card20_085 extends AbstractEvent {
    public Card20_085() {
        super(Side.FREE_PEOPLE, 2, Culture.ELVEN, "Galadriel's Wisdom", Phase.REGROUP);
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int withTwilightRemoved, int twilightModifier, boolean ignoreRoamingPenalty, boolean ignoreCheckingDeadPile) {
        return super.checkPlayRequirements(playerId, game, self, withTwilightRemoved, twilightModifier, ignoreRoamingPenalty, ignoreCheckingDeadPile)
                && PlayConditions.canExert(self, game, 1, 1, Filters.galadriel);
    }

    @Override
    public PlayEventAction getPlayCardAction(final String playerId, LotroGame game, PhysicalCard self, int twilightModifier, boolean ignoreRoamingPenalty) {
        final PlayEventAction action = new PlayEventAction(self);
        action.appendCost(
                new ChooseAndExertCharactersEffect(action, playerId, 1, 1, Filters.galadriel));
        action.appendCost(
                new RevealTopCardsOfDrawDeckEffect(self, playerId, 1) {
                    @Override
                    protected void cardsRevealed(List<PhysicalCard> revealedCards) {
                        int count = 1;
                        for (PhysicalCard revealedCard : revealedCards) {
                            if (revealedCard.getBlueprint().getCulture() == Culture.ELVEN)
                                count = 2;
                        }
                        action.appendEffect(
                                new ChooseAndDiscardCardsFromPlayEffect(action, playerId, count, count, CardType.CONDITION, Side.SHADOW));
                    }
                });
        return action;
    }
}
