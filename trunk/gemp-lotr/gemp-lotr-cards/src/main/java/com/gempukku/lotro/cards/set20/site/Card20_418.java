package com.gempukku.lotro.cards.set20.site;

import com.gempukku.lotro.cards.AbstractSite;
import com.gempukku.lotro.common.Block;
import com.gempukku.lotro.common.CardType;
import com.gempukku.lotro.common.Keyword;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.modifiers.TwilightCostModifier;

/**
 * East Road
 * 1	0
 * Forest.
 * Each companion is twilight cost +2.
 */
public class Card20_418 extends AbstractSite {
    public Card20_418() {
        super("East Road", Block.SECOND_ED, 1, 0, null);
        addKeyword(Keyword.FOREST);
    }

    @Override
    public Modifier getAlwaysOnModifier(LotroGame game, PhysicalCard self) {
        return new TwilightCostModifier(self, CardType.COMPANION, 2);
    }
}
