package com.gempukku.lotro.logic.timing.results;

import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.logic.timing.EffectResult;

import java.util.Collection;

public class WoundResult extends EffectResult {
    private Collection<PhysicalCard> _sources;
    private PhysicalCard _card;

    public WoundResult(Collection<PhysicalCard> sources, PhysicalCard card) {
        super(EffectResult.Type.FOR_EACH_WOUNDED);
        _sources = sources;
        _card = card;
    }

    public Collection<PhysicalCard> getSources() {
        return _sources;
    }

    public PhysicalCard getWoundedCard() {
        return _card;
    }
}
