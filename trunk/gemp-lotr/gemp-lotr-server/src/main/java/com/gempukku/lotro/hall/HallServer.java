package com.gempukku.lotro.hall;

import com.gempukku.lotro.AbstractServer;
import com.gempukku.lotro.chat.ChatServer;
import com.gempukku.lotro.db.CollectionDAO;
import com.gempukku.lotro.db.vo.CollectionType;
import com.gempukku.lotro.db.vo.League;
import com.gempukku.lotro.db.vo.LeagueSerie;
import com.gempukku.lotro.game.*;
import com.gempukku.lotro.game.formats.*;
import com.gempukku.lotro.league.LeagueService;
import com.gempukku.lotro.logic.vo.LotroDeck;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class HallServer extends AbstractServer {
    private ChatServer _chatServer;
    private LeagueService _leagueService;
    private LotroCardBlueprintLibrary _library;
    private CollectionDAO _collectionDao;
    private LotroServer _lotroServer;

    private Map<String, LotroFormat> _supportedFormats = new LinkedHashMap<String, LotroFormat>();

    // TODO Reading/writing from/to these maps is done in multiple threads
    private Map<String, AwaitingTable> _awaitingTables = new LinkedHashMap<String, AwaitingTable>();
    private Map<String, String> _runningTables = new LinkedHashMap<String, String>();

    private Map<String, String> _runningTableFormatNames = new HashMap<String, String>();
    private Map<String, String> _runningTableTournamentNames = new HashMap<String, String>();
    private int _nextTableId = 1;

    private final int _playerInactivityPeriod = 1000 * 10; // 10 seconds

    private Map<Player, Long> _lastVisitedPlayers = new HashMap<Player, Long>();

    private String _motd;

    private CollectionType _allCardsCollectionType = new CollectionType("default", "All cards");

    private ReadWriteLock _hallTablesLock = new ReentrantReadWriteLock(false);

    public HallServer(LotroServer lotroServer, ChatServer chatServer, LeagueService leagueService, LotroCardBlueprintLibrary library, CollectionDAO collectionDao, boolean test) {
        _lotroServer = lotroServer;
        _chatServer = chatServer;
        _leagueService = leagueService;
        _library = library;
        _collectionDao = collectionDao;
        _chatServer.createChatRoom("Game Hall", 10);

        addFormat("fotr_block", new FotRBlockFormat(library, false));
        addFormat("c_fotr_block", new FotRBlockFormat(library, true));
        addFormat("ttt_block", new TTTBlockFormat(library, false));
        addFormat("c_ttt_block", new TTTBlockFormat(library, true));
        addFormat("towers_standard", new TowersStandardFormat(library));
        addFormat("king_block", new KingBlockFormat(library, false));
        addFormat("c_king_block", new KingBlockFormat(library, true));
        addFormat("movie", new MovieFormat(library));
        addFormat("war_block", new WarOfTheRingBlockFormat(library, false));
        addFormat("c_war_block", new WarOfTheRingBlockFormat(library, true));
        addFormat("open", new OpenFormat(library));
        addFormat("expanded", new ExpandedFormat(library));
    }

    public String getMOTD() {
        return _motd;
    }

    public void setMOTD(String motd) {
        _motd = motd;
    }

    private void addFormat(String formatCode, LotroFormat format) {
        _supportedFormats.put(formatCode, format);
    }

    public int getTablesCount() {
        return _awaitingTables.size() + _runningTables.size();
    }

    public Map<String, LotroFormat> getSupportedFormats() {
        return Collections.unmodifiableMap(_supportedFormats);
    }

    public Set<League> getRunningLeagues() {
        return _leagueService.getActiveLeagues();
    }

    /**
     * @return If table created, otherwise <code>false</code> (if the user already is sitting at a table or playing).
     */
    public void createNewTable(String type, Player player, String deckName) throws HallException {
        _hallTablesLock.writeLock().lock();
        try {
            League league = null;
            LeagueSerie leagueSerie = null;
            CollectionType collectionType = _allCardsCollectionType;
            LotroFormat format = _supportedFormats.get(type);

            if (format == null) {
                // Maybe it's a league format?
                league = _leagueService.getLeagueByType(type);
                if (league != null) {
                    leagueSerie = _leagueService.getCurrentLeagueSerie(league);
                    if (leagueSerie == null)
                        throw new HallException("There is no ongoing serie for that league");
                    format = _supportedFormats.get(leagueSerie.getFormat());
                    collectionType = league.getCollectionType();
                }
            }
            // It's not a normal format and also not a league one
            if (format == null)
                throw new HallException("This format is not supported: " + type);

            LotroDeck lotroDeck = validateUserAndDeck(format, player, deckName, collectionType);

            String tableId = String.valueOf(_nextTableId++);
            AwaitingTable table = new AwaitingTable(format, collectionType, league, leagueSerie);
            _awaitingTables.put(tableId, table);

            joinTableInternal(tableId, player.getName(), table, deckName, lotroDeck);
        } finally {
            _hallTablesLock.writeLock().unlock();
        }
    }

    /**
     * @return If table joined, otherwise <code>false</code> (if the user already is sitting at a table or playing).
     */
    public boolean joinTableAsPlayer(String tableId, Player player, String deckName) throws HallException {
        _hallTablesLock.writeLock().lock();
        try {
            AwaitingTable awaitingTable = _awaitingTables.get(tableId);
            if (awaitingTable == null)
                throw new HallException("Table is already taken or was removed");

            LotroDeck lotroDeck = validateUserAndDeck(awaitingTable.getLotroFormat(), player, deckName, awaitingTable.getCollectionType());

            joinTableInternal(tableId, player.getName(), awaitingTable, deckName, lotroDeck);

            return true;
        } finally {
            _hallTablesLock.writeLock().unlock();
        }
    }

    public void leaveAwaitingTables(Player player) {
        _hallTablesLock.writeLock().lock();
        try {
            Map<String, AwaitingTable> copy = new HashMap<String, AwaitingTable>(_awaitingTables);
            for (Map.Entry<String, AwaitingTable> table : copy.entrySet()) {
                if (table.getValue().hasPlayer(player.getName())) {
                    boolean empty = table.getValue().removePlayer(player.getName());
                    if (empty)
                        _awaitingTables.remove(table.getKey());
                }
            }
        } finally {
            _hallTablesLock.writeLock().unlock();
        }
    }

    public void processTables(Player player, HallInfoVisitor visitor) {
        _hallTablesLock.readLock().lock();
        try {
            _lastVisitedPlayers.put(player, System.currentTimeMillis());
            visitor.playerIsWaiting(isPlayerWaiting(player.getName()));

            for (Map.Entry<String, AwaitingTable> tableInformation : _awaitingTables.entrySet()) {
                final AwaitingTable table = tableInformation.getValue();

                visitor.visitTable(tableInformation.getKey(), null, "Waiting", table.getLotroFormat().getName(), getTournamentName(table), table.getPlayerNames(), null);
            }

            for (Map.Entry<String, String> runningGame : _runningTables.entrySet()) {
                LotroGameMediator lotroGameMediator = _lotroServer.getGameById(runningGame.getValue());
                if (lotroGameMediator != null)
                    visitor.visitTable(runningGame.getKey(), runningGame.getValue(), lotroGameMediator.getGameStatus(), _runningTableFormatNames.get(runningGame.getKey()), _runningTableTournamentNames.get(runningGame.getKey()), lotroGameMediator.getPlayersPlaying(), lotroGameMediator.getWinner());
            }

            String playerTable = getNonFinishedPlayerTable(player.getName());
            if (playerTable != null) {
                String gameId = _runningTables.get(playerTable);
                if (gameId != null) {
                    LotroGameMediator lotroGameMediator = _lotroServer.getGameById(gameId);
                    if (lotroGameMediator != null && !lotroGameMediator.getGameStatus().equals("Finished"))
                        visitor.runningPlayerGame(gameId);
                }
            }
        } finally {
            _hallTablesLock.readLock().unlock();
        }
    }

    private LotroDeck validateUserAndDeck(LotroFormat format, Player player, String deckName, CollectionType collectionType) throws HallException {
        if (isPlayerBusy(player.getName()))
            throw new HallException("You can't play more than one game at a time or wait at more than one table");

        LotroDeck lotroDeck = _lotroServer.getParticipantDeck(player, deckName);
        if (lotroDeck == null)
            throw new HallException("You don't have a deck registered yet");

        try {
            format.validateDeck(player, lotroDeck);
        } catch (DeckInvalidException e) {
            throw new HallException("Your registered deck is not valid for this format: " + e.getMessage());
        }

        // Now check if player owns all the cards
        CardCollection collection = _collectionDao.getCollectionForPlayer(player, collectionType.getCode());

        Map<String, Integer> deckCardCounts = CollectionUtils.getTotalCardCountForDeck(lotroDeck);
        final Map<String, Integer> collectionCardCounts = collection.getAll();

        for (Map.Entry<String, Integer> cardCount : deckCardCounts.entrySet()) {
            final Integer collectionCount = collectionCardCounts.get(cardCount.getKey());
            if (collectionCount == null || collectionCount < cardCount.getValue()) {
                String cardName = _library.getLotroCardBlueprint(cardCount.getKey()).getName();
                int owned = (collectionCount == null) ? 0 : collectionCount;
                throw new HallException("You don't have the required cards in collection: " + cardName + " required " + cardCount.getValue() + ", owned " + owned);
            }
        }

        return lotroDeck;
    }

    private String getTournamentName(AwaitingTable table) {
        String tournamentName = "Casual";
        final League league = table.getLeague();
        if (league != null)
            tournamentName = league.getName() + " - " + table.getLeagueSerie().getType();
        return tournamentName;
    }

    private void createGame(String tableId, AwaitingTable awaitingTable) {
        Set<LotroGameParticipant> players = awaitingTable.getPlayers();
        LotroGameParticipant[] participants = players.toArray(new LotroGameParticipant[players.size()]);
        League league = awaitingTable.getLeague();
        String gameId = _lotroServer.createNewGame(awaitingTable.getLotroFormat(), participants, league != null);
        LotroGameMediator lotroGameMediator = _lotroServer.getGameById(gameId);
        if (league != null)
            _leagueService.leagueGameStarting(league, awaitingTable.getLeagueSerie(), lotroGameMediator);
        lotroGameMediator.startGame();
        _runningTables.put(tableId, gameId);
        _runningTableFormatNames.put(tableId, awaitingTable.getLotroFormat().getName());
        _runningTableTournamentNames.put(tableId, getTournamentName(awaitingTable));
        _awaitingTables.remove(tableId);
    }

    private void joinTableInternal(String tableId, String playerId, AwaitingTable awaitingTable, String deckName, LotroDeck lotroDeck) {
        boolean tableFull = awaitingTable.addPlayer(new LotroGameParticipant(playerId, deckName, lotroDeck));
        if (tableFull)
            createGame(tableId, awaitingTable);
    }

    private boolean isPlayerWaiting(String playerId) {
        for (AwaitingTable awaitingTable : _awaitingTables.values())
            if (awaitingTable.hasPlayer(playerId))
                return true;
        return false;
    }

    private String getNonFinishedPlayerTable(String playerId) {
        for (Map.Entry<String, AwaitingTable> table : _awaitingTables.entrySet()) {
            if (table.getValue().hasPlayer(playerId))
                return table.getKey();
        }

        for (Map.Entry<String, String> runningTable : _runningTables.entrySet()) {
            String gameId = runningTable.getValue();
            LotroGameMediator lotroGameMediator = _lotroServer.getGameById(gameId);
            if (lotroGameMediator != null && !lotroGameMediator.getGameStatus().equals("Finished"))
                if (lotroGameMediator.getPlayersPlaying().contains(playerId))
                    return runningTable.getKey();
        }

        return null;
    }

    private boolean isPlayerBusy(String playerId) {
        for (AwaitingTable awaitingTable : _awaitingTables.values())
            if (awaitingTable.hasPlayer(playerId))
                return true;

        for (String gameId : _runningTables.values()) {
            LotroGameMediator lotroGameMediator = _lotroServer.getGameById(gameId);
            if (lotroGameMediator != null && !lotroGameMediator.getGameStatus().equals("Finished") && lotroGameMediator.getPlayersPlaying().contains(playerId))
                return true;
        }
        return false;
    }

    @Override
    protected void cleanup() {
        _hallTablesLock.writeLock().lock();
        try {
            // Remove finished games
            HashMap<String, String> copy = new HashMap<String, String>(_runningTables);
            for (Map.Entry<String, String> runningTable : copy.entrySet()) {
                if (_lotroServer.getGameById(runningTable.getValue()) == null) {
                    _runningTables.remove(runningTable.getKey());
                    _runningTableFormatNames.remove(runningTable.getKey());
                    _runningTableTournamentNames.remove(runningTable.getKey());
                }
            }

            long currentTime = System.currentTimeMillis();
            Map<Player, Long> visitCopy = new LinkedHashMap<Player, Long>(_lastVisitedPlayers);
            for (Map.Entry<Player, Long> lastVisitedPlayer : visitCopy.entrySet()) {
                if (currentTime > lastVisitedPlayer.getValue() + _playerInactivityPeriod) {
                    Player player = lastVisitedPlayer.getKey();
                    _lastVisitedPlayers.remove(player);
                    leaveAwaitingTables(player);
                }
            }
        } finally {
            _hallTablesLock.writeLock().unlock();
        }
    }
}
