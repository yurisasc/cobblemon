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

data class WalkBehaviour(
    val canWalk: Boolean = true,
    val avoidsLand: Boolean = false,
    var walkSpeed: Float = 0.35F
) {

    companion object {

        @JvmField
        val CODEC: Codec<WalkBehaviour> = RecordCodecBuilder.create { builder ->
            builder.group(
                Codec.BOOL.optionalFieldOf("canWalk", true).forGetter(WalkBehaviour::canWalk),
                Codec.BOOL.optionalFieldOf("avoidsLand", false).forGetter(WalkBehaviour::avoidsLand),
                Codec.FLOAT.optionalFieldOf("walkSpeed", 0.35F).forGetter(WalkBehaviour::walkSpeed)
            ).apply(builder, ::WalkBehaviour)
        }

    }

}