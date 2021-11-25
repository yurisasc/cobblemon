package com.cablemc.pokemoncobbled.common.pokemon.pokeball

import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import net.minecraft.resources.ResourceLocation

/**
 * A static rate Pokeball
 *
 * This Pokeball always has a fixed catch rate.
 *
 * @property name the pokeball registry name
 * @property catchRate the pokeball catch rate
 */
class StaticRatePokeBall(
    name : ResourceLocation,
    val catchRate : Float
) : PokeBall(name) {

    override fun getCatchRateModifier(pokemon : PokemonEntity) : Float {
        return catchRate
    }

}