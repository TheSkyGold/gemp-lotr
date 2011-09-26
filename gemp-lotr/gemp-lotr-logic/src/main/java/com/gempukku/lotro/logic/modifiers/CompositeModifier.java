package com.gempukku.lotro.logic.modifiers;

import com.gempukku.lotro.common.Keyword;
import com.gempukku.lotro.common.Phase;
import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.logic.timing.Action;

import java.util.*;

public class CompositeModifier implements Modifier {
    private PhysicalCard _source;
    private Filter _affectFilter;
    private List<Modifier> _modifiers;
    private final ModifierEffect[] _effects;

    public CompositeModifier(PhysicalCard source, Filter affectFilter, List<Modifier> modifiers) {
        _source = source;
        _affectFilter = affectFilter;
        _modifiers = modifiers;

        Set<ModifierEffect> effects = new HashSet<ModifierEffect>();
        for (Modifier modifier : _modifiers)
            effects.addAll(Arrays.asList(modifier.getModifierEffects()));

        if (effects.contains(ModifierEffect.ALL_MODIFIER))
            _effects = new ModifierEffect[]{ModifierEffect.ALL_MODIFIER};
        else
            _effects = effects.toArray(new ModifierEffect[effects.size()]);
    }

    @Override
    public PhysicalCard getSource() {
        return _source;
    }

    @Override
    public String getText() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Modifier modifier : _modifiers) {
            if (!first)
                sb.append(", ");
            sb.append(modifier.getText());
            first = false;
        }

        return sb.toString();
    }

    @Override
    public ModifierEffect[] getModifierEffects() {
        return _effects;
    }

    @Override
    public boolean affectsCard(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _affectFilter.accepts(gameState, modifiersQuerying, physicalCard);
    }

    @Override
    public boolean hasKeyword(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, Keyword keyword, boolean result) {
        for (Modifier modifier : _modifiers)
            result = modifier.hasKeyword(gameState, modifiersQuerying, physicalCard, keyword, result);

        return result;
    }

    @Override
    public int getKeywordCount(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, Keyword keyword, int result) {
        for (Modifier modifier : _modifiers)
            result = modifier.getKeywordCount(gameState, modifiersQuerying, physicalCard, keyword, result);

        return result;
    }

    @Override
    public int getStrength(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, int result) {
        for (Modifier modifier : _modifiers)
            result = modifier.getStrength(gameState, modifiersQuerying, physicalCard, result);

        return result;
    }

    @Override
    public boolean appliesStrengthModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard modifierSource, boolean result) {
        for (Modifier modifier : _modifiers)
            result = modifier.appliesStrengthModifier(gameState, modifiersQuerying, modifierSource, result);

        return result;
    }

    @Override
    public int getVitality(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, int result) {
        for (Modifier modifier : _modifiers)
            result = modifier.getVitality(gameState, modifiersQuerying, physicalCard, result);

        return result;
    }

    @Override
    public int getTwilightCost(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, int result) {
        for (Modifier modifier : _modifiers)
            result = modifier.getTwilightCost(gameState, modifiersQuerying, physicalCard, result);

        return result;
    }

    @Override
    public int getRoamingPenalty(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, int result) {
        for (Modifier modifier : _modifiers)
            result = modifier.getRoamingPenalty(gameState, modifiersQuerying, physicalCard, result);

        return result;
    }

    @Override
    public int getPlayOnTwilightCost(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, PhysicalCard target, int result) {
        for (Modifier modifier : _modifiers)
            result = modifier.getPlayOnTwilightCost(gameState, modifiersQuerying, physicalCard, target, result);

        return result;
    }

    @Override
    public boolean isOverwhelmedByStrength(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, int strength, int opposingStrength, boolean result) {
        for (Modifier modifier : _modifiers)
            result = modifier.isOverwhelmedByStrength(gameState, modifiersQuerying, physicalCard, strength, opposingStrength, result);

        return result;
    }

    @Override
    public boolean canTakeWound(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, boolean result) {
        for (Modifier modifier : _modifiers)
            result = modifier.canTakeWound(gameState, modifiersQuerying, physicalCard, result);

        return result;
    }

    @Override
    public boolean isAllyOnCurrentSite(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card, boolean allyOnCurrentSite) {
        for (Modifier modifier : _modifiers)
            allyOnCurrentSite = modifier.isAllyOnCurrentSite(gameState, modifiersQuerying, card, allyOnCurrentSite);

        return allyOnCurrentSite;
    }

    @Override
    public int getArcheryTotal(GameState gameState, ModifiersQuerying modifiersQuerying, Side side, int result) {
        for (Modifier modifier : _modifiers)
            result = modifier.getArcheryTotal(gameState, modifiersQuerying, side, result);

        return result;
    }

    @Override
    public int getMoveLimit(GameState gameState, ModifiersQuerying modifiersQuerying, int result) {
        for (Modifier modifier : _modifiers)
            result = modifier.getMoveLimit(gameState, modifiersQuerying, result);

        return result;
    }

    @Override
    public boolean addsToArcheryTotal(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card, boolean result) {
        for (Modifier modifier : _modifiers)
            result = modifier.addsToArcheryTotal(gameState, modifiersQuerying, card, result);

        return result;
    }

    @Override
    public boolean canPlayAction(GameState gameState, ModifiersQuerying modifiersQuerying, Action action, boolean result) {
        for (Modifier modifier : _modifiers)
            result = modifier.canPlayAction(gameState, modifiersQuerying, action, result);

        return result;
    }

    @Override
    public boolean shouldSkipPhase(GameState gameState, ModifiersQuerying modifiersQuerying, Phase phase, String playerId, boolean result) {
        for (Modifier modifier : _modifiers)
            result = modifier.shouldSkipPhase(gameState, modifiersQuerying, phase, null, result);

        return result;
    }

    @Override
    public boolean isValidFreePlayerAssignments(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard companion, List<PhysicalCard> minions, boolean result) {
        for (Modifier modifier : _modifiers)
            result = modifier.isValidFreePlayerAssignments(gameState, modifiersQuerying, companion, minions, result);

        return result;
    }

    @Override
    public boolean isValidFreePlayerAssignments(GameState gameState, ModifiersQuerying modifiersQuerying, Map<PhysicalCard, List<PhysicalCard>> assignments, boolean result) {
        for (Modifier modifier : _modifiers)
            result = modifier.isValidFreePlayerAssignments(gameState, modifiersQuerying, assignments, result);

        return result;
    }

    @Override
    public boolean canBeDiscardedFromPlay(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card, PhysicalCard source, boolean result) {
        for (Modifier modifier : _modifiers)
            result = modifier.canBeDiscardedFromPlay(gameState, modifiersQuerying, card, source, result);

        return result;
    }

    @Override
    public boolean canBeHealed(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card, boolean result) {
        for (Modifier modifier : _modifiers)
            result = modifier.canBeHealed(gameState, modifiersQuerying, card, result);

        return result;
    }
}
