package com.gempukku.lotro.cards.set1.elven;

import com.gempukku.lotro.cards.AbstractOldEvent;
import com.gempukku.lotro.cards.TriggerConditions;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.effects.AddUntilEndOfPhaseActionProxyEffect;
import com.gempukku.lotro.cards.effects.AddUntilEndOfPhaseModifierEffect;
import com.gempukku.lotro.cards.effects.DiscardCardAtRandomFromHandEffect;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Phase;
import com.gempukku.lotro.common.Race;
import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.AbstractActionProxy;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.RequiredTriggerAction;
import com.gempukku.lotro.logic.effects.ChooseActiveCardEffect;
import com.gempukku.lotro.logic.modifiers.StrengthModifier;
import com.gempukku.lotro.logic.timing.EffectResult;
import com.gempukku.lotro.logic.timing.results.SkirmishResult;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Set: The Fellowship of the Ring
 * Side: Free
 * Culture: Elven
 * Twilight Cost: 0
 * Type: Event
 * Game Text: Skirmish: Make an Elf strength +1. If a minion loses this skirmish to that Elf, that minion's owner
 * discards 2 cards at random from hand.
 */
public class Card1_029 extends AbstractOldEvent {
    public Card1_029() {
        super(Side.FREE_PEOPLE, Culture.ELVEN, "Ancient Enmity", Phase.SKIRMISH);
    }

    @Override
    public int getTwilightCost() {
        return 0;
    }

    @Override
    public PlayEventAction getPlayCardAction(String playerId, LotroGame game, final PhysicalCard self, int twilightModifier, boolean ignoreRoamingPenalty) {
        final PlayEventAction action = new PlayEventAction(self);
        action.appendEffect(
                new ChooseActiveCardEffect(self, playerId, "Choose an Elf", Race.ELF) {
                    @Override
                    protected void cardSelected(LotroGame game, final PhysicalCard elf) {
                        action.appendEffect(new AddUntilEndOfPhaseModifierEffect(new StrengthModifier(self, Filters.sameCard(elf), 1), Phase.SKIRMISH));
                        action.appendEffect(
                                new AddUntilEndOfPhaseActionProxyEffect(
                                        new AbstractActionProxy() {
                                            @Override
                                            public List<? extends RequiredTriggerAction> getRequiredAfterTriggers(LotroGame lotroGame, EffectResult effectResult) {
                                                if (TriggerConditions.winsSkirmish(lotroGame, effectResult, elf)) {
                                                    SkirmishResult skirmishResult = (SkirmishResult) effectResult;
                                                    Set<PhysicalCard> losers = skirmishResult.getLosers();
                                                    Set<String> opponents = new HashSet<String>();
                                                    for (PhysicalCard loser : losers)
                                                        opponents.add(loser.getOwner());

                                                    List<RequiredTriggerAction> actions = new LinkedList<RequiredTriggerAction>();
                                                    for (String opponent : opponents) {
                                                        RequiredTriggerAction action = new RequiredTriggerAction(self);
                                                        action.appendEffect(new DiscardCardAtRandomFromHandEffect(self, opponent, true));
                                                        action.appendEffect(new DiscardCardAtRandomFromHandEffect(self, opponent, true));
                                                        actions.add(action);
                                                    }
                                                    return actions;
                                                }
                                                return null;
                                            }
                                        }, Phase.SKIRMISH
                                ));
                    }
                }
        );
        return action;
    }
}
