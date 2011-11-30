package com.gempukku.lotro.cards.actions;

import com.gempukku.lotro.cards.effects.PayTwilightCostEffect;
import com.gempukku.lotro.cards.effects.ShuffleDeckEffect;
import com.gempukku.lotro.common.Zone;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.GameUtils;
import com.gempukku.lotro.logic.actions.AbstractCostToEffectAction;
import com.gempukku.lotro.logic.effects.PlayEventEffect;
import com.gempukku.lotro.logic.timing.Effect;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class PlayEventAction extends AbstractCostToEffectAction {
    private PhysicalCard _eventPlayed;
    private boolean _requiresRanger;

    private boolean _cardRemoved;

    private Iterator<Effect> _preCostIterator;

    private PlayEventEffect _playCardEffect;
    private boolean _cardPlayed;

    private boolean _cardDiscarded;

    private boolean _skipDiscardCard;

    private String _text;

    public PlayEventAction(PhysicalCard card) {
        this(card, false);
    }

    public PlayEventAction(PhysicalCard card, boolean requiresRanger) {
        _eventPlayed = card;
        _requiresRanger = requiresRanger;

        List<Effect> preCostEffects = new LinkedList<Effect>();
        appendCost(new PayTwilightCostEffect(card));
        if (card.getZone() == Zone.DECK)
            preCostEffects.add(new ShuffleDeckEffect(card.getOwner()));

        _preCostIterator = preCostEffects.iterator();

        _playCardEffect = new PlayEventEffect(card.getZone(), card, requiresRanger);

        _text = "Play " + _eventPlayed.getBlueprint().getName();
    }

    public PhysicalCard getEventPlayed() {
        return _eventPlayed;
    }

    @Override
    public Type getType() {
        return Type.PLAY_CARD;
    }

    public boolean isRequiresRanger() {
        return _requiresRanger;
    }

    public void skipDiscardPart() {
        _skipDiscardCard = true;
    }

    @Override
    public ActionSource getActionSource() {
        return new ActionSource(_eventPlayed);
    }

    @Override
    public PhysicalCard getActionAttachedToCard() {
        return _eventPlayed;
    }

    public void setText(String text) {
        _text = text;
    }

    @Override
    public String getText(LotroGame game) {
        return _text;
    }

    @Override
    public Effect nextEffect(LotroGame game) {
        if (!_cardRemoved) {
            _cardRemoved = true;
            game.getGameState().sendMessage(_eventPlayed.getOwner() + " plays " + GameUtils.getCardLink(_eventPlayed) + " from " + _eventPlayed.getZone().getHumanReadable());
            boolean fromHand = (_eventPlayed.getZone() == Zone.HAND);
            game.getGameState().removeCardsFromZone(_eventPlayed.getOwner(), Collections.singleton(_eventPlayed));
            if (fromHand)
                game.getGameState().addCardToZone(game, _eventPlayed, Zone.VOID);
            game.getGameState().eventPlayed(_eventPlayed);
        }

        if (_preCostIterator.hasNext())
            return _preCostIterator.next();

        if (!isCostFailed()) {
            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            if (!_cardPlayed) {
                _cardPlayed = true;
                return _playCardEffect;
            }

            if (!_playCardEffect.getPlayEventResult().isEventCancelled()) {
                Effect effect = getNextEffect();
                if (effect != null)
                    return effect;
            }
        }

        if (!_cardDiscarded && !_skipDiscardCard) {
            _cardDiscarded = true;
            if (_eventPlayed.getZone() != null)
                game.getGameState().removeCardsFromZone(_eventPlayed.getOwner(), Collections.singleton(_eventPlayed));
            final Zone targetZone = _playCardEffect.getPlayEventResult().getTargetZone();
            if (targetZone != null)
                game.getGameState().addCardToZone(game, _eventPlayed, targetZone);
        }

        return null;
    }
}
