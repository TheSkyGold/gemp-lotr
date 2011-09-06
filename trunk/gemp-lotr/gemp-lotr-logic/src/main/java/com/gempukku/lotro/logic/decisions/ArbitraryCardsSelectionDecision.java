package com.gempukku.lotro.logic.decisions;

import com.gempukku.lotro.game.PhysicalCard;

import java.util.LinkedList;
import java.util.List;

public abstract class ArbitraryCardsSelectionDecision extends AbstractAwaitingDecision {
    private List<PhysicalCard> _physicalCards;
    private List<PhysicalCard> _selectable;
    private int _minimum;
    private int _maximum;

    public ArbitraryCardsSelectionDecision(int id, String text, List<PhysicalCard> physicalCard) {
        this(id, text, physicalCard, 0, physicalCard.size());
    }

    public ArbitraryCardsSelectionDecision(int id, String text, List<PhysicalCard> physicalCards, int minimum, int maximum) {
        this(id, text, physicalCards, physicalCards, minimum, maximum);
    }

    public ArbitraryCardsSelectionDecision(int id, String text, List<PhysicalCard> physicalCards, List<PhysicalCard> selectable, int minimum, int maximum) {
        super(id, text, AwaitingDecisionType.ARBITRARY_CARDS);
        _physicalCards = physicalCards;
        _selectable = selectable;
        _minimum = minimum;
        _maximum = maximum;
        setParam("min", String.valueOf(minimum));
        setParam("max", String.valueOf(maximum));
        setParam("cardId", getCardIds(physicalCards));
        setParam("blueprintId", getBlueprintIds(physicalCards));
        setParam("selectable", getSelectable(physicalCards, selectable));
    }

    private String[] getSelectable(List<PhysicalCard> physicalCards, List<PhysicalCard> selectable) {
        String[] result = new String[physicalCards.size()];
        for (int i = 0; i < physicalCards.size(); i++)
            result[i] = String.valueOf(selectable.contains(physicalCards.get(i)));
        return result;
    }

    private String[] getCardIds(List<PhysicalCard> physicalCards) {
        String[] result = new String[physicalCards.size()];
        for (int i = 0; i < physicalCards.size(); i++)
            result[i] = "temp" + i;
        return result;
    }

    private String[] getBlueprintIds(List<PhysicalCard> physicalCards) {
        String[] result = new String[physicalCards.size()];
        for (int i = 0; i < physicalCards.size(); i++)
            result[i] = physicalCards.get(i).getBlueprintId();
        return result;
    }

    protected List<PhysicalCard> getSelectedCardsByResponse(String response) throws DecisionResultInvalidException {
        String[] cardIds;
        if (response.equals(""))
            cardIds = new String[0];
        else
            cardIds = response.split(" ");

        if (cardIds.length < _minimum || cardIds.length > _maximum)
            throw new DecisionResultInvalidException();

        List<PhysicalCard> result = new LinkedList<PhysicalCard>();
        try {
            for (String cardId : cardIds) {
                PhysicalCard card = _physicalCards.get(Integer.parseInt(cardId.substring(4)));
                if (result.contains(card) || !_selectable.contains(card))
                    throw new DecisionResultInvalidException();
                result.add(card);
            }
        } catch (NumberFormatException e) {
            throw new DecisionResultInvalidException();
        } catch (IndexOutOfBoundsException e) {
            throw new DecisionResultInvalidException();
        }

        return result;
    }
}

