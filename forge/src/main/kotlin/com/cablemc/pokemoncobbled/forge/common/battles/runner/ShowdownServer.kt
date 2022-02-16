package com.cablemc.pokemoncobbled.forge.common.battles.runner

import com.caoccao.javet.interop.V8Host
import com.caoccao.javet.interop.V8Runtime
import com.caoccao.javet.values.V8Value
import java.nio.file.Path

object ShowdownServer {

    @JvmStatic
    fun main(args: Array<String>) {
        val runtime = V8Host.getNodeInstance().createV8Runtime<V8Runtime>()
        runtime.let { it.getExecutor(Path.of(args[0])).execute<V8Value>().close() }
    }

}