package com.gempukku.lotro.game.formats;

import com.gempukku.lotro.common.Block;
import com.gempukku.lotro.game.LotroCardBlueprintLibrary;

public class TTTBlockFormat extends DefaultLotroFormat {
    public TTTBlockFormat(LotroCardBlueprintLibrary library) {
        super(library, "Two Towers block", Block.TWO_TOWERS, true, 60, 4, true, true);
        addValidSet(4);
        addValidSet(5);
        addValidSet(6);
    }
}
