package com.gempukku.lotro.cards.set3.gondor;

import com.gempukku.lotro.cards.AbstractResponseOldEvent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.TriggerConditions;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.effects.AddUntilStartOfPhaseModifierEffect;
import com.gempukku.lotro.cards.effects.choose.ChooseAndExertCharactersEffect;
import com.gempukku.lotro.common.CardType;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Phase;
import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.modifiers.TwilightCostModifier;
import com.gempukku.lotro.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Realms of Elf-lords
 * Side: Free
 * Culture: Gondor
 * Twilight Cost: 0
 * Type: Event
 * Game Text: Response: If the fellowship moves in the regroup phase, exert a [GONDOR] companion twice to make each
 * minion's twilight cost +1 until the next regroup phase.
 */
public class Card3_048 extends AbstractResponseOldEvent {
    public Card3_048() {
        super(Side.FREE_PEOPLE, Culture.GONDOR, "We Must Go Warily");
    }

    @Override
    public int getTwilightCost() {
        return 0;
    }

    @Override
    public List<PlayEventAction> getOptionalAfterActions(String playerId, LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (TriggerConditions.moves(game, effectResult)
                && PlayConditions.isPhase(game, Phase.REGROUP)
                && checkPlayRequirements(playerId, game, self, 0, 0, false, false)
                && PlayConditions.canExert(self, game, 2, Culture.GONDOR, CardType.COMPANION)) {
            PlayEventAction action = new PlayEventAction(self);
            action.appendCost(
                    new ChooseAndExertCharactersEffect(action, playerId, 1, 1, 2, Culture.GONDOR, CardType.COMPANION));
            action.appendEffect(
                    new AddUntilStartOfPhaseModifierEffect(
                            new TwilightCostModifier(self, CardType.MINION, 1), Phase.REGROUP));
            return Collections.singletonList(action);
        }
        return null;
    }
}
