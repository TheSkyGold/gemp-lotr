package com.gempukku.lotro.logic.timing;

import com.gempukku.lotro.common.CardType;
import com.gempukku.lotro.common.Signet;
import com.gempukku.lotro.common.Zone;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.LotroCardBlueprint;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.game.state.Skirmish;
import com.gempukku.lotro.logic.PlayerOrder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GameStats {
    private int _fellowshipArchery;
    private int _shadowArchery;

    private int _moveLimit;
    private int _moveCount;

    private int _fellowshipSkirmishStrength;
    private int _shadowSkirmishStrength;

    private int _fellowshipSkirmishDamageBonus;
    private int _shadowSkirmishDamageBonus;

    private boolean _fpOverwhelmed;

    private Map<String, Map<Zone, Integer>> _zoneSizes = new HashMap<String, Map<Zone, Integer>>();
    private Map<String, Integer> _threats = new HashMap<String, Integer>();
    private Map<Integer, Integer> _charStrengths = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> _charVitalities = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> _siteNumbers = new HashMap<Integer, Integer>();
    private Map<Integer, String> _charResistances = new HashMap<Integer, String>();

    /**
     * @return If the stats have changed
     */
    public boolean updateGameStats(LotroGame game) {
        boolean changed = false;

        int newFellowshipArcheryTotal = RuleUtils.calculateFellowshipArcheryTotal(game);
        if (newFellowshipArcheryTotal != _fellowshipArchery) {
            changed = true;
            _fellowshipArchery = newFellowshipArcheryTotal;
        }

        int newShadowArcheryTotal = RuleUtils.calculateShadowArcheryTotal(game);
        if (newShadowArcheryTotal != _shadowArchery) {
            changed = true;
            _shadowArchery = newShadowArcheryTotal;
        }

        int newMoveLimit = RuleUtils.calculateMoveLimit(game);
        if (newMoveLimit != _moveLimit) {
            changed = true;
            _moveLimit = newMoveLimit;
        }

        int newMoveCount = game.getGameState().getMoveCount();
        if (newMoveCount != _moveCount) {
            changed = true;
            _moveCount = newMoveCount;
        }

        int newFellowshipStrength = RuleUtils.getFellowshipSkirmishStrength(game);
        if (newFellowshipStrength != _fellowshipSkirmishStrength) {
            changed = true;
            _fellowshipSkirmishStrength = newFellowshipStrength;
        }

        int newShadowStrength = RuleUtils.getShadowSkirmishStrength(game);
        if (newShadowStrength != _shadowSkirmishStrength) {
            changed = true;
            _shadowSkirmishStrength = newShadowStrength;
        }

        int newFellowshipDamageBonus = RuleUtils.getFellowshipSkirmishDamageBonus(game);
        if (newFellowshipDamageBonus != _fellowshipSkirmishDamageBonus) {
            changed = true;
            _fellowshipSkirmishDamageBonus = newFellowshipDamageBonus;
        }

        int newShadowDamageBonus = RuleUtils.getShadowSkirmishDamageBonus(game);
        if (newShadowDamageBonus != _shadowSkirmishDamageBonus) {
            changed = true;
            _shadowSkirmishDamageBonus = newShadowDamageBonus;
        }

        boolean newFpOverwhelmed = false;
        Skirmish skirmish = game.getGameState().getSkirmish();
        if (skirmish != null) {
            PhysicalCard fpChar = skirmish.getFellowshipCharacter();
            if (fpChar != null) {
                int multiplier = game.getModifiersQuerying().getOverwhelmMultiplier(game.getGameState(), fpChar);
                if (newFellowshipStrength * multiplier <= newShadowStrength && newShadowStrength != 0)
                    newFpOverwhelmed = true;
            }
        }
        if (newFpOverwhelmed != _fpOverwhelmed) {
            changed = true;
            _fpOverwhelmed = newFpOverwhelmed;
        }

        Map<String, Map<Zone, Integer>> newZoneSizes = new HashMap<String, Map<Zone, Integer>>();
        PlayerOrder playerOrder = game.getGameState().getPlayerOrder();
        if (playerOrder != null) {
            for (String player : playerOrder.getAllPlayers()) {
                final HashMap<Zone, Integer> playerZoneSizes = new HashMap<Zone, Integer>();
                playerZoneSizes.put(Zone.HAND, game.getGameState().getHand(player).size());
                playerZoneSizes.put(Zone.DECK, game.getGameState().getDeck(player).size());
                playerZoneSizes.put(Zone.ADVENTURE_DECK, game.getGameState().getAdventureDeck(player).size());
                playerZoneSizes.put(Zone.DISCARD, game.getGameState().getDiscard(player).size());
                playerZoneSizes.put(Zone.DEAD, game.getGameState().getDeadPile(player).size());
                newZoneSizes.put(player, playerZoneSizes);
            }
        }

        if (!newZoneSizes.equals(_zoneSizes)) {
            changed = true;
            _zoneSizes = newZoneSizes;
        }

        Map<Integer, Integer> newCharStrengths = new HashMap<Integer, Integer>();
        Map<Integer, Integer> newCharVitalities = new HashMap<Integer, Integer>();
        Map<Integer, Integer> newSiteNumbers = new HashMap<Integer, Integer>();
        Map<Integer, String> newCharResistances = new HashMap<Integer, String>();
        for (PhysicalCard character : Filters.filterActive(game.getGameState(), game.getModifiersQuerying(), Filters.or(CardType.COMPANION, CardType.ALLY, CardType.MINION))) {
            newCharStrengths.put(character.getCardId(), game.getModifiersQuerying().getStrength(game.getGameState(), character));
            newCharVitalities.put(character.getCardId(), game.getModifiersQuerying().getVitality(game.getGameState(), character));
            final LotroCardBlueprint blueprint = character.getBlueprint();
            if (blueprint.getCardType() == CardType.MINION || game.getModifiersQuerying().isAdditionalCardType(game.getGameState(), character, CardType.MINION))
                newSiteNumbers.put(character.getCardId(), game.getModifiersQuerying().getMinionSiteNumber(game.getGameState(), character));
            else {
                final int resistance = game.getModifiersQuerying().getResistance(game.getGameState(), character);
                if (blueprint.getCardType() == CardType.COMPANION) {
                    Signet signet = blueprint.getSignet();
                    String resistanceStr;
                    if (signet == Signet.ARAGORN)
                        resistanceStr = "A" + resistance;
                    else if (signet == Signet.FRODO)
                        resistanceStr = "F" + resistance;
                    else if (signet == Signet.GANDALF)
                        resistanceStr = "G" + resistance;
                    else if (signet == Signet.THÉODEN)
                        resistanceStr = "T" + resistance;
                    else
                        resistanceStr = String.valueOf(resistance);
                    newCharResistances.put(character.getCardId(), resistanceStr);
                } else {
                    newCharResistances.put(character.getCardId(), String.valueOf(resistance));
                }
            }
        }

        if (!newCharStrengths.equals(_charStrengths)) {
            changed = true;
            _charStrengths = newCharStrengths;
        }

        if (!newCharVitalities.equals(_charVitalities)) {
            changed = true;
            _charVitalities = newCharVitalities;
        }

        if (!newSiteNumbers.equals(_siteNumbers)) {
            changed = true;
            _siteNumbers = newSiteNumbers;
        }

        if (!newCharResistances.equals(_charResistances)) {
            changed = true;
            _charResistances = newCharResistances;
        }

        Map<String, Integer> newThreats = new HashMap<String, Integer>();
        if (playerOrder != null) {
            for (String player : playerOrder.getAllPlayers())
                newThreats.put(player, game.getGameState().getPlayerThreats(player));
        }

        if (!newThreats.equals(_threats)) {
            changed = true;
            _threats = newThreats;
        }

        return changed;
    }

    public int getFellowshipArchery() {
        return _fellowshipArchery;
    }

    public int getShadowArchery() {
        return _shadowArchery;
    }

    public int getMoveLimit() {
        return _moveLimit;
    }

    public int getMoveCount() {
        return _moveCount;
    }

    public int getFellowshipSkirmishStrength() {
        return _fellowshipSkirmishStrength;
    }

    public int getShadowSkirmishStrength() {
        return _shadowSkirmishStrength;
    }

    public int getFellowshipSkirmishDamageBonus() {
        return _fellowshipSkirmishDamageBonus;
    }

    public int getShadowSkirmishDamageBonus() {
        return _shadowSkirmishDamageBonus;
    }

    public boolean isFpOverwhelmed() {
        return _fpOverwhelmed;
    }

    public Map<String, Map<Zone, Integer>> getZoneSizes() {
        return Collections.unmodifiableMap(_zoneSizes);
    }

    public Map<String, Integer> getThreats() {
        return _threats;
    }

    public Map<Integer, Integer> getCharStrengths() {
        return Collections.unmodifiableMap(_charStrengths);
    }

    public Map<Integer, Integer> getCharVitalities() {
        return _charVitalities;
    }

    public Map<Integer, Integer> getSiteNumbers() {
        return _siteNumbers;
    }

    public Map<Integer, String> getCharResistances() {
        return _charResistances;
    }

    public GameStats makeACopy() {
        GameStats copy = new GameStats();
        copy._fellowshipArchery = _fellowshipArchery;
        copy._fellowshipSkirmishStrength = _fellowshipSkirmishStrength;
        copy._fellowshipSkirmishDamageBonus = _fellowshipSkirmishDamageBonus;
        copy._moveCount = _moveCount;
        copy._moveLimit = _moveLimit;
        copy._shadowArchery = _shadowArchery;
        copy._shadowSkirmishStrength = _shadowSkirmishStrength;
        copy._shadowSkirmishDamageBonus = _shadowSkirmishDamageBonus;
        copy._fpOverwhelmed = _fpOverwhelmed;
        copy._zoneSizes = _zoneSizes;
        copy._threats = _threats;
        copy._charStrengths = _charStrengths;
        copy._charVitalities = _charVitalities;
        copy._siteNumbers = _siteNumbers;
        copy._charResistances = _charResistances;
        return copy;
    }
}
