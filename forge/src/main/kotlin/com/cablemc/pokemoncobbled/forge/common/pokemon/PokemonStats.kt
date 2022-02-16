package com.cablemc.pokemoncobbled.forge.common.pokemon

import com.cablemc.pokemoncobbled.forge.common.api.pokemon.stats.Stats
import com.cablemc.pokemoncobbled.common.api.reactive.SimpleObservable
import com.cablemc.pokemoncobbled.forge.common.pokemon.stats.Stat
import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag

/**
 * Holds a mapping from a Stat to value that should be reducible to a short for NBT and net.
 */
open class PokemonStats : HashMap<Stat, Int>() {
    @Transient
    private val statObservables = mutableMapOf<Stat, SimpleObservable<Int>>()
    /** Emits any stat change. */
    @Transient
    val collectiveObservable = SimpleObservable<Pair<Stat, Int>>()

    override fun put(key: Stat, value: Int): Int? {
        val ret = super.put(key, value)
        statObservables.putIfAbsent(key, SimpleObservable())
        statObservables[key]?.emit(value)
        collectiveObservable.emit(key to value)
        return ret
    }

    override fun remove(key: Stat): Int? {
        val ret = super.remove(key)
        statObservables.remove(key)
        return ret
    }

    fun saveToNBT(nbt: CompoundTag): CompoundTag {
        entries.forEach { (stat, value) -> nbt.putShort(stat.id, value.toShort()) }
        return nbt
    }

    fun loadFromNBT(nbt: CompoundTag): PokemonStats {
        nbt.allKeys.forEach { statId ->
            val stat = Stats.getStat(statId) ?: return@forEach // TODO error probably? Or anonymous stat class to hold it and persist
            this[stat] = nbt.getShort(statId).toInt()
        }
        return this
    }

    fun saveToJSON(json: JsonObject): JsonObject {
        entries.forEach { (stat, value) -> json.addProperty(stat.id, value) }
        return json
    }

    fun loadFromJSON(json: JsonObject): PokemonStats {
        json.entrySet().forEach { (key, element) ->
            val stat = Stats.getStat(key) ?: return@forEach // TODO error or something as above
            this[stat] = element.asInt
        }
        return this
    }

    fun getObservable(stat: Stat) = statObservables.getOrPut(stat) { SimpleObservable() }
}