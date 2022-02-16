package com.cablemc.pokemoncobbled.forge.common.pokemon.stats

interface Stat {
    val id: String
    val name: String
        get() = "stat.$id.name"
}