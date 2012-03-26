package com.gempukku.lotro.game.state;

import com.gempukku.lotro.common.*;
import com.gempukku.lotro.communication.GameStateListener;
import com.gempukku.lotro.game.*;
import com.gempukku.lotro.logic.PlayerOrder;
import com.gempukku.lotro.logic.decisions.AwaitingDecision;
import com.gempukku.lotro.logic.modifiers.ModifierFlag;
import com.gempukku.lotro.logic.timing.GameStats;

import java.util.*;

public class GameState {
    private static final int LAST_MESSAGE_STORED_COUNT = 15;
    private PlayerOrder _playerOrder;

    private Map<String, List<PhysicalCardImpl>> _adventureDecks = new HashMap<String, List<PhysicalCardImpl>>();
    private Map<String, List<PhysicalCardImpl>> _decks = new HashMap<String, List<PhysicalCardImpl>>();
    private Map<String, List<PhysicalCardImpl>> _hands = new HashMap<String, List<PhysicalCardImpl>>();
    private Map<String, List<PhysicalCardImpl>> _discards = new HashMap<String, List<PhysicalCardImpl>>();
    private Map<String, List<PhysicalCardImpl>> _deadPiles = new HashMap<String, List<PhysicalCardImpl>>();
    private Map<String, List<PhysicalCardImpl>> _stacked = new HashMap<String, List<PhysicalCardImpl>>();

    private Map<String, List<PhysicalCardImpl>> _voids = new HashMap<String, List<PhysicalCardImpl>>();
    private Map<String, List<PhysicalCardImpl>> _removed = new HashMap<String, List<PhysicalCardImpl>>();

    private List<PhysicalCardImpl> _inPlay = new LinkedList<PhysicalCardImpl>();

    private Map<Integer, PhysicalCardImpl> _allCards = new HashMap<Integer, PhysicalCardImpl>();

    private String _currentPlayerId;
    private Phase _currentPhase = Phase.PUT_RING_BEARER;
    private int _twilightPool;

    private int _moveCount;
    private boolean _fierceSkirmishes;
    private boolean _extraSkirmishes;

    private boolean _wearingRing;
    private boolean _consecutiveAction;

    private Map<String, Integer> _playerPosition = new HashMap<String, Integer>();
    private Map<String, Integer> _playerThreats = new HashMap<String, Integer>();

    private Map<PhysicalCard, Map<Token, Integer>> _cardTokens = new HashMap<PhysicalCard, Map<Token, Integer>>();

    private Map<String, PhysicalCard> _ringBearers = new HashMap<String, PhysicalCard>();
    private Map<String, PhysicalCard> _rings = new HashMap<String, PhysicalCard>();

    private Map<String, AwaitingDecision> _playerDecisions = new HashMap<String, AwaitingDecision>();

    private List<Assignment> _assignments = new LinkedList<Assignment>();
    private Skirmish _skirmish = null;

    private Set<GameStateListener> _gameStateListeners = new HashSet<GameStateListener>();
    private LinkedList<String> _lastMessages = new LinkedList<String>();

    private int _nextCardId = 0;

    private int nextCardId() {
        return _nextCardId++;
    }

    public void init(PlayerOrder playerOrder, String firstPlayer, Map<String, List<String>> cards, Map<String, String> ringBearers, Map<String, String> rings, LotroCardBlueprintLibrary library, GameStats gameStats) {
        _playerOrder = playerOrder;
        _currentPlayerId = firstPlayer;

        for (Map.Entry<String, List<String>> stringListEntry : cards.entrySet()) {
            String playerId = stringListEntry.getKey();
            List<String> decks = stringListEntry.getValue();

            _adventureDecks.put(playerId, new LinkedList<PhysicalCardImpl>());
            _decks.put(playerId, new LinkedList<PhysicalCardImpl>());
            _hands.put(playerId, new LinkedList<PhysicalCardImpl>());
            _voids.put(playerId, new LinkedList<PhysicalCardImpl>());
            _removed.put(playerId, new LinkedList<PhysicalCardImpl>());
            _discards.put(playerId, new LinkedList<PhysicalCardImpl>());
            _deadPiles.put(playerId, new LinkedList<PhysicalCardImpl>());
            _stacked.put(playerId, new LinkedList<PhysicalCardImpl>());

            addPlayerCards(playerId, decks, library);
            _ringBearers.put(playerId, createPhysicalCard(playerId, library, ringBearers.get(playerId)));
            _rings.put(playerId, createPhysicalCard(playerId, library, rings.get(playerId)));
        }

        for (String playerId : playerOrder.getAllPlayers())
            _playerThreats.put(playerId, 0);

        for (GameStateListener listener : getAllGameStateListeners())
            listener.setPlayerOrder(playerOrder.getAllPlayers());
    }

    private void addPlayerCards(String playerId, List<String> cards, LotroCardBlueprintLibrary library) {
        for (String blueprintId : cards) {
            PhysicalCardImpl physicalCard = createPhysicalCard(playerId, library, blueprintId);
            if (physicalCard.getBlueprint().getCardType() == CardType.SITE) {
                physicalCard.setZone(Zone.ADVENTURE_DECK);
                _adventureDecks.get(playerId).add(physicalCard);
            } else {
                physicalCard.setZone(Zone.DECK);
                _decks.get(playerId).add(physicalCard);
            }
        }
    }

    private PhysicalCardImpl createPhysicalCard(String playerId, LotroCardBlueprintLibrary library, String blueprintId) {
        LotroCardBlueprint card = library.getLotroCardBlueprint(blueprintId);

        int cardId = nextCardId();
        PhysicalCardImpl result = new PhysicalCardImpl(cardId, blueprintId, playerId, card);

        _allCards.put(cardId, result);

        return result;
    }

    public boolean isConsecutiveAction() {
        return _consecutiveAction;
    }

    public void setConsecutiveAction(boolean consecutiveAction) {
        _consecutiveAction = consecutiveAction;
    }

    public void setWearingRing(boolean wearingRing) {
        _wearingRing = wearingRing;
    }

    public boolean isWearingRing() {
        return _wearingRing;
    }

    public PlayerOrder getPlayerOrder() {
        return _playerOrder;
    }

    public void addGameStateListener(String playerId, GameStateListener gameStateListener, GameStats gameStats) {
        _gameStateListeners.add(gameStateListener);
        sendStateToPlayer(playerId, gameStateListener, gameStats);
    }

    public void removeGameStateListener(GameStateListener gameStateListener) {
        _gameStateListeners.remove(gameStateListener);
    }

    private Collection<GameStateListener> getAllGameStateListeners() {
        return Collections.unmodifiableSet(_gameStateListeners);
    }

    private String getPhaseString() {
        if (isFierceSkirmishes())
            return "Fierce " + _currentPhase.getHumanReadable();
        if (isExtraSkirmishes())
            return "Extra " + _currentPhase.getHumanReadable();
        return _currentPhase.getHumanReadable();
    }

    private void sendStateToPlayer(String playerId, GameStateListener listener, GameStats gameStats) {
        if (_playerOrder != null) {
            listener.setPlayerOrder(_playerOrder.getAllPlayers());
            if (_currentPlayerId != null)
                listener.setCurrentPlayerId(_currentPlayerId);
            if (_currentPhase != null)
                listener.setCurrentPhase(getPhaseString());
            listener.setTwilight(_twilightPool);
            for (Map.Entry<String, Integer> stringIntegerEntry : _playerPosition.entrySet())
                listener.setPlayerPosition(stringIntegerEntry.getKey(), stringIntegerEntry.getValue());

            // First non-attached cards
            for (PhysicalCardImpl physicalCard : _inPlay) {
                if (physicalCard.getZone() != Zone.ATTACHED)
                    listener.cardCreated(physicalCard);
            }

            // Now the attached ones
            for (PhysicalCardImpl physicalCard : _inPlay) {
                if (physicalCard.getZone() == Zone.ATTACHED)
                    listener.cardCreated(physicalCard);
            }

            // Finally the stacked ones
            for (List<PhysicalCardImpl> physicalCards : _stacked.values())
                for (PhysicalCardImpl physicalCard : physicalCards)
                    listener.cardCreated(physicalCard);

            List<PhysicalCardImpl> hand = _hands.get(playerId);
            if (hand != null) {
                for (PhysicalCardImpl physicalCard : hand)
                    listener.cardCreated(physicalCard);
            }

            for (List<PhysicalCardImpl> physicalCards : _deadPiles.values())
                for (PhysicalCardImpl physicalCard : physicalCards)
                    listener.cardCreated(physicalCard);

            List<PhysicalCardImpl> discard = _discards.get(playerId);
            if (discard != null) {
                for (PhysicalCardImpl physicalCard : discard)
                    listener.cardCreated(physicalCard);
            }

            List<PhysicalCardImpl> adventureDeck = _adventureDecks.get(playerId);
            if (adventureDeck != null) {
                for (PhysicalCardImpl physicalCard : adventureDeck)
                    listener.cardCreated(physicalCard);
            }

            for (Assignment assignment : _assignments)
                listener.addAssignment(assignment.getFellowshipCharacter(), assignment.getShadowCharacters());

            if (_skirmish != null)
                listener.startSkirmish(_skirmish.getFellowshipCharacter(), _skirmish.getShadowCharacters());

            for (Map.Entry<PhysicalCard, Map<Token, Integer>> physicalCardMapEntry : _cardTokens.entrySet()) {
                PhysicalCard card = physicalCardMapEntry.getKey();
                for (Map.Entry<Token, Integer> tokenIntegerEntry : physicalCardMapEntry.getValue().entrySet()) {
                    Integer count = tokenIntegerEntry.getValue();
                    if (count != null && count > 0)
                        listener.addTokens(card, tokenIntegerEntry.getKey(), count);
                }
            }

            listener.sendGameStats(gameStats);
        }

        for (String lastMessage : _lastMessages)
            listener.sendMessage(lastMessage);

        final AwaitingDecision awaitingDecision = _playerDecisions.get(playerId);
        if (awaitingDecision != null)
            listener.decisionRequired(playerId, awaitingDecision);
    }

    public void sendMessage(String message) {
        _lastMessages.add(message);
        if (_lastMessages.size() > LAST_MESSAGE_STORED_COUNT)
            _lastMessages.removeFirst();
        for (GameStateListener listener : getAllGameStateListeners())
            listener.sendMessage(message);
    }

    public void playerDecisionStarted(String playerId, AwaitingDecision awaitingDecision) {
        _playerDecisions.put(playerId, awaitingDecision);
        for (GameStateListener listener : getAllGameStateListeners())
            listener.decisionRequired(playerId, awaitingDecision);
    }

    public void playerDecisionFinished(String playerId) {
        _playerDecisions.remove(playerId);
    }

    public void transferCard(PhysicalCard card, PhysicalCard transferTo) {
        if (card.getZone() != Zone.ATTACHED)
            ((PhysicalCardImpl) card).setZone(Zone.ATTACHED);

        ((PhysicalCardImpl) card).attachTo((PhysicalCardImpl) transferTo);
        for (GameStateListener listener : getAllGameStateListeners())
            listener.cardMoved(card);
    }

    public void takeControlOfCard(String playerId, LotroGame game, PhysicalCard card, Zone zone) {
        ((PhysicalCardImpl) card).setCardController(playerId);
        ((PhysicalCardImpl) card).setZone(zone);
        if (card.getBlueprint().getCardType() == CardType.SITE)
            startAffectingControlledSite(game, card);
        for (GameStateListener listener : getAllGameStateListeners())
            listener.cardMoved(card);
    }

    public void loseControlOfCard(PhysicalCard card, Zone zone) {
        if (card.getBlueprint().getCardType() == CardType.SITE)
            stopAffectingControlledSite(card);
        ((PhysicalCardImpl) card).setCardController(null);
        ((PhysicalCardImpl) card).setZone(zone);
        for (GameStateListener listener : getAllGameStateListeners())
            listener.cardMoved(card);
    }

    public void attachCard(LotroGame game, PhysicalCard card, PhysicalCard attachTo) {
        ((PhysicalCardImpl) card).attachTo((PhysicalCardImpl) attachTo);
        addCardToZone(game, card, Zone.ATTACHED);
    }

    public void stackCard(LotroGame game, PhysicalCard card, PhysicalCard stackOn) {
        ((PhysicalCardImpl) card).stackOn((PhysicalCardImpl) stackOn);
        addCardToZone(game, card, Zone.STACKED);
    }

    public void cardAffectsCard(String playerPerforming, PhysicalCard card, Collection<PhysicalCard> affectedCards) {
        for (GameStateListener listener : getAllGameStateListeners())
            listener.cardAffectedByCard(playerPerforming, card, affectedCards);
    }

    public void eventPlayed(PhysicalCard card) {
        for (GameStateListener listener : getAllGameStateListeners())
            listener.eventPlayed(card);
    }

    public void activatedCard(String playerPerforming, PhysicalCard card) {
        for (GameStateListener listener : getAllGameStateListeners())
            listener.cardActivated(playerPerforming, card);
    }

    public void setRingBearer(PhysicalCard card) {
        _ringBearers.put(card.getOwner(), card);
    }

    public PhysicalCard getRingBearer(String playerId) {
        return _ringBearers.get(playerId);
    }

    public PhysicalCard getRing(String playerId) {
        return _rings.get(playerId);
    }

    private List<PhysicalCardImpl> getZoneCards(String playerId, CardType type, Zone zone) {
        if (zone == Zone.DECK)
            return _decks.get(playerId);
        else if (zone == Zone.ADVENTURE_DECK)
            return _adventureDecks.get(playerId);
        else if (zone == Zone.DISCARD)
            return _discards.get(playerId);
        else if (zone == Zone.DEAD)
            return _deadPiles.get(playerId);
        else if (zone == Zone.HAND)
            return _hands.get(playerId);
        else if (zone == Zone.VOID)
            return _voids.get(playerId);
        else if (zone == Zone.REMOVED)
            return _removed.get(playerId);
        else if (zone == Zone.STACKED)
            return _stacked.get(playerId);
        else
            return _inPlay;
    }

    public void removeFromSkirmish(PhysicalCard card) {
        removeFromSkirmish(card, true);
    }

    public void replaceInSkirmish(PhysicalCard card) {
        _skirmish.setFellowshipCharacter(card);
        for (GameStateListener gameStateListener : getAllGameStateListeners()) {
            gameStateListener.finishSkirmish();
            gameStateListener.startSkirmish(_skirmish.getFellowshipCharacter(), _skirmish.getShadowCharacters());
        }
    }

    public void replaceInSkirmishMinion(PhysicalCard card, PhysicalCard removeMinion) {
        removeFromSkirmish(removeMinion);
        _skirmish.getShadowCharacters().add(card);
        for (GameStateListener listener : getAllGameStateListeners())
            listener.addToSkirmish(card);
    }

    private void removeFromSkirmish(PhysicalCard card, boolean notify) {
        if (_skirmish.getFellowshipCharacter() == card) {
            _skirmish.setFellowshipCharacter(null);
            _skirmish.addRemovedFromSkirmish(card);
            if (notify)
                for (GameStateListener listener : getAllGameStateListeners())
                    listener.removeFromSkirmish(card);
        }
        if (_skirmish.getShadowCharacters().remove(card)) {
            _skirmish.addRemovedFromSkirmish(card);
            if (notify)
                for (GameStateListener listener : getAllGameStateListeners())
                    listener.removeFromSkirmish(card);
        }
    }

    public void removeCardsFromZone(String playerPerforming, Collection<PhysicalCard> cards) {
        for (PhysicalCard card : cards) {
            List<PhysicalCardImpl> zoneCards = getZoneCards(card.getOwner(), card.getBlueprint().getCardType(), card.getZone());
            if (!zoneCards.contains(card))
                throw new RuntimeException("Card was not found in the expected zone");
        }

        for (PhysicalCard card : cards) {
            Zone zone = card.getZone();

            if (zone.isInPlay())
                if (card.getBlueprint().getCardType() != CardType.SITE || (getCurrentPhase() != Phase.PLAY_STARTING_FELLOWSHIP && getCurrentSite() == card))
                    stopAffecting(card);

            if (zone == Zone.STACKED)
                stopAffectingStacked(card);
            else if (zone == Zone.DISCARD)
                stopAffectingInDiscard(card);

            List<PhysicalCardImpl> zoneCards = getZoneCards(card.getOwner(), card.getBlueprint().getCardType(), zone);
            zoneCards.remove(card);

            if (zone == Zone.ATTACHED)
                ((PhysicalCardImpl) card).attachTo(null);

            if (zone == Zone.STACKED)
                ((PhysicalCardImpl) card).stackOn(null);

            for (Assignment assignment : new LinkedList<Assignment>(_assignments)) {
                if (assignment.getFellowshipCharacter() == card)
                    removeAssignment(assignment);
                if (assignment.getShadowCharacters().remove(card))
                    if (assignment.getShadowCharacters().size() == 0)
                        removeAssignment(assignment);
            }

            if (_skirmish != null)
                removeFromSkirmish(card, false);

            removeAllTokens(card);
            card.setWhileInZoneData(null);
        }

        for (GameStateListener listener : getAllGameStateListeners())
            listener.cardsRemoved(playerPerforming, cards);

        for (PhysicalCard card : cards) {
            ((PhysicalCardImpl) card).setZone(null);
        }
    }

    public void addCardToZone(LotroGame game, PhysicalCard card, Zone zone) {
        addCardToZone(game, card, zone, true);
    }

    private void addCardToZone(LotroGame game, PhysicalCard card, Zone zone, boolean end) {
        if (zone == Zone.DISCARD && game.getModifiersQuerying().hasFlagActive(game.getGameState(), ModifierFlag.REMOVE_CARDS_GOING_TO_DISCARD))
            zone = Zone.REMOVED;

        if (zone.isInPlay())
            assignNewCardId(card);

        List<PhysicalCardImpl> zoneCards = getZoneCards(card.getOwner(), card.getBlueprint().getCardType(), zone);
        if (end)
            zoneCards.add((PhysicalCardImpl) card);
        else
            zoneCards.add(0, (PhysicalCardImpl) card);

        if (card.getZone() != null)
            throw new RuntimeException("Card was in " + card.getZone() + " when tried to add to zone: " + zone);

        ((PhysicalCardImpl) card).setZone(zone);

        if (zone == Zone.ADVENTURE_PATH) {
            for (GameStateListener listener : getAllGameStateListeners())
                listener.setSite(card);
        } else {
            for (GameStateListener listener : getAllGameStateListeners())
                listener.cardCreated(card);
        }

        if (_currentPhase != Phase.PUT_RING_BEARER) {
            if (zone.isInPlay())
                if (card.getBlueprint().getCardType() != CardType.SITE || (getCurrentPhase() != Phase.PLAY_STARTING_FELLOWSHIP && getCurrentSite() == card))
                    startAffecting(game, card);

            if (zone == Zone.STACKED)
                startAffectingStacked(game, card);
            else if (zone == Zone.DISCARD)
                startAffectingInDiscard(game, card);
        }
    }

    private void assignNewCardId(PhysicalCard card) {
        _allCards.remove(card.getCardId());
        int newCardId = nextCardId();
        ((PhysicalCardImpl) card).setCardId(newCardId);
        _allCards.put(newCardId, ((PhysicalCardImpl) card));
    }

    private void removeAllTokens(PhysicalCard card) {
        Map<Token, Integer> map = _cardTokens.get(card);
        if (map != null) {
            for (Map.Entry<Token, Integer> tokenIntegerEntry : map.entrySet())
                if (tokenIntegerEntry.getValue() > 0)
                    for (GameStateListener listener : getAllGameStateListeners())
                        listener.removeTokens(card, tokenIntegerEntry.getKey(), tokenIntegerEntry.getValue());

            map.clear();
        }
    }

    public void shuffleCardsIntoDeck(Collection<? extends PhysicalCard> cards, String playerId) {
        List<PhysicalCardImpl> zoneCards = _decks.get(playerId);

        for (PhysicalCard card : cards) {
            zoneCards.add((PhysicalCardImpl) card);

            ((PhysicalCardImpl) card).setZone(Zone.DECK);
        }

        shuffleDeck(playerId);
    }

    public void putCardOnBottomOfDeck(PhysicalCard card) {
        addCardToZone(null, card, Zone.DECK, true);
    }

    public void putCardOnTopOfDeck(PhysicalCard card) {
        addCardToZone(null, card, Zone.DECK, false);
    }

    public boolean iterateActiveCards(PhysicalCardVisitor physicalCardVisitor) {
        for (PhysicalCardImpl physicalCard : _inPlay) {
            if (isCardInPlayActive(physicalCard))
                if (physicalCardVisitor.visitPhysicalCard(physicalCard))
                    return true;
        }

        return false;
    }

    public boolean iterateActiveTextCards(PhysicalCardVisitor physicalCardVisitor) {
        for (PhysicalCardImpl physicalCard : _inPlay) {
            if (physicalCard.getBlueprint().getCardType() != CardType.SITE || (_currentPhase != Phase.PUT_RING_BEARER && _currentPhase != Phase.PLAY_STARTING_FELLOWSHIP && getCurrentSite() == physicalCard))
                if (isCardInPlayActive(physicalCard))
                    if (physicalCardVisitor.visitPhysicalCard(physicalCard))
                        return true;
        }

        return false;
    }

    public boolean iterateActiveTextCards(String player, PhysicalCardVisitor physicalCardVisitor) {
        if (_currentPhase != Phase.PUT_RING_BEARER && _currentPhase != Phase.PLAY_STARTING_FELLOWSHIP)
            physicalCardVisitor.visitPhysicalCard(getCurrentSite());

        for (PhysicalCardImpl physicalCard : _inPlay) {
            if (physicalCard.getBlueprint().getCardType() != CardType.SITE && physicalCard.getOwner().equals(player) && isCardInPlayActive(physicalCard))
                if (physicalCardVisitor.visitPhysicalCard(physicalCard))
                    return true;
        }
        return false;
    }

    public boolean iterateActivableCards(String player, PhysicalCardVisitor physicalCardVisitor) {
        if (_currentPhase != Phase.PUT_RING_BEARER && _currentPhase != Phase.PLAY_STARTING_FELLOWSHIP)
            if (physicalCardVisitor.visitPhysicalCard(getCurrentSite()))
                return true;
        for (PhysicalCardImpl physicalCard : _inPlay) {
            if (physicalCard.getOwner().equals(player)
                    && physicalCard.getBlueprint().getCardType() != CardType.SITE && isCardInPlayActive(physicalCard))
                if (physicalCardVisitor.visitPhysicalCard(physicalCard))
                    return true;
        }
        for (PhysicalCardImpl physicalCard : _hands.get(player)) {
            if (_currentPlayerId.equals(player)) {
                if (physicalCard.getBlueprint().getSide() == Side.FREE_PEOPLE)
                    if (physicalCardVisitor.visitPhysicalCard(physicalCard))
                        return true;
            } else {
                if (physicalCard.getBlueprint().getSide() == Side.SHADOW)
                    if (physicalCardVisitor.visitPhysicalCard(physicalCard))
                        return true;
            }
        }
        return false;
    }

    public boolean iterateStackedActivableCards(String player, PhysicalCardVisitor physicalCardVisitor) {
        for (PhysicalCardImpl physicalCard : _stacked.get(player)) {
            Side playerSide = player.equals(getCurrentPlayerId()) ? Side.FREE_PEOPLE : Side.SHADOW;
            if (physicalCard.getBlueprint().getSide() == playerSide)
                if (physicalCardVisitor.visitPhysicalCard(physicalCard))
                    return true;
        }
        return false;
    }

    public boolean iterateDiscardActivableCards(String player, PhysicalCardVisitor physicalCardVisitor) {
        for (PhysicalCardImpl physicalCard : _discards.get(player)) {
            Side playerSide = player.equals(getCurrentPlayerId()) ? Side.FREE_PEOPLE : Side.SHADOW;
            if (physicalCard.getBlueprint().getSide() == playerSide)
                if (physicalCardVisitor.visitPhysicalCard(physicalCard))
                    return true;
        }
        return false;
    }

    public PhysicalCard findCardById(int cardId) {
        return _allCards.get(cardId);
    }

    public List<? extends PhysicalCard> getHand(String playerId) {
        return Collections.unmodifiableList(_hands.get(playerId));
    }

    public List<? extends PhysicalCard> getVoid(String playerId) {
        return Collections.unmodifiableList(_voids.get(playerId));
    }

    public List<? extends PhysicalCard> getRemoved(String playerId) {
        return Collections.unmodifiableList(_removed.get(playerId));
    }

    public List<? extends PhysicalCard> getDeck(String playerId) {
        return Collections.unmodifiableList(_decks.get(playerId));
    }

    public List<? extends PhysicalCard> getDiscard(String playerId) {
        return Collections.unmodifiableList(_discards.get(playerId));
    }

    public List<? extends PhysicalCard> getDeadPile(String playerId) {
        return Collections.unmodifiableList(_deadPiles.get(playerId));
    }

    public List<? extends PhysicalCard> getAdventureDeck(String playerId) {
        return Collections.unmodifiableList(_adventureDecks.get(playerId));
    }

    public String getCurrentPlayerId() {
        return _currentPlayerId;
    }

    public int getCurrentSiteNumber() {
        return _playerPosition.get(_currentPlayerId);
    }

    public void setPlayerPosition(String playerId, int i) {
        _playerPosition.put(playerId, i);
        for (GameStateListener listener : getAllGameStateListeners())
            listener.setPlayerPosition(playerId, i);
    }

    public void addThreats(String playerId, int count) {
        _playerThreats.put(playerId, _playerThreats.get(playerId) + count);
    }

    public void removeThreats(String playerId, int count) {
        final int oldThreats = _playerThreats.get(playerId);
        count = Math.min(count, oldThreats);
        _playerThreats.put(playerId, oldThreats - count);
    }

    public void movePlayerToNextSite(LotroGame game) {
        final String currentPlayerId = getCurrentPlayerId();
        final int oldPlayerPosition = getPlayerPosition(currentPlayerId);
        stopAffecting(getCurrentSite());
        setPlayerPosition(currentPlayerId, oldPlayerPosition + 1);
        increaseMoveCount();
        startAffecting(game, getCurrentSite());
    }

    public int getPlayerPosition(String playerId) {
        return _playerPosition.get(playerId);
    }

    public Map<Token, Integer> getTokens(PhysicalCard card) {
        Map<Token, Integer> map = _cardTokens.get(card);
        if (map == null)
            return Collections.emptyMap();
        return Collections.unmodifiableMap(map);
    }

    public int getTokenCount(PhysicalCard physicalCard, Token token) {
        Map<Token, Integer> tokens = _cardTokens.get(physicalCard);
        if (tokens == null)
            return 0;
        Integer count = tokens.get(token);
        if (count == null)
            return 0;
        return count;
    }

    public List<PhysicalCard> getAttachedCards(PhysicalCard card) {
        List<PhysicalCard> result = new LinkedList<PhysicalCard>();
        for (PhysicalCardImpl physicalCard : _inPlay) {
            if (physicalCard.getAttachedTo() != null && physicalCard.getAttachedTo() == card)
                result.add(physicalCard);
        }
        return result;
    }

    public List<PhysicalCard> getStackedCards(PhysicalCard card) {
        List<PhysicalCard> result = new LinkedList<PhysicalCard>();
        for (List<PhysicalCardImpl> physicalCardList : _stacked.values()) {
            for (PhysicalCardImpl physicalCard : physicalCardList) {
                if (physicalCard.getStackedOn() == card)
                    result.add(physicalCard);
            }
        }
        return result;
    }

    public int getWounds(PhysicalCard physicalCard) {
        return getTokenCount(physicalCard, Token.WOUND);
    }

    public void addBurdens(int burdens) {
        addTokens(_ringBearers.get(getCurrentPlayerId()), Token.BURDEN, Math.max(0, burdens));
    }

    public int getBurdens() {
        return getTokenCount(_ringBearers.get(getCurrentPlayerId()), Token.BURDEN);
    }

    public int getPlayerThreats(String playerId) {
        return _playerThreats.get(playerId);
    }

    public int getThreats() {
        return _playerThreats.get(getCurrentPlayerId());
    }

    public void removeBurdens(int burdens) {
        removeTokens(_ringBearers.get(getCurrentPlayerId()), Token.BURDEN, Math.max(0, burdens));
    }

    public void addWound(PhysicalCard card) {
        addTokens(card, Token.WOUND, 1);
    }

    public void removeWound(PhysicalCard card) {
        removeTokens(card, Token.WOUND, 1);
    }

    public void addTokens(PhysicalCard card, Token token, int count) {
        Map<Token, Integer> tokens = _cardTokens.get(card);
        if (tokens == null) {
            tokens = new HashMap<Token, Integer>();
            _cardTokens.put(card, tokens);
        }
        Integer currentCount = tokens.get(token);
        if (currentCount == null)
            tokens.put(token, count);
        else
            tokens.put(token, currentCount + count);

        for (GameStateListener listener : getAllGameStateListeners())
            listener.addTokens(card, token, count);
    }

    public void removeTokens(PhysicalCard card, Token token, int count) {
        Map<Token, Integer> tokens = _cardTokens.get(card);
        if (tokens == null) {
            tokens = new HashMap<Token, Integer>();
            _cardTokens.put(card, tokens);
        }
        Integer currentCount = tokens.get(token);
        if (currentCount != null) {
            if (currentCount < count)
                count = currentCount;

            tokens.put(token, currentCount - count);

            for (GameStateListener listener : getAllGameStateListeners())
                listener.removeTokens(card, token, count);
        }
    }

    public void setTwilight(int twilight) {
        _twilightPool = twilight;
        for (GameStateListener listener : getAllGameStateListeners())
            listener.setTwilight(_twilightPool);
    }

    public int getTwilightPool() {
        return _twilightPool;
    }

    public void startPlayerTurn(String playerId) {
        _currentPlayerId = playerId;
        setTwilight(0);
        _moveCount = 0;
        _fierceSkirmishes = false;

        for (GameStateListener listener : getAllGameStateListeners())
            listener.setCurrentPlayerId(_currentPlayerId);
    }

    public boolean isExtraSkirmishes() {
        return _extraSkirmishes;
    }

    public void setExtraSkirmishes(boolean extraSkirmishes) {
        _extraSkirmishes = extraSkirmishes;
    }

    public void setFierceSkirmishes(boolean value) {
        _fierceSkirmishes = value;
    }

    public boolean isFierceSkirmishes() {
        return _fierceSkirmishes;
    }

    private boolean isCardInPlayActive(PhysicalCard card) {
        Side side = card.getBlueprint().getSide();
        // Either it's not attached or attached to active card
        // AND is a site or fp/ring of current player or shadow of any other player
        if (card.getBlueprint().getCardType() == CardType.SITE)
            return _currentPhase != Phase.PUT_RING_BEARER && _currentPhase != Phase.PLAY_STARTING_FELLOWSHIP;

        if (card.getBlueprint().getCardType() == CardType.THE_ONE_RING)
            return card.getOwner().equals(_currentPlayerId);

        if (card.getOwner().equals(_currentPlayerId) && side == Side.SHADOW)
            return false;

        if (!card.getOwner().equals(_currentPlayerId) && side == Side.FREE_PEOPLE)
            return false;

        if (card.getAttachedTo() != null)
            return isCardInPlayActive(card.getAttachedTo());

        return true;
    }

    public void startAffectingCardsForCurrentPlayer(LotroGame game) {
        // Active non-sites are affecting
        for (PhysicalCardImpl physicalCard : _inPlay) {
            if (isCardInPlayActive(physicalCard) && physicalCard.getBlueprint().getCardType() != CardType.SITE)
                startAffecting(game, physicalCard);
            else if (physicalCard.getBlueprint().getCardType() == CardType.SITE &&
                    physicalCard.getCardController() != null) {
                startAffectingControlledSite(game, physicalCard);
            }
        }

        // Current site is affecting
        if (_currentPhase != Phase.PLAY_STARTING_FELLOWSHIP)
            startAffecting(game, getCurrentSite());

        // Stacked cards on active cards are stack-affecting
        for (List<PhysicalCardImpl> stackedCards : _stacked.values())
            for (PhysicalCardImpl stackedCard : stackedCards)
                if (isCardInPlayActive(stackedCard.getStackedOn()))
                    startAffectingStacked(game, stackedCard);

        for (List<PhysicalCardImpl> discardedCards : _discards.values())
            for (PhysicalCardImpl discardedCard : discardedCards)
                startAffectingInDiscard(game, discardedCard);
    }

    private void startAffectingControlledSite(LotroGame game, PhysicalCard physicalCard) {
        ((PhysicalCardImpl) physicalCard).startAffectingGameControlledSite(game);
    }

    public void reapplyAffectingForCard(LotroGame game, PhysicalCard card) {
        ((PhysicalCardImpl) card).stopAffectingGame();
        ((PhysicalCardImpl) card).startAffectingGame(game);
    }

    public void stopAffectingCardsForCurrentPlayer() {
        for (PhysicalCardImpl physicalCard : _inPlay) {
            if (isCardInPlayActive(physicalCard) && physicalCard.getBlueprint().getCardType() != CardType.SITE)
                stopAffecting(physicalCard);
            else if (physicalCard.getBlueprint().getCardType() == CardType.SITE &&
                    physicalCard.getCardController() != null) {
                stopAffectingControlledSite(physicalCard);
            }
        }

        stopAffecting(getCurrentSite());

        for (List<PhysicalCardImpl> stackedCards : _stacked.values())
            for (PhysicalCardImpl stackedCard : stackedCards)
                if (isCardInPlayActive(stackedCard.getStackedOn()))
                    stopAffectingStacked(stackedCard);

        for (List<PhysicalCardImpl> discardedCards : _discards.values())
            for (PhysicalCardImpl discardedCard : discardedCards)
                stopAffectingInDiscard(discardedCard);
    }

    private void stopAffectingControlledSite(PhysicalCard physicalCard) {
        ((PhysicalCardImpl) physicalCard).stopAffectingGameControlledSite();
    }

    private void startAffecting(LotroGame game, PhysicalCard card) {
        ((PhysicalCardImpl) card).startAffectingGame(game);
    }

    private void stopAffecting(PhysicalCard card) {
        card.removeData();
        ((PhysicalCardImpl) card).stopAffectingGame();
    }

    private void startAffectingStacked(LotroGame game, PhysicalCard card) {
        if (isCardAffectingGame(card))
            ((PhysicalCardImpl) card).startAffectingGameStacked(game);
    }

    private void stopAffectingStacked(PhysicalCard card) {
        if (isCardAffectingGame(card))
            ((PhysicalCardImpl) card).stopAffectingGameStacked();
    }

    private void startAffectingInDiscard(LotroGame game, PhysicalCard card) {
        if (isCardAffectingGame(card))
            ((PhysicalCardImpl) card).startAffectingGameInDiscard(game);
    }

    private void stopAffectingInDiscard(PhysicalCard card) {
        if (isCardAffectingGame(card))
            ((PhysicalCardImpl) card).stopAffectingGameInDiscard();
    }

    private boolean isCardAffectingGame(PhysicalCard card) {
        final Side side = card.getBlueprint().getSide();
        if (side == Side.SHADOW)
            return !getCurrentPlayerId().equals(card.getOwner());
        else if (side == Side.FREE_PEOPLE)
            return getCurrentPlayerId().equals(card.getOwner());
        else
            return false;
    }

    public void setCurrentPhase(Phase phase) {
        _currentPhase = phase;
        for (GameStateListener listener : getAllGameStateListeners())
            listener.setCurrentPhase(getPhaseString());
    }

    public Phase getCurrentPhase() {
        return _currentPhase;
    }

    public PhysicalCard getSite(int siteNumber) {
        for (PhysicalCardImpl physicalCard : _inPlay) {
            LotroCardBlueprint blueprint = physicalCard.getBlueprint();
            if (blueprint.getCardType() == CardType.SITE && physicalCard.getSiteNumber() == siteNumber)
                return physicalCard;
        }
        return null;
    }

    public PhysicalCard getCurrentSite() {
        return getSite(getCurrentSiteNumber());
    }

    public Block getCurrentSiteBlock() {
        return getCurrentSite().getBlueprint().getSiteBlock();
    }

    public void increaseMoveCount() {
        _moveCount++;
    }

    public int getMoveCount() {
        return _moveCount;
    }

    public void addTwilight(int twilight) {
        setTwilight(_twilightPool + Math.max(0, twilight));
    }

    public void removeTwilight(int twilight) {
        setTwilight(_twilightPool - Math.min(Math.max(0, twilight), _twilightPool));
    }

    public void assignToSkirmishes(PhysicalCard fp, Set<PhysicalCard> minions) {
        Assignment assignment = findAssignment(fp);
        if (assignment != null)
            assignment.getShadowCharacters().addAll(minions);
        else
            _assignments.add(new Assignment(fp, new HashSet<PhysicalCard>(minions)));

        for (GameStateListener listener : getAllGameStateListeners())
            listener.addAssignment(fp, minions);
    }

    public void removeAssignment(Assignment assignment) {
        _assignments.remove(assignment);
        for (GameStateListener listener : getAllGameStateListeners())
            listener.removeAssignment(assignment.getFellowshipCharacter());
    }

    public List<Assignment> getAssignments() {
        return _assignments;
    }

    private Assignment findAssignment(PhysicalCard fp) {
        for (Assignment assignment : _assignments)
            if (assignment.getFellowshipCharacter() == fp)
                return assignment;
        return null;
    }

    public void startSkirmish(PhysicalCard fellowshipCharacter, Set<PhysicalCard> shadowCharacters) {
        _skirmish = new Skirmish(fellowshipCharacter, shadowCharacters);
        for (GameStateListener listener : getAllGameStateListeners())
            listener.startSkirmish(_skirmish.getFellowshipCharacter(), _skirmish.getShadowCharacters());
    }

    public void restartSkirmish(Skirmish skirmish) {
        _skirmish = skirmish;
        for (GameStateListener listener : getAllGameStateListeners())
            listener.startSkirmish(_skirmish.getFellowshipCharacter(), _skirmish.getShadowCharacters());
    }

    public Skirmish getSkirmish() {
        return _skirmish;
    }

    public void finishSkirmish() {
        _skirmish = null;
        for (GameStateListener listener : getAllGameStateListeners())
            listener.finishSkirmish();
    }

    public PhysicalCard removeTopDeckCard(String player) {
        List<PhysicalCardImpl> deck = _decks.get(player);
        if (deck.size() > 0) {
            final PhysicalCard topDeckCard = deck.get(0);
            removeCardsFromZone(null, Collections.singleton(topDeckCard));
            return topDeckCard;
        } else {
            return null;
        }
    }

    public PhysicalCard removeBottomDeckCard(String player) {
        List<PhysicalCardImpl> deck = _decks.get(player);
        if (deck.size() > 0) {
            final PhysicalCard topDeckCard = deck.get(deck.size() - 1);
            removeCardsFromZone(null, Collections.singleton(topDeckCard));
            return topDeckCard;
        } else {
            return null;
        }
    }

    public void playerDrawsCard(String player) {
        List<PhysicalCardImpl> deck = _decks.get(player);
        if (deck.size() > 0) {
            PhysicalCard card = deck.get(0);
            removeCardsFromZone(null, Collections.singleton(card));
            addCardToZone(null, card, Zone.HAND);
        }
    }

    public void shuffleDeck(String player) {
        List<PhysicalCardImpl> deck = _decks.get(player);
        Collections.shuffle(deck);
    }

    public void sendGameStats(GameStats gameStats) {
        for (GameStateListener listener : getAllGameStateListeners())
            listener.sendGameStats(gameStats);
    }
}
