package com.gempukku.lotro.cards.set1.gandalf;

import com.gempukku.lotro.cards.AbstractAlly;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.SelfExertEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.effects.ChooseActiveCardEffect;
import com.gempukku.lotro.logic.effects.DiscardCardsFromPlayEffect;
import com.gempukku.lotro.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Free
 * Culture: Gandalf
 * Twilight Cost: 1
 * Type: Ally • Home 1 • Man
 * Strength: 3
 * Vitality: 3
 * Site: 1
 * Game Text: To play, spot Gandalf. Maneuver: Exert Albert Dreary to discard a [ISENGARD] or [MORIA] condition.
 */
public class Card1_069 extends AbstractAlly {
    public Card1_069() {
        super(1, Block.FELLOWSHIP, 1, 3, 3, Race.MAN, Culture.GANDALF, "Albert Dreary", true);
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int twilightModifier, boolean ignoreRoamingPenalty, boolean ignoreCheckingDeadPile) {
        return super.checkPlayRequirements(playerId, game, self, twilightModifier, ignoreRoamingPenalty, ignoreCheckingDeadPile)
                && Filters.canSpot(game.getGameState(), game.getModifiersQuerying(), Filters.gandalf);
    }

    @Override
    protected List<? extends Action> getExtraInPlayPhaseActions(String playerId, LotroGame game, final PhysicalCard self) {
        if (PlayConditions.canUseFPCardDuringPhase(game, Phase.MANEUVER, self)
                && PlayConditions.canExert(self, game, self)) {
            final ActivateCardAction action = new ActivateCardAction(self);
            action.appendCost(new SelfExertEffect(self));
            action.appendEffect(
                    new ChooseActiveCardEffect(self, playerId, "Choose ISENGARD or MORIA condition", Filters.or(Culture.ISENGARD, Culture.MORIA), CardType.CONDITION) {
                        @Override
                        protected void cardSelected(LotroGame game, PhysicalCard condition) {
                            action.appendEffect(new DiscardCardsFromPlayEffect(self, condition));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
