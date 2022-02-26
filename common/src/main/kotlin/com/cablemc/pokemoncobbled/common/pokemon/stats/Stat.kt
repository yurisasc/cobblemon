package com.cablemc.pokemoncobbled.common.pokemon.stats

interface Stat {
    val id: String
    val name: String
        get() = "stat.$id.name"

    class Dummy(override val id: String) : Stat {
        override val name = id
    }
}