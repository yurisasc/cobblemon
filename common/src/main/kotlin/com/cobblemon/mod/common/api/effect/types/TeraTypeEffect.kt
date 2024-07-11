/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.effect.types

import com.cobblemon.mod.common.api.effect.Effect
import com.cobblemon.mod.common.api.effect.EffectType
import com.cobblemon.mod.common.api.types.tera.TeraType
import com.cobblemon.mod.common.registry.CobblemonRegistries
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceKey

class TeraTypeEffect(private val teraTypeKey: ResourceKey<TeraType>) : Effect() {
    override fun type(): EffectType<*> = EffectType.TERA_TYPE

    override fun showdownId(): String = this.teraType().showdownId()

    fun teraType(): TeraType = CobblemonRegistries.TERA_TYPE.get(this.teraTypeKey)
        ?: throw IllegalArgumentException("Cannot resolve ${this.teraTypeKey} in ${CobblemonRegistries.TERA_TYPE_KEY}")

    companion object {

        @JvmStatic
        val CODEC: MapCodec<TeraTypeEffect> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                ResourceKey.codec(CobblemonRegistries.TERA_TYPE_KEY)
                    .fieldOf("teraType")
                    .forGetter(TeraTypeEffect::teraTypeKey)
            ).apply(instance, ::TeraTypeEffect)
        }

    }
}