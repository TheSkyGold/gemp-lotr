package com.gempukku.lotro.cards.set1.moria;

import com.gempukku.lotro.cards.AbstractMinion;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Keyword;
import com.gempukku.lotro.common.Race;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.game.state.Skirmish;
import com.gempukku.lotro.logic.modifiers.KeywordModifier;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.modifiers.ModifiersQuerying;

import java.util.Set;

/**
 * Set: The Fellowship of the Ring
 * Side: Shadow
 * Culture: Moria
 * Twilight Cost: 1
 * Type: Minion • Orc
 * Strength: 5
 * Vitality: 1
 * Site: 4
 * Game Text: This minion is damage +2 while in the same skirmish as another [MORIA] Orc.
 */
public class Card1_174 extends AbstractMinion {
    public Card1_174() {
        super(1, 5, 1, 4, Race.ORC, Culture.MORIA, "Goblin Backstabber");
    }

    @Override
    public Modifier getAlwaysOnModifier(LotroGame game, final PhysicalCard self) {
        return new KeywordModifier(self,
                Filters.and(
                        self,
                        new Filter() {
                            @Override
                            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                                Skirmish skirmish = gameState.getSkirmish();
                                if (skirmish != null) {
                                    Set<PhysicalCard> shadowChars = skirmish.getShadowCharacters();
                                    if (shadowChars.contains(self)
                                            && Filters.filter(shadowChars, gameState, modifiersQuerying, Culture.MORIA, Race.ORC, Filters.not(self)).size() > 0)
                                        return true;
                                }
                                return false;
                            }
                        }),
                Keyword.DAMAGE, 2);
    }
}
