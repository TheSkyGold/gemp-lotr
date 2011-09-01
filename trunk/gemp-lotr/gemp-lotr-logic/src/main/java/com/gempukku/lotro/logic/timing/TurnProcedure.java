package com.gempukku.lotro.logic.timing;

import com.gempukku.lotro.communication.UserFeedback;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.PlayOrder;
import com.gempukku.lotro.logic.actions.CostToEffectAction;
import com.gempukku.lotro.logic.decisions.AbstractAwaitingDecision;
import com.gempukku.lotro.logic.decisions.ActionsSelectionDecision;
import com.gempukku.lotro.logic.decisions.AwaitingDecisionType;
import com.gempukku.lotro.logic.decisions.DecisionResultInvalidException;
import com.gempukku.lotro.logic.effects.PlayoutDecisionEffect;
import com.gempukku.lotro.logic.timing.actions.SystemAction;
import com.gempukku.lotro.logic.timing.processes.GameProcess;
import com.gempukku.lotro.logic.timing.processes.pregame.BiddingGameProcess;

import java.util.*;

// Action generates multiple Effects, both costs and result of an action are Effects.

// Decision is also an Effect.
public class TurnProcedure {
    private UserFeedback _userFeedback;
    private LotroGame _game;
    private ActionStack _actionStack;
    private GameProcess _gameProcess;
    private boolean _playedGameProcess;

    public TurnProcedure(LotroGame lotroGame, Set<String> players, final UserFeedback userFeedback, ActionStack actionStack, final PlayerOrderFeedback playerOrderFeedback) {
        _userFeedback = userFeedback;
        _game = lotroGame;
        _actionStack = actionStack;

        _gameProcess = new BiddingGameProcess(players, lotroGame, playerOrderFeedback);
    }

    public void carryOutPendingActionsUntilDecisionNeeded() {
        while (!_userFeedback.hasPendingDecisions() && _game.getGameState().getWinnerPlayerId() == null) {
            if (_actionStack.isEmpty()) {
                if (_playedGameProcess) {
                    _gameProcess = _gameProcess.getNextProcess();
                    _playedGameProcess = false;
                } else {
                    _gameProcess.process();
                    _playedGameProcess = true;
                }
            } else {
                Effect effect = _actionStack.getNextEffect();
                if (effect != null) {
                    EffectResult effectResult = effect.getRespondableResult();
                    if (effectResult != null) {
                        _actionStack.stackAction(new PlayOutRecognizableEffect(effect));
                    } else {
                        if (effect.canPlayEffect(_game))
                            effect.playEffect(_game);
                        else
                            effect.setFailed();
                    }
                }
            }
        }
    }

    private class PlayOutRecognizableEffect extends SystemAction {
        private Effect _effect;
        private boolean _checkedPlayability;
        private boolean _checkedIsAboutToRequiredResponses;
        private boolean _checkedIsAboutToOptionalResponses;
        private boolean _effectPlayed;
        private boolean _checkedRequiredWhenResponses;
        private boolean _checkedOptionalWhenResponses;

        private PlayOutRecognizableEffect(Effect effect) {
            _effect = effect;
        }

        @Override
        public Effect nextEffect() {
            if (_effect.isCancelled())
                return null;

            if (!_checkedPlayability) {
                boolean canBePlayed = _effect.canPlayEffect(_game);
                _checkedPlayability = true;
                if (!canBePlayed) {
                    _effect.setFailed();
                    // This finished off this Action
                    return null;
                }
            }
            if (!_checkedIsAboutToRequiredResponses) {
                _checkedIsAboutToRequiredResponses = true;
                EffectResult effectResult = _effect.getRespondableResult();
                List<Action> requiredIsAboutToResponses = _game.getActionsEnvironment().getRequiredBeforeTriggers(_effect, effectResult);
                if (requiredIsAboutToResponses.size() > 0) {
                    CostToEffectAction action = new CostToEffectAction(null, null, null);
                    action.addEffect(new PlayoutAllActionsIfEffectNotCancelledEffect(action, _effect, requiredIsAboutToResponses));
                    return new StackActionEffect(action);
                }
            }
            if (!_checkedIsAboutToOptionalResponses) {
                _checkedIsAboutToOptionalResponses = true;
                EffectResult effectResult = _effect.getRespondableResult();
                return new StackActionEffect(new PlayoutOptionalIsAboutToResponsesAction(_effect, effectResult));
            }
            if (!_effectPlayed) {
                if (_effect.canPlayEffect(_game))
                    _effect.playEffect(_game);
                else {
                    _effect.setFailed();
                    return null;
                }
                _effectPlayed = true;
            }
            if (!_checkedRequiredWhenResponses) {
                _checkedRequiredWhenResponses = true;
                EffectResult effectResult = _effect.getRespondableResult();
                List<Action> requiredResponses = _game.getActionsEnvironment().getRequiredAfterTriggers(effectResult);
                if (requiredResponses.size() > 0) {
                    CostToEffectAction action = new CostToEffectAction(null, null, null);
                    action.addEffect(new PlayoutAllActionsIfEffectNotCancelledEffect(action, _effect, requiredResponses));
                    return new StackActionEffect(action);
                }
            }
            if (!_checkedOptionalWhenResponses) {
                _checkedOptionalWhenResponses = true;
                EffectResult effectResult = _effect.getRespondableResult();
                Map<String, List<Action>> optionalWhenResponses = _game.getActionsEnvironment().getOptionalAfterTriggers(_game.getGameState().getPlayerOrder().getAllPlayers(), effectResult);
                CostToEffectAction action = new CostToEffectAction(null, null, null);
                action.addEffect(
                        new PlayoutOptionalAfterResponsesEffect(action, optionalWhenResponses, _game.getGameState().getPlayerOrder().getCounterClockwisePlayOrder(_game.getGameState().getCurrentPlayerId(), true), 0, effectResult));
                return new StackActionEffect(action);
            }

            return null;
        }
    }

    private class PlayoutOptionalAfterResponsesEffect extends UnrespondableEffect {
        private CostToEffectAction _action;
        private Map<String, List<Action>> _actionMap = new HashMap<String, List<Action>>();
        private PlayOrder _playOrder;
        private int _passCount;
        private EffectResult _effectResult;

        private PlayoutOptionalAfterResponsesEffect(CostToEffectAction action, Map<String, List<Action>> actionMap, PlayOrder playOrder, int passCount, EffectResult effectResult) {
            _action = action;
            _actionMap = actionMap;
            _playOrder = playOrder;
            _passCount = passCount;
            _effectResult = effectResult;
        }

        @Override
        public void playEffect(LotroGame game) {
            final String activePlayer = _playOrder.getNextPlayer();
            List<Action> possibleActions = _actionMap.get(activePlayer);
            possibleActions.addAll(_game.getActionsEnvironment().getOptionalAfterActions(activePlayer, _effectResult));

            if (possibleActions.size() > 0) {
                _game.getUserFeedback().sendAwaitingDecision(activePlayer,
                        new ActionsSelectionDecision(1, "Choose action to play or DONE", possibleActions, true) {
                            @Override
                            public void decisionMade(String result) throws DecisionResultInvalidException {
                                Action action = getSelectedAction(result);
                                if (action != null) {
                                    _game.getActionsEnvironment().addActionToStack(action);
                                    _actionMap.get(activePlayer).remove(action);
                                    _action.addEffect(new PlayoutOptionalAfterResponsesEffect(_action, _actionMap, _playOrder, 0, _effectResult));
                                } else {
                                    if ((_passCount + 1) < _playOrder.getPlayerCount()) {
                                        _action.addEffect(new PlayoutOptionalAfterResponsesEffect(_action, _actionMap, _playOrder, _passCount + 1, _effectResult));
                                    }
                                }
                            }
                        });
            } else {
                if ((_passCount + 1) < _playOrder.getPlayerCount()) {
                    _action.addEffect(new PlayoutOptionalAfterResponsesEffect(_action, _actionMap, _playOrder, _passCount + 1, _effectResult));
                }
            }
        }
    }

    private class PlayoutAllActionsIfEffectNotCancelledEffect extends UnrespondableEffect {
        private CostToEffectAction _action;
        private Effect _effect;
        private List<Action> _actions;

        private PlayoutAllActionsIfEffectNotCancelledEffect(CostToEffectAction action, Effect effect, List<Action> actions) {
            _action = action;
            _effect = effect;
            _actions = actions;
        }

        @Override
        public void playEffect(LotroGame game) {
            if (!_effect.isCancelled()) {
                if (_actions.size() == 1) {
                    _game.getActionsEnvironment().addActionToStack(_actions.get(0));
                } else {
                    _game.getUserFeedback().sendAwaitingDecision(_game.getGameState().getCurrentPlayerId(),
                            new ActionsSelectionDecision(1, "Choose action to play now", _actions, false) {
                                @Override
                                public void decisionMade(String result) throws DecisionResultInvalidException {
                                    Action action = getSelectedAction(result);
                                    _game.getActionsEnvironment().addActionToStack(action);
                                    _actions.remove(action);
                                    _action.addEffect(new PlayoutAllActionsIfEffectNotCancelledEffect(_action, _effect, _actions));
                                }
                            });
                }
            }
        }
    }

    private class ChooseActionsOrderDecision extends AbstractAwaitingDecision {
        private List<Action> _actions;

        private ChooseActionsOrderDecision(List<Action> actions) {
            super(1, "Choose order of these actions", AwaitingDecisionType.ACTION_ORDER);
            _actions = actions;
        }

        @Override
        public void decisionMade(String result) throws DecisionResultInvalidException {
            String[] split = result.split(" ");
            if (split.length != _actions.size())
                throw new DecisionResultInvalidException();
            List<String> order = Arrays.asList(split);
            for (int i = 0; i < _actions.size(); i++)
                if (!order.contains(String.valueOf(i)))
                    throw new DecisionResultInvalidException();

            Collections.reverse(order);

            // We need to stack them in reverse order, so that we can play them out from top to bottom
            for (String index : order)
                _actionStack.stackAction(_actions.get(Integer.parseInt(index)));
        }
    }

    private class StackActionEffect extends UnrespondableEffect {
        private Action _action;

        private StackActionEffect(Action action) {
            _action = action;
        }

        @Override
        public void playEffect(LotroGame game) {
            _actionStack.stackAction(_action);
        }
    }

    private class PlayoutOptionalIsAboutToResponsesAction extends SystemAction {
        private Effect _effect;
        private EffectResult _effectResult;
        private PlayOrder _playOrder;
        private int _consecutivePassesCount = 0;

        private PlayoutOptionalIsAboutToResponsesAction(Effect effect, EffectResult effectResult) {
            _effect = effect;
            _effectResult = effectResult;
            _playOrder = _game.getGameState().getPlayerOrder().getCounterClockwisePlayOrder(_game.getGameState().getCurrentPlayerId(), true);
        }

        @Override
        public Effect nextEffect() {
            if (_effect.isCancelled())
                return null;

            if (_consecutivePassesCount == _playOrder.getPlayerCount())
                return null;

            String player = _playOrder.getNextPlayer();
            List<Action> actions = _game.getActionsEnvironment().getOptionalBeforeActions(player, _effect, _effectResult);
            return new PlayoutDecisionEffect(_userFeedback, player, new ActionsSelectionDecision(1, "Choose action to play or DONE", actions, true) {
                @Override
                public void decisionMade(String result) throws DecisionResultInvalidException {
                    Action action = getSelectedAction(result);
                    if (action == null)
                        _consecutivePassesCount++;
                    else {
                        _consecutivePassesCount = 0;
                        _actionStack.stackAction(action);
                    }
                }
            });
        }
    }
}
