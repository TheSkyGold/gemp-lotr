package com.gempukku.lotro.cards.set1.isengard;

import com.gempukku.lotro.cards.AbstractMinion;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Keyword;

/**
 * Set: The Fellowship of the Ring
 * Side: Shadow
 * Culture: Isengard
 * Twilight Cost: 4
 * Type: Minion • Uruk-Hai
 * Strength: 9
 * Vitality: 3
 * Site: 5
 * Game Text: Damage +1.
 */
public class Card1_158 extends AbstractMinion {
    public Card1_158() {
        super(4, 9, 3, 5, Keyword.URUK_HAI, Culture.ISENGARD, "Uruk-hai Raiding Party");
        addKeyword(Keyword.DAMAGE);
    }
}
