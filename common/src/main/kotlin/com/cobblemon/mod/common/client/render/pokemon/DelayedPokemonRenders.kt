/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.pokemon

import net.minecraft.client.util.math.MatrixStack
import java.util.*

object DelayedPokemonRenders {

    private val queued: Queue<(MatrixStack) -> Unit> = LinkedList()

    fun append(callback: (MatrixStack) -> Unit) {
        this.queued.add(callback)
    }

    fun render(stack: MatrixStack) {
        while (!this.queued.isEmpty()) {
            val callback = this.queued.poll()
            callback.invoke(stack)
        }
    }
}
