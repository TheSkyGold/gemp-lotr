package com.gempukku.lotro.cards.set1.dwarven;

import com.gempukku.lotro.cards.AbstractResponseOldEvent;
import com.gempukku.lotro.cards.TriggerConditions;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Race;
import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.common.Zone;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.effects.HealCharactersEffect;
import com.gempukku.lotro.logic.timing.EffectResult;
import com.gempukku.lotro.logic.timing.results.SkirmishResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Free
 * Culture: Dwarven
 * Twilight Cost: 0
 * Type: Event
 * Game Text: Response: If a Dwarf wins a skirmish, heal that Dwarf.
 */
public class Card1_025 extends AbstractResponseOldEvent {
    public Card1_025() {
        super(Side.FREE_PEOPLE, Culture.DWARVEN, "Still Draws Breath");
    }

    @Override
    public int getTwilightCost() {
        return 0;
    }

    @Override
    public List<PlayEventAction> getOptionalAfterActions(String playerId, LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (self.getZone() == Zone.HAND && TriggerConditions.winsSkirmish(game, effectResult, Race.DWARF)
                && checkPlayRequirements(playerId, game, self, 0, false, false)) {
            SkirmishResult skirmishResult = (SkirmishResult) effectResult;
            PlayEventAction action = new PlayEventAction(self);
            action.appendEffect(new HealCharactersEffect(self, skirmishResult.getWinners().get(0)));
            return Collections.singletonList(action);
        }
        return null;
    }
}
