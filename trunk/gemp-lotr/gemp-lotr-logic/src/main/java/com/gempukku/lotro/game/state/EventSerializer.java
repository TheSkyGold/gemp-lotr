package com.gempukku.lotro.game.state;

import com.gempukku.lotro.common.Zone;
import com.gempukku.lotro.logic.decisions.AwaitingDecision;
import com.gempukku.lotro.logic.timing.GameStats;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Map;

public class EventSerializer {
    public Node serializeEvent(Document doc, GameEvent gameEvent) {
        Element eventElem = doc.createElement("ge");
        eventElem.setAttribute("type", gameEvent.getType().name());
        if (gameEvent.getBlueprintId() != null)
            eventElem.setAttribute("blueprintId", gameEvent.getBlueprintId());
        if (gameEvent.getCardId() != null)
            eventElem.setAttribute("cardId", gameEvent.getCardId().toString());
        if (gameEvent.getIndex() != null)
            eventElem.setAttribute("index", gameEvent.getIndex().toString());
        if (gameEvent.getDate() != null)
            eventElem.setAttribute("date", String.valueOf(gameEvent.getDate().getTime()));
        if (gameEvent.getControllerId() != null)
            eventElem.setAttribute("controllerId", gameEvent.getControllerId());
        if (gameEvent.getParticipantId() != null)
            eventElem.setAttribute("participantId", gameEvent.getParticipantId());
        if (gameEvent.getAllParticipantIds() != null) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (String participantId : gameEvent.getAllParticipantIds()) {
                if (!first) sb.append(",");
                sb.append(participantId);
                first = false;
            }
            eventElem.setAttribute("allParticipantIds", sb.toString());
        }
        if (gameEvent.getPhase() != null)
            eventElem.setAttribute("phase", gameEvent.getPhase());
        if (gameEvent.getTargetCardId() != null)
            eventElem.setAttribute("targetCardId", gameEvent.getTargetCardId().toString());
        if (gameEvent.getZone() != null)
            eventElem.setAttribute("zone", gameEvent.getZone().name());
        if (gameEvent.getToken() != null)
            eventElem.setAttribute("token", gameEvent.getToken().name());
        if (gameEvent.getCount() != null)
            eventElem.setAttribute("count", gameEvent.getCount().toString());
        if (gameEvent.getOtherCardIds() != null) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (int cardId : gameEvent.getOtherCardIds()) {
                if (!first) sb.append(",");
                sb.append(cardId);
                first = false;
            }
            eventElem.setAttribute("otherCardIds", sb.toString());
        }
        if (gameEvent.getMessage() != null)
            eventElem.setAttribute("message", gameEvent.getMessage());
        if (gameEvent.getGameStats() != null) {
            GameStats gameStats = gameEvent.getGameStats();
            eventElem.setAttribute("fellowshipArchery", String.valueOf(gameStats.getFellowshipArchery()));
            eventElem.setAttribute("shadowArchery", String.valueOf(gameStats.getShadowArchery()));

            eventElem.setAttribute("fellowshipStrength", String.valueOf(gameStats.getFellowshipSkirmishStrength()));
            eventElem.setAttribute("shadowStrength", String.valueOf(gameStats.getShadowSkirmishStrength()));

            eventElem.setAttribute("fellowshipDamageBonus", String.valueOf(gameStats.getFellowshipSkirmishDamageBonus()));
            eventElem.setAttribute("shadowDamageBonus", String.valueOf(gameStats.getShadowSkirmishDamageBonus()));

            eventElem.setAttribute("fpOverwhelmed", String.valueOf(gameStats.isFpOverwhelmed()));

            eventElem.setAttribute("moveLimit", String.valueOf(gameStats.getMoveLimit()));
            eventElem.setAttribute("moveCount", String.valueOf(gameStats.getMoveCount()));

            for (Map.Entry<String, Map<Zone, Integer>> playerZoneSizes : gameStats.getZoneSizes().entrySet()) {
                final Element playerZonesElem = doc.createElement("playerZones");

                playerZonesElem.setAttribute("name", playerZoneSizes.getKey());

                for (Map.Entry<Zone, Integer> zoneSizes : playerZoneSizes.getValue().entrySet())
                    playerZonesElem.setAttribute(zoneSizes.getKey().name(), zoneSizes.getValue().toString());

                eventElem.appendChild(playerZonesElem);
            }

            for (Map.Entry<String, Integer> playerThreatEntry : gameStats.getThreats().entrySet()) {
                Element threatsElem = doc.createElement("threats");
                threatsElem.setAttribute("name", playerThreatEntry.getKey());
                threatsElem.setAttribute("value", playerThreatEntry.getValue().toString());
                eventElem.appendChild(threatsElem);
            }

            Map<Integer, Integer> charVitalities = gameStats.getCharVitalities();
            Map<Integer, Integer> charSiteNumbers = gameStats.getSiteNumbers();
            Map<Integer, String> charResistances = gameStats.getCharResistances();

            StringBuilder charStr = new StringBuilder();
            for (Map.Entry<Integer, Integer> characters : gameStats.getCharStrengths().entrySet()) {
                Integer charCardId = characters.getKey();
                charStr.append("," + charCardId + "=" + characters.getValue() + "|" + charVitalities.get(charCardId));
                if (charSiteNumbers.containsKey(charCardId))
                    charStr.append("|" + charSiteNumbers.get(charCardId));
                else if (charResistances.containsKey(charCardId))
                    charStr.append("|R" + charResistances.get(charCardId));
            }
            if (charStr.length() > 0)
                charStr.delete(0, 1);

            if (charStr.length() > 0)
                eventElem.setAttribute("charStats", charStr.toString());
        }
        if (gameEvent.getAwaitingDecision() != null) {
            AwaitingDecision decision = gameEvent.getAwaitingDecision();
            eventElem.setAttribute("id", String.valueOf(decision.getAwaitingDecisionId()));
            eventElem.setAttribute("decisionType", decision.getDecisionType().name());
            if (decision.getText() != null)
                eventElem.setAttribute("text", decision.getText());
            for (Map.Entry<String, Object> paramEntry : decision.getDecisionParameters().entrySet()) {
                if (paramEntry.getValue() instanceof String) {
                    Element decisionParam = doc.createElement("parameter");
                    decisionParam.setAttribute("name", paramEntry.getKey());
                    decisionParam.setAttribute("value", (String) paramEntry.getValue());
                    eventElem.appendChild(decisionParam);
                } else if (paramEntry.getValue() instanceof String[]) {
                    for (String value : (String[]) paramEntry.getValue()) {
                        Element decisionParam = doc.createElement("parameter");
                        decisionParam.setAttribute("name", paramEntry.getKey());
                        decisionParam.setAttribute("value", value);
                        eventElem.appendChild(decisionParam);
                    }
                }
            }
        }

        return eventElem;
    }
}
