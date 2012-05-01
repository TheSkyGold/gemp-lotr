package com.gempukku.lotro.league;

import com.gempukku.lotro.PlayerStanding;
import com.gempukku.lotro.collection.CollectionsManager;
import com.gempukku.lotro.game.CardCollection;
import com.gempukku.lotro.game.Player;

import java.util.List;

public interface LeagueData {
    public List<LeagueSerieData> getSeries();

    public CardCollection joinLeague(CollectionsManager collecionsManager, Player player, int currentTime);

    public int process(CollectionsManager collectionsManager, List<PlayerStanding> leagueStandings, int oldStatus, int currentTime);
}
