/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.registry

import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey

/**
 * Contains the various [RegistryKey]s for Cobblemon.
 */
object CobblemonRegistryKeys {

    val ELEMENTAL_TYPE: RegistryKey<Registry<ElementalType>> = RegistryKey.ofRegistry(cobblemonResource("elemental_types"))

}