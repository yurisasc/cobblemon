package com.cablemc.pokemoncobbled.common.battles.runner

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.util.AssetLoading.toPath
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Files
import javax.script.Invocable
import javax.script.ScriptEngineManager

object ShowdownServer {
    @JvmStatic
    fun main(args: Array<String>) {
        println("Starting... ${System.currentTimeMillis()}")
        val engine = ScriptEngineManager().getEngineByName("graal.js")
        val path = "/assets/${PokemonCobbled.MODID}/showdown/main.js"
        val inputStream = PokemonCobbled.javaClass.getResourceAsStream(path)!!

        BufferedReader(InputStreamReader(inputStream)).use {
            engine.eval(it)
        }
        println("Finished ${System.currentTimeMillis()}")
    }
}