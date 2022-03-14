package com.cablemc.pokemoncobbled.common.api.moves;

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.battles.runner.ShowdownServer
import com.cablemc.pokemoncobbled.common.battles.runner.exec
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File
import java.io.IOException
import java.net.InetAddress
import java.net.Socket

class MoveLoaderThread : Thread() {

    private lateinit var process: Process
    private lateinit var socket: Socket

    override fun run() {
        createProcess()

        // Wait for successful connection
        while (!attemptConnection()) {
            sleep(1000)
        }

        sleep(2000) // Wait for socket to send data

        val jsonObj : JsonObject? = readData()

        if (jsonObj == null) {
            PokemonCobbled.LOGGER.warn("There was a problem loading move data.")
            return
        }

        // TODO: Parse JsonObject into move objects
        // println(jsonObj)
    }

    /**
     * Runs the load_data script.
     */
    private fun createProcess() {
        try {
            process = exec(ShowdownServer::class.java, listOf(File("showdown/scripts/load_data.js").canonicalPath))
            Runtime.getRuntime().addShutdownHook(object : Thread() {
                override fun run() {
                    process.destroy()
                }
            })
        } catch (e: Exception) {
            println(e.stackTrace)
        }
    }

    /**
     * Opens the socket connection.
     */
    private fun openSocketConnection() {
        socket = Socket(InetAddress.getLocalHost(), 25569, InetAddress.getLocalHost(), 25568)
    }

    /**
     * Reads the data sent over the socket.
     */
    private fun readData() : JsonObject? {
        try {
            val sb = StringBuilder()
            val integerChars = '0'..'9'
            var receivingCharSize = true
            var buffer = ""
            var bufferVal = 0
            var c: Int

            while (socket.getInputStream().read().also { c = it } >= 0) {
                if (receivingCharSize) {
                    if (c.toChar() in integerChars) {
                        buffer += c.toChar()
                    }
                    else {
                        sb.append(c.toChar())
                        bufferVal = buffer.toInt()
                        receivingCharSize = false
                        PokemonCobbled.LOGGER.info("Reading moves buffer of size $bufferVal...")
                    }
                }
                else {
                    sb.append(c.toChar())

                    // Ensures the socket input stream won't hang.
                    if (sb.toString().length >= (bufferVal)) break
                }
            }
            socket.close()
            process.destroy()

            if (sb.isEmpty()) return null

            return JsonParser.parseString(sb.toString()).asJsonObject
        } catch (exception: IOException) {
            exception.printStackTrace()
        }

        return null
    }

    /**
     * Attempts to open the socket connection.
     */
    private fun attemptConnection() : Boolean {
        try {
            openSocketConnection()
        } catch (exception: IOException) {
            return false
        }
        return true
    }
}
