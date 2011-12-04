package com.gempukku.lotro.cards.set6.elven;

import com.gempukku.lotro.cards.AbstractCompanion;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.DiscardTopCardFromDeckEffect;
import com.gempukku.lotro.cards.effects.SelfExertEffect;
import com.gempukku.lotro.cards.effects.choose.ChooseAndAddUntilEOPStrengthBonusEffect;
import com.gempukku.lotro.common.CardType;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Phase;
import com.gempukku.lotro.common.Race;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Ents of Fangorn
 * Side: Free
 * Culture: Elven
 * Twilight Cost: 3
 * Type: Companion • Elf
 * Strength: 8
 * Vitality: 3
 * Resistance: 6
 * Game Text: To play, spot 3 Elf companions. Skirmish: Exert Naith Troop to discard the top card of your draw deck.
 * If it is an [ELVEN] card, make a minion skirmishing Naith Troop strength -2.
 */
public class Card6_022 extends AbstractCompanion {
    public Card6_022() {
        super(3, 8, 3, 6, Culture.ELVEN, Race.ELF, null, "Naith Troop", true);
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int withTwilightRemoved, int twilightModifier, boolean ignoreRoamingPenalty, boolean ignoreCheckingDeadPile) {
        return super.checkPlayRequirements(playerId, game, self, withTwilightRemoved, twilightModifier, ignoreRoamingPenalty, ignoreCheckingDeadPile)
                && PlayConditions.canSpot(game, 3, Race.ELF, CardType.COMPANION);
    }

    @Override
    protected List<ActivateCardAction> getExtraInPlayPhaseActions(final String playerId, LotroGame game, final PhysicalCard self) {
        if (PlayConditions.canUseFPCardDuringPhase(game, Phase.SKIRMISH, self)
                && PlayConditions.canSelfExert(self, game)) {
            final ActivateCardAction action = new ActivateCardAction(self);
            action.appendCost(
                    new SelfExertEffect(self));
            action.appendEffect(
                    new DiscardTopCardFromDeckEffect(self, playerId, false) {
                        @Override
                        protected void cardsDiscardedCallback(Collection<PhysicalCard> cards) {
                            for (PhysicalCard card : cards)
                                if (card.getBlueprint().getCulture() == Culture.ELVEN)
                                    action.appendEffect(
                                            new ChooseAndAddUntilEOPStrengthBonusEffect(action, self, playerId, -2, CardType.MINION, Filters.inSkirmishAgainst(self)));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
