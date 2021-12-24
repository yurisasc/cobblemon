package com.cablemc.pokemoncobbled.common.battles.runner

interface ShowdownConnection {
    fun open()
    fun close()
    fun write(input: String)
    fun read(): String?
}