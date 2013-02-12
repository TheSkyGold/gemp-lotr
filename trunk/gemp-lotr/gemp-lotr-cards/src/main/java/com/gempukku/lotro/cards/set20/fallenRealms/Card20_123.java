package com.gempukku.lotro.cards.set20.fallenRealms;

import com.gempukku.lotro.cards.AbstractPermanent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.TriggerConditions;
import com.gempukku.lotro.cards.effects.SelfDiscardEffect;
import com.gempukku.lotro.cards.effects.choose.ChooseAndPlayCardFromDiscardEffect;
import com.gempukku.lotro.cards.modifiers.ArcheryTotalModifier;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.modifiers.SpotCondition;
import com.gempukku.lotro.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * 2
 * •Fresh Reinforcements
 * Fallen Realms	Condition • Support Area
 * While you can spot an Easterling, the fellowship archery total is -1.
 * Response: If an Easterling is killed during the archery phase, discard this condition to play an Easterling from
 * your discard pile.
 */
public class Card20_123 extends AbstractPermanent {
    public Card20_123() {
        super(Side.SHADOW, 2, CardType.CONDITION, Culture.FALLEN_REALMS, Zone.SUPPORT, "Fresh Reinforcements", null, true);
    }

    @Override
    public Modifier getAlwaysOnModifier(LotroGame game, PhysicalCard self) {
        return new ArcheryTotalModifier(self, Side.FREE_PEOPLE, new SpotCondition(Keyword.EASTERLING), -1);
    }

    @Override
    public List<? extends ActivateCardAction> getOptionalInPlayAfterActions(String playerId, LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (TriggerConditions.forEachKilled(game, effectResult, Keyword.EASTERLING)
                && PlayConditions.isPhase(game, Phase.ARCHERY)
                && PlayConditions.canSelfDiscard(self, game)
                && PlayConditions.canPlayFromDiscard(playerId, game, Keyword.EASTERLING)) {
            ActivateCardAction action = new ActivateCardAction(self);
            action.appendCost(
                    new SelfDiscardEffect(self));
            action.appendEffect(
                    new ChooseAndPlayCardFromDiscardEffect(playerId, game, Keyword.EASTERLING));
            return Collections.singletonList(action);
        }
        return null;
    }
}
