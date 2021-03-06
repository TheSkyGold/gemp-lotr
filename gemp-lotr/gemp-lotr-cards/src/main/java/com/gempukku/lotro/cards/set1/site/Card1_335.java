package com.gempukku.lotro.cards.set1.site;

import com.gempukku.lotro.cards.AbstractSite;
import com.gempukku.lotro.common.Block;
import com.gempukku.lotro.common.Race;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.modifiers.TwilightCostModifier;

/**
 * Set: The Fellowship of the Ring
 * Twilight Cost: 3
 * Type: Site
 * Site: 2
 * Game Text: Each Nazgul's twilight cost is -1.
 */
public class Card1_335 extends AbstractSite {
    public Card1_335() {
        super("Weatherhills", Block.FELLOWSHIP, 2, 3, Direction.LEFT);
    }

    @Override
    public Modifier getAlwaysOnModifier(LotroGame game, PhysicalCard self) {
        return new TwilightCostModifier(self, Race.NAZGUL, -1);
    }
}
