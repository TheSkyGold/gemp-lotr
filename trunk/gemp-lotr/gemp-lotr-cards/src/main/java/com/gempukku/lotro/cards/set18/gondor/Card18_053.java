package com.gempukku.lotro.cards.set18.gondor;

import com.gempukku.lotro.cards.AbstractAttachableFPPossession;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.AddUntilStartOfPhaseModifierEffect;
import com.gempukku.lotro.cards.effects.ExertCharactersEffect;
import com.gempukku.lotro.cards.effects.choose.ChooseAndDiscardCardsFromPlayEffect;
import com.gempukku.lotro.cards.modifiers.OverwhelmedByMultiplierModifier;
import com.gempukku.lotro.cards.modifiers.ResistanceModifier;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.modifiers.KeywordModifier;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.modifiers.StrengthModifier;
import com.gempukku.lotro.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Treachery & Deceit
 * Side: Free
 * Culture: Gondor
 * Twilight Cost: 0
 * Type: Possession
 * Resistance: +1
 * Game Text: Bearer must be a [GONDOR] Man. Maneuver: Exert bearer and discard a follower from play to make bearer
 * strength +4 until the regroup phase (if bearer is Boromir, he is also defender +1 and cannot be overwhelmed unless
 * his strength is tripled).
 */
public class Card18_053 extends AbstractAttachableFPPossession {
    public Card18_053() {
        super(0, 0, 0, Culture.GONDOR, null, "Horn of Boromir", "The Great Horn", true);
    }

    @Override
    protected List<? extends Modifier> getNonBasicStatsModifiers(PhysicalCard self) {
        return Collections.singletonList(
                new ResistanceModifier(self, Filters.hasAttached(self), 1));
    }

    @Override
    protected Filterable getValidTargetFilter(String playerId, LotroGame game, PhysicalCard self) {
        return Filters.and(Culture.GONDOR, Race.MAN);
    }

    @Override
    protected List<? extends Action> getExtraInPlayPhaseActions(String playerId, LotroGame game, PhysicalCard self) {
        if (PlayConditions.canUseFPCardDuringPhase(game, Phase.MANEUVER, self)
                && PlayConditions.canExert(self, game, Filters.hasAttached(self))
                && PlayConditions.canDiscardFromPlay(self, game, CardType.FOLLOWER)) {
            ActivateCardAction action = new ActivateCardAction(self);
            action.appendCost(
                    new ExertCharactersEffect(action, self, self.getAttachedTo()));
            action.appendCost(
                    new ChooseAndDiscardCardsFromPlayEffect(action, playerId, 1, 1, CardType.FOLLOWER));
            action.appendEffect(
                    new AddUntilStartOfPhaseModifierEffect(
                            new StrengthModifier(self, self.getAttachedTo(), 4), Phase.REGROUP));
            if (PlayConditions.canSpot(game, self.getAttachedTo(), Filters.boromir)) {
                action.appendEffect(
                        new AddUntilStartOfPhaseModifierEffect(
                                new KeywordModifier(self, self.getAttachedTo(), Keyword.DEFENDER, 1), Phase.REGROUP));
                action.appendEffect(
                        new AddUntilStartOfPhaseModifierEffect(
                                new OverwhelmedByMultiplierModifier(self, self.getAttachedTo(), 3), Phase.REGROUP));
            }
            return Collections.singletonList(action);
        }
        return null;
    }
}
