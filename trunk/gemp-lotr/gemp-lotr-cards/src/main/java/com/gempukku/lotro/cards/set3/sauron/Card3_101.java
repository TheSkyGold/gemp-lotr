package com.gempukku.lotro.cards.set3.sauron;

import com.gempukku.lotro.cards.AbstractMinion;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.costs.ChooseAndDiscardCardsFromPlayCost;
import com.gempukku.lotro.cards.costs.ExertCharactersCost;
import com.gempukku.lotro.cards.effects.DiscardTopCardFromDeckEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Realms of Elf-lords
 * Side: Shadow
 * Culture: Sauron
 * Twilight Cost: 3
 * Type: Minion • Orc
 * Strength: 9
 * Vitality: 3
 * Site: 6
 * Game Text: Maneuver: Exert this minion and discard your [SAURON] condition to make the Free Peoples player discard
 * the top 2 cards from his or her draw deck.
 */
public class Card3_101 extends AbstractMinion {
    public Card3_101() {
        super(3, 9, 3, 6, Race.ORC, Culture.SAURON, "Orc Warrior");
    }

    @Override
    protected List<? extends Action> getExtraPhaseActions(String playerId, LotroGame game, PhysicalCard self) {
        if (PlayConditions.canUseShadowCardDuringPhase(game.getGameState(), Phase.MANEUVER, self, 0)
                && PlayConditions.canExert(self, game.getGameState(), game.getModifiersQuerying(), self)
                && Filters.countActive(game.getGameState(), game.getModifiersQuerying(), Filters.owner(playerId), Filters.culture(Culture.SAURON), Filters.type(CardType.CONDITION)) > 0) {
            ActivateCardAction action = new ActivateCardAction(self, Keyword.MANEUVER);
            action.appendCost(
                    new ExertCharactersCost(self, self));
            action.appendCost(
                    new ChooseAndDiscardCardsFromPlayCost(action, playerId, 1, 1, Filters.owner(playerId), Filters.culture(Culture.SAURON), Filters.type(CardType.CONDITION)));
            action.appendEffect(
                    new DiscardTopCardFromDeckEffect(game.getGameState().getCurrentPlayerId()));
            action.appendEffect(
                    new DiscardTopCardFromDeckEffect(game.getGameState().getCurrentPlayerId()));
            return Collections.singletonList(action);
        }
        return null;
    }
}
