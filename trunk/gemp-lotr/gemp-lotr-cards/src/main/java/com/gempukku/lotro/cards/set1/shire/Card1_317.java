package com.gempukku.lotro.cards.set1.shire;

import com.gempukku.lotro.cards.AbstractAttachable;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.AddUntilStartOfPhaseModifierEffect;
import com.gempukku.lotro.cards.effects.SelfDiscardEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.modifiers.StrengthModifier;
import com.gempukku.lotro.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Free
 * Culture: Shire
 * Twilight Cost: 1
 * Type: Condition
 * Game Text: Tale. Bearer must be a Hobbit companion. Maneuver: Discard this condition to make each Hobbit companion
 * strength +2 until the regroup phase.
 */
public class Card1_317 extends AbstractAttachable {
    public Card1_317() {
        super(Side.FREE_PEOPLE, CardType.CONDITION, 1, Culture.SHIRE, null, "There and Back Again", null, true);
        addKeyword(Keyword.TALE);
    }

    @Override
    protected Filterable getValidTargetFilter(String playerId, LotroGame game, PhysicalCard self) {
        return Filters.and(CardType.COMPANION, Race.HOBBIT);
    }

    @Override
    protected List<? extends Action> getExtraPhaseActions(String playerId, LotroGame game, PhysicalCard self) {
        if (PlayConditions.canUseFPCardDuringPhase(game, Phase.MANEUVER, self)) {
            ActivateCardAction action = new ActivateCardAction(self);
            action.appendCost(new SelfDiscardEffect(self));
            action.appendEffect(
                    new AddUntilStartOfPhaseModifierEffect(
                            new StrengthModifier(self, Filters.and(CardType.COMPANION, Race.HOBBIT), 2), Phase.REGROUP));
            return Collections.singletonList(action);
        }
        return null;
    }
}
