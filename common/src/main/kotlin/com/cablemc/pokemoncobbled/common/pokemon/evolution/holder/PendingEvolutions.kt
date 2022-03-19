package com.cablemc.pokemoncobbled.common.pokemon.evolution.holder

import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.Evolution
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.toJsonArray
import com.google.gson.JsonArray
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.network.FriendlyByteBuf

// ToDo implement states so this controls all evolutions based on Pokemon state and client input if required
class PendingEvolutions(
    evolutions: Set<Evolution> = emptySet()
) {

    private val pending = hashMapOf<String, Evolution>()

    init {
        evolutions.forEach { evolution -> this.pending[evolution.id] = evolution }
    }

    /**
     * Returns all the pending [Evolution]s if any.
     *
     * @return The pending [Evolution]s
     */
    fun pendingEvolutions() = this.pending.values.toSet()

    /**
     * Checks if the given [Evolution] is pending.
     *
     * @param evolution The [Evolution] being queried.
     * @return If it is pending.
     */
    fun isPending(evolution: Evolution) = this.pending.containsKey(evolution.id)

    /**
     * Queue the given [Evolution] for this Pokemon.
     *
     * @param evolution The [Evolution] being queued.
     */
    fun queue(evolution: Evolution) {
        if (this.isPending(evolution)) return
        this.pending[evolution.id] = evolution
        // ToDo let the client know what's up
    }

    fun saveToNBT() = ListTag().apply {
        addAll(pending.keys.map { key -> StringTag.valueOf(key) })
    }

    fun saveToJSON() = this.pending.keys.toJsonArray()

    fun saveToBuffer(buffer: FriendlyByteBuf) {
        buffer.writeInt(this.pending.size)
        this.pending.keys.forEach { id ->
            buffer.writeUtf(id)
        }
    }

    companion object {

        fun loadFromNBT(pokemon: Pokemon, nbt: ListTag) = this.load(pokemon, nbt.filterIsInstance<StringTag>().map { tag -> tag.asString })

        fun loadFromJSON(pokemon: Pokemon, json: JsonArray) = this.load(pokemon, json.filter { element -> element.isJsonPrimitive }.map { element -> element.asString })

        fun loadFromBuffer(pokemon: Pokemon, buffer: FriendlyByteBuf): PendingEvolutions {
            val quantity = buffer.readInt()
            val ids = mutableListOf<String>()
            for (i in 1..quantity) {
                ids += buffer.readUtf()
            }
            return this.load(pokemon, ids)
        }

        private fun load(pokemon: Pokemon, ids: List<String>): PendingEvolutions {
            val evolutions = hashSetOf<Evolution>()
            for (id in ids) {
                evolutions += pokemon.species.evolutions.firstOrNull { evolution -> evolution.id.equals(id, true) } ?: continue
            }
            return PendingEvolutions(evolutions)
        }

    }

}