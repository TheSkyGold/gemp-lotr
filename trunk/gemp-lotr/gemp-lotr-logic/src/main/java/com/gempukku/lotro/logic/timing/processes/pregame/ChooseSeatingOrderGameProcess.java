package com.gempukku.lotro.logic.timing.processes.pregame;

import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.PlayerOrder;
import com.gempukku.lotro.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.lotro.logic.timing.PlayerOrderFeedback;
import com.gempukku.lotro.logic.timing.processes.GameProcess;

import java.util.*;

public class ChooseSeatingOrderGameProcess implements GameProcess {
    private Map<String, Integer> _bids;
    private PlayerOrderFeedback _playerOrderFeedback;

    private Iterator<String> _biddingOrderPlayers;
    private String[] _orderedPlayers;
    private boolean _sentBids;

    public ChooseSeatingOrderGameProcess(Map<String, Integer> bids, PlayerOrderFeedback playerOrderFeedback) {
        _bids = bids;
        _playerOrderFeedback = playerOrderFeedback;

        ArrayList<String> participantList = new ArrayList<String>(bids.keySet());
        Collections.shuffle(participantList);

        Collections.sort(participantList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return _bids.get(o2) - _bids.get(o1);
            }
        });

        _biddingOrderPlayers = participantList.iterator();
        _orderedPlayers = new String[participantList.size()];
    }

    @Override
    public void process(LotroGame game) {
        if (!_sentBids) {
            _sentBids = true;
            for (Map.Entry<String, Integer> playerBid : _bids.entrySet())
                game.getGameState().sendMessage(playerBid.getKey() + " bid " + playerBid.getValue());
        }
        checkForNextSeating(game);
    }

    private void checkForNextSeating(LotroGame game) {
        String[] emptySeats = getEmptySeatNumbers();
        if (emptySeats.length > 1)
            askNextPlayerToChoosePlace(game, emptySeats);
        else {
            _orderedPlayers[Integer.parseInt(emptySeats[0]) - 1] = _biddingOrderPlayers.next();
            _playerOrderFeedback.setPlayerOrder(new PlayerOrder(Arrays.asList(_orderedPlayers)), _orderedPlayers[0]);
        }
    }

    private String[] getEmptySeatNumbers() {
        List<String> result = new LinkedList<String>();
        for (int i = 0; i < _orderedPlayers.length; i++)
            if (_orderedPlayers[i] == null)
                result.add(String.valueOf(i + 1));
        return result.toArray(new String[result.size()]);
    }

    private void participantHasChosenSeat(LotroGame game, String participant, int placeNo) {
        _orderedPlayers[placeNo - 1] = participant;

        checkForNextSeating(game);
    }

    private void askNextPlayerToChoosePlace(final LotroGame game, String[] emptySeatNumbers) {
        final String playerId = _biddingOrderPlayers.next();
        game.getUserFeedback().sendAwaitingDecision(playerId,
                new MultipleChoiceAwaitingDecision(1, "Choose a seat number at the table", emptySeatNumbers) {
                    @Override
                    protected void validDecisionMade(int index, String result) {
                        int seatNo = Integer.parseInt(result);
                        game.getGameState().sendMessage(playerId + " chosen seat number " + seatNo);
                        participantHasChosenSeat(game, playerId, seatNo);
                    }
                }
        );
    }

    @Override
    public GameProcess getNextProcess() {
        return new FirstPlayerPlaysSiteGameProcess(_bids, _orderedPlayers[0]);
    }
}
