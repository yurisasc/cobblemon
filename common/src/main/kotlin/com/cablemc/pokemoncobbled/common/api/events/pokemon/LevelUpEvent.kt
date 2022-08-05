package com.cablemc.pokemoncobbled.common.api.events.pokemon

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

/**
 * Event fired when a Pokémon levels up. The new level that it will reach is changeable.
 *
 * @author Hiroku
 * @since August 5th, 2022
 */
class LevelUpEvent(val pokemon: Pokemon, val oldLevel: Int, var newLevel: Int)