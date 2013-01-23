package com.gempukku.lotro.league;

import com.gempukku.lotro.DateUtils;
import com.gempukku.lotro.collection.CollectionsManager;
import com.gempukku.lotro.competitive.PlayerStanding;
import com.gempukku.lotro.db.vo.CollectionType;
import com.gempukku.lotro.game.CardCollection;
import com.gempukku.lotro.game.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NewConstructedLeagueData implements LeagueData {
    private LeaguePrizes _leaguePrizes = new FixedLeaguePrizes();
    private List<LeagueSerieData> _series = new ArrayList<LeagueSerieData>();

    private CollectionType _prizeCollectionType = CollectionType.MY_CARDS;
    private CollectionType _collectionType;

    public NewConstructedLeagueData(String parameters) {
        String[] params = parameters.split(",");
        int start = Integer.parseInt(params[0]);
        if (params[1].equals("default"))
            _collectionType = CollectionType.ALL_CARDS;
        else if (params[1].equals("permanent"))
            _collectionType = CollectionType.MY_CARDS;
        else
            throw new IllegalArgumentException("Unkown collection type");
        int series = Integer.parseInt(params[3]);

        int serieStart = start;
        for (int i = 0; i < series; i++) {
            String format = params[4 + i * 3];
            int duration = Integer.parseInt(params[5 + i * 3]);
            int maxMatches = Integer.parseInt(params[6 + i * 3]);
            _series.add(new DefaultLeagueSerieData(_leaguePrizes, false, "Serie " + (i + 1),
                    serieStart, DateUtils.offsetDate(serieStart, duration - 1),
                    maxMatches, format, _collectionType));

            serieStart = DateUtils.offsetDate(serieStart, duration);
        }
    }

    @Override
    public List<LeagueSerieData> getSeries() {
        return Collections.unmodifiableList(_series);
    }

    @Override
    public CardCollection joinLeague(CollectionsManager collecionsManager, Player player, int currentTime) {
        return null;
    }

    @Override
    public int process(CollectionsManager collectionsManager, List<PlayerStanding> leagueStandings, int oldStatus, int currentTime) {
        int status = oldStatus;
        if (status == 0) {
            int maxGamesPlayed = 0;
            for (LeagueSerieData sery : _series) {
                maxGamesPlayed+=sery.getMaxMatches();
            }

            LeagueSerieData lastSerie = _series.get(_series.size() - 1);
            if (currentTime > DateUtils.offsetDate(lastSerie.getEnd(), 1)) {
                for (PlayerStanding leagueStanding : leagueStandings) {
                    CardCollection leaguePrize = _leaguePrizes.getPrizeForLeague(leagueStanding.getStanding(), leagueStandings.size(), leagueStanding.getGamesPlayed(), maxGamesPlayed, _collectionType);
                    if (leaguePrize != null)
                        collectionsManager.addItemsToPlayerCollection(true, "End of league prizes", leagueStanding.getPlayerName(), _prizeCollectionType, leaguePrize.getAll().values());
                }
                status++;
            }
        }

        return status;
    }
}
