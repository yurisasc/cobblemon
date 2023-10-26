/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.moves.animations.keyframes

import com.cobblemon.mod.common.api.moves.animations.ActionEffectContext
import java.util.concurrent.CompletableFuture

interface ActionEffectKeyframe {
    companion object {
        val types = mutableMapOf<String, Class<out ActionEffectKeyframe>>()

        inline fun <reified T : ActionEffectKeyframe> register(type: String) {
            types[type] = T::class.java
        }
    }

    fun play(context: ActionEffectContext): CompletableFuture<Unit>
    fun interrupt(context: ActionEffectContext) {}
}
