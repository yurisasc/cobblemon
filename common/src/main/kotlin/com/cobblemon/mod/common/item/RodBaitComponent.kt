/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item

import com.cobblemon.mod.common.api.fishing.FishingBait
import com.cobblemon.mod.common.api.fishing.FishingBaits
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation

/**
 * A simple component that contains a reference to the [FishingBait].
 *
 * @author Hiroku
 * @since June 9th, 2024
 */
class RodBaitComponent(val bait: FishingBait) {
    companion object {
        val CODEC: Codec<RodBaitComponent> = RecordCodecBuilder.create { builder -> builder.group(
            ResourceLocation.CODEC.fieldOf("bait").forGetter { it.bait.item },
        ).apply(builder) { bait -> RodBaitComponent(FishingBaits.getFromIdentifier(bait) ?: FishingBait.BLANK_BAIT) } }

        val PACKET_CODEC: StreamCodec<ByteBuf, RodBaitComponent> = ByteBufCodecs.fromCodec(CODEC)
    }
}