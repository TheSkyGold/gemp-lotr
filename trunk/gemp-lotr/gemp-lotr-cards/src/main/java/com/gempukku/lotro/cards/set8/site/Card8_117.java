package com.gempukku.lotro.cards.set8.site;

import com.gempukku.lotro.cards.AbstractSite;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.choose.ChooseAndPlayCardFromDeckEffect;
import com.gempukku.lotro.common.Block;
import com.gempukku.lotro.common.CardType;
import com.gempukku.lotro.common.Keyword;
import com.gempukku.lotro.common.Phase;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.effects.AddThreatsEffect;
import com.gempukku.lotro.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Siege of Gondor
 * Twilight Cost: 1
 * Type: Site
 * Site: 3K
 * Game Text: Sanctuary. Underground. Fellowship: Add 3 threats to play an enduring companion from your draw deck.
 */
public class Card8_117 extends AbstractSite {
    public Card8_117() {
        super("The Dimholt", Block.KING, 3, 1, Direction.RIGHT);
        addKeyword(Keyword.UNDERGROUND);
    }

    @Override
    public List<? extends Action> getPhaseActions(String playerId, LotroGame game, PhysicalCard self) {
        if (PlayConditions.canUseSiteDuringPhase(game, Phase.FELLOWSHIP, self)
                && PlayConditions.canAddThreat(game, self, 3)) {
            ActivateCardAction action = new ActivateCardAction(self);
            action.appendCost(
                    new AddThreatsEffect(playerId, self, 3));
            action.appendEffect(
                    new ChooseAndPlayCardFromDeckEffect(playerId, CardType.COMPANION, Keyword.ENDURING));
            return Collections.singletonList(action);
        }
        return null;
    }
}
