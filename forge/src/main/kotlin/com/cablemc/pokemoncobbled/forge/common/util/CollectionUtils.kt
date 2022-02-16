package com.cablemc.pokemoncobbled.forge.common.util

import com.cablemc.pokemoncobbled.forge.common.pokemon.PokemonStats
import com.cablemc.pokemoncobbled.forge.common.pokemon.stats.Stat

fun pokemonStatsOf(vararg entries: Pair<Stat, Int>): PokemonStats {
    val stats = PokemonStats()
    entries.forEach { (stat, amount) -> stats[stat] = amount }
    return stats
}