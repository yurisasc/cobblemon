package com.cablemc.pokemoncobbled.common.battles.runner

import com.caoccao.javet.interop.V8Host
import com.caoccao.javet.interop.V8Runtime
import com.caoccao.javet.values.V8Value
import kotlin.io.path.Path

object ShowdownServer {
    @JvmStatic
    fun main(args: Array<String>) {
        val runtime = V8Host.getNodeInstance().createV8Runtime<V8Runtime>()
        runtime.use { it.getExecutor(Path(args[0])).execute<V8Value>().close() }
    }
}