package com.cablemc.pokemoncobbled.common.battles

import com.cablemc.pokemoncobbled.common.battles.runner.JavetShowdownConnection
import com.cablemc.pokemoncobbled.mod.PokemonCobbledMod
import net.minecraft.client.Minecraft
import java.io.IOException

class ShowdownThread : Thread() {
    override fun run() {
        PokemonCobbledMod.showdown = JavetShowdownConnection()
        (PokemonCobbledMod.showdown as JavetShowdownConnection).initializeServer()

        // Sleep for two seconds before attempting connection
        sleep(2000)

        var tries = 0

        // If connection fails, wait another two seconds
        while (!attemptConnection() && tries < 5) {
            tries++
            sleep(2000)
        }

        // Max attempts
        if (tries == 5) {
            PokemonCobbledMod.LOGGER.error("Failed to connect to showdown after 5 tries.")
            Minecraft.getInstance().close()
        }

        PokemonCobbledMod.LOGGER.info("Showdown has been connected!")

        // Reset tries as this will be used by reconnection attempts
        tries = 0

        // While showdown is not closed, continue to check connection and read messages
        while (!PokemonCobbledMod.showdown.isClosed()) {
            val showdown = PokemonCobbledMod.showdown

            // Attempt reconnection if not connected
            if (!showdown.isConnected()) {
                while (!attemptConnection() && tries < 5) {
                    tries++
                    sleep(2000)
                }

                // Max attempts
                if (tries == 5) {
                    PokemonCobbledMod.LOGGER.error("Failed to connect to showdown after 5 tries.")
                    Minecraft.getInstance().close()
                }

                tries = 0
                PokemonCobbledMod.LOGGER.info("Showdown has been reconnected!")
            }

            // Reads messages
            PokemonCobbledMod.showdown.read(ShowdownInterpreter::interpretMessage)

            // Read messages every half a second
            sleep(500)
        }
    }

    private fun attemptConnection() : Boolean {
        try {
            PokemonCobbledMod.showdown.open()
            return true
        } catch (exception: IOException) {
            return false
        }
    }
}