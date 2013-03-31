package com.gempukku.lotro.merchant;

import com.gempukku.lotro.cards.CardSets;
import com.gempukku.lotro.cards.packs.SetDefinition;
import com.gempukku.lotro.db.MerchantDAO;
import com.gempukku.lotro.game.LotroCardBlueprintLibrary;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StorageBasedMerchant implements Merchant {
    private static final int BOOSTER_PRICE = 1000;
    private static final long DAY = 1000 * 60 * 60 * 24;
    private static final float _profitMargin = 0.7f;

    private static final int MAX_STOCK_BUY = 99;
    private static final int OUT_OF_STOCK_MIN = 0;
    private static final int OUT_OF_STOCK_MULTIPLIER = 2;

    private static final int FOIL_PRICE_MULTIPLIER = 2;

    private static final int MIN_SELL_PRICE = 2;
    private static final int MIN_BUY_PRICE = 1;

    private static final int LOW_STOCK_MAX = 10;
    private static final int DOUBLE_AFTER_DAYS_LOW_STOCK = 10;
    private static final int HALVE_AFTER_DAYS_IN_STOCK = 30;

    private static final double PRICE_SHIFT_AFTER_TRANSACTION_PERC = 15;

    private LotroCardBlueprintLibrary _library;
    private MerchantDAO _merchantDao;
    private Map<String, SetDefinition> _rarity = new HashMap<String, SetDefinition>();
    private Date _merchantSetupDate;

    public StorageBasedMerchant(LotroCardBlueprintLibrary library, MerchantDAO merchantDao, Date merchantSetupDate, CardSets cardSets) {
        _library = library;
        _merchantDao = merchantDao;
        _merchantSetupDate = merchantSetupDate;
        for (SetDefinition setDefinition : cardSets.getSetDefinitions().values()) {
            if (setDefinition.hasFlag("merchantable"))
                _rarity.put(setDefinition.getSetId(), setDefinition);
        }
    }

    @Override
    public void cardBought(String blueprintId, Date currentTime, int price) {
        if (blueprintId.endsWith("*"))
            price = price / FOIL_PRICE_MULTIPLIER;
        blueprintId = _library.getBaseBlueprintId(blueprintId);
        _merchantDao.addTransaction(blueprintId, (price / _profitMargin), currentTime, MerchantDAO.TransactionType.BUY);
    }

    @Override
    public void cardSold(String blueprintId, Date currentTime, int price) {
        _merchantDao.addTransaction(blueprintId, price, currentTime, MerchantDAO.TransactionType.SELL);
    }

    @Override
    public Integer getCardSellPrice(String blueprintId, Date currentTime) {
        blueprintId = _library.getBaseBlueprintId(blueprintId);

        MerchantDAO.Transaction lastTransaction = _merchantDao.getLastTransaction(blueprintId);

        boolean outOfStock = (lastTransaction == null || lastTransaction.getStock() <= OUT_OF_STOCK_MIN);

        Double normalPrice = getNormalPrice(lastTransaction, blueprintId, currentTime);
        if (normalPrice == null)
            return null;

        if (outOfStock) {
            return OUT_OF_STOCK_MULTIPLIER * Math.max(MIN_SELL_PRICE, (int) Math.ceil(normalPrice));
        } else {
            return Math.max(MIN_SELL_PRICE, (int) Math.ceil(normalPrice));
        }
    }

    @Override
    public Integer getCardBuyPrice(String blueprintId, Date currentTime) {
        boolean foil = false;
        if (blueprintId.endsWith("*"))
            foil = true;

        blueprintId = _library.getBaseBlueprintId(blueprintId);

        MerchantDAO.Transaction lastTransaction = _merchantDao.getLastTransaction(blueprintId);

        if (lastTransaction != null && lastTransaction.getStock() > MAX_STOCK_BUY && !foil)
            return null;

        Double normalPrice = getNormalPrice(lastTransaction, blueprintId, currentTime);
        if (normalPrice == null)
            return null;

        int priceWithProfit = (int) Math.floor(_profitMargin * normalPrice);

        boolean outOfStock = (lastTransaction == null || lastTransaction.getStock() < OUT_OF_STOCK_MIN);

        // If card is out of stock, adjust the buy price, as it might be over-inflated (due to over-selling)
        if (outOfStock)
            priceWithProfit = priceWithProfit / OUT_OF_STOCK_MULTIPLIER;

        int price = Math.max(MIN_BUY_PRICE, priceWithProfit);

        if (foil)
            return FOIL_PRICE_MULTIPLIER * price;
        return price;
    }

    public Double getNormalPrice(MerchantDAO.Transaction lastTransaction, String blueprintId, Date currentTime) {
        float basePrice;
        float daysSinceLastTransaction;
        int stock;
        if (lastTransaction == null) {
            Integer priceBasedOnRarity = getBasePrice(blueprintId);
            if (priceBasedOnRarity == null)
                return null;
            basePrice = priceBasedOnRarity.floatValue();
            daysSinceLastTransaction = (currentTime.getTime() - _merchantSetupDate.getTime()) / (DAY * 1f);
            stock = 0;
        } else {
            basePrice = lastTransaction.getPrice();
            if (lastTransaction.getTransactionType() == MerchantDAO.TransactionType.BUY)
                basePrice /= (1 + (PRICE_SHIFT_AFTER_TRANSACTION_PERC / 100));
            else
                basePrice *= (1 + (PRICE_SHIFT_AFTER_TRANSACTION_PERC / 100));
            daysSinceLastTransaction = (currentTime.getTime() - lastTransaction.getDate().getTime()) / (DAY * 1f);
            stock = lastTransaction.getStock();
        }

        if (stock > LOW_STOCK_MAX)
            return basePrice / (1 + Math.pow(daysSinceLastTransaction / HALVE_AFTER_DAYS_IN_STOCK, 1.5));
        else
            return basePrice * (1 + Math.pow(daysSinceLastTransaction / DOUBLE_AFTER_DAYS_LOW_STOCK, 1.5));

    }

    private Integer getBasePrice(String blueprintId) {
        int underscoreIndex = blueprintId.indexOf("_");
        if (underscoreIndex == -1)
            return null;
        SetDefinition rarity = _rarity.get(blueprintId.substring(0, blueprintId.indexOf("_")));
        String cardRarity = rarity.getCardRarity(blueprintId);
        if (cardRarity.equals("X"))
            return 3 * BOOSTER_PRICE;
        if (cardRarity.equals("R") || cardRarity.equals("P"))
            return BOOSTER_PRICE;
        if (cardRarity.equals("U") || cardRarity.equals("S"))
            return BOOSTER_PRICE / 3;
        if (cardRarity.equals("C"))
            return BOOSTER_PRICE / 7;
        throw new RuntimeException("Unknown rarity for priced card: " + cardRarity);
    }
}
