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
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    println("Gonna attempt connection")
                    val sock = Socket("127.0.0.1", 3001, InetAddress.getLocalHost(), 3002)
                    val writer = sock.getOutputStream().writer(charset = Charset.forName("ascii"))
                    val streamReader = InputStreamReader(sock.getInputStream())
                    val reader = BufferedReader(streamReader)
                    writer.write(">start {\"formatid\": \"gen7randombattle\"}")
                    writer.flush()
                    Thread.sleep(1L)
                    writer.write(">player p1 {\"name\": \"Alice\"}")
                    writer.flush()
                    Thread.sleep(1L)
                    writer.write(">player p2 {\"name\": \"Bob\"}")
                    writer.flush()
                    Thread.sleep(1L)
                    println("Got to scheduling second timer")
                    Timer().schedule(object : TimerTask() {
                        override fun run() {
                            println("Running second timer")
                            val buffer = CharBuffer.allocate(4096)
                            reader.read(buffer)
                            val str = buffer.toString()
                            println(str)
                        }
                    }, 5000L)
                }
            }, 5000L)
        }
        thread.isDaemon = true
        thread.start()
    }
}