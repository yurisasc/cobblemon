package com.cablemc.pokemoncobbled.common.api.pokeball.catching

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.server.level.ServerPlayer

/**
 * A modifier that can be used to modify the chance a poke ball.
 * This is often used to add modifiers to [PokeBall] for the different types, ie. ultra ball, great ball, dive ball, etc.
 *
 * @author landonjw
 * @since  November 30, 2021
 */
interface CatchRateModifier {
    fun modifyCatchRate(currentCatchRate: Float, player: ServerPlayer, pokemon: Pokemon): Float
}