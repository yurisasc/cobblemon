package com.cobblemon.mod.common.client.storage

import com.cobblemon.mod.common.pokedex.PokedexEntry
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.Species
import net.minecraft.util.Identifier
import net.minecraft.util.collection.SortedArraySet
import java.util.*

class ClientPokedex(uuid: UUID) {
    //Entries and Sub Entries should be sorted when elements are added.
    val entries = LinkedList<PokedexEntry>()
    // sub entries are what actually show up in the filter. Think of it like a cache
    val subEntries = LinkedList<PokedexEntry>()

    fun findBySpecies(speciesIdentifier: Identifier) = entries.find{ it.speciesIdentifier == speciesIdentifier }

    fun get(index: Int) = entries.get(index)

    fun add(entry: PokedexEntry) = entries.add(entry).also{ entries.sort() }

    fun remove(entry: PokedexEntry) = entries.remove(entry)

    fun removeIndex(index: Int) = entries.removeAt(index)

    fun getSubEntries(index: Int) = subEntries.get(index)
}