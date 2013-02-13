package com.gempukku.lotro.cards.set20.site;

import com.gempukku.lotro.cards.AbstractSite;
import com.gempukku.lotro.common.Block;
import com.gempukku.lotro.common.Keyword;
import com.gempukku.lotro.common.Race;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.modifiers.KeywordModifier;
import com.gempukku.lotro.logic.modifiers.Modifier;

/**
 * Weathertop
 * 2	3
 * Battleground.
 * Nazgul are fierce.
 */
public class Card20_427 extends AbstractSite {
    public Card20_427() {
        super("Weathertop", Block.SECOND_ED, 2, 3, null);
        addKeyword(Keyword.BATTLEGROUND);
    }

    @Override
    public Modifier getAlwaysOnModifier(LotroGame game, PhysicalCard self) {
        return new KeywordModifier(self, Race.NAZGUL, Keyword.FIERCE);
    }
}
