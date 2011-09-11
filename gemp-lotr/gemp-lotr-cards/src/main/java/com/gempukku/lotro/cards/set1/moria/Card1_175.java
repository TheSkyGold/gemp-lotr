package com.gempukku.lotro.cards.set1.moria;

import com.gempukku.lotro.cards.AbstractPermanent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.ExhaustCharacterEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.DefaultCostToEffectAction;
import com.gempukku.lotro.logic.timing.Action;
import com.gempukku.lotro.logic.timing.EffectResult;
import com.gempukku.lotro.logic.timing.results.PlayCardResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Shadow
 * Culture: Moria
 * Twilight Cost: 1
 * Type: Condition
 * Game Text: Search. To play, spot a [MORIA] minion. Plays to your support area. Each time a companion is played to
 * site 4 or higher, that companion comes into play exhausted.
 */
public class Card1_175 extends AbstractPermanent {
    public Card1_175() {
        super(Side.SHADOW, 1, CardType.CONDITION, Culture.MORIA, Zone.SHADOW_SUPPORT, "Goblin Domain");
        addKeyword(Keyword.SEARCH);
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int twilightModifier) {
        return super.checkPlayRequirements(playerId, game, self, twilightModifier)
                && Filters.canSpot(game.getGameState(), game.getModifiersQuerying(), Filters.culture(Culture.MORIA), Filters.type(CardType.MINION));
    }

    @Override
    public List<? extends Action> getRequiredAfterTriggers(LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (PlayConditions.played(game.getGameState(), game.getModifiersQuerying(), effectResult, Filters.type(CardType.COMPANION))
                && game.getGameState().getCurrentSiteNumber() >= 4) {
            PlayCardResult playCardResult = (PlayCardResult) effectResult;
            DefaultCostToEffectAction action = new DefaultCostToEffectAction(self, null, "Exhaust the played companion.");
            action.addEffect(
                    new ExhaustCharacterEffect(action, false, playCardResult.getPlayedCard()));
            return Collections.singletonList(action);
        }
        return null;
    }
}
