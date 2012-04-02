package com.gempukku.lotro.cards.set1.wraith;

import com.gempukku.lotro.cards.AbstractMinion;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.SelfExertEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.game.state.Skirmish;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.effects.ChooseActiveCardEffect;
import com.gempukku.lotro.logic.effects.DiscardCardsFromPlayEffect;
import com.gempukku.lotro.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Shadow
 * Culture: Wraith
 * Twilight Cost: 5
 * Type: Minion • Nazgul
 * Strength: 10
 * Vitality: 3
 * Site: 3
 * Game Text: Fierce. Skirmish: Exert Úlairë Cantëa to discard a weapon borne by a character he is skirmishing.
 */
public class Card1_230 extends AbstractMinion {
    public Card1_230() {
        super(5, 10, 3, 3, Race.NAZGUL, Culture.WRAITH, Names.cantea, "Lieutenant of Dol Guldur", true);
        addKeyword(Keyword.FIERCE);
    }

    @Override
    protected List<? extends Action> getExtraPhaseActions(String playerId, LotroGame game, final PhysicalCard self) {
        if (PlayConditions.canUseShadowCardDuringPhase(game, Phase.SKIRMISH, self, 0)
                && PlayConditions.canExert(self, game, self)) {
            final ActivateCardAction action = new ActivateCardAction(self);
            action.appendCost(
                    new SelfExertEffect(self));
            Skirmish skirmish = game.getGameState().getSkirmish();
            if (skirmish != null && skirmish.getShadowCharacters().contains(self)) {
                action.appendEffect(
                        new ChooseActiveCardEffect(self, playerId, "Choose a weapon borne by a character he is skirmishing", Filters.weapon, Filters.attachedTo(Filters.sameCard(skirmish.getFellowshipCharacter()))) {
                            @Override
                            protected void cardSelected(LotroGame game, PhysicalCard weapon) {
                                action.appendEffect(
                                        new DiscardCardsFromPlayEffect(self, weapon));
                            }
                        });
            }
            return Collections.singletonList(action);
        }
        return null;
    }
}
