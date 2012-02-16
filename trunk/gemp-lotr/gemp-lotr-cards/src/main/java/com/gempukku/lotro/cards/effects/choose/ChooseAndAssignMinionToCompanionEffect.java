package com.gempukku.lotro.cards.effects.choose;

import com.gempukku.lotro.common.Filterable;
import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.GameUtils;
import com.gempukku.lotro.logic.actions.SubAction;
import com.gempukku.lotro.logic.effects.AssignmentEffect;
import com.gempukku.lotro.logic.effects.ChooseActiveCardEffect;
import com.gempukku.lotro.logic.timing.Action;

public class ChooseAndAssignMinionToCompanionEffect extends ChooseActiveCardEffect {
    private Action _action;
    private String _playerId;
    private PhysicalCard _companion;

    public ChooseAndAssignMinionToCompanionEffect(Action action, String playerId, PhysicalCard companion, Filterable... filters) {
        super(action.getActionSource(), playerId, "Choose minion to assign " + GameUtils.getCardLink(companion) + " to", filters);
        _action = action;
        _playerId = playerId;
        _companion = companion;
    }

    @Override
    protected Filter getExtraFilterForPlaying(final LotroGame game) {
        final Side side = game.getGameState().getCurrentPlayerId().equals(_playerId) ? Side.FREE_PEOPLE : Side.SHADOW;
        return Filters.assignableToSkirmishAgainst(side, _companion, false, false);
    }

    @Override
    protected void cardSelected(LotroGame game, PhysicalCard card) {
        SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new AssignmentEffect(_playerId, _companion, card) {
                    @Override
                    protected void assignmentMadeCallback(PhysicalCard fpChar, PhysicalCard minion) {
                        ChooseAndAssignMinionToCompanionEffect.this.assignmentMadeCallback(fpChar, minion);
                    }
                });
        game.getActionsEnvironment().addActionToStack(subAction);
    }

    protected void assignmentMadeCallback(PhysicalCard fpChar, PhysicalCard minion) {

    }
}
