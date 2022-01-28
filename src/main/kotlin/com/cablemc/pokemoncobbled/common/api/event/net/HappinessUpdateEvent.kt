package com.cablemc.pokemoncobbled.common.api.event.net

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraftforge.eventbus.api.Event

class HappinessUpdateEvent (var pokemon : Pokemon? = null, var oldHappiness : Int? = null): Event() {
}