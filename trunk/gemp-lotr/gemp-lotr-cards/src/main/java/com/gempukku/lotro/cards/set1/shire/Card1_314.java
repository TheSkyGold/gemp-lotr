package com.gempukku.lotro.cards.set1.shire;

import com.gempukku.lotro.cards.AbstractAttachable;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.game.state.Skirmish;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.modifiers.ModifiersQuerying;
import com.gempukku.lotro.logic.modifiers.StrengthModifier;

/**
 * Set: The Fellowship of the Ring
 * Side: Free
 * Culture: Shire
 * Twilight Cost: 2
 * Type: Condition
 * Game Text: Tale. To play, spot Bilbo. Plays on any character. Limit 1 per character. Each time bearer skirmishes a
 * Troll or Uruk-hai, bearer is strength +3.
 */
public class Card1_314 extends AbstractAttachable {
    public Card1_314() {
        super(Side.FREE_PEOPLE, CardType.CONDITION, 2, Culture.SHIRE, null, "Stone Trolls");
        addKeyword(Keyword.TALE);
    }

    @Override
    protected Filter getValidTargetFilter(String playerId, LotroGame game, PhysicalCard self) {
        return Filters.and(Filters.or(CardType.COMPANION, CardType.ALLY), Filters.not(Filters.hasAttached(Filters.name(getName()))));
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int withTwilightRemoved, Filter additionalAttachmentFilter, int twilightModifier) {
        return super.checkPlayRequirements(playerId, game, self, withTwilightRemoved, additionalAttachmentFilter, twilightModifier)
                && Filters.canSpot(game.getGameState(), game.getModifiersQuerying(), Filters.name("Bilbo"));
    }

    @Override
    public Modifier getAlwaysOnModifier(LotroGame game, final PhysicalCard self) {
        return new StrengthModifier(self,
                Filters.and(
                        Filters.hasAttached(self),
                        new Filter() {
                            @Override
                            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                                Skirmish skirmish = gameState.getSkirmish();
                                return skirmish != null
                                        && skirmish.getFellowshipCharacter() == self.getAttachedTo()
                                        && Filters.filter(skirmish.getShadowCharacters(), gameState, modifiersQuerying,
                                        Filters.or(
                                                Race.URUK_HAI,
                                                Race.TROLL)).size() > 0;
                            }
                        }), 3);
    }
}
