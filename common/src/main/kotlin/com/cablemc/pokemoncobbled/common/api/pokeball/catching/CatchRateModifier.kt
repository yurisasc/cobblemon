package com.cablemc.pokemoncobbled.common.api.pokeball.catching

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.entity.LivingEntity

/**
 * A modifier that can be used to modify the chance a poke ball.
 * This is often used to add modifiers to [PokeBall] for the different types, ie. ultra ball, great ball, dive ball, etc.
 *
 * @author landonjw
 * @since  November 30, 2021
 */
interface CatchRateModifier {
    fun modifyCatchRate(currentCatchRate: Float, thrower: LivingEntity, pokemon: Pokemon): Float
}