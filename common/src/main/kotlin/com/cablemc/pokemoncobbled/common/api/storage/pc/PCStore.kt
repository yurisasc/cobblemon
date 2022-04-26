package com.cablemc.pokemoncobbled.common.api.storage.pc

import com.cablemc.pokemoncobbled.common.api.reactive.Observable
import com.cablemc.pokemoncobbled.common.api.storage.PokemonStore
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.DataKeys
import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

class PCStore(override val uuid: UUID) : PokemonStore<PCPosition>() {
    val boxes = mutableListOf<PCBox>()
    override fun iterator() = boxes.flatMap { it.toList() }.iterator()

    val pcChangeObservable: Observable

    override fun getFirstAvailablePosition(): PCPosition? {
        boxes.forEach { it.getFirstAvailablePosition()?.let { return it } }
        return null
    }

    override fun getObservingPlayers(): Iterable<ServerPlayer> {
        TODO("Not yet implemented")
    }

    override fun sendTo(player: ServerPlayer) {
        TODO("Not yet implemented")
    }

    override fun initialize() {
        TODO("Not yet implemented")
    }

    override fun saveToNBT(nbt: CompoundTag): CompoundTag {
        DataKeys
        TODO("Not yet implemented")
    }

    override fun loadFromNBT(nbt: CompoundTag): PokemonStore<PCPosition> {
        TODO("Not yet implemented")
    }

    override fun saveToJSON(json: JsonObject): JsonObject {
        TODO("Not yet implemented")
    }

    override fun loadFromJSON(json: JsonObject): PokemonStore<PCPosition> {
        TODO("Not yet implemented")
    }

    override fun getAnyChangeObservable(): Observable<Unit> {
        TODO("Not yet implemented")
    }

    override fun setAtPosition(position: PCPosition, pokemon: Pokemon?) {
        TODO("Not yet implemented")
    }

    override fun get(position: PCPosition): Pokemon? {
        TODO("Not yet implemented")
    }
}