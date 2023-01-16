/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.world.placement

import com.cobblemon.mod.common.util.cobblemonResource
import com.mojang.serialization.Codec
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKeys
import net.minecraft.world.gen.placementmodifier.PlacementModifier
import net.minecraft.world.gen.placementmodifier.PlacementModifierType

object CobblemonPlacementTypes {
    lateinit var BIOME_TAG_FILTER: PlacementModifierType<BiomeTagModifier>

    private fun <P : PlacementModifier> registerType(id: String, codec: Codec<P>): PlacementModifierType<P> {
        return Registry.register(RegistryKeys.PLACEMENT_MODIFIER_TYPE, cobblemonResource(id), PlacementModifierType { codec })
    }

    fun register() {
        BIOME_TAG_FILTER = registerType("biome_tag_filter", BiomeTagModifier.CODEC)
    }
}