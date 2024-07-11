/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.types.tera

import com.cobblemon.mod.common.api.effect.Effect
import com.cobblemon.mod.common.api.effect.types.TeraTypeEffect
import com.cobblemon.mod.common.api.resistance.Resistance
import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.registry.CobblemonRegistries
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import net.minecraft.network.chat.Component

class ElementalTypeTeraType(val elementalType: ElementalType) : BaseTeraType() {

    override fun displayName(): Component = this.elementalType.displayName

    override fun resistanceTo(effect: Effect): Resistance = this.elementalType.resistanceTo(effect)

    override fun codec(): Codec<out TeraType> = CODEC

    override fun mapCodec(): MapCodec<out TeraType> = MAP_CODEC

    override fun asEffect(): Effect = TeraTypeEffect(this.resourceKey())

    companion object {
        @JvmStatic
        val MAP_CODEC: MapCodec<ElementalTypeTeraType> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                CobblemonRegistries.ELEMENTAL_TYPE.byNameCodec().fieldOf("elementalType")
                    .forGetter(ElementalTypeTeraType::elementalType)
            ).apply(instance, ::ElementalTypeTeraType)
        }

        @JvmStatic
        val CODEC: Codec<ElementalTypeTeraType> = MAP_CODEC.codec()
    }
}