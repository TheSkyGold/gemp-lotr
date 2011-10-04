package com.gempukku.lotro.cards.set3.sauron;

import com.gempukku.lotro.cards.AbstractEvent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.effects.ChooseAndDiscardCardsFromHandEffect;
import com.gempukku.lotro.cards.effects.ChooseAndExertCharactersEffect;
import com.gempukku.lotro.cards.effects.RevealRandomCardsFromHandEffect;
import com.gempukku.lotro.common.CardType;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;

import java.util.List;

/**
 * Set: Realms of Elf-lords
 * Side: Shadow
 * Culture: Sauron
 * Twilight Cost: 2
 * Type: Event
 * Game Text: Regroup: Exert a [SAURON] minion to reveal a card at random from the Free Peoples player's hand. That
 * player must discard X cards from hand, where X is the twilight cost of the card revealed.
 */
public class Card3_087 extends AbstractEvent {
    public Card3_087() {
        super(Side.SHADOW, Culture.SAURON, "The Dark Lord Advances");
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int twilightModifier) {
        return super.checkPlayRequirements(playerId, game, self, twilightModifier)
                && PlayConditions.canExert(self, game.getGameState(), game.getModifiersQuerying(), Filters.culture(Culture.SAURON), Filters.type(CardType.MINION));
    }

    @Override
    public int getTwilightCost() {
        return 2;
    }

    @Override
    public PlayEventAction getPlayCardAction(String playerId, final LotroGame game, PhysicalCard self, int twilightModifier) {
        final PlayEventAction action = new PlayEventAction(self);
        action.appendCost(
                new ChooseAndExertCharactersEffect(action, playerId, 1, 1, Filters.culture(Culture.SAURON), Filters.type(CardType.MINION)));
        action.appendEffect(
                new RevealRandomCardsFromHandEffect(game.getGameState().getCurrentPlayerId(), 1) {
                    @Override
                    protected void cardsRevealed(List<PhysicalCard> revealedCards) {
                        if (revealedCards.size() > 0) {
                            PhysicalCard revealedCard = revealedCards.get(0);
                            int twilightCost = revealedCard.getBlueprint().getTwilightCost();
                            action.appendEffect(
                                    new ChooseAndDiscardCardsFromHandEffect(action, game.getGameState().getCurrentPlayerId(), twilightCost));
                        }
                    }
                });
        return action;
    }
}
