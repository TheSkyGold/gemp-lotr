package com.gempukku.lotro.cards.set1.sauron;

import com.gempukku.lotro.cards.AbstractMinion;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.ExertCharacterEffect;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Keyword;
import com.gempukku.lotro.common.Phase;
import com.gempukku.lotro.common.Race;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.game.state.Skirmish;
import com.gempukku.lotro.logic.actions.DefaultCostToEffectAction;
import com.gempukku.lotro.logic.effects.WoundCharacterEffect;
import com.gempukku.lotro.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Shadow
 * Culture: Sauron
 * Twilight Cost: 2
 * Type: Minion • Orc
 * Strength: 7
 * Vitality: 2
 * Site: 6
 * Game Text: Skirmish: Exert this minion to wound a character he is skirmishing.
 */
public class Card1_271 extends AbstractMinion {
    public Card1_271() {
        super(2, 7, 2, 6, Race.ORC, Culture.SAURON, "Orc Soldier");
    }

    @Override
    protected List<? extends Action> getExtraPhaseActions(String playerId, LotroGame game, PhysicalCard self) {
        if (PlayConditions.canUseShadowCardDuringPhase(game.getGameState(), Phase.SKIRMISH, self, 0)
                && PlayConditions.canExert(game.getGameState(), game.getModifiersQuerying(), self)) {
            DefaultCostToEffectAction action = new DefaultCostToEffectAction(self, Keyword.SKIRMISH, "Exert this minion to wound a character he is skirmishing.");
            action.addCost(
                    new ExertCharacterEffect(playerId, self));
            Skirmish skirmish = game.getGameState().getSkirmish();
            if (skirmish != null && skirmish.getShadowCharacters().contains(self)) {
                PhysicalCard fpChar = skirmish.getFellowshipCharacter();
                if (fpChar != null)
                    action.addEffect(new WoundCharacterEffect(playerId, fpChar));
            }
            return Collections.singletonList(action);
        }
        return null;
    }

}
