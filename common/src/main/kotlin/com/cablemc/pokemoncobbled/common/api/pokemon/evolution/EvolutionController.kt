package com.cablemc.pokemoncobbled.common.api.pokemon.evolution

import com.cablemc.pokemoncobbled.common.api.serialization.BufferSerializer
import com.cablemc.pokemoncobbled.common.api.serialization.DataSerializer
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.evolution.controller.CobbledClientEvolutionController
import com.cablemc.pokemoncobbled.common.pokemon.evolution.controller.CobbledServerEvolutionController
import com.google.gson.JsonElement
import net.minecraft.nbt.NbtElement

/**
 * Responsible for holding all available [EvolutionLike]s in the [Pokemon].
 * Also handles all the networking behind them.
 * For the Cobbled default implementations see [CobbledClientEvolutionController] & [CobbledServerEvolutionController].
 *
 * @author Licious
 * @since April 28th, 2022
 */
interface EvolutionController<T : EvolutionLike> : MutableSet<T>, DataSerializer<NbtElement, JsonElement>, BufferSerializer {

    /**
     * The [Pokemon] this controller is attached to.
     */
    val pokemon: Pokemon

    /**
     * Starts the given evolution on this controller.
     * All the necessary networking will be handled.
     *
     * @param evolution The [EvolutionLike] starting.
     */
    fun start(evolution: T)

}