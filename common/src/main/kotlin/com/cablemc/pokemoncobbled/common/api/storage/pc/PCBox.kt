package com.cablemc.pokemoncobbled.common.api.storage.pc

import com.cablemc.pokemoncobbled.common.api.reactive.SimpleObservable
import com.cablemc.pokemoncobbled.common.api.storage.StoreCoordinates
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag

class PCBox(val pc: PCStore) : Iterable<Pokemon> {
    override fun iterator() = pokemon.filterNotNull().iterator()

    val boxChangeEmitter = SimpleObservable<Pair<Int, Pokemon?>>()

    private var emit = true

    private val pokemon = Array<Pokemon?>(POKEMON_PER_BOX) { null }

    operator fun get(index: Int): Pokemon? {
        return if (index in 0 until POKEMON_PER_BOX) {
            pokemon[index]
        } else {
            null
        }
    }

    operator fun set(index: Int, pokemon: Pokemon?) {
        if (index in 0 until POKEMON_PER_BOX) {
            this.pokemon[index] = pokemon
            pokemon?.run { storeCoordinates.set(StoreCoordinates(pc, PCPosition(boxNumber, index))) }
            if (emit) {
                boxChangeEmitter.emit(index to pokemon)
            }
        }
    }

    val boxNumber: Int
        get() = this.pc.boxes.indexOf(this)

    fun getFirstAvailablePosition(): PCPosition? {
        for (index in 0 until POKEMON_PER_BOX) {
            if (this.pokemon[index] == null) {
                return PCPosition(boxNumber, index)
            }
        }
        return null
    }

    fun saveToNBT(nbt: CompoundTag) {

    }

    fun saveToJSON(json: JsonObject) {

    }

    fun loadFromJSON(json: JsonObject) {

    }

    fun loadFromNBT(nbt: CompoundTag) {

    }
}