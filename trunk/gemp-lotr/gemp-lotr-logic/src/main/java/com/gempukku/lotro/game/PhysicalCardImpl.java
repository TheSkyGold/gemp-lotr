package com.gempukku.lotro.game;

import com.gempukku.lotro.common.Zone;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.modifiers.ModifierHook;

import java.util.LinkedList;
import java.util.List;

public class PhysicalCardImpl implements PhysicalCard {
    private int _cardId;
    private String _blueprintId;
    private String _owner;
    private String _cardController;
    private Zone _zone;
    private LotroCardBlueprint _blueprint;

    private PhysicalCardImpl _attachedTo;
    private PhysicalCardImpl _stackedOn;

    private List<ModifierHook> _modifierHooks;
    private List<ModifierHook> _modifierHooksStacked;

    private Object _data;

    public PhysicalCardImpl(int cardId, String blueprintId, String owner, LotroCardBlueprint blueprint) {
        _cardId = cardId;
        _blueprintId = blueprintId;
        _owner = owner;
        _blueprint = blueprint;
    }

    public void setCardId(int cardId) {
        _cardId = cardId;
    }

    @Override
    public String getBlueprintId() {
        return _blueprintId;
    }

    public void setZone(Zone zone) {
        _zone = zone;
    }

    @Override
    public Zone getZone() {
        return _zone;
    }

    @Override
    public String getOwner() {
        return _owner;
    }

    public void setCardController(String siteController) {
        _cardController = siteController;
    }

    @Override
    public String getCardController() {
        return _cardController;
    }

    public void startAffectingGame(LotroGame game) {
        List<? extends Modifier> modifiers = _blueprint.getAlwaysOnModifiers(game, this);
        if (modifiers != null) {
            _modifierHooks = new LinkedList<ModifierHook>();
            for (Modifier modifier : modifiers)
                _modifierHooks.add(game.getModifiersEnvironment().addAlwaysOnModifier(modifier));
        }
    }

    public void stopAffectingGame() {
        if (_modifierHooks != null) {
            for (ModifierHook modifierHook : _modifierHooks)
                modifierHook.stop();
            _modifierHooks = null;
        }
    }

    public void startAffectingGameStacked(LotroGame game) {
        List<? extends Modifier> modifiers = _blueprint.getStackedOnModifiers(game, this);
        if (modifiers != null) {
            _modifierHooksStacked = new LinkedList<ModifierHook>();
            for (Modifier modifier : modifiers)
                _modifierHooksStacked.add(game.getModifiersEnvironment().addAlwaysOnModifier(modifier));
        }
    }

    public void stopAffectingGameStacked() {
        if (_modifierHooksStacked != null) {
            for (ModifierHook modifierHook : _modifierHooksStacked)
                modifierHook.stop();
            _modifierHooksStacked = null;
        }
    }

    @Override
    public int getCardId() {
        return _cardId;
    }

    @Override
    public LotroCardBlueprint getBlueprint() {
        return _blueprint;
    }

    public void attachTo(PhysicalCardImpl physicalCard) {
        _attachedTo = physicalCard;
    }

    @Override
    public PhysicalCard getAttachedTo() {
        return _attachedTo;
    }

    public void stackOn(PhysicalCardImpl physicalCard) {
        _stackedOn = physicalCard;
    }

    @Override
    public PhysicalCard getStackedOn() {
        return _stackedOn;
    }

    @Override
    public void storeData(Object object) {
        _data = object;
    }

    @Override
    public Object getData() {
        return _data;
    }

    @Override
    public void removeData() {
        _data = null;
    }
}
