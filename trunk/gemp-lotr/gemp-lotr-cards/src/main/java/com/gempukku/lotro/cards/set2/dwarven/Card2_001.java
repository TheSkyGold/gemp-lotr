package com.gempukku.lotro.cards.set2.dwarven;

import com.gempukku.lotro.cards.AbstractPermanent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.DiscardTopCardFromDeckEffect;
import com.gempukku.lotro.cards.effects.choose.ChooseAndPlayCardFromDiscardEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Mines of Moria
 * Side: Free
 * Culture: Dwarven
 * Twilight Cost: 1
 * Type: Condition
 * Game Text: Tale. Plays to your support area. Fellowship: Discard the top 3 cards from your draw deck to play
 * a [DWARVEN] weapon from your discard pile.
 */
public class Card2_001 extends AbstractPermanent {
    public Card2_001() {
        super(Side.FREE_PEOPLE, 1, CardType.CONDITION, Culture.DWARVEN, Zone.SUPPORT, "Beneath the Mountains");
        addKeyword(Keyword.TALE);
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int twilightModifier, boolean ignoreRoamingPenalty, boolean ignoreCheckingDeadPile) {
        return super.checkPlayRequirements(playerId, game, self, twilightModifier, ignoreRoamingPenalty, ignoreCheckingDeadPile)
                && game.getGameState().getDeck(playerId).size() >= 3;
    }

    @Override
    protected List<? extends Action> getExtraPhaseActions(String playerId, LotroGame game, PhysicalCard self) {
        if (PlayConditions.canUseFPCardDuringPhase(game, Phase.FELLOWSHIP, self)
                && Filters.filter(game.getGameState().getDiscard(playerId), game.getGameState(), game.getModifiersQuerying(), Culture.DWARVEN, Filters.weapon, Filters.playable(game)).size() > 0) {
            ActivateCardAction action = new ActivateCardAction(self);
            action.appendCost(
                    new DiscardTopCardFromDeckEffect(self, playerId, false));
            action.appendCost(
                    new DiscardTopCardFromDeckEffect(self, playerId, false));
            action.appendCost(
                    new DiscardTopCardFromDeckEffect(self, playerId, false));
            action.appendEffect(
                    new ChooseAndPlayCardFromDiscardEffect(playerId, game, Filters.and(Culture.DWARVEN, Filters.weapon)));
            return Collections.singletonList(action);
        }
        return null;
    }
}
