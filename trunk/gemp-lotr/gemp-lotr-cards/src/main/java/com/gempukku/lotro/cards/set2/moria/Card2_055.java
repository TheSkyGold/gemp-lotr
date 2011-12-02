package com.gempukku.lotro.cards.set2.moria;

import com.gempukku.lotro.cards.AbstractPermanent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.effects.ChooseActiveCardEffect;
import com.gempukku.lotro.logic.effects.PlaySiteEffect;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.modifiers.StrengthModifier;
import com.gempukku.lotro.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Mines of Moria
 * Side: Shadow
 * Culture: Moria
 * Twilight Cost: 1
 * Type: Condition
 * Game Text: Plays to your support area. Each unique [MORIA] minion is strength +1. Shadow: Spot an opponent's site
 * to replace it with your marsh or underground site of the same number.
 */
public class Card2_055 extends AbstractPermanent {
    public Card2_055() {
        super(Side.SHADOW, 1, CardType.CONDITION, Culture.MORIA, Zone.SUPPORT, "Dark Places");
    }

    @Override
    public List<? extends Modifier> getAlwaysOnModifiers(LotroGame game, PhysicalCard self) {
        return Collections.singletonList(
                new StrengthModifier(self,
                        Filters.and(
                                Culture.MORIA,
                                CardType.MINION,
                                Filters.unique
                        ), 1));
    }


    @Override
    protected List<? extends Action> getExtraPhaseActions(final String playerId, final LotroGame game, final PhysicalCard self) {
        if (PlayConditions.canUseShadowCardDuringPhase(game, Phase.SHADOW, self, 0)
                && Filters.canSpot(game.getGameState(), game.getModifiersQuerying(), CardType.SITE, Filters.not(Filters.owner(playerId)))) {
            final ActivateCardAction action = new ActivateCardAction(self);
            action.appendEffect(
                    new ChooseActiveCardEffect(self, playerId, "Choose opponent's site", CardType.SITE, Filters.not(Filters.owner(playerId))) {
                        @Override
                        protected void cardSelected(LotroGame game, PhysicalCard card) {
                            int siteNumber = card.getBlueprint().getSiteNumber();
                            action.appendEffect(
                                    new PlaySiteEffect(action, playerId, Block.FELLOWSHIP, siteNumber, Filters.or(Keyword.MARSH, Keyword.UNDERGROUND)));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
