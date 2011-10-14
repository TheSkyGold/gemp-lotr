package com.gempukku.lotro.cards.set2.elven;

import com.gempukku.lotro.cards.AbstractPermanent;
import com.gempukku.lotro.cards.effects.PreventCardEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.effects.ChooseActiveCardEffect;
import com.gempukku.lotro.logic.effects.WoundCharactersEffect;
import com.gempukku.lotro.logic.modifiers.AbstractModifier;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.modifiers.ModifierEffect;
import com.gempukku.lotro.logic.modifiers.ModifiersQuerying;
import com.gempukku.lotro.logic.timing.Effect;
import com.gempukku.lotro.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Mines of Moria
 * Side: Free
 * Culture: Elven
 * Twilight Cost: 2
 * Type: Condition
 * Game Text: Tale. Plays to your support area. While you can spot an Elf companion, the minion archery total is -1.
 * Response: If an Elf is about to take a wound, discard this condition to prevent that wound.
 */
public class Card2_018 extends AbstractPermanent {
    public Card2_018() {
        super(Side.FREE_PEOPLE, 2, CardType.CONDITION, Culture.ELVEN, Zone.SUPPORT, "Hosts of the Last Alliance");
        addKeyword(Keyword.TALE);
    }

    @Override
    public Modifier getAlwaysOnModifier(PhysicalCard self) {
        return new AbstractModifier(self, "While you can spot an Elf companion, the minion archery total is -1", null, new ModifierEffect[]{ModifierEffect.ARCHERY_MODIFIER}) {
            @Override
            public int getArcheryTotal(GameState gameState, ModifiersQuerying modifiersQuerying, Side side, int result) {
                if (Filters.canSpot(gameState, modifiersQuerying, Filters.race(Race.ELF), Filters.type(CardType.COMPANION)))
                    return result - 1;
                return result;
            }
        };
    }

    @Override
    public List<? extends ActivateCardAction> getOptionalInPlayBeforeActions(String playerId, LotroGame game, Effect effect, PhysicalCard self) {
        if (effect.getType() == EffectResult.Type.WOUND) {
            final WoundCharactersEffect woundEffect = (WoundCharactersEffect) effect;
            Collection<PhysicalCard> woundedCharacters = woundEffect.getAffectedCardsMinusPrevented(game);
            if (Filters.filter(woundedCharacters, game.getGameState(), game.getModifiersQuerying(), Filters.race(Race.ELF)).size() > 0) {
                final ActivateCardAction action = new ActivateCardAction(self);
                action.appendEffect(
                        new ChooseActiveCardEffect(self, playerId, "Choose an Elf", Filters.race(Race.ELF), Filters.in(woundedCharacters)) {
                            @Override
                            protected void cardSelected(LotroGame game, PhysicalCard elf) {
                                action.appendEffect(
                                        new PreventCardEffect(woundEffect, elf));
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
