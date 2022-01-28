package com.cablemc.pokemoncobbled.common.battles.runner

import java.io.*
import java.net.InetAddress
import java.net.Socket
import java.nio.charset.Charset

class JavetShowdownConnection : ShowdownConnection {

    private lateinit var process: Process
    private lateinit var socket: Socket
    private lateinit var writer: OutputStreamWriter
    private lateinit var reader: BufferedReader
    private var data = ""
    private var closed: Boolean = false

    companion object {
        var process: Process? = null
    }

    fun initializeServer() {
        process = exec(ShowdownServer.javaClass, listOf(File("showdown/scripts/index.js").canonicalPath))
        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                close()
            }
        })
    }

    override fun open() {
        socket = Socket(InetAddress.getLocalHost(), 25567, InetAddress.getLocalHost(), 25566)
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
        writer.write(input + ShowdownConnection.lineEnder)
        writer.flush()
    }

    override fun read(messageHandler: (String) -> Unit) {
        try {
            while (reader.ready()) {
                val char = reader.read()
                if (char > -1) {
                    data += char.toChar()
                    if(data.endsWith(ShowdownConnection.lineEnder)) {
                        messageHandler(data.replace(ShowdownConnection.lineEnder, ""))
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

    /**
     * Modified from https://lankydan.dev/running-a-kotlin-class-as-a-subprocess
     */
    private fun spawnProcess(clazz: Class<*>, args: List<String> = emptyList(), jvmArgs: List<String> = emptyList()): Process? {
        val javaHome = System.getProperty("java.home")
        val javaBin = javaHome + File.separator + "bin" + File.separator + "java"
        val classpath = System.getProperty("java.class.path")
        val className = clazz.name

        val command = ArrayList<String>()
        command.add(javaBin)
        command.addAll(jvmArgs)
        command.add("-cp")
        command.add(classpath)
        command.add(className)
        command.addAll(args)

        val builder = ProcessBuilder(command)
        return builder.inheritIO().start()
    }

}