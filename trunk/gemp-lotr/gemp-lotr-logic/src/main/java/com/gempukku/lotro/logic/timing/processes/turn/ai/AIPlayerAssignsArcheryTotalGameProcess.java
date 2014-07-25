package com.gempukku.lotro.logic.timing.processes.turn.ai;

import com.gempukku.lotro.common.CardType;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.SubAction;
import com.gempukku.lotro.logic.actions.SystemQueueAction;
import com.gempukku.lotro.logic.effects.WoundCharactersEffect;
import com.gempukku.lotro.logic.modifiers.ModifiersQuerying;
import com.gempukku.lotro.logic.timing.UnrespondableEffect;
import com.gempukku.lotro.logic.timing.processes.GameProcess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class AIPlayerAssignsArcheryTotalGameProcess implements GameProcess {
    private int _woundsToAssign;
    private GameProcess _followingProcess;

    public AIPlayerAssignsArcheryTotalGameProcess(int woundsToAssign, GameProcess followingProcess) {
        _woundsToAssign = woundsToAssign;
        _followingProcess = followingProcess;
    }

    @Override
    public void process(LotroGame game) {
        if (_woundsToAssign > 0) {
            final Filter filterPriority =
                    Filters.and(
                            CardType.MINION,
                            Filters.owner("AI"),
                            new Filter() {
                                @Override
                                public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                                    return modifiersQuerying.canTakeArcheryWound(gameState, physicalCard)
                                            && gameState.getWounds(physicalCard) < modifiersQuerying.getVitality(gameState, physicalCard) - 1;
                                }
                            });

            final Filter filterFallback =
                    Filters.and(
                            CardType.MINION,
                            Filters.owner("AI"),
                            new Filter() {
                                @Override
                                public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                                    return modifiersQuerying.canTakeArcheryWound(gameState, physicalCard);
                                }
                            }
                    );

            final SystemQueueAction action = new SystemQueueAction();
            for (int i = 0; i < _woundsToAssign; i++) {
                UnrespondableEffect chooseRandomMinionAndWound = new UnrespondableEffect() {
                    @Override
                    protected void doPlayEffect(LotroGame game) {
                        Collection<PhysicalCard> acceptableCards = Filters.filterActive(game.getGameState(), game.getModifiersQuerying(), filterPriority);
                        if (acceptableCards.size() == 0)
                            acceptableCards = Filters.filterActive(game.getGameState(), game.getModifiersQuerying(), filterFallback);

                        List<PhysicalCard> possibleChoices = new ArrayList<PhysicalCard>(acceptableCards);
                        if (possibleChoices.size()>0) {
                            SubAction subAction = new SubAction(action);
                            Random rnd = new Random();
                            final int randomIndex = rnd.nextInt(possibleChoices.size());
                            WoundCharactersEffect woundCharacter = new WoundCharactersEffect((PhysicalCard) null, possibleChoices.get(randomIndex));
                            woundCharacter.setSourceText("Archery Fire");
                            subAction.appendEffect(woundCharacter);
                            game.getActionsEnvironment().addActionToStack(subAction);
                        }
                    }
                };
                action.appendEffect(chooseRandomMinionAndWound);
            }

            game.getActionsEnvironment().addActionToStack(action);
        }
    }

    @Override
    public GameProcess getNextProcess() {
        return _followingProcess;
    }
}
