package com.gempukku.lotro.cards.modifiers;

import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Filterable;
import com.gempukku.lotro.common.Keyword;
import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.logic.modifiers.AbstractModifier;
import com.gempukku.lotro.logic.modifiers.ModifierEffect;
import com.gempukku.lotro.logic.modifiers.ModifiersQuerying;

public class AllyParticipatesInArcheryFireAndSkirmishesModifier extends AbstractModifier {
    private PhysicalCard _source;

    public AllyParticipatesInArcheryFireAndSkirmishesModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "Can participate in archery and skirmishes", affectFilter, ModifierEffect.PRESENCE_MODIFIER);
        _source = source;
    }

    @Override
    public boolean isAllyParticipateInArcheryFire(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return true;
    }

    @Override
    public boolean isParticipateInSkirmishes(GameState gameState, Side sidePlayer, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        boolean unhasty = modifiersQuerying.hasKeyword(gameState, card, Keyword.UNHASTY);
        return sidePlayer == Side.SHADOW
                || !unhasty || _source.getBlueprint().getCulture() == Culture.GANDALF;
    }
}
