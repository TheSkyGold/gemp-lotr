package com.gempukku.lotro.at;

import com.gempukku.lotro.common.Phase;
import com.gempukku.lotro.common.Token;
import com.gempukku.lotro.common.Zone;
import com.gempukku.lotro.game.CardNotFoundException;
import com.gempukku.lotro.game.DefaultAdventureLibrary;
import com.gempukku.lotro.game.DefaultUserFeedback;
import com.gempukku.lotro.game.LotroFormat;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.PhysicalCardImpl;
import com.gempukku.lotro.game.formats.LotroFormatLibrary;
import com.gempukku.lotro.logic.decisions.AwaitingDecision;
import com.gempukku.lotro.logic.decisions.AwaitingDecisionType;
import com.gempukku.lotro.logic.decisions.DecisionResultInvalidException;
import com.gempukku.lotro.logic.timing.DefaultLotroGame;
import com.gempukku.lotro.logic.vo.LotroDeck;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class IndividualCardAtTest extends AbstractAtTest {
    @Test
    public void dwarvenAxeDoesNotFreeze() throws DecisionResultInvalidException, CardNotFoundException {
        Map<String, Collection<String>> extraCards = new HashMap<String, Collection<String>>();
        initializeSimplestGame(extraCards);

        PhysicalCardImpl gimli = new PhysicalCardImpl(100, "1_13", P1, _library.getLotroCardBlueprint("1_13"));
        PhysicalCardImpl dwarvenAxe = new PhysicalCardImpl(101, "1_9", P1, _library.getLotroCardBlueprint("1_9"));
        PhysicalCardImpl goblinRunner = new PhysicalCardImpl(102, "1_178", P2, _library.getLotroCardBlueprint("1_178"));

        skipMulligans();

        _game.getGameState().addCardToZone(_game, gimli, Zone.FREE_CHARACTERS);
        _game.getGameState().attachCard(_game, dwarvenAxe, gimli);

        // End fellowship phase
        assertEquals(Phase.FELLOWSHIP, _game.getGameState().getCurrentPhase());
        playerDecided(P1, "");

        _game.getGameState().addCardToZone(_game, goblinRunner, Zone.SHADOW_CHARACTERS);

        // End shadow phase
        assertEquals(Phase.SHADOW, _game.getGameState().getCurrentPhase());
        playerDecided(P2, "");

        // End maneuver phase
        playerDecided(P1, "");
        playerDecided(P2, "");

        // End archery phase
        playerDecided(P1, "");
        playerDecided(P2, "");

        // End assignment phase
        playerDecided(P1, "");
        playerDecided(P2, "");

        // Assign
        playerDecided(P1, gimli.getCardId() + " " + goblinRunner.getCardId());

        // Start skirmish
        playerDecided(P1, String.valueOf(gimli.getCardId()));

        // End skirmish phase
        playerDecided(P1, "");
        playerDecided(P2, "");

        final AwaitingDecision awaitingDecision = _userFeedback.getAwaitingDecision(P1);
        assertEquals(AwaitingDecisionType.ACTION_CHOICE, awaitingDecision.getDecisionType());
        validateContents(new String[]{"1_9", "rules"}, (String[]) awaitingDecision.getDecisionParameters().get("blueprintId"));

        playerDecided(P1, "0");

        assertEquals(Phase.REGROUP, _game.getGameState().getCurrentPhase());
        assertEquals(Zone.DISCARD, goblinRunner.getZone());
    }

    @Test
    public void playDiscountAsfalothOnArwen() throws DecisionResultInvalidException {
        Map<String, Collection<String>> extraCards = new HashMap<String, Collection<String>>();
        extraCards.put(P1, Arrays.asList("1_30", "1_31"));
        initializeSimplestGame(extraCards);

        // Play first character
        AwaitingDecision firstCharacterDecision = _userFeedback.getAwaitingDecision(P1);
        assertEquals(AwaitingDecisionType.ARBITRARY_CARDS, firstCharacterDecision.getDecisionType());
        validateContents(new String[]{"1_30"}, ((String[]) firstCharacterDecision.getDecisionParameters().get("blueprintId")));

        playerDecided(P1, getArbitraryCardId(firstCharacterDecision, "1_30"));

        skipMulligans();

        PhysicalCard asfaloth = _game.getGameState().getHand(P1).get(0);

        final AwaitingDecision awaitingDecision = _userFeedback.getAwaitingDecision(P1);
        assertEquals(AwaitingDecisionType.CARD_ACTION_CHOICE, awaitingDecision.getDecisionType());
        validateContents(new String[]{"" + asfaloth.getCardId()}, (String[]) awaitingDecision.getDecisionParameters().get("cardId"));

        assertEquals(0, _game.getGameState().getTwilightPool());

        playerDecided(P1, "0");

        assertEquals(0, _game.getGameState().getTwilightPool());
        assertEquals(Zone.ATTACHED, asfaloth.getZone());
    }

    @Test
    public void playDiscountAsfalothOnOtherElf() throws DecisionResultInvalidException {
        Map<String, Collection<String>> extraCards = new HashMap<String, Collection<String>>();
        extraCards.put(P1, Arrays.asList("1_51", "1_31"));
        initializeSimplestGame(extraCards);

        // Play first character
        AwaitingDecision firstCharacterDecision = _userFeedback.getAwaitingDecision(P1);
        assertEquals(AwaitingDecisionType.ARBITRARY_CARDS, firstCharacterDecision.getDecisionType());
        validateContents(new String[]{"1_51"}, ((String[]) firstCharacterDecision.getDecisionParameters().get("blueprintId")));

        playerDecided(P1, getArbitraryCardId(firstCharacterDecision, "1_51"));

        skipMulligans();

        PhysicalCard asfaloth = _game.getGameState().getHand(P1).get(0);

        final AwaitingDecision awaitingDecision = _userFeedback.getAwaitingDecision(P1);
        assertEquals(AwaitingDecisionType.CARD_ACTION_CHOICE, awaitingDecision.getDecisionType());
        validateContents(new String[]{"" + asfaloth.getCardId()}, (String[]) awaitingDecision.getDecisionParameters().get("cardId"));

        assertEquals(0, _game.getGameState().getTwilightPool());

        playerDecided(P1, "0");

        assertEquals(2, _game.getGameState().getTwilightPool());
        assertEquals(Zone.ATTACHED, asfaloth.getZone());
    }

    @Test
    public void bilboRingBearerWithConsortingAndMorgulBrute() throws DecisionResultInvalidException, CardNotFoundException {
        Map<String, LotroDeck> decks = new HashMap<String, LotroDeck>();
        final LotroDeck p1Deck = createSimplestDeck();
        p1Deck.setRingBearer("9_49");
        decks.put(P1, p1Deck);
        final LotroDeck p2Deck = createSimplestDeck();
        p2Deck.addCard("7_188");
        decks.put(P2, p2Deck);

        initializeGameWithDecks(decks);

        skipMulligans();

        PhysicalCard morgulBrute = _game.getGameState().getHand(P2).iterator().next();

        PhysicalCardImpl consortingWithWizards = new PhysicalCardImpl(100, "2_97", P1, _library.getLotroCardBlueprint("2_97"));
        PhysicalCardImpl ÚlairëEnquëa = new PhysicalCardImpl(101, "1_231", P2, _library.getLotroCardBlueprint("1_231"));

        _game.getGameState().attachCard(_game, consortingWithWizards, _game.getGameState().getRingBearer(P1));

        _game.getGameState().addTwilight(3);

        // End fellowship phase
        playerDecided(P1, "");

        _game.getGameState().addCardToZone(_game, ÚlairëEnquëa, Zone.SHADOW_CHARACTERS);

        final AwaitingDecision shadowDecision = _userFeedback.getAwaitingDecision(P2);
        assertEquals(AwaitingDecisionType.CARD_ACTION_CHOICE, shadowDecision.getDecisionType());
        validateContents(new String[]{"" + morgulBrute.getCardId()}, ((String[]) shadowDecision.getDecisionParameters().get("cardId")));

        playerDecided(P2, "0");

        final AwaitingDecision optionalPlayDecision = _userFeedback.getAwaitingDecision(P2);
        assertEquals(AwaitingDecisionType.CARD_ACTION_CHOICE, optionalPlayDecision.getDecisionType());
        validateContents(new String[]{"" + morgulBrute.getCardId()}, ((String[]) optionalPlayDecision.getDecisionParameters().get("cardId")));

        assertEquals(1, _game.getGameState().getBurdens());

        // User optional trigger of Morgul Brute
        playerDecided(P2, "0");

        // This should add burden without asking FP player for choice, since Bilbo can't be wounded, because of Consorting With Wizards
        assertEquals(2, _game.getGameState().getBurdens());
    }

    @Test
    public void mumakChieftainPlayingMumakForFree() throws DecisionResultInvalidException, CardNotFoundException {
        Map<String, Collection<String>> extraCards = new HashMap<String, Collection<String>>();
        initializeSimplestGame(extraCards);

        PhysicalCardImpl mumakChieftain = new PhysicalCardImpl(100, "10_45", P2, _library.getLotroCardBlueprint("10_45"));
        PhysicalCardImpl mumak = new PhysicalCardImpl(100, "5_73", P2, _library.getLotroCardBlueprint("5_73"));

        skipMulligans();

        _game.getGameState().addCardToZone(_game, mumak, Zone.DISCARD);
        _game.getGameState().addCardToZone(_game, mumakChieftain, Zone.HAND);
        _game.getGameState().setTwilight(5);

        // End fellowship phase
        playerDecided(P1, "");

        assertEquals(7, _game.getGameState().getTwilightPool());

        // Play mumak chieftain
        final AwaitingDecision shadowDecision = _userFeedback.getAwaitingDecision(P2);
        assertEquals(AwaitingDecisionType.CARD_ACTION_CHOICE, shadowDecision.getDecisionType());
        validateContents(new String[]{"" + mumakChieftain.getCardId()}, ((String[]) shadowDecision.getDecisionParameters().get("cardId")));
        playerDecided(P2, "0");

        assertEquals(0, _game.getGameState().getTwilightPool());

        final AwaitingDecision optionalPlayDecision = _userFeedback.getAwaitingDecision(P2);
        assertEquals(AwaitingDecisionType.CARD_ACTION_CHOICE, optionalPlayDecision.getDecisionType());
        validateContents(new String[]{"" + mumakChieftain.getCardId()}, ((String[]) optionalPlayDecision.getDecisionParameters().get("cardId")));
        playerDecided(P2, "0");

        assertEquals(Zone.ATTACHED, mumak.getZone());
    }

    @Test
    public void musterFrodoAllowsToDiscardAndDraw() throws DecisionResultInvalidException, CardNotFoundException {
        Map<String, LotroDeck> decks = new HashMap<String, LotroDeck>();
        final LotroDeck p1Deck = createSimplestDeck();
        p1Deck.setRingBearer("11_164");
        decks.put(P1, p1Deck);
        decks.put(P2, createSimplestDeck());

        initializeGameWithDecks(decks);

        skipMulligans();

        PhysicalCardImpl mumakChieftain = new PhysicalCardImpl(100, "10_45", P1, _library.getLotroCardBlueprint("10_45"));
        PhysicalCardImpl mumakChieftain2 = new PhysicalCardImpl(101, "10_45", P1, _library.getLotroCardBlueprint("10_45"));

        _game.getGameState().addCardToZone(_game, mumakChieftain, Zone.HAND);
        _game.getGameState().addCardToZone(_game, mumakChieftain2, Zone.DECK);

        // End fellowship phase
        playerDecided(P1, "");

        // End shadow phase
        playerDecided(P2, "");

        playerDecided(P1, "0");

        assertEquals(1, _game.getGameState().getWounds(_game.getGameState().getRingBearer(P1)));
        assertEquals(1, _game.getGameState().getHand(P1).size());

        final AwaitingDecision musterUseDecision = _userFeedback.getAwaitingDecision(P1);
        assertEquals(AwaitingDecisionType.CARD_ACTION_CHOICE, musterUseDecision.getDecisionType());
        validateContents(new String[]{"" + _game.getGameState().getRingBearer(P1).getCardId()}, ((String[]) musterUseDecision.getDecisionParameters().get("cardId")));
        playerDecided(P1, "0");

        final AwaitingDecision musterDiscardDecision = _userFeedback.getAwaitingDecision(P1);
        assertEquals(AwaitingDecisionType.CARD_SELECTION, musterDiscardDecision.getDecisionType());
        validateContents(new String[]{"" + mumakChieftain.getCardId()}, ((String[]) musterDiscardDecision.getDecisionParameters().get("cardId")));
        playerDecided(P1, ((String[]) musterDiscardDecision.getDecisionParameters().get("cardId"))[0]);

        assertEquals(1, _game.getGameState().getHand(P1).size());

        assertEquals(Zone.HAND, mumakChieftain2.getZone());
        assertEquals(Phase.REGROUP, _game.getGameState().getCurrentPhase());
    }

    @Test
    public void legolasBowWithToil() throws DecisionResultInvalidException {
        Map<String, Collection<String>> extraCards = new HashMap<String, Collection<String>>();
        extraCards.put(P1, Arrays.asList("11_21", "11_23", "11_17"));
        initializeSimplestGame(extraCards);

        // Play first character
        AwaitingDecision firstCharacterDecision = _userFeedback.getAwaitingDecision(P1);
        assertEquals(AwaitingDecisionType.ARBITRARY_CARDS, firstCharacterDecision.getDecisionType());
        validateContents(new String[]{"11_21"}, ((String[]) firstCharacterDecision.getDecisionParameters().get("blueprintId")));

        playerDecided(P1, getArbitraryCardId(firstCharacterDecision, "11_21"));

        skipMulligans();

        AwaitingDecision playFirstFellowship = _userFeedback.getAwaitingDecision(P1);
        playerDecided(P1, getCardActionId(playFirstFellowship, "Attach Legolas"));

        AwaitingDecision playSecondFellowship = _userFeedback.getAwaitingDecision(P1);
        playerDecided(P1, getCardActionId(playSecondFellowship, "Play Elven"));

        AwaitingDecision toilExertion = _userFeedback.getAwaitingDecision(P1);
        assertEquals(AwaitingDecisionType.CARD_SELECTION, toilExertion.getDecisionType());
        String legolasCardId = ((String[]) toilExertion.getDecisionParameters().get("cardId"))[0];
        playerDecided(P1, legolasCardId);

        assertEquals(1, _game.getGameState().getWounds(_game.getGameState().findCardById(Integer.parseInt(legolasCardId))));

        AwaitingDecision bowHeal = _userFeedback.getAwaitingDecision(P1);
        assertEquals(AwaitingDecisionType.CARD_ACTION_CHOICE, bowHeal.getDecisionType());
        playerDecided(P1, "0");

        assertEquals(0, _game.getGameState().getWounds(_game.getGameState().findCardById(Integer.parseInt(legolasCardId))));
    }

    @Test
    public void endofTheGameGivingDamagePlusOne() throws DecisionResultInvalidException, CardNotFoundException {
        initializeSimplestGame();

        skipMulligans();

        PhysicalCardImpl aragorn = new PhysicalCardImpl(100, "1_89", P1, _library.getLotroCardBlueprint("1_89"));
        PhysicalCardImpl urukHaiRaidingParty = new PhysicalCardImpl(100, "1_158", P2, _library.getLotroCardBlueprint("1_158"));
        PhysicalCardImpl endOfTheGame = new PhysicalCardImpl(100, "10_30", P1, _library.getLotroCardBlueprint("10_30"));

        _game.getGameState().addCardToZone(_game, aragorn, Zone.FREE_CHARACTERS);
        _game.getGameState().addWound(aragorn);
        _game.getGameState().addWound(aragorn);
        _game.getGameState().addWound(aragorn);

        _game.getGameState().addCardToZone(_game, endOfTheGame, Zone.HAND);

        // End fellowship phase
        playerDecided(P1, "");

        _game.getGameState().addCardToZone(_game, urukHaiRaidingParty, Zone.SHADOW_CHARACTERS);

        // End shadow phase
        playerDecided(P2, "");

        // End maneuvers phase
        playerDecided(P1, "");
        playerDecided(P2, "");

        // End arhcery phase
        playerDecided(P1, "");
        playerDecided(P2, "");

        // End assignment phase
        playerDecided(P1, "");
        playerDecided(P2, "");

        // Assign
        playerDecided(P1, aragorn.getCardId() + " " + urukHaiRaidingParty.getCardId());

        // Start skirmish
        playerDecided(P1, String.valueOf(aragorn.getCardId()));

        AwaitingDecision playSkirmishEvent = _userFeedback.getAwaitingDecision(P1);
        assertEquals(AwaitingDecisionType.CARD_ACTION_CHOICE, playSkirmishEvent.getDecisionType());
        playerDecided(P1, "0");

        assertEquals(10, _game.getModifiersQuerying().getStrength(_game.getGameState(), aragorn));

        // End skirmish phase
        playerDecided(P2, "");
        playerDecided(P1, "");

        AwaitingDecision skirmishWinResponse = _userFeedback.getAwaitingDecision(P1);
        assertEquals(AwaitingDecisionType.ACTION_CHOICE, skirmishWinResponse.getDecisionType());
        playerDecided(P1, getCardActionId(skirmishWinResponse, "Required"));

        AwaitingDecision effectChoice = _userFeedback.getAwaitingDecision(P1);
        assertEquals(AwaitingDecisionType.MULTIPLE_CHOICE, effectChoice.getDecisionType());

        int index = -1;
        String[] choices = ((String[]) effectChoice.getDecisionParameters().get("results"));
        for (int i = 0; i < choices.length; i++)
            if (choices[i].startsWith("Make "))
                index = i;

        playerDecided(P1, String.valueOf(index));

        assertEquals(2, _game.getGameState().getWounds(urukHaiRaidingParty));
    }

    @Test
    public void oneGoodTurnDeservesAnother() throws DecisionResultInvalidException, CardNotFoundException {
        initializeSimplestGame();

        PhysicalCardImpl smeagol = new PhysicalCardImpl(100, "5_29", P1, _library.getLotroCardBlueprint("5_29"));
        PhysicalCardImpl oneGoodTurnDeservesAnother = new PhysicalCardImpl(101, "11_49", P1, _library.getLotroCardBlueprint("11_49"));

        _game.getGameState().addCardToZone(_game, oneGoodTurnDeservesAnother, Zone.HAND);
        _game.getGameState().addCardToZone(_game, smeagol, Zone.FREE_CHARACTERS);

        skipMulligans();

        playerDecided(P1, "0");

        assertEquals(1, _game.getGameState().getBurdens());
        assertEquals(0, _game.getGameState().getHand(P1).size());

        playerDecided(P1, "0");

        assertEquals(2, _game.getGameState().getBurdens());
        assertEquals(1, _game.getGameState().getHand(P1).size());
    }

    @Test
    public void oElberethGilthoniel() throws DecisionResultInvalidException, CardNotFoundException {
        Map<String, LotroDeck> decks = new HashMap<String, LotroDeck>();
        final LotroDeck p1Deck = createSimplestDeck();
        p1Deck.setRingBearer("9_49");
        decks.put(P1, p1Deck);

        decks.put(P2, createSimplestDeck());

        initializeGameWithDecks(decks);

        skipMulligans();

        PhysicalCardImpl oElberethGilthoniel = new PhysicalCardImpl(100, "2_108", P1, _library.getLotroCardBlueprint("2_108"));
        PhysicalCardImpl ulaireToldea = new PhysicalCardImpl(101, "1_236", P2, _library.getLotroCardBlueprint("1_236"));

        _game.getGameState().attachCard(_game, oElberethGilthoniel, _game.getGameState().getRingBearer(P1));

        _game.getGameState().addTwilight(3);

        // End fellowship phase
        playerDecided(P1, "");

        _game.getGameState().addCardToZone(_game, ulaireToldea, Zone.SHADOW_CHARACTERS);

        // End shadow phase
        playerDecided(P2, "");

        // End maneuvers phase
        playerDecided(P1, "");
        playerDecided(P2, "");

        // End archery phase
        playerDecided(P1, "");
        playerDecided(P2, "");

        // End assignment phase
        playerDecided(P1, "");
        playerDecided(P2, "");

        // Assign
        playerDecided(P1, _game.getGameState().getRingBearer(P1).getCardId() + " " + ulaireToldea.getCardId());

        // Start skirmish
        playerDecided(P1, String.valueOf(_game.getGameState().getRingBearer(P1).getCardId()));

        AwaitingDecision playSkirmishEvent = _userFeedback.getAwaitingDecision(P1);
        assertEquals(AwaitingDecisionType.CARD_ACTION_CHOICE, playSkirmishEvent.getDecisionType());
        validateContents(new String[]{"" + oElberethGilthoniel.getCardId()}, ((String[]) playSkirmishEvent.getDecisionParameters().get("cardId")));
        playerDecided(P1, "0");

        // Should not cancel skirmish (since it's a ring-bearer)
        assertEquals(Phase.SKIRMISH, _game.getGameState().getCurrentPhase());
    }

    @Test
    public void frodoCantBePlayedInStartingFellowship() throws DecisionResultInvalidException {
        Map<String, LotroDeck> decks = new HashMap<String, LotroDeck>();
        final LotroDeck p1Deck = createSimplestDeck();
        p1Deck.setRingBearer("13_156");
        p1Deck.addCard("4_301");
        decks.put(P1, p1Deck);

        decks.put(P2, createSimplestDeck());

        initializeGameWithDecks(decks);

        AwaitingDecision decision = _userFeedback.getAwaitingDecision(P1);
        assertEquals(AwaitingDecisionType.MULTIPLE_CHOICE, decision.getDecisionType());
    }

    @Test
    public void fordOfBruinenReduce() throws DecisionResultInvalidException, CardNotFoundException {
        initializeSimplestGame();

        skipMulligans();

        // End fellowship
        playerDecided(P1, "");

        // End shadow
        playerDecided(P2, "");

        PhysicalCardImpl ford = new PhysicalCardImpl(101, "1_338", P2, _library.getLotroCardBlueprint("1_338"));
        ford.setSiteNumber(3);
        _game.getGameState().addCardToZone(_game, ford, Zone.ADVENTURE_PATH);

        // End regroup
        playerDecided(P1, "");
        playerDecided(P2, "");

        // Decide to move
        playerDecided(P1, getMultipleDecisionIndex(_userFeedback.getAwaitingDecision(P1), "Yes"));

        // End shadow
        playerDecided(P2, "");

        // End regroup
        playerDecided(P1, "");
        playerDecided(P2, "");

        // P2 Turn
        PhysicalCardImpl attea = new PhysicalCardImpl(102, "1_229", P1, _library.getLotroCardBlueprint("1_229"));
        _game.getGameState().addCardToZone(_game, attea, Zone.HAND);

        // End fellowship
        playerDecided(P2, "");

        assertEquals(8, _game.getModifiersQuerying().getTwilightCost(_game.getGameState(), attea, 0, false));
    }

    @Test
    public void sentBackAllowsPlayingCardInDeadPile() throws DecisionResultInvalidException, CardNotFoundException {
        initializeSimplestGame();

        PhysicalCardImpl sentBack = new PhysicalCardImpl(100, "9_27", P1, _library.getLotroCardBlueprint("9_27"));
        _game.getGameState().addCardToZone(_game, sentBack, Zone.SUPPORT);

        PhysicalCardImpl radagast1 = new PhysicalCardImpl(101, "9_26", P1, _library.getLotroCardBlueprint("9_26"));
        _game.getGameState().addCardToZone(_game, radagast1, Zone.DEAD);

        PhysicalCardImpl radagast2 = new PhysicalCardImpl(101, "9_26", P1, _library.getLotroCardBlueprint("9_26"));
        _game.getGameState().addCardToZone(_game, radagast2, Zone.HAND);

        skipMulligans();

        // End fellowship
        AwaitingDecision playFellowshipAction = _userFeedback.getAwaitingDecision(P1);
        assertEquals(AwaitingDecisionType.CARD_ACTION_CHOICE, playFellowshipAction.getDecisionType());
        validateContents(new String[]{"" + sentBack.getCardId()}, ((String[]) playFellowshipAction.getDecisionParameters().get("cardId")));
        playerDecided(P1, "0");

        assertEquals(Zone.FREE_CHARACTERS, radagast2.getZone());
        assertEquals(4, _game.getGameState().getTwilightPool());
    }

    @Test
    public void rushOfSteeds() throws DecisionResultInvalidException, CardNotFoundException {
        initializeSimplestGame();

        PhysicalCardImpl rushOfSteeds = new PhysicalCardImpl(100, "11_157", P1, _library.getLotroCardBlueprint("11_157"));
        _game.getGameState().addCardToZone(_game, rushOfSteeds, Zone.SUPPORT);

        PhysicalCardImpl nelya = new PhysicalCardImpl(100, "1_233", P2, _library.getLotroCardBlueprint("1_233"));
        _game.getGameState().addCardToZone(_game, nelya, Zone.SHADOW_CHARACTERS);

        skipMulligans();

        // End fellowship
        playerDecided(P1, "");

        AwaitingDecision playShadowEffect = _userFeedback.getAwaitingDecision(P2);
        assertEquals(AwaitingDecisionType.CARD_ACTION_CHOICE, playShadowEffect.getDecisionType());
        playerDecided(P2, getCardActionId(playShadowEffect, "Use Úlairë "));

        AwaitingDecision rushOfSteedsOptional = _userFeedback.getAwaitingDecision(P1);
        assertEquals(AwaitingDecisionType.CARD_ACTION_CHOICE, rushOfSteedsOptional.getDecisionType());
        validateContents(new String[]{"" + rushOfSteeds.getCardId()}, ((String[]) rushOfSteedsOptional.getDecisionParameters().get("cardId")));

        playerDecided(P1, "0");

        assertEquals(P1, _game.getGameState().getSite(1).getOwner());
        assertEquals(Zone.HAND, nelya.getZone());

        playShadowEffect = _userFeedback.getAwaitingDecision(P2);
        assertEquals(AwaitingDecisionType.CARD_ACTION_CHOICE, playShadowEffect.getDecisionType());
        validateContents(new String[0], (String[]) playShadowEffect.getDecisionParameters().get("cardId"));
    }

    @Test
    public void hisFirstSeriousCheck() throws DecisionResultInvalidException, CardNotFoundException {
        initializeSimplestGame();

        PhysicalCardImpl gandalf = new PhysicalCardImpl(100, "1_72", P1, _library.getLotroCardBlueprint("1_72"));
        _game.getGameState().addCardToZone(_game, gandalf, Zone.FREE_CHARACTERS);

        PhysicalCardImpl hisFirstSeriousCheck = new PhysicalCardImpl(100, "3_33", P1, _library.getLotroCardBlueprint("3_33"));
        _game.getGameState().addCardToZone(_game, hisFirstSeriousCheck, Zone.HAND);

        skipMulligans();

        // End fellowship
        playerDecided(P1, "");

        PhysicalCardImpl urukHaiRaidingParty = new PhysicalCardImpl(102, "1_158", P2, _library.getLotroCardBlueprint("1_158"));
        _game.getGameState().addCardToZone(_game, urukHaiRaidingParty, Zone.SHADOW_CHARACTERS);

        // End shadow
        playerDecided(P2, "");

        AwaitingDecision playManeuverAction = _userFeedback.getAwaitingDecision(P1);
        assertEquals(AwaitingDecisionType.CARD_ACTION_CHOICE, playManeuverAction.getDecisionType());
        validateContents(new String[]{hisFirstSeriousCheck.getCardId() + ""}, (String[]) playManeuverAction.getDecisionParameters().get("cardId"));
    }

    @Test
    public void scouringOfTheShireAndCorsairMarauder() throws DecisionResultInvalidException, CardNotFoundException {
        initializeSimplestGame();

        PhysicalCardImpl corsairWarGalley = new PhysicalCardImpl(100, "8_59", P2, _library.getLotroCardBlueprint("8_59"));
        PhysicalCardImpl corsairMarauder = new PhysicalCardImpl(101, "8_57", P2, _library.getLotroCardBlueprint("8_57"));
        PhysicalCardImpl corsairMarauder2 = new PhysicalCardImpl(101, "8_57", P2, _library.getLotroCardBlueprint("8_57"));
        PhysicalCardImpl scourgeOfTheShire = new PhysicalCardImpl(102, "18_112", P1, _library.getLotroCardBlueprint("18_112"));
        PhysicalCardImpl hobbitSword = new PhysicalCardImpl(102, "1_299", P1, _library.getLotroCardBlueprint("1_299"));

        _game.getGameState().addCardToZone(_game, scourgeOfTheShire, Zone.SUPPORT);
        _game.getGameState().attachCard(_game, hobbitSword, _game.getGameState().getRingBearer(P1));
        _game.getGameState().addCardToZone(_game, corsairWarGalley, Zone.SUPPORT);
        _game.getGameState().addCardToZone(_game, corsairMarauder, Zone.HAND);
        _game.getGameState().addCardToZone(_game, corsairMarauder2, Zone.SHADOW_CHARACTERS);

        skipMulligans();
        _game.getGameState().addTwilight(10);

        // End fellowship
        playerDecided(P1, "");

        _game.getGameState().addTokens(corsairWarGalley, Token.RAIDER, 1);

        AwaitingDecision playShadowAction = _userFeedback.getAwaitingDecision(P2);
        assertEquals(AwaitingDecisionType.CARD_ACTION_CHOICE, playShadowAction.getDecisionType());
        playerDecided(P2, getCardActionId(playShadowAction, "Play Cor"));

        // Use Corsair Marauder's trigger
        playerDecided(P2, "0");

        // Choose Hobbit Sword to discard
        playerDecided(P2, ""+hobbitSword.getCardId());

        // Use Scourge of the Shire
        playerDecided(P1, "0");

        assertEquals(1, _game.getGameState().getTokenCount(scourgeOfTheShire, Token.SHIRE));
        assertEquals(Zone.ATTACHED, hobbitSword.getZone());
        assertEquals(1, _game.getGameState().getTokenCount(corsairWarGalley, Token.RAIDER));
    }

    @Test
    public void returnToItsMaster() throws DecisionResultInvalidException, CardNotFoundException {
        LotroDeck p1Deck = createSimplestDeck();
        p1Deck.setRing("4_1");
        LotroDeck p2Deck = createSimplestDeck();

        Map<String, LotroDeck> decks = new HashMap<String, LotroDeck>();
        decks.put(P1, p1Deck);
        decks.put(P2, p2Deck);

        initializeGameWithDecks(decks);

        skipMulligans();

        PhysicalCardImpl returnToItsMaster = new PhysicalCardImpl(102, "1_224", P2, _library.getLotroCardBlueprint("1_224"));
        _game.getGameState().addCardToZone(_game, returnToItsMaster, Zone.HAND);

        PhysicalCardImpl nelya = new PhysicalCardImpl(102, "1_233", P2, _library.getLotroCardBlueprint("1_233"));
        _game.getGameState().addCardToZone(_game, nelya, Zone.SHADOW_CHARACTERS);

        PhysicalCardImpl hobbitSword = new PhysicalCardImpl(102, "1_299", P1, _library.getLotroCardBlueprint("1_299"));
        _game.getGameState().attachCard(_game, hobbitSword, _game.getGameState().getRingBearer(P1));

        // End fellowship
        playerDecided(P1, "");

        // End shadow
        playerDecided(P2, "");

        // End maneuvers phase
        playerDecided(P1, "");
        playerDecided(P2, "");

        // End archery phase
        playerDecided(P1, "");
        playerDecided(P2, "");

        // End assignment phase
        playerDecided(P1, "");
        playerDecided(P2, "");

        // Assign
        playerDecided(P1, _game.getGameState().getRingBearer(P1).getCardId() + " " + nelya.getCardId());

        // Choose skirmish to resolve
        playerDecided(P1, ""+_game.getGameState().getRingBearer(P1).getCardId());

        // Skirmish phase
        AwaitingDecision skirmishAction = _userFeedback.getAwaitingDecision(P1);
        playerDecided(P1, getCardActionId(skirmishAction, "Use The One"));

        assertTrue(_game.getGameState().isWearingRing());

        // End skirmish phase
        playerDecided(P2, "");
        playerDecided(P1, "");

        // Don't use Return to Its Master
        playerDecided(P2, "");

        // End assignment phase
        playerDecided(P1, "");
        playerDecided(P2, "");

        // Assign
        playerDecided(P1, _game.getGameState().getRingBearer(P1).getCardId() + " " + nelya.getCardId());

        // Choose skirmish to resolve
        playerDecided(P1, ""+_game.getGameState().getRingBearer(P1).getCardId());

        // End fierce skirmish phase
        playerDecided(P1, "");
        playerDecided(P2, "");

        AwaitingDecision playeReturnDecision = _userFeedback.getAwaitingDecision(P2);
        playerDecided(P2, getCardActionId(playeReturnDecision, "Play Return"));

        assertEquals(_game.getGameState().getRingBearer(P1), _game.getGameState().getSkirmish().getFellowshipCharacter());
        assertEquals(1, _game.getGameState().getSkirmish().getShadowCharacters().size());
        assertEquals(nelya, _game.getGameState().getSkirmish().getShadowCharacters().iterator().next());
    }

    @Test
    public void moreYetToComeWorks() throws DecisionResultInvalidException, CardNotFoundException {
        initializeSimplestGame();

        PhysicalCardImpl gimli = new PhysicalCardImpl(100, "1_12", P1, _library.getLotroCardBlueprint("1_12"));
        PhysicalCardImpl moreYetToCome = new PhysicalCardImpl(101, "10_3", P1, _library.getLotroCardBlueprint("10_3"));
        PhysicalCardImpl goblinRunner = new PhysicalCardImpl(102, "1_178", P2, _library.getLotroCardBlueprint("1_178"));
        PhysicalCardImpl goblinRunner2 = new PhysicalCardImpl(103, "1_178", P2, _library.getLotroCardBlueprint("1_178"));

        _game.getGameState().addCardToZone(_game, moreYetToCome, Zone.HAND);

        skipMulligans();

        _game.getGameState().addCardToZone(_game, gimli, Zone.FREE_CHARACTERS);

        // End fellowship
        playerDecided(P1, "");

        _game.getGameState().addCardToZone(_game, goblinRunner, Zone.SHADOW_CHARACTERS);
        _game.getGameState().addCardToZone(_game, goblinRunner2, Zone.SHADOW_CHARACTERS);

        // End shadow
        playerDecided(P2, "");

        // End maneuver
        playerDecided(P1, "");
        playerDecided(P2, "");

        // End archery
        playerDecided(P1, "");
        playerDecided(P2, "");

        // End assignment
        playerDecided(P1, "");
        playerDecided(P2, "");

        // Assign Gimli to goblin runner
        playerDecided(P1, gimli.getCardId() + " " + goblinRunner.getCardId());

        playerDecided(P2, "");

        // Choose skirmish to start
        playerDecided(P1, gimli.getCardId()+"");

        // End skirmish
        playerDecided(P1, "");
        playerDecided(P2, "");

        assertEquals(Zone.DISCARD, goblinRunner.getZone());
        AwaitingDecision playMoreYetToCome = _userFeedback.getAwaitingDecision(P1);
        playerDecided(P1, getCardActionId(playMoreYetToCome, "Play More"));

        assertEquals(Zone.DISCARD, goblinRunner2.getZone());
    }

    @Test
    public void treebeardEarthborn() throws DecisionResultInvalidException, CardNotFoundException {
        initializeSimplestGame();

        PhysicalCardImpl treebeard = new PhysicalCardImpl(100, "4_103", P1, _library.getLotroCardBlueprint("4_103"));
        PhysicalCardImpl merry = new PhysicalCardImpl(101, "4_311", P1, _library.getLotroCardBlueprint("4_311"));
        PhysicalCardImpl goblinRunner = new PhysicalCardImpl(102, "1_178", P2, _library.getLotroCardBlueprint("1_178"));

        skipMulligans();

        _game.getGameState().addCardToZone(_game, treebeard, Zone.SUPPORT);
        _game.getGameState().addCardToZone(_game, merry, Zone.FREE_CHARACTERS);

        // End fellowship
        playerDecided(P1, "");

        _game.getGameState().addCardToZone(_game, goblinRunner, Zone.SHADOW_CHARACTERS);

        // End shadow
        playerDecided(P2, "");

        // End maneuver
        playerDecided(P1, "");
        playerDecided(P2, "");

        // End archery
        playerDecided(P1, "");
        playerDecided(P2, "");

        // End assignment
        playerDecided(P1, "");
        playerDecided(P2, "");

        // Assign Gimli to goblin runner
        PhysicalCard frodo = _game.getGameState().getRingBearer(P1);
        playerDecided(P1, frodo.getCardId() + " " + goblinRunner.getCardId());

        // Choose skirmish to start
        playerDecided(P1, frodo.getCardId()+"");

        AwaitingDecision playSkirmishAction = _userFeedback.getAwaitingDecision(P1);
        assertEquals(AwaitingDecisionType.CARD_ACTION_CHOICE, playSkirmishAction.getDecisionType());
        playerDecided(P1, getCardActionId(playSkirmishAction, "Use Merry"));

        AwaitingDecision treebeardDecision = _userFeedback.getAwaitingDecision(P1);
        assertEquals(AwaitingDecisionType.CARD_ACTION_CHOICE, treebeardDecision.getDecisionType());
        playerDecided(P1, getCardActionId(treebeardDecision, "Use Tree"));

        assertEquals(Zone.STACKED, merry.getZone());
        assertEquals(treebeard, merry.getStackedOn());
    }

    @Test
    public void garradrielCorruptionAtStart() throws DecisionResultInvalidException {
        Map<String, LotroDeck> decks = new HashMap<String, LotroDeck>();
        LotroDeck lotroDeck = new LotroDeck("Some deck");
        lotroDeck.setRingBearer("9_14");
        lotroDeck.setRing("11_1");
        lotroDeck.addSite("7_330");
        lotroDeck.addSite("7_335");
        lotroDeck.addSite("8_117");
        lotroDeck.addSite("7_342");
        lotroDeck.addSite("7_345");
        lotroDeck.addSite("7_350");
        lotroDeck.addSite("8_120");
        lotroDeck.addSite("10_120");
        lotroDeck.addSite("7_360");
        decks.put(P1, lotroDeck);
        decks.put(P2, createSimplestDeck());

        _userFeedback = new DefaultUserFeedback();

        LotroFormatLibrary formatLibrary = new LotroFormatLibrary(new DefaultAdventureLibrary(), _library);
        LotroFormat format = formatLibrary.getFormat("movie");

        _game = new DefaultLotroGame(format, decks, _userFeedback, _library);
        _userFeedback.setGame(_game);
        _game.startGame();

        // Bidding
        playerDecided(P1, "5");
        playerDecided(P2, "0");

        // Seating choice
        playerDecided(P1, "0");

        _game.carryOutPendingActionsUntilDecisionNeeded();
        
        assertFalse(_game.isFinished());
    }

    @Test
    public void greenHillCountryWorksForFirstPlayer() throws DecisionResultInvalidException {
        Map<String, LotroDeck> decks = new HashMap<String, LotroDeck>();
        LotroDeck lotroDeck = new LotroDeck("Some deck");
        lotroDeck.setRingBearer("10_121");
        lotroDeck.setRing("1_2");
        // 7_330,7_336,8_117,7_342,7_345,7_350,8_120,10_120,7_360
        lotroDeck.addSite("1_323");
        lotroDeck.addSite("7_335");
        lotroDeck.addSite("8_117");
        lotroDeck.addSite("7_342");
        lotroDeck.addSite("7_345");
        lotroDeck.addSite("7_350");
        lotroDeck.addSite("8_120");
        lotroDeck.addSite("10_120");
        lotroDeck.addSite("7_360");
        lotroDeck.addCard("1_303");
        decks.put(P1, lotroDeck);
        decks.put(P2, createSimplestDeck());

        _userFeedback = new DefaultUserFeedback();

        LotroFormatLibrary formatLibrary = new LotroFormatLibrary(new DefaultAdventureLibrary(), _library);
        LotroFormat format = formatLibrary.getFormat("movie");

        _game = new DefaultLotroGame(format, decks, _userFeedback, _library);
        _userFeedback.setGame(_game);
        _game.startGame();

        // Bidding
        playerDecided(P1, "1");
        playerDecided(P2, "0");

        // Seating choice
        playerDecided(P1, "0");

        // Play no starting companions
        playerDecided(P1, "");

        // Mulligans
        playerDecided(P1, "0");
        playerDecided(P2, "0");

        // Fellowship phase
        AwaitingDecision playFellowshipAction = _userFeedback.getAwaitingDecision(P1);
        assertEquals(AwaitingDecisionType.CARD_ACTION_CHOICE, playFellowshipAction.getDecisionType());
        playerDecided(P1, getCardActionId(playFellowshipAction, "Play Merry"));

        assertEquals(0, _game.getGameState().getTwilightPool());
    }

    @Test
    public void trollMonstrousFiend() throws CardNotFoundException, DecisionResultInvalidException {
        initializeSimplestGame();

        PhysicalCardImpl troll = new PhysicalCardImpl(100, "20_256", P2, _library.getLotroCardBlueprint("20_256"));
        PhysicalCardImpl runner1  = new PhysicalCardImpl(101, "20_268", P2, _library.getLotroCardBlueprint("20_268"));
        PhysicalCardImpl runner2  = new PhysicalCardImpl(102, "20_268", P2, _library.getLotroCardBlueprint("20_268"));
        PhysicalCardImpl runner3  = new PhysicalCardImpl(103, "20_268", P2, _library.getLotroCardBlueprint("20_268"));
        PhysicalCardImpl runner4  = new PhysicalCardImpl(104, "20_268", P2, _library.getLotroCardBlueprint("20_268"));

        skipMulligans();

        _game.getGameState().addCardToZone(_game, troll, Zone.HAND);
        _game.getGameState().addCardToZone(_game, runner1, Zone.HAND);
        _game.getGameState().addCardToZone(_game, runner2, Zone.HAND);
        _game.getGameState().addCardToZone(_game, runner3, Zone.HAND);
        _game.getGameState().addCardToZone(_game, runner4, Zone.HAND);

        _game.getGameState().addTwilight(7);
        
        // End fellowship
        playerDecided(P1, "");

        assertEquals(9, _game.getGameState().getTwilightPool());

        final AwaitingDecision playShadowAction = _userFeedback.getAwaitingDecision(P2);
        assertEquals(AwaitingDecisionType.CARD_ACTION_CHOICE, playShadowAction.getDecisionType());
        playerDecided(P2, getCardActionId(playShadowAction, "Play Cave"));

        final AwaitingDecision discardGoblins = _userFeedback.getAwaitingDecision(P2);
        assertEquals("3", discardGoblins.getDecisionParameters().get("min"));
    }
}
