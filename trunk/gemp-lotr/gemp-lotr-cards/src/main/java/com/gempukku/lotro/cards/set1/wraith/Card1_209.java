package com.gempukku.lotro.cards.set1.wraith;

import com.gempukku.lotro.cards.AbstractLotroCardBlueprint;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.actions.PlayPermanentAction;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.DefaultCostToEffectAction;
import com.gempukku.lotro.logic.timing.Action;
import com.gempukku.lotro.logic.timing.EffectResult;
import com.gempukku.lotro.logic.timing.UnrespondableEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Shadow
 * Culture: Wraith
 * Twilight Cost: 0
 * Type: Condition
 * Game Text: Plays to your support area. Response: If your Nazgul wins a skirmish, transfer this condition from your
 * support area to the losing character. Limit 1 per character. Wound bearer at the start of each fellowship phase.
 * (If bearer is the Ring-bearer, add a burden instead )
 */
public class Card1_209 extends AbstractLotroCardBlueprint {
    public Card1_209() {
        super(Side.SHADOW, CardType.CONDITION, Culture.WRAITH, "Blade Tip");
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int twilightModifier) {
        return PlayConditions.canPayForShadowCard(game, self, twilightModifier);
    }

    @Override
    public Action getPlayCardAction(String playerId, LotroGame game, PhysicalCard self, int twilightModifier) {
        return new PlayPermanentAction(self, Zone.SHADOW_SUPPORT, twilightModifier);
    }

    @Override
    public int getTwilightCost() {
        return 0;
    }

    @Override
    public List<? extends Action> getOptionalAfterActions(String playerId, LotroGame game, EffectResult effectResult, final PhysicalCard self) {
        if (PlayConditions.winsSkirmish(game.getGameState(), game.getModifiersQuerying(), effectResult, Filters.and(Filters.owner(self.getOwner()), Filters.keyword(Keyword.NAZGUL)))
                && self.getZone() == Zone.SHADOW_SUPPORT
                && game.getGameState().getSkirmish() != null && game.getGameState().getSkirmish().getFellowshipCharacter() != null
                && Filters.canSpot(game.getGameState(), game.getModifiersQuerying(), Filters.sameCard(game.getGameState().getSkirmish().getFellowshipCharacter()), Filters.not(Filters.hasAttached(Filters.name("Blade Tip"))))) {
            DefaultCostToEffectAction action = new DefaultCostToEffectAction(self, null, "Transfer condition to losing character");
            action.addEffect(
                    new UnrespondableEffect() {
                        @Override
                        public void playEffect(LotroGame game) {
                            PhysicalCard fpCharacter = game.getGameState().getSkirmish().getFellowshipCharacter();
                            game.getGameState().attachCard(self, fpCharacter);

                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
