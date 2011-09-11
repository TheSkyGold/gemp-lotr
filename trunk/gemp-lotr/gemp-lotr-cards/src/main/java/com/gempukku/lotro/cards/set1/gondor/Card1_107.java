package com.gempukku.lotro.cards.set1.gondor;

import com.gempukku.lotro.cards.AbstractAttachableFPPossession;
import com.gempukku.lotro.cards.modifiers.ArcheryTotalModifier;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Keyword;
import com.gempukku.lotro.common.Race;
import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.modifiers.Modifier;

/**
 * Set: The Fellowship of the Ring
 * Side: Free
 * Culture: Gondor
 * Twilight Cost: 1
 * Type: Possession • Shield
 * Game Text: Bearer must be a Man. The minion archery total is -1.
 */
public class Card1_107 extends AbstractAttachableFPPossession {
    public Card1_107() {
        super(1, Culture.GONDOR, Keyword.SHIELD, "Great Shield");
    }

    @Override
    protected Filter getValidTargetFilter(String playerId, LotroGame game, PhysicalCard self) {
        return Filters.race(Race.MAN);
    }

    @Override
    public Modifier getAlwaysOnEffect(PhysicalCard self) {
        return new ArcheryTotalModifier(self, Side.SHADOW, -1);
    }
}
