package com.gempukku.lotro.cards.set9.dwarven;

import com.gempukku.lotro.cards.AbstractCompanion;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.TriggerConditions;
import com.gempukku.lotro.cards.effects.AddUntilEndOfPhaseModifierEffect;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Keyword;
import com.gempukku.lotro.common.Phase;
import com.gempukku.lotro.common.Race;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.actions.OptionalTriggerAction;
import com.gempukku.lotro.logic.effects.ChooseActiveCardEffect;
import com.gempukku.lotro.logic.effects.ChooseAndDiscardCardsFromHandEffect;
import com.gempukku.lotro.logic.effects.HealCharactersEffect;
import com.gempukku.lotro.logic.modifiers.KeywordModifier;
import com.gempukku.lotro.logic.timing.EffectResult;
import com.gempukku.lotro.logic.timing.results.CharacterWonSkirmishResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Reflections
 * Side: Free
 * Culture: Dwarven
 * Twilight Cost: 2
 * Type: Companion • Dwarf
 * Strength: 7
 * Vitality: 3
 * Resistance: 6
 * Game Text: Each time a Dwarf wins a fierce skirmish, you may heal that Dwarf. Skirmish: Discard a [DWARVEN] card
 * from hand to make a Dwarf damage +1.
 */
public class Card9_005 extends AbstractCompanion {
    public Card9_005() {
        super(2, 7, 3, 6, Culture.DWARVEN, Race.DWARF, null, "Linnar", "Dwarven Lord", true);
    }

    @Override
    public List<OptionalTriggerAction> getOptionalAfterTriggers(String playerId, LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (TriggerConditions.winsSkirmish(game, effectResult, Race.DWARF)
                && game.getGameState().isFierceSkirmishes()) {
            CharacterWonSkirmishResult skirmishResult = (CharacterWonSkirmishResult) effectResult;
            PhysicalCard winner = skirmishResult.getWinner();
            OptionalTriggerAction action = new OptionalTriggerAction(self);
            action.appendEffect(
                    new HealCharactersEffect(self, winner));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<ActivateCardAction> getExtraInPlayPhaseActions(String playerId, LotroGame game, final PhysicalCard self) {
        if (PlayConditions.canUseFPCardDuringPhase(game, Phase.SKIRMISH, self)
                && PlayConditions.canDiscardFromHand(game, playerId, 1, Culture.DWARVEN)) {
            final ActivateCardAction action = new ActivateCardAction(self);
            action.appendCost(
                    new ChooseAndDiscardCardsFromHandEffect(action, playerId, false, 1, Culture.DWARVEN));
            action.appendEffect(
                    new ChooseActiveCardEffect(self, playerId, "Choose a Dwarf", Race.DWARF) {
                        @Override
                        protected void cardSelected(LotroGame game, PhysicalCard card) {
                            action.appendEffect(
                                    new AddUntilEndOfPhaseModifierEffect(
                                            new KeywordModifier(self, card, Keyword.DAMAGE, 1)));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
