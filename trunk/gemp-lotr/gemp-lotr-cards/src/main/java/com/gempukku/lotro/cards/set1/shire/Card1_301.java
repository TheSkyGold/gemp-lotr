package com.gempukku.lotro.cards.set1.shire;

import com.gempukku.lotro.cards.AbstractAlly;
import com.gempukku.lotro.cards.effects.AddTwilightEffect;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Keyword;
import com.gempukku.lotro.common.Zone;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.DefaultCostToEffectAction;
import com.gempukku.lotro.logic.timing.Action;
import com.gempukku.lotro.logic.timing.UnrespondableEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Free
 * Culture: Shire
 * Twilight Cost: 1
 * Type: Ally • Home 1 • Hobbit
 * Strength: 2
 * Vitality: 2
 * Site: 1
 * Game Text: Fellowship: If the twilight pool has fewer than 3 twilight tokens, add (2) to reveal the top 3 cards of
 * your draw deck. Take all [SHIRE] cards revealed into hand and discard the rest.
 */
public class Card1_301 extends AbstractAlly {
    public Card1_301() {
        super(1, 1, 2, 2, Culture.SHIRE, "Master Proudfoot", true);
        addKeyword(Keyword.HOBBIT);
    }

    @Override
    protected List<? extends Action> getExtraPhaseActions(final String playerId, LotroGame game, PhysicalCard self) {
        if (game.getGameState().getTwilightPool() < 3) {
            DefaultCostToEffectAction action = new DefaultCostToEffectAction(self, Keyword.FELLOWSHIP, "Add (2) to reveal the top 3 cards of your draw deck. Take all SHIRE cards revealed into hand and discard the rest.");
            action.addCost(new AddTwilightEffect(2));
            action.addEffect(
                    new UnrespondableEffect() {
                        @Override
                        public void playEffect(LotroGame game) {
                            List<? extends PhysicalCard> deck = game.getGameState().getDeck(playerId);
                            int cardCount = Math.min(deck.size(), 3);
                            List<? extends PhysicalCard> cards = new LinkedList<PhysicalCard>(deck.subList(0, cardCount));

                            for (PhysicalCard card : cards) {
                                game.getGameState().removeCardFromZone(card);
                                if (card.getBlueprint().getCulture() == Culture.SHIRE)
                                    game.getGameState().addCardToZone(card, Zone.HAND);
                                else
                                    game.getGameState().addCardToZone(card, Zone.DISCARD);
                            }
                        }
                    });

            return Collections.singletonList(action);
        }
        return null;
    }
}
