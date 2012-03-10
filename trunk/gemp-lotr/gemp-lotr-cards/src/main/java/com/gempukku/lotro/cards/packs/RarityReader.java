package com.gempukku.lotro.cards.packs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RarityReader {
    public SetRarity getSetRarity(String setNo) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(RarityReader.class.getResourceAsStream("/set" + setNo + "-rarity.txt"), "UTF-8"));
            try {
                String line;
                List<String> tengwar = new LinkedList<String>();
                Map<String, List<String>> cardsByRarity = new HashMap<String, List<String>>();
                Map<String, String> cardRarity = new HashMap<String, String>();

                while ((line = bufferedReader.readLine()) != null) {
                    if (line.endsWith("T")) {
                        if (!line.substring(0, setNo.length()).equals(setNo))
                            throw new IllegalStateException("Seems the rarity is for some other set");
                        String rarity = line.substring(setNo.length(), setNo.length() + 1);
                        List<String> cards = cardsByRarity.get(rarity);
                        if (cards == null) {
                            cards = new LinkedList<String>();
                            cardsByRarity.put(rarity, cards);
                        }
                        String blueprintId = setNo + "_" + line.substring(setNo.length() + 1);
                        tengwar.add(blueprintId);
                    } else {
                        if (!line.substring(0, setNo.length()).equals(setNo))
                            throw new IllegalStateException("Seems the rarity is for some other set");
                        String rarity = line.substring(setNo.length(), setNo.length() + 1);
                        List<String> cards = cardsByRarity.get(rarity);
                        if (cards == null) {
                            cards = new LinkedList<String>();
                            cardsByRarity.put(rarity, cards);
                        }
                        String blueprintId = setNo + "_" + line.substring(setNo.length() + 1);
                        cards.add(blueprintId);
                        cardRarity.put(blueprintId, rarity);
                    }
                }

                return new DefaultSetRarity(tengwar, cardsByRarity, cardRarity);
            } finally {
                bufferedReader.close();
            }
        } catch (IOException exp) {
            throw new RuntimeException("Problem loading rarity of set " + setNo, exp);
        }
    }
}
