package com.gempukku.lotro.logic.timing.processes.pregame;

import com.gempukku.lotro.common.Phase;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.timing.processes.GameProcess;
import com.gempukku.lotro.logic.timing.processes.turn.BetweenTurnsProcess;

public class PlayersDrawEightCardsGameProcess implements GameProcess {
    private String _firstPlayer;
    private GameProcess _followingGameProcess;

    public PlayersDrawEightCardsGameProcess(String firstPlayer) {
        _firstPlayer = firstPlayer;
    }

    @Override
    public void process(LotroGame game) {
        GameState gameState = game.getGameState();
        for (String player : gameState.getPlayerOrder().getAllPlayers()) {
            gameState.shuffleDeck(player);
            for (int i = 0; i < 8; i++)
                gameState.playerDrawsCard(player);
        }
        if (game.getFormat().hasMulliganRule()) {
            gameState.setCurrentPhase(Phase.BETWEEN_TURNS);
            _followingGameProcess = new MulliganProcess(game.getGameState().getPlayerOrder().getClockwisePlayOrder(_firstPlayer, false));
        } else {
            gameState.setCurrentPhase(Phase.BETWEEN_TURNS);
            _followingGameProcess = new BetweenTurnsProcess();
        }
    }

    @Override
    public GameProcess getNextProcess() {
        return _followingGameProcess;
    }
}
