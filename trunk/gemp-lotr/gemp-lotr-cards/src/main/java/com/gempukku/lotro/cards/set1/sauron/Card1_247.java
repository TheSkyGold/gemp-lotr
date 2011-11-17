package com.gempukku.lotro.cards.set1.sauron;

import com.gempukku.lotro.cards.AbstractResponseOldEvent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.effects.AddUntilStartOfPhaseModifierEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.effects.ChooseActiveCardEffect;
import com.gempukku.lotro.logic.modifiers.KeywordModifier;
import com.gempukku.lotro.logic.timing.EffectResult;
import com.gempukku.lotro.logic.timing.results.SkirmishResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Shadow
 * Culture: Sauron
 * Twilight Cost: 0
 * Type: Event
 * Game Text: Response: If a [SAURON] Orc wins a skirmish, make that Orc fierce until the regroup phase.
 */
public class Card1_247 extends AbstractResponseOldEvent {
    public Card1_247() {
        super(Side.SHADOW, Culture.SAURON, "Enheartened Foe");
    }

    @Override
    public List<PlayEventAction> getOptionalAfterActions(String playerId, LotroGame game, EffectResult effectResult, final PhysicalCard self) {
        if (PlayConditions.winsSkirmish(game, effectResult, Filters.and(Culture.SAURON, Race.ORC))
                && checkPlayRequirements(playerId, game, self, 0, false)) {
            SkirmishResult skirmishResult = (SkirmishResult) effectResult;

            final PlayEventAction action = new PlayEventAction(self);
            action.appendEffect(
                    new ChooseActiveCardEffect(self, playerId, "Choose an Orc", Culture.SAURON, Race.ORC, Filters.in(skirmishResult.getWinners())) {
                        @Override
                        protected void cardSelected(LotroGame game, PhysicalCard winningSauronOrc) {
                            action.appendEffect(
                                    new AddUntilStartOfPhaseModifierEffect(
                                            new KeywordModifier(self, Filters.sameCard(winningSauronOrc), Keyword.FIERCE), Phase.REGROUP));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    public int getTwilightCost() {
        return 0;
    }
}
