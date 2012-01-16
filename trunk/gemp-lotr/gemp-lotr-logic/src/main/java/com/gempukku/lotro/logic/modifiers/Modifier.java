package com.gempukku.lotro.logic.modifiers;

import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.timing.Action;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Modifier {
    public PhysicalCard getSource();

    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self);

    public ModifierEffect getModifierEffect();

    public Condition getCondition();

    public boolean affectsCard(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);

    public boolean hasRemovedText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);

    public boolean hasKeyword(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, Keyword keyword);

    public boolean hasSignet(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, Signet signet);

    public int getKeywordCountModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, Keyword keyword);

    public boolean appliesKeywordModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard modifierSource, Keyword keyword);

    public boolean isKeywordRemoved(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, Keyword keyword);

    public int getStrengthModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);

    public boolean appliesStrengthBonusModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard modifierSource, PhysicalCard modifierTaget);

    public int getVitalityModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);

    public int getResistanceModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);

    public int getMinionSiteNumberModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);

    public boolean isAdditionalCardTypeModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, CardType cardType);

    public int getTwilightCostModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, boolean ignoreRoamingPenalty);

    public int getPlayOnTwilightCostModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, PhysicalCard target);

    public int getRoamingPenaltyModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);

    public int getOverwhelmMultiplier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);

    public boolean canTakeWounds(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, int woundsAlreadyTakenInPhase, int woundsToTake);

    public boolean canTakeArcheryWound(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);

    public boolean canBeExerted(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard source, PhysicalCard card);

    public boolean isAllyParticipateInArcheryFire(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card);

    public boolean isAllyParticipateInSkirmishes(GameState gameState, Side sidePlayer, ModifiersQuerying modifiersQuerying, PhysicalCard card);

    public boolean isUnhastyCompanionAllowedToParticipateInSkirmishes(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card);

    public boolean isAllyPreventedFromParticipatingInArcheryFire(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card);

    public boolean isAllyPreventedFromParticipatingInSkirmishes(GameState gameState, Side sidePlayer, ModifiersQuerying modifiersQuerying, PhysicalCard card);

    public int getArcheryTotalModifier(GameState gameState, ModifiersQuerying modifiersQuerying, Side side);

    public int getMoveLimitModifier(GameState gameState, ModifiersQuerying modifiersQuerying);

    public boolean addsToArcheryTotal(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card);

    public boolean canPlayAction(GameState gameState, ModifiersQuerying modifiersQuerying, String performingPlayer, Action action);

    public List<? extends ActivateCardAction> getExtraPhaseAction(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card);

    public List<? extends Action> getExtraPhaseActionFromStacked(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card);

    public boolean canHavePlayedOn(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard playedCard, PhysicalCard target);

    public boolean canHaveTransferredOn(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard playedCard, PhysicalCard target);

    public boolean shouldSkipPhase(GameState gameState, ModifiersQuerying modifiersQuerying, Phase phase, String playerId);

    public boolean isValidAssignments(GameState gameState, Side Side, ModifiersQuerying modifiersQuerying, PhysicalCard companion, Set<PhysicalCard> minions);

    public boolean isValidAssignments(GameState gameState, Side Side, ModifiersQuerying modifiersQuerying, Map<PhysicalCard, Set<PhysicalCard>> assignments);

    public boolean isPreventedFromBeingAssignedToSkirmish(GameState gameState, Side sidePlayer, ModifiersQuerying modifiersQuerying, PhysicalCard card);

    public boolean canBeDiscardedFromPlay(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card, PhysicalCard source);

    public boolean canBeHealed(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card);

    public boolean canRemoveBurden(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard source);

    public boolean canRemoveThreat(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard source);

    public boolean canLookOrRevealCardsInHand(GameState gameState, ModifiersQuerying modifiersQuerying, String playerId);

    public boolean canDiscardCardsFromHand(GameState gameState, ModifiersQuerying modifiersQuerying, String playerId, PhysicalCard source);

    public boolean canDiscardCardsFromTopOfDeck(GameState gameState, ModifiersQuerying modifiersQuerying, String playerId, PhysicalCard source);

    public int getSpotCountModifier(GameState gameState, ModifiersQuerying modifiersQuerying, Filter filter);

    public boolean hasFlagActive(GameState gameState, ModifiersQuerying modifiersQuerying, ModifierFlag modifierFlag);

    public Side hasInitiative(GameState gameState, ModifiersQuerying modifiersQuerying);

    public int getInitiativeHandSizeModifier(GameState gameState, ModifiersQuerying modifiersQuerying);
}
