/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.registry

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.pokemon.species.Species
import com.cobblemon.mod.common.api.types.ElementalType
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey

object CobblemonRegistries {

    val ELEMENTAL_TYPE: Registry<ElementalType> get() = this.get(CobblemonRegistryKeys.ELEMENTAL_TYPE)

    val SPECIES: Registry<Species> get() = this.get(CobblemonRegistryKeys.SPECIES)

    private fun <T> get(key: RegistryKey<Registry<T>>): Registry<T> = Cobblemon.implementation.getRegistry(key)

}