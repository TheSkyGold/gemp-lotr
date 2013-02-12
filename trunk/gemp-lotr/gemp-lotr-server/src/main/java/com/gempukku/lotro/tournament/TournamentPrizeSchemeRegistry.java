package com.gempukku.lotro.tournament;

public class TournamentPrizeSchemeRegistry {
    public TournamentPrizes getTournamentPrizes(String prizesScheme) {
        if (prizesScheme == null || prizesScheme.equals("none"))
            return new NoPrizes();
        
        if (prizesScheme.equals("onDemand"))
            return new SingleEliminationOnDemandPrizes("onDemand");

        if (prizesScheme.equals("daily"))
            return new DailyTournamentPrizes("daily");

        return null;
    }
}