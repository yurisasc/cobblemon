package com.cablemc.pokemoncobbled.common.battles.runner

import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.InetAddress
import java.net.Socket
import java.nio.charset.Charset

class JavetShowdownConnection : ShowdownConnection {

    private lateinit var process: Process
    private lateinit var socket: Socket
    private lateinit var writer: OutputStreamWriter
    private lateinit var reader: BufferedReader
    private var data = ""
    private var closed = false
    val serverThread = Thread { ShowdownServer.start() }

    fun initializeServer() {
        serverThread.start()
    }

    override fun open() {
        socket = Socket(InetAddress.getLocalHost(), ShowdownServer.port, InetAddress.getLocalHost(), 0)
        socket.keepAlive = true
        writer = socket.getOutputStream().writer(charset = Charset.forName("ascii"))
        reader = BufferedReader(InputStreamReader(socket.getInputStream()))
    }

    override fun close() {
        socket.close()
        process.destroy()
        closed = true
    }

    override fun write(input: String) {
        writer.write(input + ShowdownConnection.LINE_END)
        writer.flush()
    }

    override fun read(messageHandler: (String) -> Unit) {
        try {
            while (reader.ready()) {
                val char = reader.read()
                if (char > -1) {
                    data += char.toChar()
                    if (data.endsWith(ShowdownConnection.LINE_END)) {
                        messageHandler(data.replace(ShowdownConnection.LINE_END, ""))
                        data = ""
                    }
                }
            }
        } catch (exception: IOException) {
            exception.printStackTrace()
        }
    }

    override fun isClosed() : Boolean {
        return closed
    }

    override fun isConnected(): Boolean {
        return socket.isConnected
    }
}