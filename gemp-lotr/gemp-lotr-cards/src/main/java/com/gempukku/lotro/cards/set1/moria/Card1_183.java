package com.gempukku.lotro.cards.set1.moria;

import com.gempukku.lotro.cards.AbstractPermanent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.ChooseArbitraryCardsEffect;
import com.gempukku.lotro.cards.effects.StackCardFromPlayEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.DefaultCostToEffectAction;
import com.gempukku.lotro.logic.effects.ChooseActiveCardEffect;
import com.gempukku.lotro.logic.timing.Action;
import com.gempukku.lotro.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Shadow
 * Culture: Moria
 * Twilight Cost: 1
 * Type: Condition
 * Game Text: Plays to your support area. Response: If your [MORIA] Orc wins a skirmish, discard cards and wounds on
 * that Orc and stack that Orc on this condition. Shadow: Play an Orc stacked here as if played from hand.
 */
public class Card1_183 extends AbstractPermanent {
    public Card1_183() {
        super(Side.SHADOW, 1, CardType.CONDITION, Culture.MORIA, Zone.SHADOW_SUPPORT, "Goblin Swarms");
    }

    @Override
    public List<? extends Action> getExtraPhaseActions(final String playerId, final LotroGame game, PhysicalCard self) {
        List<PhysicalCard> stackedCards = game.getGameState().getStackedCards(self);
        if (PlayConditions.canUseShadowCardDuringPhase(game.getGameState(), Phase.SHADOW, self, 0)
                && Filters.filter(stackedCards, game.getGameState(), game.getModifiersQuerying(), Filters.playable(game)).size() > 0) {
            DefaultCostToEffectAction action = new DefaultCostToEffectAction(self, null, "Play an Orc stacked here as if played from hand.");
            action.addEffect(
                    new ChooseArbitraryCardsEffect(playerId, "Choose an Orc to play", stackedCards, Filters.playable(game), 1, 1) {
                        @Override
                        protected void cardsSelected(List<PhysicalCard> stackedOrcs) {
                            PhysicalCard stackedOrc = stackedOrcs.get(0);
                            game.getActionsEnvironment().addActionToStack(stackedOrc.getBlueprint().getPlayCardAction(playerId, game, stackedOrc, 0));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    public List<? extends Action> getOptionalAfterActions(String playerId, LotroGame game, EffectResult effectResult, final PhysicalCard self) {
        if (PlayConditions.winsSkirmish(game.getGameState(), game.getModifiersQuerying(), effectResult, Filters.and(Filters.culture(Culture.MORIA), Filters.keyword(Keyword.ORC)))) {
            List<PhysicalCard> shadowCharacters = game.getGameState().getSkirmish().getShadowCharacters();
            final DefaultCostToEffectAction action = new DefaultCostToEffectAction(self, null, "Stack winning MORIA Orc on this condition");
            action.addEffect(
                    new ChooseActiveCardEffect(playerId, "Choose a MORIA Orc", Filters.culture(Culture.MORIA), Filters.keyword(Keyword.ORC), Filters.in(shadowCharacters)) {
                        @Override
                        protected void cardSelected(PhysicalCard moriaOrc) {
                            action.addEffect(new StackCardFromPlayEffect(moriaOrc, self));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
