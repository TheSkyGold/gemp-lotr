package com.gempukku.lotro.cards.set20.rohan;

import com.gempukku.lotro.cards.AbstractPermanent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.TriggerConditions;
import com.gempukku.lotro.cards.actions.PlayPermanentAction;
import com.gempukku.lotro.cards.effects.AddUntilStartOfPhaseModifierEffect;
import com.gempukku.lotro.cards.effects.ChoiceEffect;
import com.gempukku.lotro.cards.effects.choose.ChooseAndExertCharactersEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.RequiredTriggerAction;
import com.gempukku.lotro.logic.modifiers.StrengthModifier;
import com.gempukku.lotro.logic.timing.Effect;
import com.gempukku.lotro.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 2
 * •Horn of Helm Hammerhand
 * Rohan	Artifact • Support Area
 * To play exert Theoden or two Valiant [Rohan] Men.
 * Each time you play a [Rohan] fortification, each mounted [Rohan] man is strength +1 until the regroup phase.
 */
public class Card20_332 extends AbstractPermanent {
    public Card20_332() {
        super(Side.FREE_PEOPLE, 2, CardType.ARTIFACT, Culture.ROHAN, Zone.SUPPORT, "Horn of Helm Hammerhand", null, true);
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int withTwilightRemoved, int twilightModifier, boolean ignoreRoamingPenalty, boolean ignoreCheckingDeadPile) {
        return super.checkPlayRequirements(playerId, game, self, withTwilightRemoved, twilightModifier, ignoreRoamingPenalty, ignoreCheckingDeadPile)
                && (PlayConditions.canExert(self, game, Filters.name(Names.theoden)) || PlayConditions.canExert(self, game, 1, 2, Culture.ROHAN, Race.MAN, Keyword.VALIANT));
    }

    @Override
    public PlayPermanentAction getPlayCardAction(String playerId, LotroGame game, PhysicalCard self, int twilightModifier, boolean ignoreRoamingPenalty) {
        final PlayPermanentAction action = super.getPlayCardAction(playerId, game, self, twilightModifier, ignoreRoamingPenalty);
        List<Effect> possibleCosts = new LinkedList<Effect>();
        possibleCosts.add(
                new ChooseAndExertCharactersEffect(action, playerId, 1, 1, Filters.name(Names.theoden)) {
                    @Override
                    public String getText(LotroGame game) {
                        return "Exert "+Names.theoden;
                    }
                });
        possibleCosts.add(
                new ChooseAndExertCharactersEffect(action, playerId, 2, 2, Culture.ROHAN, Race.MAN, Keyword.VALIANT) {
                    @Override
                    public String getText(LotroGame game) {
                        return "Exert two Valiant ROHAN Man";
                    }
                });
        action.appendCost(
                new ChoiceEffect(action, playerId, possibleCosts));
        return action;
    }

    @Override
    public List<RequiredTriggerAction> getRequiredAfterTriggers(LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (TriggerConditions.played(game, effectResult, Filters.owner(self.getOwner()), Culture.ROHAN, Keyword.FORTIFICATION)) {
            RequiredTriggerAction action = new RequiredTriggerAction(self);
            final Collection<PhysicalCard> affectedCards = Filters.filterActive(game.getGameState(), game.getModifiersQuerying(), Culture.ROHAN, Race.MAN, Filters.mounted);
            action.appendEffect(
                    new AddUntilStartOfPhaseModifierEffect(
                            new StrengthModifier(self, Filters.in(affectedCards), 1), Phase.REGROUP));
            return Collections.singletonList(action);
        }
        return null;
    }
}
