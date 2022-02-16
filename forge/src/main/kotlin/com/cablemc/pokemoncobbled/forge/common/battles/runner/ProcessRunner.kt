package com.cablemc.pokemoncobbled.forge.common.battles.runner

import java.io.File
import java.io.IOException

/**
 * Modified from https://lankydan.dev/running-a-kotlin-class-as-a-subprocess
 *
 * Spawns a process and returns it
 */
@Throws(IOException::class, InterruptedException::class)
fun exec(clazz: Class<*>, args: List<String> = emptyList(), jvmArgs: List<String> = emptyList()): Process {
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