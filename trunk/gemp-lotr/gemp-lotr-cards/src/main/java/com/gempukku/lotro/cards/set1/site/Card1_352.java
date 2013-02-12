package com.gempukku.lotro.cards.set1.site;

import com.gempukku.lotro.cards.AbstractSite;
import com.gempukku.lotro.common.Block;
import com.gempukku.lotro.common.CardType;
import com.gempukku.lotro.common.Keyword;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.modifiers.StrengthModifier;

/**
 * Set: The Fellowship of the Ring
 * Twilight Cost: 3
 * Type: Site
 * Site: 6
 * Game Text: Forest. Sanctuary. Each ally whose home is site 6 is strength +3.
 */
public class Card1_352 extends AbstractSite {
    public Card1_352() {
        super("Lothlorien Woods", Block.FELLOWSHIP, 6, 3, Direction.LEFT);
        addKeyword(Keyword.FOREST);

    }

    @Override
    public Modifier getAlwaysOnModifier(LotroGame game, PhysicalCard self) {
        return new StrengthModifier(self, Filters.and(CardType.ALLY, Filters.isAllyHome(6, Block.FELLOWSHIP)), 3);
    }
}
