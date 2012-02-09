package com.gempukku.lotro.cards;

import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.LotroCardBlueprint;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.OptionalTriggerAction;
import com.gempukku.lotro.logic.actions.RequiredTriggerAction;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.modifiers.ModifiersQuerying;
import com.gempukku.lotro.logic.timing.Action;
import com.gempukku.lotro.logic.timing.Effect;
import com.gempukku.lotro.logic.timing.EffectResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractLotroCardBlueprint implements LotroCardBlueprint {
    private String _name;
    private CardType _cardType;
    private Side _side;
    private Culture _culture;
    private boolean _unique;
    private Map<Keyword, Integer> _keywords = new HashMap<Keyword, Integer>();

    public AbstractLotroCardBlueprint(Side side, CardType cardType, Culture culture, String name) {
        this(side, cardType, culture, name, false);
    }

    public AbstractLotroCardBlueprint(Side side, CardType cardType, Culture culture, String name, boolean unique) {
        _side = side;
        _cardType = cardType;
        _culture = culture;
        _name = name;
        _unique = unique;
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int withTwilightRemoved, int twilightModifier, boolean ignoreRoamingPenalty, boolean ignoreCheckingDeadPile) {
        if (!game.getModifiersQuerying().canPayExtraCostsToPlay(game.getGameState(), self))
            return false;

        int toilCount = game.getModifiersQuerying().getKeywordCount(game.getGameState(), self, Keyword.TOIL);
        if (toilCount > 0)
            twilightModifier -= toilCount * Filters.countActive(game.getGameState(), game.getModifiersQuerying(), Filters.owner(playerId), getCulture(), Filters.character, Filters.canExert(self));
        return (getSide() != Side.SHADOW || PlayConditions.canPayForShadowCard(game, self, withTwilightRemoved, twilightModifier, ignoreRoamingPenalty));
    }

    protected void addKeyword(Keyword keyword) {
        addKeyword(keyword, 1);
    }

    protected void addKeyword(Keyword keyword, int number) {
        _keywords.put(keyword, number);
    }

    @Override
    public boolean hasKeyword(Keyword keyword) {
        return _keywords.containsKey(keyword);
    }

    @Override
    public int getKeywordCount(Keyword keyword) {
        Integer count = _keywords.get(keyword);
        if (count == null)
            return 0;
        else
            return count;
    }

    @Override
    public Culture getCulture() {
        return _culture;
    }

    @Override
    public CardType getCardType() {
        return _cardType;
    }

    @Override
    public Side getSide() {
        return _side;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public boolean isUnique() {
        return _unique;
    }

    @Override
    public List<? extends Modifier> getAlwaysOnModifiers(LotroGame game, PhysicalCard self) {
        Modifier modifier = getAlwaysOnModifier(self);
        if (modifier != null)
            return Collections.singletonList(modifier);
        return null;
    }

    @Override
    public List<? extends Modifier> getStackedOnModifiers(LotroGame game, PhysicalCard self) {
        return null;
    }

    @Override
    public List<? extends Modifier> getInDiscardModifiers(LotroGame game, PhysicalCard self) {
        return null;
    }

    public Modifier getAlwaysOnModifier(PhysicalCard self) {
        return null;
    }

    @Override
    public Race getRace() {
        return null;
    }

    @Override
    public int getStrength() {
        throw new UnsupportedOperationException("This method should not be called on this card");
    }

    @Override
    public int getVitality() {
        throw new UnsupportedOperationException("This method should not be called on this card");
    }

    @Override
    public int getResistance() {
        throw new UnsupportedOperationException("This method should not be called on this card");
    }

    @Override
    public boolean isAllyAtHome(int siteNumber, Block siteBlock) {
        throw new UnsupportedOperationException("This method should not be called on this card");
    }

    @Override
    public Block getSiteBlock() {
        throw new UnsupportedOperationException("This method should not be called on this card");
    }

    @Override
    public int getSiteNumber() {
        throw new UnsupportedOperationException("This method should not be called on this card");
    }

    @Override
    public int getTwilightCostModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return 0;
    }

    @Override
    public PossessionClass getPossessionClass() {
        return null;
    }

    @Override
    public List<? extends Action> getPhaseActions(String playerId, LotroGame game, PhysicalCard self) {
        return null;
    }

    @Override
    public List<? extends Action> getPhaseActionsFromStacked(String playerId, LotroGame game, PhysicalCard self) {
        return null;
    }

    @Override
    public List<? extends Action> getPhaseActionsFromDiscard(String playerId, LotroGame game, PhysicalCard self) {
        return null;
    }

    @Override
    public List<RequiredTriggerAction> getRequiredBeforeTriggers(LotroGame game, Effect effect, PhysicalCard self) {
        return null;
    }

    @Override
    public List<OptionalTriggerAction> getOptionalBeforeTriggers(String playerId, LotroGame game, Effect effect, PhysicalCard self) {
        return null;
    }

    @Override
    public List<? extends Action> getOptionalBeforeActions(String playerId, LotroGame game, Effect effect, PhysicalCard self) {
        return null;
    }

    @Override
    public List<RequiredTriggerAction> getRequiredAfterTriggers(LotroGame game, EffectResult effectResult, PhysicalCard self) {
        return null;
    }

    @Override
    public List<OptionalTriggerAction> getOptionalAfterTriggers(String playerId, LotroGame game, EffectResult effectResult, PhysicalCard self) {
        return null;
    }

    @Override
    public List<OptionalTriggerAction> getOptionalAfterTriggersFromHand(String playerId, LotroGame game, EffectResult effectResult, PhysicalCard self) {
        return null;
    }

    @Override
    public List<? extends Action> getOptionalAfterActions(String playerId, LotroGame game, EffectResult effectResult, PhysicalCard self) {
        return null;
    }

    @Override
    public RequiredTriggerAction getDiscardedFromPlayRequiredTrigger(LotroGame game, PhysicalCard self) {
        return null;
    }

    @Override
    public OptionalTriggerAction getDiscardedFromPlayOptionalTrigger(String playerId, LotroGame game, PhysicalCard self) {
        return null;
    }

    @Override
    public OptionalTriggerAction getKilledOptionalTrigger(String playerId, LotroGame game, PhysicalCard self) {
        return null;
    }

    @Override
    public Signet getSignet() {
        return null;
    }

    @Override
    public String getDisplayableInformation(PhysicalCard self) {
        return null;
    }

    @Override
    public Direction getSiteDirection() {
        throw new UnsupportedOperationException("This method should not be called on this card");
    }
}
