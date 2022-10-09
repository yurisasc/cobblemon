/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.world.placement

import com.cablemc.pokemod.common.util.pokemodResource
import com.mojang.serialization.Codec
import net.minecraft.util.registry.Registry
import net.minecraft.world.gen.placementmodifier.PlacementModifier
import net.minecraft.world.gen.placementmodifier.PlacementModifierType

object PokemodPlacementTypes {
    lateinit var IS_BIOME_TAG_FILTER: PlacementModifierType<IsBiomeTagFilter>

    private fun <P : PlacementModifier> registerType(id: String, codec: Codec<P>): PlacementModifierType<P> {
        return Registry.register(Registry.PLACEMENT_MODIFIER_TYPE, pokemodResource(id), PlacementModifierType { codec })
    }

    fun register() {
        IS_BIOME_TAG_FILTER = registerType("is_biome_tag_filter", IsBiomeTagFilter.CODEC)
    }
}