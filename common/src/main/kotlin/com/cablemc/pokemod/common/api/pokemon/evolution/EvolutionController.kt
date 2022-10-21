/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.pokemon.evolution

import com.cablemc.pokemod.common.api.serialization.BufferSerializer
import com.cablemc.pokemod.common.api.serialization.DataSerializer
import com.cablemc.pokemod.common.pokemon.Pokemon
import com.cablemc.pokemod.common.pokemon.evolution.controller.ClientEvolutionController
import com.cablemc.pokemod.common.pokemon.evolution.controller.ServerEvolutionController
import com.google.gson.JsonElement
import net.minecraft.nbt.NbtElement

/**
 * Responsible for holding all available [EvolutionLike]s in the [Pokemon].
 * Also handles all the networking behind them.
 * For the Cobbled default implementations see [ClientEvolutionController] & [ServerEvolutionController].
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