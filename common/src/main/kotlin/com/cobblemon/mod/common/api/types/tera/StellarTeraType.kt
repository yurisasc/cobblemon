/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.types.tera

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.effect.Effect
import com.cobblemon.mod.common.api.effect.types.TeraTypeEffect
import com.cobblemon.mod.common.api.resistance.Resistance
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import net.minecraft.core.Holder
import net.minecraft.network.chat.Component

object StellarTeraType : BaseTeraType() {

    override fun displayName(): Component = Component.translatable("${Cobblemon.MODID}.tera_type.stellar")

    override fun resistanceTo(effect: Effect): Resistance = Resistance.NEUTRAL

    override fun codec(): Codec<out TeraType> = Codec.unit(StellarTeraType)

    override fun mapCodec(): MapCodec<out TeraType> = MapCodec.unit(StellarTeraType)

    override fun asEffect(): Effect = TeraTypeEffect(this.resourceKey())
}