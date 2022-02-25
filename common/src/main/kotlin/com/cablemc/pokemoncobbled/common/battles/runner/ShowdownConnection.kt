package com.cablemc.pokemoncobbled.common.battles.runner

interface ShowdownConnection {
    fun open()
    fun close()
    fun write(input: String)
    fun read(messageHandler: (String) -> Unit)
    fun isClosed(): Boolean
    fun isConnected(): Boolean

    companion object {
        const val LINE_END = "{EOT}"
        const val LINE_START = "{SOT}"
    }
}