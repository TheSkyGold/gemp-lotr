package com.gempukku.lotro.cards.set1.wraith;

import com.gempukku.lotro.cards.AbstractOldEvent;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.decisions.ForEachYouSpotDecision;
import com.gempukku.lotro.cards.effects.DiscardCardFromDeckEffect;
import com.gempukku.lotro.cards.effects.PutCardFromDeckIntoHandOrDiscardEffect;
import com.gempukku.lotro.cards.effects.RevealTopCardsOfDrawDeckEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.decisions.DecisionResultInvalidException;
import com.gempukku.lotro.logic.effects.PlayoutDecisionEffect;

import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Shadow
 * Culture: Wraith
 * Twilight Cost: 1
 * Type: Event
 * Game Text: Search. Shadow: Spot X Nazgul to reveal the top X cards of your draw deck. Take into your hand all
 * [WRAITH] cards revealed and discard the rest.
 */
public class Card1_215 extends AbstractOldEvent {
    public Card1_215() {
        super(Side.SHADOW, Culture.WRAITH, "The Master's Will", Phase.SHADOW);
        addKeyword(Keyword.SEARCH);
    }

    @Override
    public PlayEventAction getPlayCardAction(final String playerId, final LotroGame game, final PhysicalCard self, int twilightModifier, boolean ignoreRoamingPenalty) {
        final PlayEventAction action = new PlayEventAction(self);
        action.appendEffect(
                new PlayoutDecisionEffect(playerId,
                        new ForEachYouSpotDecision(1, "Choose how many Nazgul you want to spot", game, Integer.MAX_VALUE, Race.NAZGUL) {
                            @Override
                            public void decisionMade(String result) throws DecisionResultInvalidException {
                                int spotCount = getValidatedResult(result);
                                action.appendEffect(
                                        new RevealTopCardsOfDrawDeckEffect(self, playerId, spotCount) {
                                            @Override
                                            protected void cardsRevealed(List<PhysicalCard> revealedCards) {
                                                for (PhysicalCard revealedCard : revealedCards) {
                                                    if (revealedCard.getBlueprint().getCulture() == Culture.WRAITH)
                                                        action.appendEffect(
                                                                new PutCardFromDeckIntoHandOrDiscardEffect(revealedCard));
                                                    else
                                                        action.appendEffect(
                                                                new DiscardCardFromDeckEffect(revealedCard));
                                                }
                                            }
                                        });
                            }
                        }));
        return action;
    }

    @Override
    public int getTwilightCost() {
        return 1;
    }
}
