package com.gempukku.lotro.cards.set2.moria;

import com.gempukku.lotro.cards.AbstractResponseOldEvent;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.common.CardType;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.effects.WoundCharactersEffect;
import com.gempukku.lotro.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Mines of Moria
 * Side: Shadow
 * Culture: Moria
 * Twilight Cost: 1
 * Type: Event
 * Game Text: Response: If a skirmish that involved The Balrog bearing Whip of Many Thongs is about to end, wound
 * a companion in that skirmish twice.
 */
public class Card2_057 extends AbstractResponseOldEvent {
    public Card2_057() {
        super(Side.SHADOW, Culture.MORIA, "Final Cry");
    }

    @Override
    public int getTwilightCost() {
        return 1;
    }

    @Override
    public List<PlayEventAction> getOptionalAfterActions(String playerId, LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (effectResult.getType() == EffectResult.Type.SKIRMISH_ABOUT_TO_END
                && checkPlayRequirements(playerId, game, self, 0, 0, false, false)
                && Filters.filter(game.getGameState().getSkirmish().getShadowCharacters(), game.getGameState(), game.getModifiersQuerying(),
                Filters.balrog, Filters.hasAttached(Filters.name("Whip of Many Thongs"))).size() > 0) {
            PlayEventAction action = new PlayEventAction(self);
            action.appendEffect(
                    new WoundCharactersEffect(self, CardType.COMPANION, Filters.inSkirmish));
            action.appendEffect(
                    new WoundCharactersEffect(self, CardType.COMPANION, Filters.inSkirmish));
            return Collections.singletonList(action);
        }
        return null;
    }
}
