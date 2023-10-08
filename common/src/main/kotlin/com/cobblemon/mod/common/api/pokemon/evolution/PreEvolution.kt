/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pokemon.evolution

import com.cobblemon.mod.common.api.pokemon.species.Species
import com.cobblemon.mod.common.api.registry.CobblemonRegistries
import com.mojang.serialization.Codec
import net.minecraft.util.Identifier

/**
 * Represents the previous stage in the evolutionary line of a given Pokémon.
 * Not all species will have one.
 *
 * @author Licious
 * @since March 22nd, 2022
 */
class PreEvolution internal constructor(private val speciesKey: Identifier){

    val species: Species by lazy { CobblemonRegistries.SPECIES.get(this.speciesKey)!! }

    companion object {

        @JvmField
        val CODEC: Codec<PreEvolution> = Identifier.CODEC.xmap(::PreEvolution, PreEvolution::speciesKey)

    }

}