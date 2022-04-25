package com.cablemc.pokemoncobbled.common.api.pokemon.stats

interface Stat {
    val id: String
    val name: String
        get() = "stat.$id.name"

    class Dummy(
        override val id: String,
        override val name: String = id
    ) : Stat
}