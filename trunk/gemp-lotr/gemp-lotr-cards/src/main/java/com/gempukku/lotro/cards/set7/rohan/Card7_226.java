package com.gempukku.lotro.cards.set7.rohan;

import com.gempukku.lotro.cards.AbstractCompanion;
import com.gempukku.lotro.common.CardType;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Keyword;
import com.gempukku.lotro.common.Race;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.modifiers.KeywordModifier;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.modifiers.SpotCondition;
import com.gempukku.lotro.logic.modifiers.StrengthModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Return of the King
 * Side: Free
 * Culture: Rohan
 * Twilight Cost: 2
 * Type: Companion • Man
 * Strength: 5
 * Vitality: 3
 * Resistance: 6
 * Game Text: While you can spot another [ROHAN] Man and this companion is skirmishing a wounded minion, this companion
 * is strength +2 and damage +1.
 */
public class Card7_226 extends AbstractCompanion {
    public Card7_226() {
        super(2, 5, 3, Culture.ROHAN, Race.MAN, null, "Enraged Horseman");
    }

    @Override
    public List<? extends Modifier> getAlwaysOnModifiers(LotroGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(
                new StrengthModifier(self,
                        Filters.and(self, Filters.inSkirmishAgainst(CardType.MINION, Filters.wounded)),
                        new SpotCondition(Filters.not(self), Culture.ROHAN, Race.MAN), 2));
        modifiers.add(
                new KeywordModifier(self,
                        Filters.and(self, Filters.inSkirmishAgainst(CardType.MINION, Filters.wounded)),
                        new SpotCondition(Filters.not(self), Culture.ROHAN, Race.MAN), Keyword.DAMAGE, 1));
        return modifiers;
    }
}
