package com.cablemc.pokemoncobbled.common.pokemon

import com.cablemc.pokemoncobbled.common.api.pokemon.stats.Stats
import com.cablemc.pokemoncobbled.common.pokemon.stats.Stat
import net.minecraft.nbt.CompoundTag

/**
 * Holds a mapping from a Stat to value that should be reducable to a short for NBT.
 */
class PokemonStats : HashMap<Stat, Int>() {
    fun save(nbt: CompoundTag): CompoundTag {
        entries.forEach { (stat, value) -> nbt.putShort(stat.id, value.toShort()) }
        return nbt
    }

    fun load(nbt: CompoundTag): PokemonStats {
        nbt.allKeys.forEach { statId ->
            val stat = Stats.getStat(statId) ?: return@forEach // TODO error probably? Or anonymous stat class to hold it and persist
            this[stat] = nbt.getShort(statId).toInt()
        }
        return this
    }
}