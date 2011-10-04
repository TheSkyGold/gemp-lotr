package com.gempukku.lotro.cards.set3.elven;

import com.gempukku.lotro.cards.AbstractEvent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.effects.CancelSkirmishEffect;
import com.gempukku.lotro.cards.effects.ChooseAndExertCharactersEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.game.state.Skirmish;

import java.util.Collection;

/**
 * Set: Realms of Elf-lords
 * Side: Free
 * Culture: Elven
 * Twilight Cost: 2
 * Type: Event
 * Game Text: Tale. Skirmish: Exert an Elf at a river or forest to cancel a skirmish involving that Elf.
 */
public class Card3_028 extends AbstractEvent {
    public Card3_028() {
        super(Side.FREE_PEOPLE, Culture.ELVEN, "Voice of Nimrodel", Phase.SKIRMISH);
        addKeyword(Keyword.TALE);
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int twilightModifier) {
        return super.checkPlayRequirements(playerId, game, self, twilightModifier)
                && PlayConditions.canExert(self, game.getGameState(), game.getModifiersQuerying(), Filters.race(Race.ELF))
                && (game.getModifiersQuerying().hasKeyword(game.getGameState(), game.getGameState().getCurrentSite(), Keyword.RIVER)
                || game.getModifiersQuerying().hasKeyword(game.getGameState(), game.getGameState().getCurrentSite(), Keyword.FOREST));
    }

    @Override
    public int getTwilightCost() {
        return 2;
    }

    @Override
    public PlayEventAction getPlayCardAction(String playerId, final LotroGame game, PhysicalCard self, int twilightModifier) {
        final PlayEventAction action = new PlayEventAction(self);
        action.appendCost(
                new ChooseAndExertCharactersEffect(action, playerId, 1, 1, Filters.race(Race.ELF)) {

                    @Override
                    protected void cardsSelected(LotroGame game, Collection<PhysicalCard> characters) {
                        super.cardsSelected(game, characters);    //To change body of overridden methods use File | Settings | File Templates.
                        if (characters.size() > 0) {
                            PhysicalCard elf = characters.iterator().next();
                            Skirmish skirmish = game.getGameState().getSkirmish();
                            if (skirmish != null && skirmish.getFellowshipCharacter() == elf) {
                                action.appendEffect(
                                        new CancelSkirmishEffect());
                            }
                        }
                    }
                });
        return action;
    }
}
