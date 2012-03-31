package com.gempukku.lotro.cards.set1.moria;

import com.gempukku.lotro.cards.AbstractPermanent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.SelfDiscardEffect;
import com.gempukku.lotro.cards.effects.choose.ChooseAndAddUntilEOPStrengthBonusEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.actions.RequiredTriggerAction;
import com.gempukku.lotro.logic.effects.AddTwilightEffect;
import com.gempukku.lotro.logic.timing.Action;
import com.gempukku.lotro.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Shadow
 * Culture: Moria
 * Twilight Cost: 2
 * Type: Condition
 * Game Text: Plays to your support area. When the fellowship moves to site 4 or 5, add (2) for each Dwarf companion.
 * Skirmish: Discard this condition to make your [MORIA] Orc strength +2.
 */
public class Card1_188 extends AbstractPermanent {
    public Card1_188() {
        super(Side.SHADOW, 2, CardType.CONDITION, Culture.MORIA, Zone.SUPPORT, "The Long Dark", null, true);
    }

    @Override
    public List<? extends Action> getExtraPhaseActions(String playerId, LotroGame game, final PhysicalCard self) {
        if (PlayConditions.canUseShadowCardDuringPhase(game, Phase.SKIRMISH, self, 0)) {
            final ActivateCardAction action = new ActivateCardAction(self);
            action.appendCost(
                    new SelfDiscardEffect(self));
            action.appendEffect(
                    new ChooseAndAddUntilEOPStrengthBonusEffect(action, self, playerId, 2, Filters.owner(playerId), Culture.MORIA, Race.ORC));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    public List<RequiredTriggerAction> getRequiredAfterTriggers(LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (effectResult.getType() == EffectResult.Type.WHEN_MOVE_TO
                && (game.getGameState().getCurrentSiteNumber() == 4 || game.getGameState().getCurrentSiteNumber() == 5) && game.getGameState().getCurrentSiteBlock() == Block.FELLOWSHIP) {
            RequiredTriggerAction action = new RequiredTriggerAction(self);
            int dwarfCompanions = Filters.countActive(game.getGameState(), game.getModifiersQuerying(), Race.DWARF, CardType.COMPANION);
            action.appendEffect(new AddTwilightEffect(self, dwarfCompanions * 2));
            return Collections.singletonList(action);
        }
        return null;
    }
}
