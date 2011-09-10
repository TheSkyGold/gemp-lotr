package com.gempukku.lotro.logic.timing.processes.turn.assign;

import com.gempukku.lotro.common.CardType;
import com.gempukku.lotro.common.Keyword;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.game.state.Skirmish;
import com.gempukku.lotro.logic.PlayOrder;
import com.gempukku.lotro.logic.actions.DefaultCostToEffectAction;
import com.gempukku.lotro.logic.decisions.DecisionResultInvalidException;
import com.gempukku.lotro.logic.decisions.PlayerAssignMinionsDecision;
import com.gempukku.lotro.logic.effects.AssignmentEffect;
import com.gempukku.lotro.logic.modifiers.ModifiersQuerying;
import com.gempukku.lotro.logic.timing.processes.GameProcess;

import java.util.List;
import java.util.Map;

public class ShadowPlayerAssignsHisMinionsGameProcess implements GameProcess {
    private LotroGame _game;
    private PlayOrder _shadowOrder;
    private String _playerId;
    private GameProcess _followingGameProcess;

    public ShadowPlayerAssignsHisMinionsGameProcess(LotroGame game, PlayOrder shadowOrder, String playerId, GameProcess followingGameProcess) {
        _game = game;
        _shadowOrder = shadowOrder;
        _playerId = playerId;
        _followingGameProcess = followingGameProcess;
    }

    @Override
    public void process() {
        GameState gameState = _game.getGameState();
        Filter minionFilter = Filters.and(Filters.type(CardType.MINION), Filters.owner(_playerId));
        if (gameState.isFierceSkirmishes())
            minionFilter = Filters.and(
                    Filters.keyword(Keyword.FIERCE),
                    minionFilter);

        final List<PhysicalCard> minions = Filters.filterActive(gameState, _game.getModifiersQuerying(), minionFilter);

        for (Skirmish skirmish : gameState.getAssignments())
            minions.removeAll(skirmish.getShadowCharacters());

        if (minions.size() > 0) {
            final List<PhysicalCard> freePeopleTargets = Filters.filterActive(gameState, _game.getModifiersQuerying(),
                    Filters.or(
                            Filters.type(CardType.COMPANION),
                            new Filter() {
                                @Override
                                public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                                    return modifiersQuerying.isAllyOnCurrentSite(gameState, physicalCard);
                                }
                            }
                    ));

            _game.getUserFeedback().sendAwaitingDecision(_playerId,
                    new PlayerAssignMinionsDecision(1, "Assign minions to companions or allies at home", freePeopleTargets, minions) {
                        @Override
                        public void decisionMade(String result) throws DecisionResultInvalidException {
                            Map<PhysicalCard, List<PhysicalCard>> assignments = getAssignmentsBasedOnResponse(result);

                            DefaultCostToEffectAction action = new DefaultCostToEffectAction(null, null, "Shadow player assignments");
                            action.addEffect(
                                    new AssignmentEffect(assignments, "Shadow player assignments"));

                            _game.getActionsEnvironment().addActionToStack(action);
                        }
                    });
        }
    }

    @Override
    public GameProcess getNextProcess() {
        String nextPlayerId = _shadowOrder.getNextPlayer();
        if (nextPlayerId != null)
            return new ShadowPlayerAssignsHisMinionsGameProcess(_game, _shadowOrder, nextPlayerId, _followingGameProcess);
        else
            return _followingGameProcess;
    }
}
