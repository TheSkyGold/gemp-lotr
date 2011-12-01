package com.gempukku.lotro.cards.actions;

import com.gempukku.lotro.cards.effects.DiscountEffect;
import com.gempukku.lotro.cards.effects.PayTwilightCostEffect;
import com.gempukku.lotro.cards.effects.ShuffleDeckEffect;
import com.gempukku.lotro.cards.effects.discount.ToilDiscountEffect;
import com.gempukku.lotro.common.Keyword;
import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.common.Zone;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.GameUtils;
import com.gempukku.lotro.logic.actions.AbstractCostToEffectAction;
import com.gempukku.lotro.logic.effects.PlayCardEffect;
import com.gempukku.lotro.logic.effects.SendMessageEffect;
import com.gempukku.lotro.logic.timing.Effect;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class PlayPermanentAction extends AbstractCostToEffectAction implements DiscountableAction {
    private PhysicalCard _permanentPlayed;
    private Zone _zone;
    private int _twilightModifier;
    private boolean _ignoreRoamingPenalty;

    private boolean _cardRemoved;

    private Iterator<Effect> _preCostIterator;

    private Effect _playCardEffect;
    private boolean _cardPlayed;

    private boolean _cardDiscarded;

    private boolean _discountResolved;
    private DiscountEffect _discountEffect;

    private boolean _discountApplied;

    public PlayPermanentAction(PhysicalCard card, Zone zone, int twilightModifier, boolean ignoreRoamingPenalty) {
        _permanentPlayed = card;
        _zone = zone;
        _twilightModifier = twilightModifier;
        _ignoreRoamingPenalty = ignoreRoamingPenalty;

        List<Effect> preCostEffects = new LinkedList<Effect>();
        preCostEffects.add(new SendMessageEffect(card.getOwner() + " plays " + GameUtils.getCardLink(card) + " from " + card.getZone().getHumanReadable()));

        if (card.getZone() == Zone.DECK)
            preCostEffects.add(new ShuffleDeckEffect(card.getOwner()));

        _preCostIterator = preCostEffects.iterator();

        PhysicalCard playedFromCard = null;
        if (card.getZone() == Zone.STACKED)
            playedFromCard = card.getStackedOn();
        else if (card.getZone() == Zone.ATTACHED)
            playedFromCard = card.getAttachedTo();

        _playCardEffect = new PlayCardEffect(card.getZone(), card, zone, playedFromCard);
    }

    @Override
    public Type getType() {
        return Type.PLAY_CARD;
    }

    @Override
    public void setDiscountEffect(DiscountEffect discountEffect) {
        _discountEffect = discountEffect;
    }

    @Override
    public PhysicalCard getActionSource() {
        return _permanentPlayed;
    }

    @Override
    public PhysicalCard getActionAttachedToCard() {
        return _permanentPlayed;
    }

    @Override
    public String getText(LotroGame game) {
        return "Play " + _permanentPlayed.getBlueprint().getName();
    }

    @Override
    public Effect nextEffect(LotroGame game) {
        if (_preCostIterator.hasNext())
            return _preCostIterator.next();

        if (!_cardRemoved) {
            _cardRemoved = true;
            boolean fromHand = (_permanentPlayed.getZone() == Zone.HAND);
            game.getGameState().removeCardsFromZone(_permanentPlayed.getOwner(), Collections.singleton(_permanentPlayed));
            if (fromHand)
                game.getGameState().addCardToZone(game, _permanentPlayed, Zone.VOID);
        }

        if (!_discountResolved) {
            _discountResolved = true;
            if (_discountEffect == null) {
                int toilCount = game.getModifiersQuerying().getKeywordCount(game.getGameState(), _permanentPlayed, Keyword.TOIL);
                if (toilCount > 0) {
                    _discountEffect = new ToilDiscountEffect(this, _permanentPlayed, _permanentPlayed.getOwner(), _permanentPlayed.getBlueprint().getCulture(), toilCount);
                }
            }
            if (_discountEffect != null) {
                int requiredDiscount = 0;
                if (_permanentPlayed.getBlueprint().getSide() == Side.SHADOW) {
                    int twilightCost = game.getModifiersQuerying().getTwilightCost(game.getGameState(), _permanentPlayed, _ignoreRoamingPenalty);
                    int underPool = twilightCost - game.getGameState().getTwilightPool();
                    if (underPool > 0)
                        requiredDiscount = underPool;
                }
                _discountEffect.setMinimalRequiredDiscount(requiredDiscount);
                return _discountEffect;
            }
        }

        if (!_discountApplied) {
            _discountApplied = true;
            if (_discountEffect != null)
                _twilightModifier -= _discountEffect.getDiscountPaidFor();
            insertCost(new PayTwilightCostEffect(_permanentPlayed, _twilightModifier, _ignoreRoamingPenalty));
        }

        if (!isCostFailed()) {
            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            if (!_cardPlayed) {
                _cardPlayed = true;
                return _playCardEffect;
            }

            Effect effect = getNextEffect();
            if (effect != null)
                return effect;
        } else {
            if (!_cardDiscarded) {
                _cardDiscarded = true;
                if (_permanentPlayed.getZone() != null)
                    game.getGameState().removeCardsFromZone(_permanentPlayed.getOwner(), Collections.singleton(_permanentPlayed));
                game.getGameState().addCardToZone(game, _permanentPlayed, Zone.DISCARD);
            }
        }

        return null;
    }

    public boolean wasSuccessful() {
        return _cardPlayed && _playCardEffect.wasSuccessful();
    }

    public boolean wasCarriedOut() {
        return _cardPlayed && _playCardEffect.wasCarriedOut();
    }
}
