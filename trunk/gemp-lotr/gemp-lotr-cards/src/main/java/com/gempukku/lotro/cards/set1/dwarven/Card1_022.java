package com.gempukku.lotro.cards.set1.dwarven;

import com.gempukku.lotro.cards.AbstractOldEvent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.effects.PutCardFromDiscardIntoHandEffect;
import com.gempukku.lotro.cards.effects.choose.ChooseAndExertCharactersEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.GameUtils;
import com.gempukku.lotro.logic.actions.CostToEffectAction;
import com.gempukku.lotro.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.lotro.logic.effects.PlayoutDecisionEffect;
import com.gempukku.lotro.logic.timing.UnrespondableEffect;

/**
 * Set: The Fellowship of the Ring
 * Side: Free
 * Culture: Dwarven
 * Twilight Cost: 0
 * Type: Event
 * Game Text: Maneuver: Exert a Dwarf to discard cards from the top of your draw deck until you choose to stop
 * (limit 5). Add (1) for each card discarded in this way. Take the last card discarded into hand.
 */
public class Card1_022 extends AbstractOldEvent {
    public Card1_022() {
        super(Side.FREE_PEOPLE, Culture.DWARVEN, "Mithril Shaft", Phase.MANEUVER);
    }

    @Override
    public int getTwilightCost() {
        return 0;
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int withTwilightRemoved, int twilightModifier, boolean ignoreRoamingPenalty, boolean ignoreCheckingDeadPile) {
        return super.checkPlayRequirements(playerId, game, self, withTwilightRemoved, twilightModifier, ignoreRoamingPenalty, ignoreCheckingDeadPile)
                && PlayConditions.canExert(self, game, Race.DWARF);
    }

    @Override
    public PlayEventAction getPlayCardAction(String playerId, LotroGame game, PhysicalCard self, int twilightModifier, boolean ignoreRoamingPenalty) {
        final PlayEventAction action = new PlayEventAction(self);
        action.appendCost(
                new ChooseAndExertCharactersEffect(action, playerId, 1, 1, Race.DWARF));
        action.appendEffect(new DiscardAndChooseToPutToHandEffect(action, playerId, null, 0));
        return action;
    }

    private class DiscardAndChooseToPutToHandEffect extends UnrespondableEffect {
        private CostToEffectAction _action;
        private String _player;
        private int _count;
        private PhysicalCard _lastCard;

        private DiscardAndChooseToPutToHandEffect(CostToEffectAction action, String player, PhysicalCard lastCard, int count) {
            _action = action;
            _player = player;
            _lastCard = lastCard;
            _count = count;
        }

        @Override
        public void doPlayEffect(LotroGame game) {
            final GameState gameState = game.getGameState();
            PhysicalCard card = gameState.removeTopDeckCard(_player);
            if (card != null) {
                gameState.addCardToZone(game, card, Zone.DISCARD);
                gameState.addTwilight(1);
                _lastCard = card;
            }
            if (card != null && _count < 5) {
                _action.appendEffect(new PlayoutDecisionEffect(_player,
                        new MultipleChoiceAwaitingDecision(1, "Do you want to put " + GameUtils.getFullName(_lastCard) + " in your hand?", new String[]{"Yes", "No"}) {
                            @Override
                            protected void validDecisionMade(int index, String result) {
                                if (result.equals("Yes")) {
                                    _action.appendEffect(new PutCardFromDiscardIntoHandEffect(_lastCard));
                                } else {
                                    _action.appendEffect(new DiscardAndChooseToPutToHandEffect(_action, _player, _lastCard, _count + 1));
                                }
                            }
                        }));
            } else if (_lastCard != null) {
                _action.appendEffect(new PutCardFromDiscardIntoHandEffect(_lastCard));
            }
        }
    }
}
