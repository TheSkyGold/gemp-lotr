package com.gempukku.lotro.cards.set4.isengard;

import com.gempukku.lotro.cards.AbstractMinion;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Keyword;
import com.gempukku.lotro.common.Race;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.logic.modifiers.ModifiersQuerying;

/**
 * Set: The Two Towers
 * Side: Shadow
 * Culture: Isengard
 * Twilight Cost: 6
 * Type: Minion • Uruk-Hai
 * Strength: 12
 * Vitality: 3
 * Site: 5
 * Game Text: Damage +1. The twilight cost of this minion is -1 for each site you control.
 */
public class Card4_203 extends AbstractMinion {
    public Card4_203() {
        super(6, 12, 3, 5, Race.URUK_HAI, Culture.ISENGARD, "Uruk-hai Horde");
        addKeyword(Keyword.DAMAGE);
    }

    @Override
    public int getTwilightCostModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return -Filters.countActive(gameState, modifiersQuerying, Filters.siteControlled(self.getOwner()));
    }
}
