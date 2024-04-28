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
