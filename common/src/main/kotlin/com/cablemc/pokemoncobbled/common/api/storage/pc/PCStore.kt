package com.cablemc.pokemoncobbled.common.api.storage.pc

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.reactive.SimpleObservable
import com.cablemc.pokemoncobbled.common.api.storage.BottomlessStore
import com.cablemc.pokemoncobbled.common.api.storage.PokemonStore
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.RemoveClientPokemonPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.SwapClientPokemonPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.pc.InitializePCPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.pc.MoveClientPCPokemonPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.pc.SetPCPokemonPacket
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.DataKeys
import com.cablemc.pokemoncobbled.common.util.getPlayer
import com.google.gson.JsonObject
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity
import java.util.UUID

/**
 * The store used for PCs. It is divided into some number of [PCBox]es, and can
 * handle overflow into a [BottomlessStore].
 *
 * The overflow is reserved only for when the number of PC boxes changes and there
 * is existing data that no longer fits. Using it for handling [getFirstAvailablePosition]
 * would be a bad idea because it would make it easy to create an explosively large
 * store by a bad actor.
 *
 * @author Hiroku
 * @since April 26th, 2022
 */
open class PCStore(override val uuid: UUID) : PokemonStore<PCPosition>() {
    val boxes = mutableListOf<PCBox>()
    protected var lockedSize = false
    val backupStore = BottomlessStore(UUID(0L, 0L))
    val observingUUIDs = mutableListOf(uuid)
    override fun iterator() = boxes.flatMap { it.toList() }.iterator()
    override fun getObservingPlayers() = observingUUIDs.mapNotNull { it.getPlayer() }

    val pcChangeObservable = SimpleObservable<Unit>()

    override fun getFirstAvailablePosition(): PCPosition? {
        boxes.forEach { it.getFirstAvailablePosition()?.let { return it } }
        return null
    }

    override fun sendTo(player: ServerPlayerEntity) {
        InitializePCPacket(this).sendToPlayer(player)
        boxes.forEach { it.sendTo(player) }
    }

    override fun initialize() {
        boxes.forEach { it.initialize() }
        backupStore.initialize()
    }

    fun relocateEvictedBoxPokemon(pokemon: Pokemon) {
        val space = getFirstAvailablePosition()
        if (space != null) {
            this[space] = pokemon
        } else {
            backupStore.add(pokemon)
        }
    }

    fun resize(newSize: Int, lockNewSize: Boolean = false, overflowHandler: (Pokemon) -> Unit = ::relocateEvictedBoxPokemon) {
        if (newSize <= 0) {
            throw java.lang.IllegalArgumentException("Invalid box count: Must be greater than zero.")
        }

        this.lockedSize = lockNewSize
        if (boxes.size > newSize) {
            // reduce
            val slicedBoxes = boxes.slice(boxes.size until newSize)
            boxes.removeAll(slicedBoxes)
            slicedBoxes.flatMap { it.asIterable() }.forEach(overflowHandler)
        } else {
            // expand
            while (boxes.size < newSize) {
                this.boxes.add(PCBox(this))
            }

            tryRestoreBackedUpPokemon()
        }
        pcChangeObservable.emit(Unit)
    }

    fun tryRestoreBackedUpPokemon() {
        var newPosition = getFirstAvailablePosition()
        val backedUpPokemon = backupStore.pokemon.toMutableList()
        while (newPosition != null && backedUpPokemon.isNotEmpty()) {
            this[newPosition] = backedUpPokemon.removeAt(0)
            newPosition = getFirstAvailablePosition()
        }
    }

    override fun saveToNBT(nbt: NbtCompound): NbtCompound {
        nbt.putShort(DataKeys.STORE_BOX_COUNT, boxes.size.toShort())
        nbt.putBoolean(DataKeys.STORE_BOX_COUNT_LOCKED, lockedSize)
        boxes.forEachIndexed { index, box ->
            nbt.put(DataKeys.STORE_BOX + index, box.saveToNBT(NbtCompound()))
        }
        nbt.put(DataKeys.STORE_BACKUP, backupStore.saveToNBT(NbtCompound()))
        return nbt
    }

    override fun loadFromNBT(nbt: NbtCompound): PokemonStore<PCPosition> {
        val boxCountStored = nbt.getShort(DataKeys.STORE_BOX_COUNT)
        for (boxNumber in 0 until boxCountStored) {
            boxes.add(PCBox(this).loadFromNBT(nbt.getCompound(DataKeys.STORE_BOX + boxNumber)))
        }
        lockedSize = nbt.getBoolean(DataKeys.STORE_BOX_COUNT_LOCKED)
        if (!lockedSize && boxes.size != PokemonCobbled.config.defaultBoxCount) {
            resize(PokemonCobbled.config.defaultBoxCount, lockNewSize = false)
        } else {
            tryRestoreBackedUpPokemon()
        }
        return this
    }

    override fun saveToJSON(json: JsonObject): JsonObject {
        json.addProperty(DataKeys.STORE_BOX_COUNT, boxes.size.toShort())
        json.addProperty(DataKeys.STORE_BOX_COUNT_LOCKED, lockedSize)
        boxes.forEachIndexed { index, box ->
            json.add(DataKeys.STORE_BOX + index, box.saveToJSON(JsonObject()))
        }
        json.add(DataKeys.STORE_BACKUP, backupStore.saveToJSON(JsonObject()))
        return json
    }

    override operator fun set(position: PCPosition, pokemon: Pokemon) {
        super.set(position, pokemon)
        sendPacketToObservers(SetPCPokemonPacket(uuid, position, pokemon))
    }

    override fun remove(pokemon: Pokemon): Boolean {
        return if (super.remove(pokemon)) {
            sendPacketToObservers(RemoveClientPokemonPacket(this, pokemon.uuid))
            true
        } else {
            false
        }
    }

    override fun swap(position1: PCPosition, position2: PCPosition) {
        val pokemon1 = get(position1)
        val pokemon2 = get(position2)
        super.swap(position1, position2)
        if (pokemon1 != null && pokemon2 != null) {
            sendPacketToObservers(SwapClientPokemonPacket(this, pokemon1.uuid, pokemon2.uuid))
        } else if (pokemon1 != null || pokemon2 != null) {
            val newPosition = if (pokemon1 == null) position1 else position2
            val pokemon = pokemon1 ?: pokemon2!!
            sendPacketToObservers(MoveClientPCPokemonPacket(uuid, pokemon.uuid, newPosition))
        }
    }

    override fun loadFromJSON(json: JsonObject): PokemonStore<PCPosition> {
        val boxCountStored = json.get(DataKeys.STORE_BOX_COUNT).asShort
        for (boxNumber in 0 until boxCountStored) {
            boxes.add(PCBox(this).loadFromJSON(json.getAsJsonObject(DataKeys.STORE_BOX + boxNumber)))
        }
        lockedSize = json.get(DataKeys.STORE_BOX_COUNT_LOCKED).asBoolean
        if (!lockedSize && boxes.size != PokemonCobbled.config.defaultBoxCount) {
            resize(newSize = PokemonCobbled.config.defaultBoxCount, lockNewSize = false)
        } else {
            tryRestoreBackedUpPokemon()
        }
        return this
    }

    override fun getAnyChangeObservable() = pcChangeObservable

    override fun setAtPosition(position: PCPosition, pokemon: Pokemon?) {
        if (position.box !in 0 until boxes.size) {
            throw IllegalArgumentException("Invalid box number ${position.box}. Should be between 0 and ${boxes.size}")
        }
        boxes[position.box][position.slot] = pokemon
    }

    override operator fun get(position: PCPosition): Pokemon? {
        return if (position.box !in 0 until boxes.size) {
            null
        } else {
            boxes[position.box][position.slot]
        }
    }
}