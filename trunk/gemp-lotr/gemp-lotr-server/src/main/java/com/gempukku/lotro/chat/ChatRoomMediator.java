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

    public ChatRoomMediator(int secondsTimeoutPeriod) {
        _channelInactivityTimeoutPeriod = 1000 * secondsTimeoutPeriod;
    }

    public synchronized List<ChatMessage> joinUser(String playerId) {
        GatheringChatRoomListener value = new GatheringChatRoomListener();
        _listeners.put(playerId, value);
        _chatRoom.joinChatRoom(playerId, value);
        return value.consumeMessages();
    }

    public synchronized List<ChatMessage> getPendingMessages(String playerId) {
        GatheringChatRoomListener gatheringChatRoomListener = _listeners.get(playerId);
        if (gatheringChatRoomListener == null)
            return null;
        return gatheringChatRoomListener.consumeMessages();
    }

    public synchronized void partUser(String playerId) {
        _chatRoom.partChatRoom(playerId);
        _listeners.remove(playerId);
    }

    public synchronized void sendMessage(String playerId, String message) {
        _chatRoom.postMessage(playerId, message);
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