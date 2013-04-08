package com.gempukku.lotro.cards;

import com.gempukku.lotro.cards.actions.TransferPermanentAction;
import com.gempukku.lotro.cards.modifiers.VitalityModifier;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.LotroCardBlueprint;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.modifiers.ModifiersQuerying;
import com.gempukku.lotro.logic.modifiers.StrengthModifier;
import com.gempukku.lotro.logic.modifiers.evaluator.ConstantEvaluator;
import com.gempukku.lotro.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractAttachableFPPossession extends AbstractAttachable {
    private int _strength;
    private int _vitality;

    public AbstractAttachableFPPossession(int twilight, int strength, int vitality, Culture culture, PossessionClass possessionClass, String name) {
        this(twilight, strength, vitality, culture, possessionClass, name, null, false);
    }

    public AbstractAttachableFPPossession(int twilight, int strength, int vitality, Culture culture, PossessionClass possessionClass, String name, String subTitle, boolean unique) {
        this(twilight, strength, vitality, culture, CardType.POSSESSION, possessionClass, name, subTitle, unique);
    }

    public AbstractAttachableFPPossession(int twilight, int strength, int vitality, Culture culture, CardType cardType, PossessionClass possessionClass, String name, String subTitle, boolean unique) {
        super(Side.FREE_PEOPLE, cardType, twilight, culture, possessionClass, name, subTitle, unique);
        _strength = strength;
        _vitality = vitality;
    }

    private void appendTransferPossessionAction(List<Action> actions, LotroGame game, final PhysicalCard self, Filter validTargetFilter) {
        GameState gameState = game.getGameState();
        if (Filters.countActive(gameState, game.getModifiersQuerying(), validTargetFilter)>0
                && gameState.getCurrentPhase() == Phase.FELLOWSHIP
                && self.getZone() == Zone.ATTACHED) {

            Filter validTransferFilter;

            LotroCardBlueprint attachedTo = self.getAttachedTo().getBlueprint();
            if (attachedTo.getCardType() == CardType.COMPANION) {
                validTransferFilter = Filters.and(validTargetFilter,
                        Filters.or(
                                CardType.COMPANION,
                                Filters.allyAtHome));
            } else if (attachedTo.isAllyAtHome(gameState.getCurrentSiteNumber(), gameState.getCurrentSiteBlock())) {
                validTransferFilter = Filters.and(validTargetFilter,
                        Filters.or(
                                CardType.COMPANION,
                                Filters.allyAtHome));
            } else {
                validTransferFilter = Filters.and(validTargetFilter,
                        CardType.ALLY, Filters.allyAtHome
                );
            }

            validTransferFilter = Filters.and(validTransferFilter,
                    Filters.not(self.getAttachedTo()),
                    new Filter() {
                        @Override
                        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                            return modifiersQuerying.canHaveTransferredOn(gameState, self, physicalCard);
                        }
                    });

            if (Filters.countActive(game.getGameState(), game.getModifiersQuerying(), validTransferFilter)>0)
                actions.add(new TransferPermanentAction(self, validTransferFilter));
        }
    }

    @Override
    public final List<? extends Modifier> getAlwaysOnModifiers(LotroGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        if (_strength != 0)
            modifiers.add(new StrengthModifier(self, Filters.hasAttached(self), null, new ConstantEvaluator(_strength), true));
        if (_vitality != 0)
            modifiers.add(new VitalityModifier(self, Filters.hasAttached(self), _vitality, true));

        List<? extends Modifier> extraModifiers = getNonBasicStatsModifiers(self);
        if (extraModifiers != null)
            modifiers.addAll(extraModifiers);

        return modifiers;
    }

    @Override
    public final Modifier getAlwaysOnModifier(LotroGame game, PhysicalCard self) {
        throw new UnsupportedOperationException("This should not be called");
    }

    protected List<? extends Modifier> getNonBasicStatsModifiers(PhysicalCard self) {
        return null;
    }

    protected List<? extends Action> getExtraInPlayPhaseActions(String playerId, LotroGame game, PhysicalCard self) {
        return null;
    }

    @Override
    protected final List<? extends Action> getExtraPhaseActions(String playerId, LotroGame game, PhysicalCard self) {
        List<Action> actions = new LinkedList<Action>();
        if (game.getModifiersQuerying().canBeTransferred(game.getGameState(), self))
            appendTransferPossessionAction(actions, game, self, getFullValidTargetFilter(playerId, game, self));
        List<? extends Action> extraActions = getExtraInPlayPhaseActions(playerId, game, self);
        if (extraActions != null)
            actions.addAll(extraActions);
        return actions;
    }
}
