package com.gempukku.lotro.cards.set1.elven;

import com.gempukku.lotro.cards.AbstractPermanent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.ChooseArbitraryCardsEffect;
import com.gempukku.lotro.cards.effects.PutCardFromHandOnBottomOfDeckEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Free
 * Culture: Elven
 * Twilight Cost: 1
 * Type: Condition
 * Game Text: Plays to your support area. Fellowship: Reveal an [ELVEN] card from hand and place it beneath your draw
 * deck.
 */
public class Card1_054 extends AbstractPermanent {
    public Card1_054() {
        super(Side.FREE_PEOPLE, 1, CardType.CONDITION, Culture.ELVEN, Zone.FREE_SUPPORT, "Mallorn-trees");
    }

    @Override
    public List<? extends Action> getExtraPhaseActions(String playerId, LotroGame game, PhysicalCard self) {
        if (PlayConditions.canUseFPCardDuringPhase(game.getGameState(), Phase.FELLOWSHIP, self)) {
            final ActivateCardAction action = new ActivateCardAction(self, Keyword.FELLOWSHIP);
            action.appendEffect(
                    new ChooseArbitraryCardsEffect(playerId, "Choose ELVEN card", game.getGameState().getHand(playerId), Filters.culture(Culture.ELVEN), 1, 1) {
                        @Override
                        protected void cardsSelected(List<PhysicalCard> selectedCards) {
                            if (selectedCards.size() > 0)
                                action.appendEffect(new PutCardFromHandOnBottomOfDeckEffect(selectedCards.get(0)));
                        }
                    });
            return Collections.singletonList(action);
        }

        return null;
    }
}
