package com.cablemc.pokemoncobbled.common.battles.runner

interface ShowdownConnection {
    fun open()
    fun close()
    fun write(input: String)
    fun read(messageHandler: (String) -> Unit)

    companion object {
        val lineEnder: String = "{EOT}"
        val lineStarter: String = "{SOT}"
    }
}