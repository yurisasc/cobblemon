/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.resistance.registry

import com.cobblemon.mod.common.api.resistance.Resistible
import com.cobblemon.mod.common.registry.CobblemonRegistries
import com.mojang.serialization.Codec
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation

object ResistibleTypes {

    @JvmStatic
    val ELEMENTAL_TYPE = this.register(CobblemonRegistries.ELEMENTAL_TYPE_KEY.location(), CobblemonRegistries.ELEMENTAL_TYPE.byNameCodec())

    fun <T : Resistible> register(id: ResourceLocation, codec: Codec<T>): ResistibleType<T> {
        val type = ResistibleType(codec.fieldOf("value"))
        return Registry.register(ResistibleType.REGISTRY, id, type)
    }

}