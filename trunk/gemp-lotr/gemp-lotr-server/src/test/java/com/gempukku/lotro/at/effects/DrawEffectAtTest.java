package com.gempukku.lotro.at.effects;

import com.gempukku.lotro.at.AbstractAtTest;
import com.gempukku.lotro.cards.TriggerConditions;
import com.gempukku.lotro.game.AbstractActionProxy;
import com.gempukku.lotro.game.PhysicalCardImpl;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.RequiredTriggerAction;
import com.gempukku.lotro.logic.decisions.DecisionResultInvalidException;
import com.gempukku.lotro.logic.effects.DrawCardsEffect;
import com.gempukku.lotro.logic.effects.PreventEffect;
import com.gempukku.lotro.logic.timing.Effect;
import com.gempukku.lotro.logic.timing.EffectResult;
import com.gempukku.lotro.logic.timing.Preventable;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class DrawEffectAtTest extends AbstractAtTest {
    @Test
    public void drawingSuccessful() throws DecisionResultInvalidException {
        initializeSimplestGame();

        skipMulligans();

        final PhysicalCardImpl merry = new PhysicalCardImpl(101, "1_303", P1, _library.getLotroCardBlueprint("1_303"));

        _game.getGameState().putCardOnTopOfDeck(merry);

        final AtomicInteger triggerCount = new AtomicInteger(0);

        _game.getActionsEnvironment().addUntilEndOfTurnActionProxy(
                new AbstractActionProxy() {
                    @Override
                    public List<? extends RequiredTriggerAction> getRequiredAfterTriggers(LotroGame game, EffectResult effectResult) {
                        if (TriggerConditions.forEachCardDrawn(game, effectResult, P1)) {
                            RequiredTriggerAction action = new RequiredTriggerAction(merry);
                            action.appendEffect(
                                    new IncrementEffect(triggerCount));
                            return Collections.singletonList(action);
                        }
                        return null;
                    }
                });

        DrawCardsEffect drawEffect = new DrawCardsEffect(null, P1, 1);

        carryOutEffectInPhaseActionByPlayer(P1, drawEffect);

        assertEquals(1, _game.getGameState().getHand(P1).size());
        assertEquals(0, _game.getGameState().getDeck(P1).size());
        assertTrue(_game.getGameState().getHand(P1).contains(merry));
        assertTrue(drawEffect.wasCarriedOut());

        assertEquals(1, triggerCount.get());
    }

    @Test
    public void drawingMultipleNotSuccessful() throws DecisionResultInvalidException {
        initializeSimplestGame();

        skipMulligans();

        final PhysicalCardImpl merry = new PhysicalCardImpl(101, "1_303", P1, _library.getLotroCardBlueprint("1_303"));

        _game.getGameState().putCardOnTopOfDeck(merry);

        final AtomicInteger triggerCount = new AtomicInteger(0);

        _game.getActionsEnvironment().addUntilEndOfTurnActionProxy(
                new AbstractActionProxy() {
                    @Override
                    public List<? extends RequiredTriggerAction> getRequiredAfterTriggers(LotroGame game, EffectResult effectResult) {
                        if (TriggerConditions.forEachCardDrawn(game, effectResult, P1)) {
                            RequiredTriggerAction action = new RequiredTriggerAction(merry);
                            action.appendEffect(
                                    new IncrementEffect(triggerCount));
                            return Collections.singletonList(action);
                        }
                        return null;
                    }
                });

        DrawCardsEffect drawEffect = new DrawCardsEffect(null, P1, 2);

        carryOutEffectInPhaseActionByPlayer(P1, drawEffect);

        assertEquals(1, _game.getGameState().getHand(P1).size());
        assertEquals(0, _game.getGameState().getDeck(P1).size());
        assertTrue(_game.getGameState().getHand(P1).contains(merry));
        assertFalse(drawEffect.wasCarriedOut());

        assertEquals(1, triggerCount.get());
    }

    @Test
    public void drawingMultipleSuccessful() throws DecisionResultInvalidException {
        initializeSimplestGame();

        skipMulligans();

        final PhysicalCardImpl merry = new PhysicalCardImpl(101, "1_303", P1, _library.getLotroCardBlueprint("1_303"));
        final PhysicalCardImpl merry2 = new PhysicalCardImpl(102, "1_303", P1, _library.getLotroCardBlueprint("1_303"));

        _game.getGameState().putCardOnTopOfDeck(merry);
        _game.getGameState().putCardOnTopOfDeck(merry2);

        final AtomicInteger triggerCount = new AtomicInteger(0);

        _game.getActionsEnvironment().addUntilEndOfTurnActionProxy(
                new AbstractActionProxy() {
                    @Override
                    public List<? extends RequiredTriggerAction> getRequiredAfterTriggers(LotroGame game, EffectResult effectResult) {
                        if (TriggerConditions.forEachCardDrawn(game, effectResult, P1)) {
                            RequiredTriggerAction action = new RequiredTriggerAction(merry);
                            action.appendEffect(
                                    new IncrementEffect(triggerCount));
                            return Collections.singletonList(action);
                        }
                        return null;
                    }
                });

        DrawCardsEffect drawEffect = new DrawCardsEffect(null, P1, 2);

        carryOutEffectInPhaseActionByPlayer(P1, drawEffect);

        assertEquals(2, _game.getGameState().getHand(P1).size());
        assertEquals(0, _game.getGameState().getDeck(P1).size());
        assertTrue(_game.getGameState().getHand(P1).contains(merry));
        assertTrue(_game.getGameState().getHand(P1).contains(merry2));
        assertTrue(drawEffect.wasCarriedOut());

        assertEquals(2, triggerCount.get());
    }

    @Test
    public void insteadOfDraw() throws DecisionResultInvalidException {
        initializeSimplestGame();

        skipMulligans();

        final PhysicalCardImpl merry = new PhysicalCardImpl(101, "1_303", P1, _library.getLotroCardBlueprint("1_303"));

        _game.getGameState().putCardOnTopOfDeck(merry);

        final AtomicInteger triggerCount = new AtomicInteger(0);
        final AtomicInteger preventCount = new AtomicInteger(0);

        _game.getActionsEnvironment().addUntilEndOfTurnActionProxy(
                new AbstractActionProxy() {
                    @Override
                    public List<? extends RequiredTriggerAction> getRequiredAfterTriggers(LotroGame game, EffectResult effectResult) {
                        if (TriggerConditions.forEachCardDrawn(game, effectResult, P1)) {
                            RequiredTriggerAction action = new RequiredTriggerAction(merry);
                            action.appendEffect(
                                    new IncrementEffect(triggerCount));
                            return Collections.singletonList(action);
                        }
                        return null;
                    }

                    @Override
                    public List<? extends RequiredTriggerAction> getRequiredBeforeTriggers(LotroGame game, Effect effect) {
                        if (TriggerConditions.isDrawingACard(effect, game, P1)) {
                            RequiredTriggerAction action = new RequiredTriggerAction(merry);
                            action.appendEffect(
                                    new PreventEffect((Preventable) effect));
                            action.appendEffect(
                                    new IncrementEffect(preventCount));
                            return Collections.singletonList(action);
                        }
                        return null;
                    }
                });

        DrawCardsEffect drawEffect = new DrawCardsEffect(null, P1, 1);

        carryOutEffectInPhaseActionByPlayer(P1, drawEffect);

        assertEquals(0, _game.getGameState().getHand(P1).size());
        assertEquals(1, _game.getGameState().getDeck(P1).size());
        assertFalse(drawEffect.wasCarriedOut());

        assertEquals(0, triggerCount.get());
        assertEquals(1, preventCount.get());
    }
}
