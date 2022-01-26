package com.cablemc.pokemoncobbled.common.battles.runner

interface ShowdownConnection {
    fun open()
    fun close()
    fun write(input: String)
    fun read(messageHandler: (String) -> Unit)
    fun isClosed(): Boolean
    fun isConnected(): Boolean

    companion object {
        val lineEnder: String = "{EOT}"
        val lineStarter: String = "{SOT}"
    }
}