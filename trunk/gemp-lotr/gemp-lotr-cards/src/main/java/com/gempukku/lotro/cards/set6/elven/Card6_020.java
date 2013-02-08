package com.gempukku.lotro.cards.set6.elven;

import com.gempukku.lotro.cards.AbstractEvent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.effects.DiscardCardFromDeckEffect;
import com.gempukku.lotro.cards.effects.RevealTopCardsOfDrawDeckEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.GameUtils;
import com.gempukku.lotro.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.lotro.logic.effects.ChooseAndHealCharactersEffect;
import com.gempukku.lotro.logic.effects.PlayoutDecisionEffect;

import java.util.List;

/**
 * Set: Ents of Fangorn
 * Side: Free
 * Culture: Elven
 * Twilight Cost: 0
 * Type: Event
 * Game Text: Fellowship: Spot an Elf to reveal the top card of your draw deck. Heal up to 2 companions whose culture
 * matches the revealed card. You may discard the revealed card.
 */
public class Card6_020 extends AbstractEvent {
    public Card6_020() {
        super(Side.FREE_PEOPLE, 0, Culture.ELVEN, "Must Be a Dream", Phase.FELLOWSHIP);
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int withTwilightRemoved, int twilightModifier, boolean ignoreRoamingPenalty, boolean ignoreCheckingDeadPile) {
        return super.checkPlayRequirements(playerId, game, self, withTwilightRemoved, twilightModifier, ignoreRoamingPenalty, ignoreCheckingDeadPile)
                && PlayConditions.canSpot(game, Race.ELF);
    }

    @Override
    public PlayEventAction getPlayCardAction(final String playerId, final LotroGame game, PhysicalCard self, int twilightModifier, boolean ignoreRoamingPenalty) {
        final PlayEventAction action = new PlayEventAction(self);
        action.appendEffect(
                new RevealTopCardsOfDrawDeckEffect(self, playerId, 1) {
                    @Override
                    protected void cardsRevealed(List<PhysicalCard> revealedCards) {
                        for (final PhysicalCard card : revealedCards) {
                            Culture cardCulture = card.getBlueprint().getCulture();
                            action.appendEffect(
                                    new ChooseAndHealCharactersEffect(action, playerId, 0, 2, CardType.COMPANION, cardCulture));
                            action.appendEffect(
                                    new PlayoutDecisionEffect(playerId,
                                            new MultipleChoiceAwaitingDecision(1, "Do you wish to discard " + GameUtils.getFullName(card), new String[]{"Yes", "No"}) {
                                                @Override
                                                protected void validDecisionMade(int index, String result) {
                                                    if (result.equals("Yes"))
                                                        action.appendEffect(
                                                                new DiscardCardFromDeckEffect(card));
                                                }
                                            }));
                        }
                    }
                });
        return action;
    }
}
