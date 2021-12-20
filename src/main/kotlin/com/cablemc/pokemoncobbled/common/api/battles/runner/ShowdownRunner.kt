package com.cablemc.pokemoncobbled.common.api.battles.runner

import com.caoccao.javet.interop.V8Host
import com.caoccao.javet.interop.V8Runtime
import com.caoccao.javet.values.V8Value
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.Socket
import java.nio.CharBuffer
import java.nio.charset.Charset
import java.util.Scanner
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.atomic.AtomicReference

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

    fun initialize() {
        val v8Ref = AtomicReference<V8Runtime>()
        val battleMap = HashMap<String, Any>()
        val thread = Thread {
            println("1")
            val runtime = V8Host.getNodeInstance().createV8Runtime<V8Runtime>()
            v8Ref.set(runtime)
            println("2")
            runtime.use { it.getExecutor(File("./showdown/scripts/index.js").toPath().toAbsolutePath()).execute<V8Value>().close() }
            println("3")
        }

        val connectionThread = Thread() {
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
                    readUntil(reader, "cobble-accepted")
                    writer.write(">player p1 {\"name\": \"Alice\"}\n")
                    writer.flush();
                    println("testUntil2")
                    readUntil(reader, "cobble-accepted")
                    writer.write(">player p2 {\"name\": \"Bob\"}\n")
                    writer.flush();
                    println("testUntil3")
                    while (true) {
                        println(readUntil(reader, "cobble-incoming"))
                    }
                }
            }, 5000L)
        }
        thread.isDaemon = true
        thread.start()

        connectionThread.isDaemon = true;
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
}