package com.gempukku.lotro.cards.set2.site;

import com.gempukku.lotro.cards.AbstractSite;
import com.gempukku.lotro.common.Block;
import com.gempukku.lotro.common.Keyword;
import com.gempukku.lotro.common.Race;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.modifiers.RemoveKeywordModifier;

import java.util.Collections;
import java.util.List;

/**
 * Set: Mines of Moria
 * Twilight Cost: 3
 * Type: Site
 * Site: 4
 * Game Text: Plains. Uruk-hai are not roaming.
 */
public class Card2_119 extends AbstractSite {
    public Card2_119() {
        super("Hollin", Block.FELLOWSHIP, 4, 3, Direction.RIGHT);
        addKeyword(Keyword.PLAINS);
    }

    @Override
    public List<? extends Modifier> getAlwaysOnModifiers(LotroGame game, PhysicalCard self) {
        return Collections.singletonList(
                new RemoveKeywordModifier(self, Race.URUK_HAI, Keyword.ROAMING));
    }
}
