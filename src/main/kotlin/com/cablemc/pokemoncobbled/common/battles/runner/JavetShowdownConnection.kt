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
    private var lineEnder: String = "{EOT}"

    fun initializeServer() {
        ShowdownRunner.process = exec(ShowdownServer.javaClass, listOf(File("../showdown/scripts/index.js").canonicalPath))
    }

    override fun open() {
        socket = Socket(InetAddress.getLocalHost(), 25567, InetAddress.getLocalHost(), 25566)
        writer = socket.getOutputStream().writer(charset = Charset.forName("ascii"))
        reader = BufferedReader(InputStreamReader(socket.getInputStream()))
    }

    override fun close() {
        socket.close()
        process.destroy()
    }

    override fun write(input: String) {
        writer.write(input + lineEnder)
        writer.flush()
    }

    override fun read(): String? {
        return try {
            reader.readLine()
        } catch (exception: IOException) {
            null
        }
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