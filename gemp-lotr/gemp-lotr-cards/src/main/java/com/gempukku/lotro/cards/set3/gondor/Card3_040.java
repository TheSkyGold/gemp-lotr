package com.gempukku.lotro.cards.set3.gondor;

import com.gempukku.lotro.cards.AbstractPermanent;
import com.gempukku.lotro.cards.TriggerConditions;
import com.gempukku.lotro.common.CardType;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.common.Zone;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.OptionalTriggerAction;
import com.gempukku.lotro.logic.effects.ChooseAndHealCharactersEffect;
import com.gempukku.lotro.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Realms of Elf-lords
 * Side: Free
 * Culture: Gondor
 * Twilight Cost: 1
 * Type: Condition
 * Game Text: Plays to your support area. You may heal a [GONDOR] companion at the end of each turn during which
 * no companion or ally lost a skirmish.
 */
public class Card3_040 extends AbstractPermanent {
    public Card3_040() {
        super(Side.FREE_PEOPLE, 1, CardType.CONDITION, Culture.GONDOR, Zone.SUPPORT, "Citadel of Minas Tirith");
    }

    @Override
    public List<OptionalTriggerAction> getOptionalAfterTriggers(String playerId, LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (TriggerConditions.endOfTurn(game, effectResult)
                && !game.getActionsEnvironment().hasLostSkirmishThisTurn(game, Filters.or(CardType.COMPANION, CardType.ALLY))) {
            OptionalTriggerAction action = new OptionalTriggerAction(self);
            action.appendEffect(
                    new ChooseAndHealCharactersEffect(action, playerId, Culture.GONDOR, CardType.COMPANION));
            return Collections.singletonList(action);
        }
        return null;
    }
}
