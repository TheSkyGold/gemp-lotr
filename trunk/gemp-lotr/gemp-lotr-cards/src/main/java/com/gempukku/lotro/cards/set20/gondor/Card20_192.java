package com.gempukku.lotro.cards.set20.gondor;

import com.gempukku.lotro.cards.AbstractAttachableFPPossession;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.AddUntilEndOfPhaseModifierEffect;
import com.gempukku.lotro.cards.effects.choose.ChooseAndExertCharactersEffect;
import com.gempukku.lotro.cards.modifiers.ArcheryTotalModifier;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.modifiers.KeywordModifier;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * 1
 * •Faramir's Bow, Ithilien Longbow
 * Gondor	Possession •  Ranged Weapon
 * Bearer must be Faramir.
 * He is an archer.
 * Archery: If at a site from your adventure deck, exert Faramir to add 1 to the fellowship archery total.
 */
public class Card20_192 extends AbstractAttachableFPPossession {
    public Card20_192() {
        super(1, 0, 0, Culture.GONDOR, PossessionClass.RANGED_WEAPON, "Faramir's Bow", "Itilien Longbow", true);
    }

    @Override
    protected Filterable getValidTargetFilter(String playerId, LotroGame game, PhysicalCard self) {
        return Filters.name("Faramir");
    }

    @Override
    protected List<? extends Modifier> getNonBasicStatsModifiers(PhysicalCard self) {
        return Collections.singletonList(
                new KeywordModifier(self, Filters.hasAttached(self), Keyword.ARCHER));
    }

    @Override
    protected List<? extends Action> getExtraInPlayPhaseActions(String playerId, LotroGame game, PhysicalCard self) {
        if (PlayConditions.canUseFPCardDuringPhase(game, Phase.ARCHERY, self)
                && PlayConditions.location(game, Filters.owner(playerId))
                && PlayConditions.canExert(self, game, Filters.name("Faramir"))) {
            ActivateCardAction action = new ActivateCardAction(self);
            action.appendCost(
                    new ChooseAndExertCharactersEffect(action, playerId, 1, 1, Filters.name("Faramir")));
            action.appendEffect(
                    new AddUntilEndOfPhaseModifierEffect(
                            new ArcheryTotalModifier(self, Side.FREE_PEOPLE, 1)));
            return Collections.singletonList(action);
        }
        return null;
    }
}
