package com.gempukku.lotro.cards.set0.shire;

import com.gempukku.lotro.cards.AbstractCompanion;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.modifiers.AbstractModifier;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.modifiers.ModifierEffect;
import com.gempukku.lotro.logic.modifiers.ModifiersQuerying;

import java.util.Collections;
import java.util.List;

/**
 * Set: Promotional
 * Side: Free
 * Culture: Shire
 * Twilight Cost: 0
 * Type: Companion • Hobbit
 * Strength: 3
 * Vitality: 4
 * Resistance: 10
 * Signet: Gandalf
 * Game Text: Ring-bearer (Resistance 10). The twilight cost of each artifact, possession, and [SHIRE] tale played
 * on Frodo is -1.
 */
public class Card0_067 extends AbstractCompanion {
    public Card0_067() {
        super(0, 3, 4, 10, Culture.SHIRE, Race.HOBBIT, Signet.GANDALF, "Frodo", true);
        addKeyword(Keyword.CAN_START_WITH_RING);
    }

    @Override
    public List<? extends Modifier> getAlwaysOnModifiers(LotroGame game, final PhysicalCard self) {
        return Collections.singletonList(
                new AbstractModifier(self, "The cost of each artifact, possession, and [SHIRE] tale played on Frodo  is -1.", Filters.or(CardType.ARTIFACT, CardType.POSSESSION, Filters.and(Culture.SHIRE, Keyword.TALE)), ModifierEffect.TWILIGHT_COST_MODIFIER) {
                    @Override
                    public int getPlayOnTwilightCostModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, PhysicalCard target) {
                        if (target == self)
                            return -1;
                        return 0;
                    }
                });
    }
}
