package com.cablemc.pokemoncobbled.common.pokemon.evolution.controller

import com.cablemc.pokemoncobbled.common.api.events.CobbledEvents
import com.cablemc.pokemoncobbled.common.api.events.pokemon.evolution.EvolutionAcceptedEvent
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.Evolution
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.EvolutionController
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update.evolution.AddEvolutionPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update.evolution.ClearEvolutionsPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update.evolution.EvolutionUpdatePacket
import com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update.evolution.RemoveEvolutionPacket
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.ifClient
import com.cablemc.pokemoncobbled.common.util.readSizedInt
import com.cablemc.pokemoncobbled.common.util.toJsonArray
import com.cablemc.pokemoncobbled.common.util.writeSizedInt
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString
import net.minecraft.network.PacketByteBuf
import java.util.*

internal class CobbledServerEvolutionController(override val pokemon: Pokemon) : EvolutionController<Evolution> {

    private val evolutions = hashSetOf<Evolution>()

    override val size: Int
        get() = this.evolutions.size

    override fun start(evolution: Evolution) {
        CobbledEvents.EVOLUTION_ACCEPTED.postThen(
            event = EvolutionAcceptedEvent(this.pokemon, evolution),
            ifSucceeded = {
                this.clear()
                evolution.forceEvolve(this.pokemon)
            }
        )
    }

    override fun saveToNBT(): NbtElement {
        val list = NbtList()
        this.evolutions.forEach { evolution ->
            list += NbtString.of(evolution.id)
        }
        return list
    }

    override fun loadFromNBT(nbt: NbtElement) {
        val list = nbt as? NbtList ?: return
        this.clear()
        for (tag in list.filterIsInstance<NbtString>()) {
            val id = tag.asString()
            val evolution = this.findEvolutionFromId(id) ?: continue
            this.add(evolution)
        }
    }

    override fun saveToJson(): JsonElement = this.evolutions
        .map { evolution -> evolution.id }
        .toJsonArray()

    override fun loadFromJson(json: JsonElement) {
        for (element in json as? JsonArray ?: return) {
            val id = (element as? JsonPrimitive)?.asString ?: continue
            val evolution = this.findEvolutionFromId(id) ?: continue
            this.add(evolution)
        }
    }

    override fun saveToBuffer(buffer: PacketByteBuf, toClient: Boolean) {
        if (!toClient) {
            return
        }
        buffer.writeInt(this.size)
        this.evolutions.forEach { evolution ->
            val display = EvolutionUpdatePacket.createSending(this.pokemon, evolution)
            EvolutionUpdatePacket.encodeSending(display, buffer)
        }
    }

    override fun loadFromBuffer(buffer: PacketByteBuf) {
        // Nothing is done on the server
    }

    override fun add(element: Evolution): Boolean {
        if (this.evolutions.add(element)) {
            this.pokemon.notify(AddEvolutionPacket(this.pokemon, element))
            return true
        }
        return true
    }

    override fun addAll(elements: Collection<Evolution>): Boolean {
        var result = false
        elements.forEach { element ->
            if (this.add(element)) {
                result = true
            }
        }
        return result
    }


    override fun clear() {
        this.evolutions.clear()
        this.pokemon.notify(ClearEvolutionsPacket(this.pokemon))
    }

    override fun contains(element: Evolution) = this.evolutions.contains(element)

    override fun containsAll(elements: Collection<Evolution>) = this.evolutions.containsAll(elements)

    override fun isEmpty() = this.evolutions.isEmpty()

    override fun iterator() = this.evolutions.iterator()

    override fun remove(element: Evolution): Boolean {
        if (this.evolutions.remove(element)) {
            this.pokemon.notify(RemoveEvolutionPacket(this.pokemon, element))
            return true
        }
        return false
    }

    override fun removeAll(elements: Collection<Evolution>): Boolean {
        var result = false
        elements.forEach { element ->
            if (this.remove(element)) {
                result = true
            }
        }
        return result
    }

    override fun retainAll(elements: Collection<Evolution>): Boolean  {
        var result = false
        val comparedSet = elements.toSet()
        val iterator = this.iterator()
        while (iterator.hasNext()) {
            val element = iterator.next()
            if (!comparedSet.contains(element)) {
                iterator.remove()
                result = true
            }
        }
        return result
    }

    private fun findEvolutionFromId(id: String) = this.pokemon.evolutions
        .firstOrNull { evolution -> evolution.id.equals(id, true) }

}