package com.cablemc.pokemoncobbled.common.api.pokemon.evolution

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.evolution.CobbledClientEvolutionController
import com.cablemc.pokemoncobbled.common.pokemon.evolution.CobbledServerEvolutionController
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
interface EvolutionController<T : EvolutionLike> : MutableSet<T> {

    val pokemon: Pokemon

    fun start(evolution: T)

    fun saveToNBT(): NbtElement

    fun loadFromNBT(nbt: NbtElement)

    fun saveToJson(): JsonElement

    fun loadFromJson(json: JsonElement)

}