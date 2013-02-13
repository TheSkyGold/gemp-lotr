package com.gempukku.lotro.cards.set20.site;

import com.gempukku.lotro.cards.AbstractSite;
import com.gempukku.lotro.cards.TriggerConditions;
import com.gempukku.lotro.common.Block;
import com.gempukku.lotro.common.Keyword;
import com.gempukku.lotro.common.Race;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.RequiredTriggerAction;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.modifiers.ModifiersQuerying;
import com.gempukku.lotro.logic.modifiers.TwilightCostModifier;
import com.gempukku.lotro.logic.timing.EffectResult;

import java.util.List;

/**
 * Deep of Helm
 * 5	6
 * Plains. Battleground.
 * The twilight cost of the first Uruk-hai played to Deep of Helm is -3.
 */
public class Card20_441 extends AbstractSite {
    public Card20_441() {
        super("Deep of Helm", Block.SECOND_ED, 5, 6, null);
        addKeyword(Keyword.PLAINS);
        addKeyword(Keyword.BATTLEGROUND);
    }


    @Override
    public Modifier getAlwaysOnModifier(LotroGame game, final PhysicalCard self) {
        return new TwilightCostModifier(self,
                Filters.and(
                        Race.URUK_HAI,
                        new Filter() {
                            @Override
                            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                                return gameState.getCurrentSite() == self && modifiersQuerying.getUntilEndOfTurnLimitCounter(self).getUsedLimit() < 1;
                            }
                        }), -3);
    }

    @Override
    public List<RequiredTriggerAction> getRequiredAfterTriggers(LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (TriggerConditions.played(game, effectResult, Race.URUK_HAI)
                && game.getGameState().getCurrentSite() == self)
            game.getModifiersQuerying().getUntilEndOfTurnLimitCounter(self).incrementToLimit(1, 1);
        return null;
    }
}
