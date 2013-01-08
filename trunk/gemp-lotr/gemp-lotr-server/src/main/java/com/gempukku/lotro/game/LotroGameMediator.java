package com.gempukku.lotro.game;

import com.gempukku.lotro.PrivateInformationException;
import com.gempukku.lotro.SubscriptionConflictException;
import com.gempukku.lotro.SubscriptionExpiredException;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.communication.GameStateListener;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.state.GameEvent;
import com.gempukku.lotro.game.state.GatheringParticipantCommunicationChannel;
import com.gempukku.lotro.logic.GameUtils;
import com.gempukku.lotro.logic.decisions.AwaitingDecision;
import com.gempukku.lotro.logic.decisions.DecisionResultInvalidException;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.timing.DefaultLotroGame;
import com.gempukku.lotro.logic.timing.GameResultListener;
import com.gempukku.lotro.logic.vo.LotroDeck;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LotroGameMediator {
    private static final Logger LOG = Logger.getLogger(LotroGameMediator.class);

    private Map<String, GatheringParticipantCommunicationChannel> _communicationChannels = new HashMap<String, GatheringParticipantCommunicationChannel>();
    private DefaultUserFeedback _userFeedback;
    private DefaultLotroGame _lotroGame;
    private Map<String, Integer> _playerClocks = new HashMap<String, Integer>();
    private Map<String, Long> _decisionQuerySentTimes = new HashMap<String, Long>();
    private Set<String> _playersPlaying = new HashSet<String>();

    private int _maxSecondsForGamePerPlayer = 60 * 80; // 80 minutes
    private boolean _allowSpectators;
    private boolean _cancellable;
    //    private final int _maxSecondsForGamePerPlayer = 60 * 40; // 40 minutes
    private final int _playerDecisionTimeoutPeriod = 1000 * 60 * 10; // 10 minutes

    private ReentrantReadWriteLock _lock = new ReentrantReadWriteLock(true);
    private ReentrantReadWriteLock.ReadLock _readLock = _lock.readLock();
    private ReentrantReadWriteLock.WriteLock _writeLock = _lock.writeLock();
    private int _channelNextIndex = 0;

    public LotroGameMediator(LotroFormat lotroFormat, LotroGameParticipant[] participants, LotroCardBlueprintLibrary library, int maxSecondsForGamePerPlayer,
                             boolean allowSpectators, boolean cancellable) {
        _maxSecondsForGamePerPlayer = maxSecondsForGamePerPlayer;
        _allowSpectators = allowSpectators;
        _cancellable = cancellable;
        if (participants.length < 1)
            throw new IllegalArgumentException("Game can't have less than one participant");

        Map<String, LotroDeck> decks = new HashMap<String, LotroDeck>();

        for (LotroGameParticipant participant : participants) {
            String participantId = participant.getPlayerId();
            decks.put(participantId, participant.getDeck());
            _playerClocks.put(participantId, 0);
            _playersPlaying.add(participantId);
        }

        _userFeedback = new DefaultUserFeedback();
        _lotroGame = new DefaultLotroGame(lotroFormat, decks, _userFeedback, library);
        _userFeedback.setGame(_lotroGame);
    }

    public boolean isAllowSpectators() {
        return _allowSpectators;
    }

    public void setPlayerAutoPassSettings(String playerId, Set<Phase> phases) {
        if (_playersPlaying.contains(playerId)) {
            _lotroGame.setPlayerAutoPassSettings(playerId, phases);
        }
    }

    public void sendMessageToPlayers(String message) {
        _lotroGame.getGameState().sendMessage(message);
    }

    public void addGameStateListener(String playerId, GameStateListener listener) {
        _lotroGame.addGameStateListener(playerId, listener);
    }

    public void removeGameStateListener(GameStateListener listener) {
        _lotroGame.removeGameStateListener(listener);
    }

    public void addGameResultListener(GameResultListener listener) {
        _lotroGame.addGameResultListener(listener);
    }

    public void removeGameResultListener(GameResultListener listener) {
        _lotroGame.removeGameResultListener(listener);
    }

    public String getWinner() {
        return _lotroGame.getWinnerPlayerId();
    }

    public List<String> getPlayersPlaying() {
        return new LinkedList<String>(_playersPlaying);
    }

    public String getGameStatus() {
        if (_lotroGame.isCancelled())
            return "Cancelled";
        if (_lotroGame.isFinished())
            return "Finished";
        final Phase currentPhase = _lotroGame.getGameState().getCurrentPhase();
        if (currentPhase == Phase.PLAY_STARTING_FELLOWSHIP || currentPhase == Phase.PUT_RING_BEARER)
            return "Preparation";
        return "At sites: " + getPlayerPositions();
    }

    public boolean isFinished() {
        return _lotroGame.isFinished();
    }

    public String produceCardInfo(Player player, int cardId) {
        _readLock.lock();
        try {
            PhysicalCard card = _lotroGame.getGameState().findCardById(cardId);
            if (card == null || card.getZone() == null)
                return null;

            if (card.getZone().isInPlay() || card.getZone() == Zone.HAND) {
                StringBuilder sb = new StringBuilder();

                if (card.getZone() == Zone.HAND)
                    sb.append("<b>Card is in hand - stats are only provisional</b><br><br>");
                else if (Filters.filterActive(_lotroGame.getGameState(), _lotroGame.getModifiersQuerying(), card).size() == 0)
                    sb.append("<b>Card is inactive - current stats may be inaccurate</b><br><br>");

                sb.append("<b>Affecting card:</b>");
                Collection<Modifier> modifiers = _lotroGame.getModifiersQuerying().getModifiersAffecting(_lotroGame.getGameState(), card);
                for (Modifier modifier : modifiers) {
                    PhysicalCard source = modifier.getSource();
                    if (source != null)
                        sb.append("<br><b>" + GameUtils.getCardLink(source) + ":</b> " + modifier.getText(_lotroGame.getGameState(), _lotroGame.getModifiersQuerying(), card));
                    else
                        sb.append("<br><b><i>System</i>:</b> " + modifier.getText(_lotroGame.getGameState(), _lotroGame.getModifiersQuerying(), card));
                }
                if (modifiers.size() == 0)
                    sb.append("<br><i>nothing</i>");

                if (card.getZone().isInPlay() && card.getBlueprint().getCardType() == CardType.SITE)
                    sb.append("<br><b>Owner:</b> " + card.getOwner());

                Map<Token, Integer> map = _lotroGame.getGameState().getTokens(card);
                if (map != null && map.size() > 0) {
                    sb.append("<br><b>Tokens:</b>");
                    for (Map.Entry<Token, Integer> tokenIntegerEntry : map.entrySet())
                        sb.append("<br>" + tokenIntegerEntry.getKey().toString() + ": " + tokenIntegerEntry.getValue());
                }

                List<PhysicalCard> stackedCards = _lotroGame.getGameState().getStackedCards(card);
                if (stackedCards != null && stackedCards.size() > 0) {
                    sb.append("<br><b>Stacked cards:</b>");
                    sb.append("<br>" + GameUtils.getAppendedNames(stackedCards));
                }

                final String extraDisplayableInformation = card.getBlueprint().getDisplayableInformation(card);
                if (extraDisplayableInformation != null) {
                    sb.append("<br><b>Extra information:</b>");
                    sb.append("<br>" + extraDisplayableInformation);
                }

                sb.append("<br><br><b>Effective stats:</b>");
                try {
                    int twilightCost = _lotroGame.getModifiersQuerying().getTwilightCost(_lotroGame.getGameState(), card, false);
                    sb.append("<br><b>Twilight cost:</b> " + twilightCost);
                } catch (UnsupportedOperationException exp) {
                }
                try {
                    int strength = _lotroGame.getModifiersQuerying().getStrength(_lotroGame.getGameState(), card);
                    sb.append("<br><b>Strength:</b> " + strength);
                } catch (UnsupportedOperationException exp) {
                }
                try {
                    int vitality = _lotroGame.getModifiersQuerying().getVitality(_lotroGame.getGameState(), card);
                    sb.append("<br><b>Vitality:</b> " + vitality);
                } catch (UnsupportedOperationException exp) {
                }
                try {
                    int resistance = _lotroGame.getModifiersQuerying().getResistance(_lotroGame.getGameState(), card);
                    sb.append("<br><b>Resistance:</b> " + resistance);
                } catch (UnsupportedOperationException exp) {
                }
                try {
                    int siteNumber = _lotroGame.getModifiersQuerying().getMinionSiteNumber(_lotroGame.getGameState(), card);
                    sb.append("<br><b>Site number:</b> " + siteNumber);
                } catch (UnsupportedOperationException exp) {
                }

                StringBuilder keywords = new StringBuilder();
                for (Keyword keyword : Keyword.values()) {
                    if (keyword.isInfoDisplayable()) {
                        if (keyword.isMultiples()) {
                            int count = _lotroGame.getModifiersQuerying().getKeywordCount(_lotroGame.getGameState(), card, keyword);
                            if (count > 0)
                                keywords.append(keyword.getHumanReadable() + " +" + count + ", ");
                        } else {
                            if (_lotroGame.getModifiersQuerying().hasKeyword(_lotroGame.getGameState(), card, keyword))
                                keywords.append(keyword.getHumanReadable() + ", ");
                        }
                    }
                }
                if (keywords.length() > 0)
                    sb.append("<br><b>Keywords:</b> " + keywords.substring(0, keywords.length() - 2));
                return sb.toString();
            } else {
                return null;
            }
        } finally {
            _readLock.unlock();
        }
    }

    public void startGame() {
        _writeLock.lock();
        try {
            _lotroGame.startGame();
            startClocksForUsersPendingDecision();
        } finally {
            _writeLock.unlock();
        }
    }

    public void cleanup() {
        _writeLock.lock();
        try {
            long currentTime = System.currentTimeMillis();
            Map<String, GatheringParticipantCommunicationChannel> channelsCopy = new HashMap<String, GatheringParticipantCommunicationChannel>(_communicationChannels);
            for (Map.Entry<String, GatheringParticipantCommunicationChannel> playerChannels : channelsCopy.entrySet()) {
                String playerId = playerChannels.getKey();
                // Channel is stale (user no longer connected to game, to save memory, we remove the channel
                // User can always reconnect and establish a new channel
                GatheringParticipantCommunicationChannel channel = playerChannels.getValue();
                if (currentTime > channel.getLastConsumed().getTime() + _playerDecisionTimeoutPeriod) {
                    _lotroGame.removeGameStateListener(channel);
                    _communicationChannels.remove(playerId);
                }
            }

            if (_lotroGame.getGameState() != null && _lotroGame.getWinnerPlayerId() == null) {
                for (Map.Entry<String, Long> playerDecision : new HashMap<String, Long>(_decisionQuerySentTimes).entrySet()) {
                    String playerId = playerDecision.getKey();
                    long decisionSent = playerDecision.getValue();
                    if (currentTime > decisionSent + _playerDecisionTimeoutPeriod) {
                        addTimeSpentOnDecisionToUserClock(playerId);
                        _lotroGame.playerLost(playerId, "Player decision timed-out");
                    }
                }

                for (Map.Entry<String, Integer> playerClock : _playerClocks.entrySet()) {
                    String player = playerClock.getKey();
                    if (_maxSecondsForGamePerPlayer - playerClock.getValue() - getCurrentUserPendingTime(player) < 0) {
                        addTimeSpentOnDecisionToUserClock(player);
                        _lotroGame.playerLost(player, "Player run out of time");
                    }
                }
            }
        } finally {
            _writeLock.unlock();
        }
    }

    public void concede(Player player) {
        String playerId = player.getName();
        _writeLock.lock();
        try {
            if (_lotroGame.getWinnerPlayerId() == null && _playersPlaying.contains(playerId)) {
                addTimeSpentOnDecisionToUserClock(playerId);
                _lotroGame.playerLost(playerId, "Concession");
            }
        } finally {
            _writeLock.unlock();
        }
    }

    public void cancel(Player player) {
        if (!_cancellable)
            _userFeedback.sendWarning(player.getName(), "You can't cancel this game");

        String playerId = player.getName();
        _writeLock.lock();
        try {
            if (_playersPlaying.contains(playerId))
                _lotroGame.requestCancel(playerId);
        } finally {
            _writeLock.unlock();
        }
    }

    public void playerAnswered(Player player, int channelNumber, int decisionId, String answer) throws SubscriptionConflictException, SubscriptionExpiredException {
        String playerName = player.getName();
        _writeLock.lock();
        try {
            GatheringParticipantCommunicationChannel communicationChannel = _communicationChannels.get(playerName);
            if (communicationChannel != null) {
                if (communicationChannel.getChannelNumber() == channelNumber) {
                    AwaitingDecision awaitingDecision = _userFeedback.getAwaitingDecision(playerName);
                    if (awaitingDecision != null) {
                        if (awaitingDecision.getAwaitingDecisionId() == decisionId && !_lotroGame.isFinished()) {
                            try {
                                _userFeedback.participantDecided(playerName);
                                awaitingDecision.decisionMade(answer);

                                // Decision successfully made, add the time to user clock
                                addTimeSpentOnDecisionToUserClock(playerName);

                                _lotroGame.carryOutPendingActionsUntilDecisionNeeded();
                                startClocksForUsersPendingDecision();

                            } catch (DecisionResultInvalidException decisionResultInvalidException) {
                                // Participant provided wrong answer - send a warning message, and ask again for the same decision
                                _userFeedback.sendWarning(playerName, decisionResultInvalidException.getWarningMessage());
                                _userFeedback.sendAwaitingDecision(playerName, awaitingDecision);
                            } catch (RuntimeException runtimeException) {
                                LOG.error("Error processing game decision", runtimeException);
                                _lotroGame.cancelGame();
                            }
                        }
                    }
                } else {
                    throw new SubscriptionConflictException();
                }
            } else {
                throw new SubscriptionExpiredException();
            }
        } finally {
            _writeLock.unlock();
        }
    }

    public boolean hasAnyNewMessages(Player player, int channelNumber) throws PrivateInformationException, SubscriptionConflictException, SubscriptionExpiredException {
        String playerName = player.getName();
        if (!player.getType().contains("a") && !_allowSpectators && !_playersPlaying.contains(playerName))
            throw new PrivateInformationException();

        _readLock.lock();
        try {
            GatheringParticipantCommunicationChannel communicationChannel = _communicationChannels.get(playerName);
            if (communicationChannel != null) {
                if (communicationChannel.getChannelNumber() == channelNumber) {
                    return communicationChannel.hasGameEvents() || _userFeedback.hasWarning(playerName);
                } else {
                    throw new SubscriptionConflictException();
                }
            } else {
                throw new SubscriptionExpiredException();
            }
        } finally {
            _readLock.unlock();
        }
    }

    public void processCommunicationChannel(Player player, int channelNumber, ParticipantCommunicationVisitor visitor) throws PrivateInformationException, SubscriptionConflictException, SubscriptionExpiredException {
        String playerName = player.getName();
        if (!player.getType().contains("a") && !_allowSpectators && !_playersPlaying.contains(playerName))
            throw new PrivateInformationException();

        _readLock.lock();
        try {
            GatheringParticipantCommunicationChannel communicationChannel = _communicationChannels.get(playerName);
            if (communicationChannel != null) {
                if (communicationChannel.getChannelNumber() == channelNumber) {
                    visitor.visitChannelNumber(channelNumber);
                    for (GameEvent gameEvent : communicationChannel.consumeGameEvents())
                        visitor.visitGameEvent(gameEvent);

                    String warning = _userFeedback.consumeWarning(playerName);
                    if (warning != null)
                        visitor.visitGameEvent(new GameEvent(GameEvent.Type.W).message(warning));

                    Map<String, Integer> secondsLeft = new HashMap<String, Integer>();
                    for (Map.Entry<String, Integer> playerClock : _playerClocks.entrySet()) {
                        String playerClockName = playerClock.getKey();
                        secondsLeft.put(playerClockName, _maxSecondsForGamePerPlayer - playerClock.getValue() - getCurrentUserPendingTime(playerClockName));
                    }
                    visitor.visitClock(secondsLeft);
                } else {
                    throw new SubscriptionConflictException();
                }
            } else {
                throw new SubscriptionExpiredException();
            }
        } finally {
            _readLock.unlock();
        }
    }

    public void singupUserForGame(Player player, ParticipantCommunicationVisitor visitor) throws PrivateInformationException {
        String playerName = player.getName();
        if (!player.getType().contains("a") && !_allowSpectators && !_playersPlaying.contains(playerName))
            throw new PrivateInformationException();

        _readLock.lock();
        try {
            int number = _channelNextIndex;
            _channelNextIndex++;

            GatheringParticipantCommunicationChannel participantCommunicationChannel = new GatheringParticipantCommunicationChannel(playerName, number);
            _communicationChannels.put(playerName, participantCommunicationChannel);

            _lotroGame.addGameStateListener(playerName, participantCommunicationChannel);

            visitor.visitChannelNumber(number);

            for (GameEvent gameEvent : participantCommunicationChannel.consumeGameEvents())
                visitor.visitGameEvent(gameEvent);

            Map<String, Integer> secondsLeft = new HashMap<String, Integer>();
            for (Map.Entry<String, Integer> playerClock : _playerClocks.entrySet()) {
                String playerId = playerClock.getKey();
                secondsLeft.put(playerId, _maxSecondsForGamePerPlayer - playerClock.getValue() - getCurrentUserPendingTime(playerId));
            }
            visitor.visitClock(secondsLeft);
        } finally {
            _readLock.unlock();
        }
    }

    private void startClocksForUsersPendingDecision() {
        long currentTime = System.currentTimeMillis();
        Set<String> users = _userFeedback.getUsersPendingDecision();
        for (String user : users)
            _decisionQuerySentTimes.put(user, currentTime);
    }

    private void addTimeSpentOnDecisionToUserClock(String participantId) {
        Long queryTime = _decisionQuerySentTimes.remove(participantId);
        if (queryTime != null) {
            long currentTime = System.currentTimeMillis();
            long diffSec = (currentTime - queryTime) / 1000;
            _playerClocks.put(participantId, _playerClocks.get(participantId) + (int) diffSec);
        }
    }

    private int getCurrentUserPendingTime(String participantId) {
        if (!_decisionQuerySentTimes.containsKey(participantId))
            return 0;
        long queryTime = _decisionQuerySentTimes.get(participantId);
        long currentTime = System.currentTimeMillis();
        return (int) ((currentTime - queryTime) / 1000);
    }

    public String getPlayerPositions() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String player : _playersPlaying) {
            stringBuilder.append(_lotroGame.getGameState().getPlayerPosition(player) + ", ");
        }
        if (stringBuilder.length() > 0)
            stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());

        return stringBuilder.toString();
    }
}
