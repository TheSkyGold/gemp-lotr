package com.gempukku.lotro.game.formats;

import com.gempukku.lotro.common.Block;
import com.gempukku.lotro.game.LotroCardBlueprintLibrary;

public class KingBlockFormat extends DefaultLotroFormat {
    public KingBlockFormat(LotroCardBlueprintLibrary library, boolean mulliganRule) {
        super(library, Block.KING, true, 60, 4, mulliganRule);
        addRestrictedCard("7_49");
        addValidSet(7);
        addValidSet(8);
        addValidSet(9);
    }

    @Override
    public boolean isOrderedSites() {
        return true;
    }
}
