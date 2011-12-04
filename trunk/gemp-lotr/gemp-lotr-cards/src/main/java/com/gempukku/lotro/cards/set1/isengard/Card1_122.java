package com.gempukku.lotro.cards.set1.isengard;

import com.gempukku.lotro.cards.AbstractResponseOldEvent;
import com.gempukku.lotro.cards.TriggerConditions;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.effects.PutCardFromDiscardOnBottomOfDeckEffect;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Race;
import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.timing.EffectResult;
import com.gempukku.lotro.logic.timing.results.PlayCardResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Shadow
 * Culture: Isengard
 * Twilight Cost: 1
 * Type: Event
 * Game Text: Response: If you play an Uruk-hai, take all copies of that card in your discard pile and place them
 * beneath your draw deck.
 */
public class Card1_122 extends AbstractResponseOldEvent {
    public Card1_122() {
        super(Side.SHADOW, Culture.ISENGARD, "Breeding Pit");
    }

    @Override
    public int getTwilightCost() {
        return 1;
    }

    @Override
    public List<PlayEventAction> getOptionalAfterActions(String playerId, LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (TriggerConditions.played(game, effectResult, Filters.and(Race.URUK_HAI, Filters.owner(playerId)))
                && checkPlayRequirements(playerId, game, self, 0, 0, false, false)) {
            final PlayEventAction action = new PlayEventAction(self);
            String playedCardName = ((PlayCardResult) effectResult).getPlayedCard().getBlueprint().getName();
            Collection<PhysicalCard> cardsInDiscardWithSameName = Filters.filter(game.getGameState().getDiscard(playerId), game.getGameState(), game.getModifiersQuerying(), Filters.name(playedCardName));
            for (PhysicalCard physicalCard : cardsInDiscardWithSameName)
                action.appendEffect(new PutCardFromDiscardOnBottomOfDeckEffect(physicalCard));

            return Collections.singletonList(action);
        }
        return null;
    }
}
