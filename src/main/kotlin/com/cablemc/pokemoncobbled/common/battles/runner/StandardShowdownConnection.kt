package com.cablemc.pokemoncobbled.common.battles.runner

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.InetAddress
import java.net.Socket
import java.nio.charset.Charset

class StandardShowdownConnection(host: InetAddress, port: Int): ShowdownConnection {

    private lateinit var socket: Socket
    private lateinit var writer: OutputStreamWriter
    private lateinit var reader: BufferedReader

    override fun open() {
        socket = Socket(InetAddress.getLocalHost(), 25567, InetAddress.getLocalHost(), 25566)
        writer = socket.getOutputStream().writer(charset = Charset.forName("ascii"))
        reader = BufferedReader(InputStreamReader(socket.getInputStream()))
    }

    override fun close() {
        socket.close()
    }

    override fun write(input: String) {
        writer.write(input + ShowdownConnection.lineEnder)
        writer.flush()
    }

    override fun read(): String? {
        return try {
            var data = ""
            while (reader.ready()) {
                val char = reader.read()
                if (char > -1) {
                    data += char.toChar()
                }
            }
            data
        } catch (exception: IOException) {
            null
        }
    }
}