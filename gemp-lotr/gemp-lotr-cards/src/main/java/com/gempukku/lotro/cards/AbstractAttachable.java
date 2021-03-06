package com.gempukku.lotro.cards;

import com.gempukku.lotro.cards.actions.AttachPermanentAction;
import com.gempukku.lotro.common.CardType;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Filterable;
import com.gempukku.lotro.common.Phase;
import com.gempukku.lotro.common.PossessionClass;
import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.modifiers.ModifiersQuerying;
import com.gempukku.lotro.logic.timing.Action;
import com.gempukku.lotro.logic.timing.Effect;
import com.gempukku.lotro.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractAttachable extends AbstractLotroCardBlueprint {
    private int _twilight;
    private PossessionClass _possessionClass;

    public AbstractAttachable(Side side, CardType cardType, int twilight, Culture culture, PossessionClass possessionClass, String name) {
        this(side, cardType, twilight, culture, possessionClass, name, null, false);
    }

    public AbstractAttachable(Side side, CardType cardType, int twilight, Culture culture, PossessionClass possessionClass, String name, String subTitle, boolean unique) {
        super(side, cardType, culture, name, subTitle, unique);
        _twilight = twilight;
        _possessionClass = possessionClass;
    }

    public final PossessionClass getPossessionClass() {
        return _possessionClass;
    }

    public boolean isExtraPossessionClass(LotroGame game, PhysicalCard self, PhysicalCard attachedTo) {
        return false;
    }

    public final Filter getFullValidTargetFilter(String playerId, final LotroGame game, final PhysicalCard self) {
        return Filters.and(getValidTargetFilter(playerId, game, self),
                new Filter() {
                    @Override
                    public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                        final CardType thisType = getCardType();
                        if (thisType == CardType.POSSESSION || thisType == CardType.ARTIFACT) {
                            final CardType targetType = physicalCard.getBlueprint().getCardType();
                            return targetType == CardType.COMPANION || targetType == CardType.ALLY
                                    || targetType == CardType.MINION;
                        }
                        return true;
                    }
                },
                new Filter() {
                    @Override
                    public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                        Set<PossessionClass> possessionClasses = getPossessionClasses();
                        if (possessionClasses != null) {
                            for (PossessionClass possessionClass : possessionClasses) {
                                boolean extraPossessionClass = isExtraPossessionClass(game, self, physicalCard);
                                List<PhysicalCard> attachedCards = game.getGameState().getAttachedCards(physicalCard);
                                Collection<PhysicalCard> matchingClassPossessions = Filters.filter(attachedCards, gameState, modifiersQuerying, Filters.or(CardType.POSSESSION, CardType.ARTIFACT), possessionClass);
                                if (matchingClassPossessions.size() > 1)
                                    return false;
                                if (!extraPossessionClass && matchingClassPossessions.size() == 1 &&
                                        !((AbstractAttachable) matchingClassPossessions.iterator().next().getBlueprint()).isExtraPossessionClass(game, self, physicalCard))
                                    return false;
                            }
                        }
                        return true;
                    }
                });
    }

    private Filter getFullAttachValidTargetFilter(String playerId, final LotroGame game, final PhysicalCard self) {
        return Filters.and(getFullValidTargetFilter(playerId, game, self),
                new Filter() {
                    @Override
                    public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                        return modifiersQuerying.canHavePlayedOn(gameState, self, physicalCard);
                    }
                });
    }

    protected abstract Filterable getValidTargetFilter(String playerId, LotroGame game, PhysicalCard self);

    @Override
    public final boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int withTwilightRemoved, int twilightModifier, boolean ignoreRoamingPenalty, boolean ignoreCheckingDeadPile) {
        return checkPlayRequirements(playerId, game, self, withTwilightRemoved, Filters.any, twilightModifier);
    }

    protected boolean skipUniquenessCheck() {
        return false;
    }

    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int withTwilightRemoved, Filter additionalAttachmentFilter, int twilightModifier) {
        return super.checkPlayRequirements(playerId, game, self, withTwilightRemoved, twilightModifier, false, false)
                && (skipUniquenessCheck() || PlayConditions.checkUniqueness(game.getGameState(), game.getModifiersQuerying(), self, false))
                && Filters.countActive(game.getGameState(), game.getModifiersQuerying(), getFullAttachValidTargetFilter(playerId, game, self), additionalAttachmentFilter)>0;
    }

    @Override
    public final List<? extends Action> getPhaseActions(String playerId, LotroGame game, PhysicalCard self) {
        List<Action> actions = new LinkedList<Action>();
        Side side = self.getBlueprint().getSide();
        if (((side == Side.FREE_PEOPLE && PlayConditions.canPlayCardDuringPhase(game, Phase.FELLOWSHIP, self))
                || (side == Side.SHADOW && PlayConditions.canPlayCardDuringPhase(game, Phase.SHADOW, self)))
                && checkPlayRequirements(playerId, game, self, 0, 0, false, false)) {
            actions.add(getPlayCardAction(playerId, game, self, 0, false));
        }

        List<? extends Action> extraPhaseActions = getExtraPhaseActions(playerId, game, self);
        if (extraPhaseActions != null)
            actions.addAll(extraPhaseActions);

        return actions;
    }

    @Override
    public final AttachPermanentAction getPlayCardAction(String playerId, LotroGame game, PhysicalCard self, int twilightModifier, boolean ignoreRoamingPenalty) {
        return getPlayCardAction(playerId, game, self, Filters.any, twilightModifier);
    }

    public AttachPermanentAction getPlayCardAction(String playerId, LotroGame game, PhysicalCard self, Filterable additionalAttachmentFilter, int twilightModifier) {
        return new AttachPermanentAction(game, self, Filters.and(getFullAttachValidTargetFilter(playerId, game, self), additionalAttachmentFilter), getAttachCostModifiers(playerId, game, self), twilightModifier);
    }

    protected List<? extends Action> getExtraPhaseActions(String playerId, LotroGame game, PhysicalCard self) {
        return null;
    }

    protected Map<Filter, Integer> getAttachCostModifiers(String playerId, LotroGame game, PhysicalCard self) {
        return Collections.emptyMap();
    }

    @Override
    public final int getTwilightCost() {
        return _twilight;
    }

    @Override
    public final List<? extends Action> getOptionalAfterActions(String playerId, LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (self.getZone().isInPlay())
            return getOptionalInPlayAfterActions(playerId, game, effectResult, self);
        return null;
    }

    @Override
    public final List<? extends Action> getOptionalBeforeActions(String playerId, LotroGame game, Effect effect, PhysicalCard self) {
        if (self.getZone().isInPlay())
            return getOptionalInPlayBeforeActions(playerId, game, effect, self);
        return null;
    }

    public List<? extends Action> getOptionalInPlayAfterActions(String playerId, LotroGame game, EffectResult effectResult, PhysicalCard self) {
        return null;
    }

    public List<? extends Action> getOptionalInPlayBeforeActions(String playerId, LotroGame game, Effect effect, PhysicalCard self) {
        return null;
    }
}
