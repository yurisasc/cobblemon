/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.types.client

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.util.Identifier

/**
 * Represents various properties used in UIs.
 * This data is present on the server and synchronized to clients.
 *
 * @property hue The RGB tint applied in a few UIs.
 * @property primaryTexture The primary texture for the type icon.
 * @property secondaryTexture The secondary texture used when a miniature icon is wanted.
 * @property uOffset The uOffset on the blitk.
 * @property vOffset The vOffset on the blitk.
 */
data class ClientData(
    val hue: Int,
    val primaryTexture: Identifier,
    val secondaryTexture: Identifier,
    val uOffset: Float,
    val vOffset: Float
) {

    companion object {

        val CODEC: Codec<ClientData> = RecordCodecBuilder.create { builder ->
            builder.group(
                Codec.INT.fieldOf("hue").forGetter(ClientData::hue),
                Identifier.CODEC.fieldOf("primaryTexture").forGetter(ClientData::primaryTexture),
                Identifier.CODEC.fieldOf("secondaryTexture").forGetter(ClientData::secondaryTexture),
                Codec.FLOAT.optionalFieldOf("uOffset", 0F).forGetter(ClientData::uOffset),
                Codec.FLOAT.optionalFieldOf("vOffset", 0F).forGetter(ClientData::vOffset)
            ).apply(builder, ::ClientData)
        }

    }

}
