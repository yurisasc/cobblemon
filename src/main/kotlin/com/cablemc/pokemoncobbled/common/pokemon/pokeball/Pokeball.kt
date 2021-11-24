package com.cablemc.pokemoncobbled.common.pokemon.pokeball

import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import net.minecraft.resources.ResourceLocation

/**
 * Base Pokeball object
 * @property name the pokeball registry name
 * @constructor Creates a Pokeball
 */
open class Pokeball(
    val name : ResourceLocation
) {

    /**
     * Gets the catch rate modifier for the Pokemon entity
     * @property pokemon the pokemon entity
     */
    open fun getCatchRateModifier(pokemon : PokemonEntity) : Float {
        return 1f
    }

}