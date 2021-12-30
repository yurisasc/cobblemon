package com.cablemc.pokemoncobbled.common.pokemon

import com.cablemc.pokemoncobbled.common.pokemon.stats.Stat
import net.minecraft.world.entity.EntityDimensions

class Species {
    var name: String = "bulbasaur"
    var nationalPokedexNumber = 1

    val baseStats: MutableMap<Stat, Int> = mutableMapOf()
    /** The ratio of the species being male. If -1, the Pokémon is genderless. */
    val maleRatio = 0.5F
    val catchRate = 45
    val baseScale = 1F
    val hitbox = EntityDimensions(1F, 1F, false)

    var forms = mutableListOf(FormData())
}