package com.gempukku.lotro.game.state;

import com.gempukku.lotro.common.Phase;
import com.gempukku.lotro.communication.UserFeedback;
import com.gempukku.lotro.game.ActionsEnvironment;
import com.gempukku.lotro.game.LotroCardBlueprintLibrary;
import com.gempukku.lotro.game.LotroFormat;
import com.gempukku.lotro.logic.modifiers.ModifiersEnvironment;
import com.gempukku.lotro.logic.modifiers.ModifiersQuerying;

public interface LotroGame {
    public GameState getGameState();

    public LotroCardBlueprintLibrary getLotroCardBlueprintLibrary();

    public ModifiersEnvironment getModifiersEnvironment();

    public ModifiersQuerying getModifiersQuerying();

    public ActionsEnvironment getActionsEnvironment();

    public UserFeedback getUserFeedback();

    public void checkRingBearerCorruption();

    public void checkRingBearerAlive();

    public void playerWon(String currentPlayerId, String reason);

    public void playerLost(String currentPlayerId, String reason);

    public String getWinnerPlayerId();

    public LotroFormat getFormat();

    public boolean shouldAutoPass(String playerId, Phase phase);

    public boolean isSolo();
}
