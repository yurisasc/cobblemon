package com.cobblemon.mod.common.api.storage.pokedex

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.api.storage.BottomlessStore
import com.cobblemon.mod.common.api.storage.PokemonStore
import com.cobblemon.mod.common.api.storage.StoreCoordinates
import com.cobblemon.mod.common.api.storage.pc.PCBox
import com.cobblemon.mod.common.api.storage.pc.PCPosition
import com.cobblemon.mod.common.api.storage.pc.POKEMON_PER_BOX
import com.cobblemon.mod.common.net.messages.client.storage.RemoveClientPokemonPacket
import com.cobblemon.mod.common.net.messages.client.storage.SwapClientPokemonPacket
import com.cobblemon.mod.common.net.messages.client.storage.pc.InitializePCPacket
import com.cobblemon.mod.common.net.messages.client.storage.pc.MoveClientPCPokemonPacket
import com.cobblemon.mod.common.net.messages.client.storage.pc.SetPCPokemonPacket
import com.cobblemon.mod.common.net.messages.client.storage.pokedex.InitializePokedexPacket
import com.cobblemon.mod.common.net.messages.client.storage.pokedex.SetPokedexEntriesPacket
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.DataKeys
import com.cobblemon.mod.common.util.getPlayer
import com.cobblemon.mod.common.util.lang
import com.google.gson.JsonObject
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.MutableText
import net.minecraft.util.Identifier
import java.util.*

class PokedexStore (
        final val uuid: UUID,
        val name: MutableText
){
    constructor(uuid: UUID): this(uuid, lang("your_pokedex"))

    val entries = mutableMapOf<Identifier, PokedexEntry>()
    val observingUUIDs = mutableSetOf(uuid)
    fun getObservingPlayers() = observingUUIDs.mapNotNull { it.getPlayer() }
    fun addObserver(player: ServerPlayerEntity) {
        observingUUIDs.add(player.uuid)
        sendTo(player)
    }
    fun removeObserver(playerID: UUID) {
        observingUUIDs.remove(playerID)
    }

    val pokedexChangeObservable = SimpleObservable<Unit>()

    fun sendTo(player: ServerPlayerEntity) {
        InitializePokedexPacket(this).sendToPlayer(player)
        SetPokedexEntriesPacket(this).sendToPlayer(player)
    }

    /*fun initialize() {
        entries.forEach { it.initialize() }
    }*/

    /*override fun saveToNBT(nbt: NbtCompound): NbtCompound {
        nbt.putShort(DataKeys.STORE_BOX_COUNT, boxes.size.toShort())
        nbt.putBoolean(DataKeys.STORE_BOX_COUNT_LOCKED, lockedSize)
        boxes.forEachIndexed { index, box ->
            nbt.put(DataKeys.STORE_BOX + index, box.saveToNBT(NbtCompound()))
        }
        nbt.put(DataKeys.STORE_BACKUP, backupStore.saveToNBT(NbtCompound()))
        return nbt
    }*/

    /*override fun loadFromNBT(nbt: NbtCompound): PokedexStore {
        val boxCountStored = nbt.getShort(DataKeys.STORE_BOX_COUNT)
        for (boxNumber in 0 until boxCountStored) {
            boxes.add(PCBox(this).loadFromNBT(nbt.getCompound(DataKeys.STORE_BOX + boxNumber)))
        }
        lockedSize = nbt.getBoolean(DataKeys.STORE_BOX_COUNT_LOCKED)
        if (!lockedSize && boxes.size != Cobblemon.config.defaultBoxCount) {
            resize(Cobblemon.config.defaultBoxCount, lockNewSize = false)
        } else {
            tryRestoreBackedUpPokemon()
        }

        removeDuplicates()

        return this
    }*/

    fun saveToJSON(json: JsonObject): JsonObject {
        entries.forEach { (identifier, pokedexEntry) ->
            json.add(identifier.toString(), pokedexEntry.saveToJSON(JsonObject()))
        }
        return json
    }

    /*fun remove(identifier: Identifier): Boolean {
        sendPacketToObservers(RemoveClientPokedexPacket(this, identifier))
    }*/

    fun loadFromJSON(json: JsonObject): PokedexStore {
        json.asMap().forEach{(identifierString, pokedexEntryJson) ->
            val identifier = Identifier(identifierString)
            entries[identifier] = PokedexEntry(identifier).loadFromJson(pokedexEntryJson.asJsonObject)
        }

        return this
    }

    fun getAnyChangeObservable() = pokedexChangeObservable

    operator fun get(identifier: Identifier): PokedexEntry? {
        return entries[identifier]
    }

    /*operator fun set(identifier: Identifier, entry : PokedexEntry) {
        entries[identifier] = entry
        sendPacketToObservers(SetPokedexPacket(uuid, identifier, entry))
    }*/

    fun clearPokedex() {
        entries.clear()
    }
}