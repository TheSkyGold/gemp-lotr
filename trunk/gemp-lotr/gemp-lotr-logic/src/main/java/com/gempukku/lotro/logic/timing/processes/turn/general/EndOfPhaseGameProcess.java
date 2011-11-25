package com.gempukku.lotro.logic.timing.processes.turn.general;

import com.gempukku.lotro.common.Phase;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.game.state.actions.DefaultActionsEnvironment;
import com.gempukku.lotro.logic.actions.SystemQueueAction;
import com.gempukku.lotro.logic.effects.TriggeringResultEffect;
import com.gempukku.lotro.logic.modifiers.ModifiersLogic;
import com.gempukku.lotro.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.lotro.logic.timing.processes.GameProcess;
import com.gempukku.lotro.logic.timing.results.EndOfPhaseResult;

public class EndOfPhaseGameProcess implements GameProcess {
    private Phase _phase;
    private GameProcess _followingGameProcess;

    public EndOfPhaseGameProcess(Phase phase, GameProcess followingGameProcess) {
        _phase = phase;
        _followingGameProcess = followingGameProcess;
    }

    @Override
    public void process(LotroGame game) {
        SystemQueueAction action = new SystemQueueAction() {
            @Override
            public String getText(LotroGame game) {
                return "End of " + _phase + " phase";
            }
        };
        action.appendEffect(
                new TriggeringResultEffect(null, new EndOfPhaseResult(_phase), "End of " + _phase + " phase"));
        action.appendEffect(
                new AbstractSuccessfulEffect() {
                    @Override
                    public String getText(LotroGame game) {
                        return null;
                    }

                    @Override
                    public Type getType() {
                        return null;
                    }

                    @Override
                    public void playEffect(LotroGame game) {
                        ((ModifiersLogic) game.getModifiersEnvironment()).removeEndOfPhase(_phase);
                        ((DefaultActionsEnvironment) game.getActionsEnvironment()).removeEndOfPhaseActionProxies(_phase);
                    }
                });
        game.getActionsEnvironment().addActionToStack(action);
    }

    @Override
    public GameProcess getNextProcess() {
        return _followingGameProcess;
    }
}
