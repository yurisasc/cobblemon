package com.cablemc.pokemoncobbled.common

interface PokemonCobbledModImplementation {
    /** Only access from the client logical side or death will occur. */
    val client: PokemonCobbledClientImplementation
}

interface PokemonCobbledClientImplementation {
    fun initialize()
}
