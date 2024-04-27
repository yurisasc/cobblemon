package com.cobblemon.mod.common.client.render.pokemon

import java.util.*

object DelayedPokemonRenders {

    private val queued: Queue<() -> Unit> = LinkedList()

    fun append(callback: () -> Unit) {
        this.queued.add(callback)
    }

    fun render() {
        while (!this.queued.isEmpty()) {
            val callback = this.queued.poll()
            callback.invoke()
        }
    }
}
