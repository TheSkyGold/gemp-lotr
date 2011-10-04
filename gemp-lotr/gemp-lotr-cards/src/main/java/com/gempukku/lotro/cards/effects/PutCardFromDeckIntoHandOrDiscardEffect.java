package com.gempukku.lotro.cards.effects;

import com.gempukku.lotro.common.Zone;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.timing.AbstractEffect;
import com.gempukku.lotro.logic.timing.EffectResult;
import com.gempukku.lotro.logic.timing.results.DrawCardOrPutIntoHandResult;

public class PutCardFromDeckIntoHandOrDiscardEffect extends AbstractEffect {
    private PhysicalCard _physicalCard;

    public PutCardFromDeckIntoHandOrDiscardEffect(PhysicalCard physicalCard) {
        _physicalCard = physicalCard;
    }

    public PhysicalCard getCard() {
        return _physicalCard;
    }

    @Override
    public EffectResult.Type getType() {
        return EffectResult.Type.DRAW_CARD_OR_PUT_INTO_HAND;
    }

    @Override
    public String getText(LotroGame game) {
        return "Put card from deck into hand";
    }

    @Override
    public boolean isPlayableInFull(LotroGame game) {
        return _physicalCard.getZone() == Zone.DECK;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(LotroGame game) {
        if (_physicalCard.getZone() == Zone.DECK) {
            if (game.getModifiersQuerying().canDrawCardAndIncrement(game.getGameState(), _physicalCard.getOwner())) {
                game.getGameState().sendMessage(_physicalCard.getOwner() + " puts card from deck into his or her hand");
                game.getGameState().removeCardFromZone(_physicalCard);
                game.getGameState().addCardToZone(_physicalCard, Zone.HAND);
                return new FullEffectResult(new EffectResult[]{new DrawCardOrPutIntoHandResult(_physicalCard.getOwner(), 1)}, true, true);
            } else {
                game.getGameState().sendMessage(_physicalCard.getOwner() + " discards " + _physicalCard.getBlueprint().getName() + " from deck");
                game.getGameState().removeCardFromZone(_physicalCard);
                game.getGameState().addCardToZone(_physicalCard, Zone.DISCARD);
            }
        }
        return new FullEffectResult(null, false, false);
    }
}
