package com.gempukku.lotro.cards.set3.wraith;

import com.gempukku.lotro.cards.AbstractMinion;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.TriggerConditions;
import com.gempukku.lotro.cards.effects.SelfExertEffect;
import com.gempukku.lotro.cards.effects.TransferPermanentEffect;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Keyword;
import com.gempukku.lotro.common.Race;
import com.gempukku.lotro.common.Zone;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.OptionalTriggerAction;
import com.gempukku.lotro.logic.effects.ChooseActiveCardEffect;
import com.gempukku.lotro.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Realms of Elf-lords
 * Side: Shadow
 * Culture: Wraith
 * Twilight Cost: 4
 * Type: Minion • Nazgul
 * Strength: 9
 * Vitality: 3
 * Site: 3
 * Game Text: Twilight. Each time Úlairë Otsëa wins a skirmish, you may exert him to transfer Blade Tip from your
 * support area to the Ring-bearer.
 */
public class Card3_086 extends AbstractMinion {
    public Card3_086() {
        super(4, 9, 3, 3, Race.NAZGUL, Culture.WRAITH, "Úlairë Otsëa", true);
        addKeyword(Keyword.TWILIGHT);
    }

    @Override
    public List<OptionalTriggerAction> getOptionalAfterTriggers(String playerId, final LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (TriggerConditions.winsSkirmish(game, effectResult, self)
                && PlayConditions.canExert(self, game, self)) {
            final OptionalTriggerAction action = new OptionalTriggerAction(self);
            action.appendCost(
                    new SelfExertEffect(self));
            action.appendEffect(
                    new ChooseActiveCardEffect(self, playerId, "Choose Blade Tip", Filters.name("Blade Tip"), Zone.SUPPORT, Filters.owner(playerId)) {
                        @Override
                        protected void cardSelected(LotroGame game, PhysicalCard card) {
                            action.insertEffect(
                                    new TransferPermanentEffect(card, Filters.findFirstActive(game.getGameState(), game.getModifiersQuerying(), Filters.ringBearer)));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
