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
import net.minecraft.util.Identifier

class ParticleActionEffectKeyframe : ActionEffectKeyframe {
    var delay = 1F
    val effect: Identifier? = null
    // rethink this, it could be played at a position or at other entities that aren't players
    val locator: Set<String> = setOf("root")
    val variables: List<Expression> = listOf()
    override fun play(context: ActionEffectContext): CompletableFuture<Unit> {
        // Start a particle storm on the client
        return delayedFuture(seconds = delay)
    }
}
