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

data class IdleBehaviour(
    val pointsAtSpawn: Boolean = false
) {

    companion object {

        @JvmField
        val CODEC: Codec<IdleBehaviour> = RecordCodecBuilder.create { builder ->
            builder.group(
                Codec.BOOL.optionalFieldOf("pointsAtSpawn", false).forGetter(IdleBehaviour::pointsAtSpawn)
            ).apply(builder, ::IdleBehaviour)
        }

    }

}