/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.api.storage.pc

import com.cablemc.pokemoncobbled.common.api.reactive.Observable.Companion.stopAfter
import com.cablemc.pokemoncobbled.common.api.reactive.SimpleObservable
import com.cablemc.pokemoncobbled.common.api.storage.StoreCoordinates
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.pc.SetPCBoxPokemonPacket
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.DataKeys
import com.google.gson.JsonObject
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity

/**
 * A single box of a PC. The list of Pokémon is strictly sized at [POKEMON_PER_BOX] - 30.
 * Any change to any contained Pokémon is emitted through the [boxChangeEmitter].
 *
 * @author Hiroku
 * @since April 26th, 2022
 */
open class PCBox(val pc: PCStore) : Iterable<Pokemon> {
    override fun iterator() = pokemon.filterNotNull().iterator()

    val boxChangeEmitter = SimpleObservable<Unit>()

    protected var emit = true

    protected val pokemon = Array<Pokemon?>(POKEMON_PER_BOX) { null }

    open operator fun get(index: Int): Pokemon? {
        return if (index in 0 until POKEMON_PER_BOX) {
            pokemon[index]
        } else {
            null
        }
    }

    open operator fun set(index: Int, pokemon: Pokemon?) {
        if (index in 0 until POKEMON_PER_BOX) {
            this.pokemon[index] = pokemon
            if (pokemon != null) {
                val previousCoordinates = pokemon.storeCoordinates.get()
                val position = previousCoordinates?.position
                pokemon.storeCoordinates.set(StoreCoordinates(pc, PCPosition(boxNumber, index)))
                if (previousCoordinates?.store != this || (position as PCPosition).box != boxNumber) {
                    trackPokemon(pokemon)
                }
            }
            if (emit) {
                boxChangeEmitter.emit(Unit)
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

    open fun initialize() {
        val box = boxNumber
        pokemon.forEachIndexed { slot, pokemon ->
            if (pokemon != null) {
                val position = PCPosition(box, slot)
                pokemon.storeCoordinates.set(StoreCoordinates(pc, position))
                trackPokemon(pokemon)
            }
        }
        boxChangeEmitter.subscribe { pc.pcChangeObservable.emit(Unit) }
    }

    fun trackPokemon(pokemon: Pokemon) {
        pokemon.getChangeObservable()
            .pipe(
                stopAfter {
                    val coordinates = it.storeCoordinates.get() ?: return@stopAfter true
                    return@stopAfter coordinates.store != this || (coordinates.position as PCPosition).box != boxNumber
                }
            )
            .subscribe { boxChangeEmitter.emit(Unit) }
    }

    fun sendTo(player: ServerPlayerEntity) {
        SetPCBoxPokemonPacket(this).sendToPlayer(player)
    }

    open fun saveToNBT(nbt: NbtCompound): NbtCompound {
        for (slot in 0 until POKEMON_PER_BOX) {
            val pokemon = pokemon[slot] ?: continue
            nbt.put(DataKeys.STORE_SLOT + slot, pokemon.saveToNBT(NbtCompound()))
        }
        return nbt
    }

    open fun saveToJSON(json: JsonObject): JsonObject {
        for (slot in 0 until POKEMON_PER_BOX) {
            val pokemon = pokemon[slot] ?: continue
            json.add(DataKeys.STORE_SLOT + slot, pokemon.saveToJSON(JsonObject()))
        }
        return json
    }

    open fun loadFromJSON(json: JsonObject): PCBox {
        for (slot in 0 until POKEMON_PER_BOX) {
            if (json.has(DataKeys.STORE_SLOT + slot)) {
                pokemon[slot] = Pokemon().loadFromJSON(json.getAsJsonObject(DataKeys.STORE_SLOT + slot))
            }
        }
        return this
    }

    open fun loadFromNBT(nbt: NbtCompound): PCBox {
        for (slot in 0 until POKEMON_PER_BOX) {
            if (nbt.contains(DataKeys.STORE_SLOT + slot)) {
                pokemon[slot] = Pokemon().loadFromNBT(nbt.getCompound(DataKeys.STORE_SLOT + slot))
            }
        }
        return this
    }

    fun getNonEmptySlots() = (0 until POKEMON_PER_BOX).filter { get(it) != null }.associateWith { get(it)!! }
}