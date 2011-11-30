package com.gempukku.lotro.logic.timing.rules;

import com.gempukku.lotro.game.AbstractActionProxy;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.game.state.actions.DefaultActionsEnvironment;
import com.gempukku.lotro.logic.actions.RequiredTriggerAction;
import com.gempukku.lotro.logic.effects.KillEffect;
import com.gempukku.lotro.logic.timing.EffectResult;
import com.gempukku.lotro.logic.timing.actions.ResolveSkirmishDamageAction;
import com.gempukku.lotro.logic.timing.results.NormalSkirmishResult;
import com.gempukku.lotro.logic.timing.results.OverwhelmSkirmishResult;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResolveSkirmishRule {
    private LotroGame _lotroGame;
    private DefaultActionsEnvironment _actionsEnvironment;

    public ResolveSkirmishRule(LotroGame lotroGame, DefaultActionsEnvironment actionsEnvironment) {
        _lotroGame = lotroGame;
        _actionsEnvironment = actionsEnvironment;
    }

    public void applyRule() {
        _actionsEnvironment.addAlwaysOnActionProxy(
                new AbstractActionProxy("Resolve skirmish") {
                    @Override
                    public List<? extends RequiredTriggerAction> getRequiredAfterTriggers(LotroGame lotroGame, EffectResult effectResult) {
                        if (effectResult.getType() == EffectResult.Type.SKIRMISH_FINISHED_NORMALLY) {
                            NormalSkirmishResult skirmishResult = (NormalSkirmishResult) effectResult;
                            ResolveSkirmishDamageAction action = new ResolveSkirmishDamageAction(skirmishResult);
                            return Collections.singletonList(action);
                        } else if (effectResult.getType() == EffectResult.Type.SKIRMISH_FINISHED_WITH_OVERWHELM) {
                            OverwhelmSkirmishResult skirmishResult = (OverwhelmSkirmishResult) effectResult;
                            Set<PhysicalCard> losers = new HashSet<PhysicalCard>(skirmishResult.getInSkirmishLosers());

                            RequiredTriggerAction action = new RequiredTriggerAction(null);
                            action.setText("Resolving skirmish");
                            action.appendEffect(new KillEffect(losers, KillEffect.Cause.OVERWHELM));

                            return Collections.singletonList(action);
                        }
                        return null;
                    }
                }
        );
    }
}
