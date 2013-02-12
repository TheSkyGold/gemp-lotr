package com.gempukku.lotro.cards.set20.fallenRealms;

import com.gempukku.lotro.cards.AbstractMinion;
import com.gempukku.lotro.cards.modifiers.ArcheryTotalModifier;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Keyword;
import com.gempukku.lotro.common.Race;
import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.modifiers.GameHasCondition;
import com.gempukku.lotro.logic.modifiers.Modifier;

/**
 * 3
 * Southron Sharpshooter
 * Fallen Realms	Minion • Man
 * 7	3	4
 * Southron. Archer.
 * While this minion is mounted, the minion archery total is +1.
 */
public class Card20_141 extends AbstractMinion {
    public Card20_141() {
        super(3, 7, 3, 4, Race.MAN, Culture.FALLEN_REALMS, "Southron Sharpshooter");
        addKeyword(Keyword.SOUTHRON);
        addKeyword(Keyword.ARCHER);
    }

    @Override
    public Modifier getAlwaysOnModifier(LotroGame game, final PhysicalCard self) {
        return new ArcheryTotalModifier(self, Side.SHADOW,
                new GameHasCondition(self, Filters.mounted), 1);
    }
}
