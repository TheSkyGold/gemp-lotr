package com.gempukku.lotro.cards;

import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.effects.DiscardCardsFromHandEffect;
import com.gempukku.lotro.logic.effects.HealCharactersEffect;
import com.gempukku.lotro.logic.timing.Action;
import com.gempukku.lotro.logic.timing.UnrespondableEffect;

import java.util.Collections;
import java.util.List;

public class AbstractAlly extends AbstractPermanent {
    private Block _siteBlock;
    private int[] _siteNumbers;
    private int _strength;
    private int _vitality;
    private Race _race;

    public AbstractAlly(int twilight, Block siteBlock, int siteNumber, int strength, int vitality, Race race, Culture culture, String name) {
        this(twilight, siteBlock, siteNumber, strength, vitality, race, culture, name, null, false);
    }

    public AbstractAlly(int twilight, Block siteBlock, int siteNumber, int strength, int vitality, Race race, Culture culture, String name, String subTitle, boolean unique) {
        this(twilight, siteBlock, new int[]{siteNumber}, strength, vitality, race, culture, name, subTitle, unique);
    }

    public AbstractAlly(int twilight, Block siteBlock, int[] siteNumbers, int strength, int vitality, Race race, Culture culture, String name) {
        this(twilight, siteBlock, siteNumbers, strength, vitality, race, culture, name, null, false);
    }

    public AbstractAlly(int twilight, Block siteBlock, int[] siteNumbers, int strength, int vitality, Race race, Culture culture, String name, String subTitle, boolean unique) {
        super(Side.FREE_PEOPLE, twilight, CardType.ALLY, culture, Zone.SUPPORT, name, subTitle, unique);
        _siteBlock = siteBlock;
        _siteNumbers = siteNumbers;
        _strength = strength;
        _vitality = vitality;
        _race = race;
    }

    public final Race getRace() {
        return _race;
    }

    protected final List<? extends Action> getExtraPhaseActions(String playerId, LotroGame game, final PhysicalCard self) {
        if (PlayConditions.canHealByDiscarding(game.getGameState(), game.getModifiersQuerying(), self)) {
            // TODO this is not really an activated action...
            ActivateCardAction action = new ActivateCardAction(self);
            action.appendCost(new DiscardCardsFromHandEffect(null, playerId, Collections.singleton(self), false));
            action.appendCost(
                    new UnrespondableEffect() {
                        @Override
                        protected void doPlayEffect(LotroGame game) {
                            game.getGameState().eventPlayed(self);
                        }
                    });

            PhysicalCard active = Filters.findFirstActive(game.getGameState(), game.getModifiersQuerying(), Filters.name(self.getBlueprint().getName()));
            if (active != null)
                action.appendEffect(new HealCharactersEffect(self, active));

            return Collections.singletonList(action);
        }
        return getExtraInPlayPhaseActions(playerId, game, self);
    }

    protected List<? extends Action> getExtraInPlayPhaseActions(String playerId, LotroGame game, PhysicalCard self) {
        return null;
    }

    @Override
    public final boolean isAllyAtHome(int siteNumber, Block block) {
        if (block != _siteBlock)
            return false;
        for (int number : _siteNumbers)
            if (number == siteNumber)
                return true;
        return false;
    }

    @Override
    public final int getStrength() {
        return _strength;
    }

    @Override
    public final int getVitality() {
        return _vitality;
    }

    @Override
    public final int getResistance() {
        return 0;
    }
}
