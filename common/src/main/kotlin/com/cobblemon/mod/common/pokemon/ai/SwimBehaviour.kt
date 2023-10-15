/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.ai

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.fluid.Fluid
import net.minecraft.registry.tag.FluidTags
import net.minecraft.registry.tag.TagKey

@Suppress("unused")
data class SwimBehaviour(
    val avoidsWater: Boolean = false,
    val hurtByLava: Boolean = true,
    val canSwimInWater: Boolean = true,
    val canSwimInLava: Boolean = true,
    val swimSpeed: Float = 0.3F,
    val canBreatheUnderwater: Boolean = false,
    val canBreatheUnderlava: Boolean = false,
    val canWalkOnWater: Boolean = false,
    val canWalkOnLava: Boolean = false
) {

    fun canWalkOnFluid(tag: TagKey<Fluid>) = if (tag == FluidTags.WATER) canWalkOnWater else if (tag == FluidTags.LAVA) canWalkOnLava else false
    fun canBreatheUnderFluid(tag: TagKey<Fluid>) = if (tag == FluidTags.WATER) canBreatheUnderwater else if (tag == FluidTags.LAVA) canBreatheUnderlava else false
    fun canSwimInFluid(tag: TagKey<Fluid>) = if (tag == FluidTags.WATER) canSwimInWater else if (tag == FluidTags.LAVA) canSwimInLava else false

    companion object {

        @JvmField
        val CODEC: Codec<SwimBehaviour> = RecordCodecBuilder.create { builder ->
            builder.group(
                Codec.BOOL.optionalFieldOf("avoidsWater", false).forGetter(SwimBehaviour::avoidsWater),
                Codec.BOOL.optionalFieldOf("hurtByLava", true).forGetter(SwimBehaviour::hurtByLava),
                Codec.BOOL.optionalFieldOf("canSwimInWater", true).forGetter(SwimBehaviour::canSwimInWater),
                Codec.BOOL.optionalFieldOf("canSwimInLava", true).forGetter(SwimBehaviour::canSwimInLava),
                Codec.FLOAT.optionalFieldOf("swimSpeed", 0.3F).forGetter(SwimBehaviour::swimSpeed),
                Codec.BOOL.optionalFieldOf("canBreatheUnderwater", false).forGetter(SwimBehaviour::canBreatheUnderwater),
                Codec.BOOL.optionalFieldOf("canBreatheUnderlava", false).forGetter(SwimBehaviour::canBreatheUnderlava),
                Codec.BOOL.optionalFieldOf("canWalkOnWater", false).forGetter(SwimBehaviour::canWalkOnWater),
                Codec.BOOL.optionalFieldOf("canWalkOnLava", false).forGetter(SwimBehaviour::canWalkOnLava)
            ).apply(builder, ::SwimBehaviour)
        }

    }

}