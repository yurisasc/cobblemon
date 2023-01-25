/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.feature

import com.cobblemon.mod.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cobblemon.mod.common.api.pokemon.feature.SpeciesFeature
import com.cobblemon.mod.common.api.pokemon.feature.SpeciesFeatureProvider
import com.cobblemon.mod.common.pokemon.Pokemon
import com.google.gson.JsonObject
import net.minecraft.nbt.NbtCompound

/**
 * A [SpeciesFeatureProvider] for features only assigned if a given [EvolutionRequirement] is present on a [Pokemon]s evolution.
 *
 * @param T The type of [SpeciesFeature].
 * @property provider The instance creator of the [SpeciesFeature].
 * @property validator A lambda to check if a [Pokemon] can have the feature assigned.
 *
 * @author Licious
 * @since January 25th, 2023
 */
class EvolutionRequirementFeatureProvider<T : SpeciesFeature>(private val provider: () -> T, private val validator: (requirement: EvolutionRequirement) -> Boolean) : SpeciesFeatureProvider<T> {

    override fun invoke(pokemon: Pokemon): T? {
        if (pokemon.form.evolutions.any { evolution -> evolution.requirements.any { requirement -> this.validator(requirement) } })
            return this.provider()
        return null
    }

    // It's fine for these to not return null as the validation has been successful before these are invoked
    override fun invoke(nbt: NbtCompound): T = this.provider().apply { loadFromNBT(nbt) }
    override fun invoke(json: JsonObject): T = this.provider().apply { loadFromJSON(json) }
}