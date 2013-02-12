package com.gempukku.lotro.cards.set1.elven;

import com.gempukku.lotro.cards.AbstractCompanion;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Keyword;
import com.gempukku.lotro.common.Race;
import com.gempukku.lotro.common.Signet;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.game.state.Skirmish;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.modifiers.ModifiersQuerying;
import com.gempukku.lotro.logic.modifiers.StrengthModifier;

/**
 * Set: The Fellowship of the Ring
 * Side: Free
 * Culture: Elven
 * Twilight Cost: 2
 * Type: Companion � Elf
 * Strength: 6
 * Vitality: 3
 * Resistance: 6
 * Signet: Gandalf
 * Game Text: Archer. While skirmishing a Nazgul, Legolas is strength +3.
 */
public class Card1_051 extends AbstractCompanion {
    public Card1_051() {
        super(2, 6, 3, 6, Culture.ELVEN, Race.ELF, Signet.GANDALF, "Legolas", "Prince of Mirkwood", true);
        addKeyword(Keyword.ARCHER);
    }

    @Override
    public Modifier getAlwaysOnModifier(LotroGame game, final PhysicalCard self) {
        return new StrengthModifier(self,
                Filters.and(
                        self,
                        Filters.inSkirmish,
                        new Filter() {
                            @Override
                            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                                Skirmish activeSkirmish = gameState.getSkirmish();
                                return Filters.filter(activeSkirmish.getShadowCharacters(), gameState, modifiersQuerying, Race.NAZGUL).size() > 0;
                            }
                        }), 3);
    }
}
