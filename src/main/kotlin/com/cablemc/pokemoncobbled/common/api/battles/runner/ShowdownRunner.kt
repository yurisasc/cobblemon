package com.cablemc.pokemoncobbled.common.api.battles.runner

import com.caoccao.javet.interop.V8Runtime
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.Socket
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread

var running = true
fun main(args: Array<String>) {
    ShowdownRunner.initialize()

    val scanner = Scanner(System.`in`)
    while (running) {
        if (scanner.hasNext()) {
            val input = scanner.nextLine();
            when (input.lowercase()) {
                "quit" -> {
                    println("Attempting to shut down....")
                    running = false;
                }
                else -> {}
            }
        }
    }
}

object ShowdownRunner {

    var process: Process? = null

    fun initialize() {
        val v8Ref = AtomicReference<V8Runtime>()
        val battleMap = HashMap<String, Any>()
//        val thread = thread(
//            isDaemon = true,
//            start = false
//        ) {
//            println("1")
//            val runtime = V8Host.getNodeInstance().createV8Runtime<V8Runtime>()
//            v8Ref.set(runtime)
//            println("2")
//            runtime.use { it.getExecutor(Path(File("../showdown/scripts/index.js").canonicalPath)).execute<V8Value>().close() }
//            println("3")
//        }

        process = exec(ShowdownServer.javaClass, listOf(File("../showdown/scripts/index.js").canonicalPath))

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                process!!.destroy()
            }
        })

        val connectionThread = thread(
            isDaemon = true,
            start = false
        ) {
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    println("Gonna attempt connection")
                    val sock = Socket(InetAddress.getLocalHost(), 3001, InetAddress.getLocalHost(), 3005)
                    val writer = sock.getOutputStream().writer(charset = Charset.forName("ascii"))
                    val streamReader = InputStreamReader(sock.getInputStream())
                    val reader = BufferedReader(streamReader)
                    readUntil(reader, "ready")

                    writer.write(">start {\"formatid\": \"gen7randombattle\"}\n")
                    writer.flush();
                    writer.write(">player p1 {\"name\": \"Alice\"}\n")
                    writer.flush();
                    writer.write(">player p2 {\"name\": \"Bob\"}\n")
                    writer.flush()

                    writer.write(">p1 team 123456")
                    writer.flush()

                    while (true) {
                        println("(Battle) ${readUntil(reader, "cobble-incoming")}")
                    }
                }
            }, 5000L)
        }
        //thread.start()

        connectionThread.start()
    }

    fun readUntil(reader: BufferedReader, str: String, size: Int = 4096): String {
        val buffer = CharArray(size)
        while (true) {
            val chars = reader.read(buffer)
            if(chars == 0)
                continue;
            val readStr = String(buffer);
            if(readStr.contains(str)) {
                return readStr.replace(str, "")
            }
        }
    }

    fun exit() {
        process?.run {
            this.destroy()
        }
    }
}