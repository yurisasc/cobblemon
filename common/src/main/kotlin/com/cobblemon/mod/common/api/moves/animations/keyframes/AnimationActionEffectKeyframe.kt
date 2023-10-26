/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.moves.animations.keyframes

import com.bedrockk.molang.Expression
import com.cobblemon.mod.common.api.moves.animations.ActionEffectContext
import com.cobblemon.mod.common.api.scheduling.delayedFuture
import java.util.concurrent.CompletableFuture
import net.minecraft.server.world.ServerWorld

class AnimationActionEffectKeyframe : ActionEffectKeyframe {
    var delay = 1F
    var visibilityRange = 200
    var animation: Set<String> = setOf("physical")
    var variables: List<Expression> = listOf()

    override fun play(context: ActionEffectContext): CompletableFuture<Unit> {
        val world = context.userPokemon.world as ServerWorld
        val players = world.getPlayers { it.distanceTo(context.userPokemon) <= visibilityRange }

        return delayedFuture(seconds = delay)
    }
}