package com.cablemc.pokemoncobbled.common.battles.runner

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.PokemonCobbled.LOGGER
import org.graalvm.polyglot.Context
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader

object ShowdownServer {
    fun start(): Context {
        val path = "/assets/${PokemonCobbled.MODID}/showdown/main.js"
        val inputStream = PokemonCobbled.javaClass.getResourceAsStream(path)!!
        val js = InputStreamReader(inputStream).readText()

        val out = ByteArrayOutputStream()
        val context = Context.newBuilder("js")
            .out(out)
            .option("js.strict", "true")
            .allowAllAccess(true)
            .build()
        context.eval("js", js)
        LOGGER.info("Showdown has started.")
        return context
    }
}