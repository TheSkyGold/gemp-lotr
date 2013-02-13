package com.gempukku.lotro.game;

import com.gempukku.lotro.common.Block;
import com.gempukku.lotro.logic.vo.LotroDeck;

import java.util.List;

public interface LotroFormat {
    public boolean isOrderedSites();

    public boolean canCancelRingBearerSkirmish();

    public boolean hasRuleOfFour();

    public boolean hasMulliganRule();

    public boolean winAtEndOfRegroup();

    public String getName();

    public void validateCard(String cardId) throws DeckInvalidException;

    public void validateDeck(LotroDeck deck) throws DeckInvalidException;

    public List<Integer> getValidSets();

    public List<String> getBannedCards();

    public List<String> getRestrictedCards();

    public List<String> getValidCards();

    public Block getSiteBlock();
}
