package com.gempukku.lotro.cards.set4.rohan;

import com.gempukku.lotro.cards.AbstractAttachableFPPossession;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.TriggerConditions;
import com.gempukku.lotro.cards.effects.ExertCharactersEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.RequiredTriggerAction;
import com.gempukku.lotro.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Two Towers
 * Side: Free
 * Culture: Rohan
 * Twilight Cost: 2
 * Type: Possession • Mount
 * Game Text: To play, spot a [ROHAN] Man. Bearer must be a Man, Elf, or Wizard. At the start of each skirmish
 * involving bearer, each minion skirmishing bearer must exert.
 */
public class Card4_283 extends AbstractAttachableFPPossession {
    public Card4_283() {
        super(2, 0, 0, Culture.ROHAN, PossessionClass.MOUNT, "Horse of Rohan");
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, Filter additionalAttachmentFilter, int twilightModifier) {
        return super.checkPlayRequirements(playerId, game, self, additionalAttachmentFilter, twilightModifier)
                && PlayConditions.canSpot(game, Culture.ROHAN, Race.MAN);
    }

    @Override
    protected Filter getValidTargetFilter(String playerId, LotroGame game, PhysicalCard self) {
        return Filters.or(Race.MAN, Race.ELF, Race.WIZARD);
    }

    @Override
    public List<RequiredTriggerAction> getRequiredAfterTriggers(LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (TriggerConditions.startOfPhase(game, effectResult, Phase.SKIRMISH)
                && Filters.inSkirmish.accepts(game.getGameState(), game.getModifiersQuerying(), self.getAttachedTo())) {
            RequiredTriggerAction action = new RequiredTriggerAction(self);
            action.appendEffect(
                    new ExertCharactersEffect(self, Filters.and(CardType.MINION, Filters.inSkirmishAgainst(Filters.hasAttached(self)))));
            return Collections.singletonList(action);
        }
        return null;
    }
}
