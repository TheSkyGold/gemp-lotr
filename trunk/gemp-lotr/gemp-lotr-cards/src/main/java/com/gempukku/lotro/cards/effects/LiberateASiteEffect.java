package com.gempukku.lotro.cards.effects;

import com.gempukku.lotro.common.Zone;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.timing.AbstractEffect;
import com.gempukku.lotro.logic.timing.Effect;
import com.gempukku.lotro.logic.timing.results.DiscardCardsFromPlayResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LiberateASiteEffect extends AbstractEffect {
    private PhysicalCard _source;

    public LiberateASiteEffect(PhysicalCard source) {
        _source = source;
    }

    @Override
    public boolean isPlayableInFull(LotroGame game) {
        return Filters.countActive(game.getGameState(), game.getModifiersQuerying(), Filters.siteControlledByShadowPlayer(game.getGameState().getCurrentPlayerId())) > 0;
    }

    @Override
    public String getText(LotroGame game) {
        return "Liberate a site";
    }

    @Override
    public Effect.Type getType() {
        return null;
    }

    private PhysicalCard getSiteToLiberate(LotroGame game) {
        int maxUnoccupiedSite = Integer.MAX_VALUE;
        for (String playerId : game.getGameState().getPlayerOrder().getAllPlayers())
            maxUnoccupiedSite = Math.min(maxUnoccupiedSite, game.getGameState().getPlayerPosition(playerId) - 1);

        for (int i = maxUnoccupiedSite; i >= 1; i--) {
            PhysicalCard site = game.getGameState().getSite(i);
            if (site != null && site.getCardController() != null
                    && !site.getCardController().equals(game.getGameState().getCurrentPlayerId()))
                return site;
        }

        return null;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(LotroGame game) {
        PhysicalCard siteToLiberate = getSiteToLiberate(game);
        if (siteToLiberate != null) {
            Set<PhysicalCard> cardsToRemove = new HashSet<PhysicalCard>();
            Set<PhysicalCard> discardedCards = new HashSet<PhysicalCard>();

            List<PhysicalCard> stackedCards = game.getGameState().getStackedCards(siteToLiberate);
            cardsToRemove.addAll(stackedCards);

            List<PhysicalCard> attachedCards = game.getGameState().getAttachedCards(siteToLiberate);
            cardsToRemove.addAll(attachedCards);
            discardedCards.addAll(attachedCards);

            game.getGameState().removeCardsFromZone(_source.getOwner(), cardsToRemove);
            for (PhysicalCard removedCard : cardsToRemove)
                game.getGameState().addCardToZone(game, removedCard, Zone.DISCARD);

            game.getGameState().loseControlOfCard(siteToLiberate, Zone.ADVENTURE_PATH);

            for (PhysicalCard discardedCard : discardedCards)
                game.getActionsEnvironment().emitEffectResult(new DiscardCardsFromPlayResult(discardedCard));

            liberatedSiteCallback(siteToLiberate);

            return new FullEffectResult(true, true);
        }
        return new FullEffectResult(false, false);
    }

    public void liberatedSiteCallback(PhysicalCard liberatedSite) {
    }
}
