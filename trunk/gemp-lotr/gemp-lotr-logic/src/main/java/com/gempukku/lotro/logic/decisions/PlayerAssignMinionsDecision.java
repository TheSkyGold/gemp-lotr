package com.gempukku.lotro.logic.decisions;

import com.gempukku.lotro.game.PhysicalCard;

import java.util.*;

public abstract class PlayerAssignMinionsDecision extends AbstractAwaitingDecision {
    private List<PhysicalCard> _freeCharacters;
    private List<PhysicalCard> _minions;

    public PlayerAssignMinionsDecision(int id, String text, Collection<PhysicalCard> freeCharacters, Collection<PhysicalCard> minions) {
        super(id, text, AwaitingDecisionType.ASSIGN_MINIONS);
        _freeCharacters = new LinkedList<PhysicalCard>(freeCharacters);
        _minions = new LinkedList<PhysicalCard>(minions);
        setParam("freeCharacters", getCardIds(_freeCharacters));
        setParam("minions", getCardIds(_minions));
    }

    protected Map<PhysicalCard, Set<PhysicalCard>> getAssignmentsBasedOnResponse(String response) throws DecisionResultInvalidException {
        Map<PhysicalCard, Set<PhysicalCard>> assignments = new HashMap<PhysicalCard, Set<PhysicalCard>>();
        if (response.equals(""))
            return assignments;

        Set<PhysicalCard> assignedMinions = new HashSet<PhysicalCard>();

        try {
            String[] groups = response.split(",");
            for (String group : groups) {
                String[] cardIds = group.split(" ");
                PhysicalCard freeCard = getCardId(_freeCharacters, Integer.parseInt(cardIds[0]));
                if (assignments.containsKey(freeCard))
                    throw new DecisionResultInvalidException();

                Set<PhysicalCard> minions = new HashSet<PhysicalCard>();
                for (int i = 1; i < cardIds.length; i++) {
                    PhysicalCard minion = getCardId(_minions, Integer.parseInt(cardIds[i]));
                    if (assignedMinions.contains(minion))
                        throw new DecisionResultInvalidException();
                    minions.add(minion);
                    assignedMinions.add(minion);
                }

                assignments.put(freeCard, minions);
            }
        } catch (NumberFormatException exp) {
            throw new DecisionResultInvalidException();
        }

        return assignments;
    }

    private PhysicalCard getCardId(List<PhysicalCard> physicalCards, int cardId) throws DecisionResultInvalidException {
        for (PhysicalCard physicalCard : physicalCards) {
            if (physicalCard.getCardId() == cardId)
                return physicalCard;
        }
        throw new DecisionResultInvalidException();
    }

    private String[] getCardIds(List<PhysicalCard> cards) {
        String[] result = new String[cards.size()];
        for (int i = 0; i < cards.size(); i++)
            result[i] = String.valueOf(cards.get(i).getCardId());
        return result;
    }
}
