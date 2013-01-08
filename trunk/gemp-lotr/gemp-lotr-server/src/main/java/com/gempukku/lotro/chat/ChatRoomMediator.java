package com.gempukku.lotro.chat;

import com.gempukku.lotro.PrivateInformationException;
import com.gempukku.lotro.SubscriptionExpiredException;
import com.gempukku.lotro.game.GatheringChatRoomListener;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ChatRoomMediator {
    private Logger _logger;
    private ChatRoom _chatRoom;

    private Map<String, GatheringChatRoomListener> _listeners = new HashMap<String, GatheringChatRoomListener>();

    private int _channelInactivityTimeoutPeriod = 1000 * 20; // 10 seconds
    private Set<String> _allowedPlayers;

    private ReadWriteLock _lock = new ReentrantReadWriteLock();

    public ChatRoomMediator(String roomName, boolean muteJoinPartMessages, int secondsTimeoutPeriod) {
        this(roomName, muteJoinPartMessages, secondsTimeoutPeriod, null);
    }

    public ChatRoomMediator(String roomName, boolean muteJoinPartMessages, int secondsTimeoutPeriod, Set<String> allowedPlayers) {
        _logger = Logger.getLogger("chat."+roomName);
        _allowedPlayers = allowedPlayers;
        _channelInactivityTimeoutPeriod = 1000 * secondsTimeoutPeriod;
        _chatRoom = new ChatRoom(muteJoinPartMessages);
    }

    public List<ChatMessage> joinUser(String playerId, boolean admin) throws PrivateInformationException {
        _lock.writeLock().lock();
        try {
            if (!admin && _allowedPlayers != null && !_allowedPlayers.contains(playerId))
                throw new PrivateInformationException();

            GatheringChatRoomListener value = new GatheringChatRoomListener();
            _listeners.put(playerId, value);
            _chatRoom.joinChatRoom(playerId, value);
            return value.consumeMessages();
        } finally {
            _lock.writeLock().unlock();
        }
    }

    public List<ChatMessage> getPendingMessages(String playerId) throws SubscriptionExpiredException {
        _lock.readLock().lock();
        try {
            GatheringChatRoomListener gatheringChatRoomListener = _listeners.get(playerId);
            if (gatheringChatRoomListener == null)
                throw new SubscriptionExpiredException();
            return gatheringChatRoomListener.consumeMessages();
        } finally {
            _lock.readLock().unlock();
        }
    }

    public void partUser(String playerId) {
        _lock.writeLock().lock();
        try {
            _chatRoom.partChatRoom(playerId);
            _listeners.remove(playerId);
        } finally {
            _lock.writeLock().unlock();
        }
    }

    public void sendMessage(String playerId, String message, boolean admin) throws PrivateInformationException {
        _lock.writeLock().lock();
        try {
            if (!admin && _allowedPlayers != null && !_allowedPlayers.contains(playerId))
                throw new PrivateInformationException();

            _logger.info(playerId+": "+message);
            _chatRoom.postMessage(playerId, message);
        } finally {
            _lock.writeLock().unlock();
        }
    }

    public void cleanup() {
        _lock.writeLock().lock();
        try {
            long currentTime = System.currentTimeMillis();
            Map<String, GatheringChatRoomListener> copy = new HashMap<String, GatheringChatRoomListener>(_listeners);
            for (Map.Entry<String, GatheringChatRoomListener> playerListener : copy.entrySet()) {
                String playerId = playerListener.getKey();
                GatheringChatRoomListener listener = playerListener.getValue();
                if (currentTime > listener.getLastConsumed().getTime() + _channelInactivityTimeoutPeriod) {
                    _chatRoom.partChatRoom(playerId);
                    _listeners.remove(playerId);
                }
            }
        } finally {
            _lock.writeLock().unlock();
        }
    }

    public Collection<String> getUsersInRoom() {
        _lock.readLock().lock();
        try {
            return _chatRoom.getUsersInRoom();
        } finally {
            _lock.readLock().unlock();
        }
    }
}
