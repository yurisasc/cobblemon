package com.cablemc.pokemoncobbled.common.battles

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.PokemonCobbled.LOGGER
import com.cablemc.pokemoncobbled.common.PokemonCobbled.config
import com.cablemc.pokemoncobbled.common.PokemonCobbled.showdown
import com.cablemc.pokemoncobbled.common.api.moves.Moves
import com.cablemc.pokemoncobbled.common.battles.runner.JavetShowdownConnection
import com.cablemc.pokemoncobbled.common.util.FileUtils
import com.cablemc.pokemoncobbled.common.util.extractTo
import com.cablemc.pokemoncobbled.common.util.fromJson
import com.google.gson.GsonBuilder
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import org.graalvm.polyglot.Context
import java.io.*
import java.util.concurrent.CompletableFuture

class ShowdownThread : Thread() {
    companion object {
        var contextFuture = CompletableFuture<Context>()
    }
    val gson = GsonBuilder()
        .disableHtmlEscaping()
        .create()

    override fun run() {
        val maxTries = 15
        var tries = 0

        // If connection fails, wait another two seconds
        while (!attemptConnection() && tries < maxTries) {
            tries++
            sleep(1000)
        }

        // Max attempts
        if (tries == maxTries) {
            LOGGER.error("Failed to connect to showdown after 5 tries.")
            Minecraft.getInstance().close()
        }

        LOGGER.info("Showdown has been connected!")

        // Should this be moved outside the thread?
        Moves.load()
        LOGGER.info("Loaded " + Moves.count() + " moves.")

        // Reset tries as this will be used by reconnection attempts
        tries = 0

        // While showdown is not closed, continue to check connection and read messages
        while (!showdown.isClosed()) {

            // Attempt reconnection if not connected
            if (!showdown.isConnected()) {
                while (!attemptConnection() && tries < maxTries) {
                    tries++
                    sleep(1000)
                }

                // Max attempts
                if (tries == maxTries) {
                    LOGGER.error("Failed to connect to showdown after $maxTries tries.")
                    Minecraft.getInstance().close()
                }

                tries = 0
                LOGGER.info("Showdown has been reconnected!")
            }

            // Reads messages
            showdown.read(ShowdownInterpreter::interpretMessage)

            // Read messages every half a second
            sleep(500)
        }
    }

    private fun attemptConnection() : Boolean {
        try {
            showdown.open()
            return true
        } catch (exception: IOException) {
            return false
        }
    }

    private fun loadShowdownMetadata() : ShowdownMetadata? {
        try {
            val inputStream = javaClass.getResourceAsStream("/assets/${PokemonCobbled.MODID}/showdown.json")!!
            return gson.fromJson<ShowdownMetadata>(InputStreamReader(inputStream))
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return null
    }

    private data class ShowdownMetadata(
        val showdownVersion: Double
    )
}