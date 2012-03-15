package com.gempukku.lotro.chat;

import com.gempukku.lotro.game.GatheringChatRoomListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChatRoomMediator {
    private ChatRoom _chatRoom = new ChatRoom();

    private Map<String, GatheringChatRoomListener> _listeners = new HashMap<String, GatheringChatRoomListener>();

    private int _channelInactivityTimeoutPeriod = 1000 * 10; // 10 seconds
    private Set<String> _allowedPlayers;

    public ChatRoomMediator(int secondsTimeoutPeriod) {
        this(secondsTimeoutPeriod, null);
    }

    public ChatRoomMediator(int secondsTimeoutPeriod, Set<String> allowedPlayers) {
        _allowedPlayers = allowedPlayers;
        _channelInactivityTimeoutPeriod = 1000 * secondsTimeoutPeriod;
    }

    public synchronized List<ChatMessage> joinUser(String playerId) {
        if (_allowedPlayers != null && !_allowedPlayers.contains(playerId))
            return null;

        GatheringChatRoomListener value = new GatheringChatRoomListener();
        _listeners.put(playerId, value);
        _chatRoom.joinChatRoom(playerId, value);
        return value.consumeMessages();
    }

    public synchronized List<ChatMessage> getPendingMessages(String playerId) {
        if (_allowedPlayers != null && !_allowedPlayers.contains(playerId))
            return null;

        GatheringChatRoomListener gatheringChatRoomListener = _listeners.get(playerId);
        if (gatheringChatRoomListener == null)
            return null;
        return gatheringChatRoomListener.consumeMessages();
    }

    public synchronized void partUser(String playerId) {
        _chatRoom.partChatRoom(playerId);
        _listeners.remove(playerId);
    }

    public synchronized boolean sendMessage(String playerId, String message) {
        if (_allowedPlayers != null && !_allowedPlayers.contains(playerId))
            return false;

        _chatRoom.postMessage(playerId, message);
        return true;
    }

    public synchronized void cleanup() {
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
    }

    public synchronized Set<String> getUsersInRoom() {
        return _chatRoom.getUsersInRoom();
    }
}