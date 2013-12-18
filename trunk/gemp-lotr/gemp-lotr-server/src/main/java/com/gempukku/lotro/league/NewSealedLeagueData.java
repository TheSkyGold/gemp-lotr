package com.gempukku.lotro.league;

import com.gempukku.lotro.DateUtils;
import com.gempukku.lotro.cards.CardSets;
import com.gempukku.lotro.collection.CollectionsManager;
import com.gempukku.lotro.competitive.PlayerStanding;
import com.gempukku.lotro.db.vo.CollectionType;
import com.gempukku.lotro.game.CardCollection;
import com.gempukku.lotro.game.DefaultCardCollection;
import com.gempukku.lotro.game.MutableCardCollection;
import com.gempukku.lotro.game.Player;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NewSealedLeagueData implements LeagueData {
    private SealedLeagueType _leagueType;
    private List<LeagueSerieData> _series;
    private CollectionType _collectionType;
    private CollectionType _prizeCollectionType = CollectionType.MY_CARDS;
    private LeaguePrizes _leaguePrizes;
    private SealedLeagueProduct _leagueProduct;

    public NewSealedLeagueData(CardSets cardSets, String parameters) {
        _leaguePrizes = new FixedLeaguePrizes(cardSets);
        
        String[] params = parameters.split(",");
        _leagueType = SealedLeagueType.getLeagueType(params[0]);
        int start = Integer.parseInt(params[1]);
        int serieDuration = Integer.parseInt(params[2]);
        int maxMatches = Integer.parseInt(params[3]);

        _collectionType = new CollectionType(params[4], params[5]);

        _leagueProduct = new SealedLeagueProduct();

        _series = new LinkedList<LeagueSerieData>();
        for (int i = 0; i < 4; i++) {
            _series.add(
                    new DefaultLeagueSerieData(_leaguePrizes, true, "Serie " + (i + 1),
                            DateUtils.offsetDate(start, i * serieDuration), DateUtils.offsetDate(start, (i + 1) * serieDuration - 1), maxMatches,
                            _leagueType.getFormat(), _collectionType));
        }
    }

    @Override
    public List<LeagueSerieData> getSeries() {
        return Collections.unmodifiableList(_series);
    }

    @Override
    public CardCollection joinLeague(CollectionsManager collectionManager, Player player, int currentTime) {
        MutableCardCollection startingCollection = new DefaultCardCollection();
        for (int i = 0; i < _series.size(); i++) {
            LeagueSerieData serie = _series.get(i);
            if (currentTime >= serie.getStart()) {
                CardCollection leagueProduct = _leagueProduct.getCollectionForSerie(_leagueType.getSealedCode(), i);

                for (Map.Entry<String, CardCollection.Item> serieCollectionItem : leagueProduct.getAll().entrySet())
                    startingCollection.addItem(serieCollectionItem.getKey(), serieCollectionItem.getValue().getCount());
            }
        }
        collectionManager.addPlayerCollection(true, "Sealed league product", player, _collectionType, startingCollection);
        return startingCollection;
    }

    @Override
    public int process(CollectionsManager collectionsManager, List<PlayerStanding> leagueStandings, int oldStatus, int currentTime) {
        int status = oldStatus;

        for (int i = status; i < _series.size(); i++) {
            LeagueSerieData serie = _series.get(i);
            if (currentTime >= serie.getStart()) {
                CardCollection leagueProduct = _leagueProduct.getCollectionForSerie(_leagueType.getSealedCode(), i);
                Map<Player, CardCollection> map = collectionsManager.getPlayersCollection(_collectionType.getCode());
                for (Map.Entry<Player, CardCollection> playerCardCollectionEntry : map.entrySet()) {
                    collectionsManager.addItemsToPlayerCollection(true, "New sealed league product", playerCardCollectionEntry.getKey(), _collectionType, leagueProduct.getAll().values());
                }
                status = i + 1;
            }
        }

        int maxGamesTotal = 0;
        for (LeagueSerieData sery : _series)
            maxGamesTotal+=sery.getMaxMatches();

        if (status == _series.size()) {
            LeagueSerieData lastSerie = _series.get(_series.size() - 1);
            if (currentTime > DateUtils.offsetDate(lastSerie.getEnd(), 1)) {
                for (PlayerStanding leagueStanding : leagueStandings) {
                    CardCollection leaguePrize = _leaguePrizes.getPrizeForLeague(leagueStanding.getStanding(), leagueStandings.size(), leagueStanding.getGamesPlayed(), maxGamesTotal, _collectionType);
                    if (leaguePrize != null)
                        collectionsManager.addItemsToPlayerCollection(true, "End of league prizes", leagueStanding.getPlayerName(), _prizeCollectionType, leaguePrize.getAll().values());
                    final CardCollection leagueTrophies = _leaguePrizes.getTrophiesForLeague(leagueStanding.getStanding(), leagueStandings.size(), leagueStanding.getGamesPlayed(), maxGamesTotal, _collectionType);
                    if (leagueTrophies != null)
                        collectionsManager.addItemsToPlayerCollection(true, "End of league prizes", leagueStanding.getPlayerName(), CollectionType.TROPHY, leagueTrophies.getAll().values());
                }
                status++;
            }
        }

        return status;
    }
}