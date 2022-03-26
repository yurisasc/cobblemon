package com.cablemc.pokemoncobbled.common.battles

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.PokemonCobbled.LOGGER
import com.cablemc.pokemoncobbled.common.PokemonCobbled.config
import com.cablemc.pokemoncobbled.common.PokemonCobbled.showdown
import com.cablemc.pokemoncobbled.common.api.moves.MoveLoader
import com.cablemc.pokemoncobbled.common.api.moves.Moves
import com.cablemc.pokemoncobbled.common.battles.runner.JavetShowdownConnection
import com.cablemc.pokemoncobbled.common.util.DataKeys
import com.cablemc.pokemoncobbled.common.util.FileUtils
import com.cablemc.pokemoncobbled.common.util.extractTo
import com.cablemc.pokemoncobbled.common.util.fromJson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import java.io.*
import java.nio.file.Files

class ShowdownThread : Thread() {

    val gson = GsonBuilder()
        .disableHtmlEscaping()
        .create()

    override fun run() {
        var showdownDir = File("showdown/")

        val showdownMetadata = loadShowdownMetadata()

        // Check if showdown needs to be installed
        if (!showdownDir.exists() || config.autoUpdateShowdown) {
            val showdownZip = File(showdownDir, "showdown.zip")
            val showdownMetadataFile = File(showdownDir, "showdown.json")

            var extract = true

            if (showdownMetadataFile.exists()) {
                val localShowdownMetadata = gson.fromJson<ShowdownMetadata>(InputStreamReader(FileInputStream(showdownMetadataFile)))
                if (showdownMetadata!!.showdownVersion == localShowdownMetadata.showdownVersion) {
                    extract = false
                } else {
                    showdownDir.renameTo(File("showdown-backup"))
                }
            }

            if (extract) {
                showdownDir = File("showdown")
                showdownDir.mkdir()
                ResourceLocation(PokemonCobbled.MODID, "showdown.zip").extractTo(showdownZip)
                ResourceLocation(PokemonCobbled.MODID, "showdown.json").extractTo(showdownMetadataFile)
                FileUtils.unzipFile(showdownZip.toPath(), showdownDir.toPath())
            }
        }

        // Initialize showdown connection
        showdown = JavetShowdownConnection()
        (showdown as JavetShowdownConnection).initializeServer()

        // Sleep for two seconds before attempting connection
        sleep(2000)

        val maxTries = 15
        var tries = 0

        // If connection fails, wait another two seconds
        while (!attemptConnection() && tries < maxTries) {
            tries++
            sleep(3000)
        }

        // Max attempts
        if (tries == maxTries) {
            LOGGER.error("Failed to connect to showdown after 5 tries.")
            Minecraft.getInstance().close()
        }

        LOGGER.info("Showdown has been connected!")

//        // Request and register showdown move data.
//        LOGGER.info("Receiving move data.")
//        if (!Files.exists(MoveLoader.dirPath)) {
//            val request = JsonObject()
//            request.addProperty(DataKeys.REQUEST_TYPE, DataKeys.REQUEST_RECEIVE_MOVE_DATA)
//            println(gson.toJson(request))
//            showdown.write(gson.toJson(request))
//            sleep(2000) // Wait for the socket to send a response.
//            showdown.read(MoveLoader::createFiles)
//        }

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
                    sleep(3000)
                }

                // Max attempts
                if (tries == maxTries) {
                    LOGGER.error("Failed to connect to showdown after 5 tries.")
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