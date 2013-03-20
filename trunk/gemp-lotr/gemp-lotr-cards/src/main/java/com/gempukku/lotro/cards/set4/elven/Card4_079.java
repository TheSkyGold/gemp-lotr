package com.gempukku.lotro.cards.set4.elven;

import com.gempukku.lotro.cards.AbstractResponseOldEvent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.TriggerConditions;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.effects.ChoiceEffect;
import com.gempukku.lotro.cards.effects.ExertCharactersEffect;
import com.gempukku.lotro.cards.effects.LiberateASiteEffect;
import com.gempukku.lotro.common.CardType;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Race;
import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.effects.ChooseAndWoundCharactersEffect;
import com.gempukku.lotro.logic.timing.Effect;
import com.gempukku.lotro.logic.timing.EffectResult;
import com.gempukku.lotro.logic.timing.results.CharacterWonSkirmishResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Two Towers
 * Side: Free
 * Culture: Elven
 * Twilight Cost: 1
 * Type: Event
 * Game Text: Response: If an Elf wins a skirmish, exert that Elf to liberate a site or wound a minion.
 */
public class Card4_079 extends AbstractResponseOldEvent {
    public Card4_079() {
        super(Side.FREE_PEOPLE, Culture.ELVEN, "Night Without End");
    }

    @Override
    public int getTwilightCost() {
        return 1;
    }

    @Override
    public List<PlayEventAction> getOptionalAfterActions(String playerId, LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (TriggerConditions.winsSkirmish(game, effectResult, Race.ELF)) {
            PhysicalCard winner = ((CharacterWonSkirmishResult) effectResult).getWinner();
            if (PlayConditions.canExert(self, game, winner)) {
                PlayEventAction action = new PlayEventAction(self);
                action.appendCost(
                        new ExertCharactersEffect(action, self, winner));

                List<Effect> possibleEffects = new LinkedList<Effect>();
                possibleEffects.add(
                        new LiberateASiteEffect(self, playerId, null));
                possibleEffects.add(
                        new ChooseAndWoundCharactersEffect(action, playerId, 1, 1, CardType.MINION) {
                            @Override
                            public String getText(LotroGame game) {
                                return "Wound a minion";
                            }
                        });

                action.appendEffect(
                        new ChoiceEffect(action, playerId, possibleEffects));
                return Collections.singletonList(action);
            }
        }

        return null;
    }
}
