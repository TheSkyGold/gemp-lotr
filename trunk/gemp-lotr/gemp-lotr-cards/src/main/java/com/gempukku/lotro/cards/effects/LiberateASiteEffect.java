package com.gempukku.lotro.cards.effects;

import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.common.Zone;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.GameUtils;
import com.gempukku.lotro.logic.timing.AbstractEffect;
import com.gempukku.lotro.logic.timing.Effect;
import com.gempukku.lotro.logic.timing.results.DiscardCardsFromPlayResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LiberateASiteEffect extends AbstractEffect {
    private PhysicalCard _source;
    private String _playerId;

    public LiberateASiteEffect(PhysicalCard source) {
        this(source, source.getOwner());
    }

    public LiberateASiteEffect(PhysicalCard source, String playerId) {
        _source = source;
        _playerId = playerId;
    }

    @Override
    public boolean isPlayableInFull(LotroGame game) {
        return PlayConditions.canLiberateASite(game, _source.getOwner(), _source, _playerId);
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
            if (_playerId == null) {
                if (site != null && site.getCardController() != null
                        && !site.getCardController().equals(game.getGameState().getCurrentPlayerId()))
                    return site;
            } else {
                if (site != null && site.getCardController() != null && site.getCardController().equals(_playerId))
                    return site;
            }
        }

        return null;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(LotroGame game) {
        if (isPlayableInFull(game)) {
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
                    game.getActionsEnvironment().emitEffectResult(new DiscardCardsFromPlayResult(null, discardedCard));

                game.getGameState().sendMessage(_playerId + " liberated a " + GameUtils.getCardLink(siteToLiberate) + " using " + GameUtils.getCardLink(_source));

                liberatedSiteCallback(siteToLiberate);

                return new FullEffectResult(true);
            }
        }
        return new FullEffectResult(false);
    }

    public void liberatedSiteCallback(PhysicalCard liberatedSite) {
    }
}
