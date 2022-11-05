/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.evolution.controller

import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.pokemon.evolution.EvolutionAcceptedEvent
import com.cobblemon.mod.common.api.pokemon.evolution.Evolution
import com.cobblemon.mod.common.api.pokemon.evolution.EvolutionController
import com.cobblemon.mod.common.api.text.green
import com.cobblemon.mod.common.net.messages.client.pokemon.update.evolution.AddEvolutionPacket
import com.cobblemon.mod.common.net.messages.client.pokemon.update.evolution.ClearEvolutionsPacket
import com.cobblemon.mod.common.net.messages.client.pokemon.update.evolution.EvolutionUpdatePacket
import com.cobblemon.mod.common.net.messages.client.pokemon.update.evolution.RemoveEvolutionPacket
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.asTranslated
import com.cobblemon.mod.common.util.toJsonArray
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString
import net.minecraft.network.PacketByteBuf
class ServerEvolutionController(override val pokemon: Pokemon) : EvolutionController<Evolution> {

    private val evolutions = hashSetOf<Evolution>()

    override val size: Int
        get() = this.evolutions.size

    override fun start(evolution: Evolution) {
        CobblemonEvents.EVOLUTION_ACCEPTED.postThen(
            event = EvolutionAcceptedEvent(this.pokemon, evolution),
            ifSucceeded = {
                // Evolution will clear the pending stuff after if successful
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
            this.pokemon.getOwnerPlayer()?.sendMessage("cobblemon.ui.evolve.hint".asTranslated(pokemon.displayName).green())
            this.pokemon.notify(AddEvolutionPacket(this.pokemon, element))
            return true
        }
        return false
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
        // We don't want to send unnecessary packets
        if (this.evolutions.isNotEmpty()) {
            this.evolutions.clear()
            this.pokemon.notify(ClearEvolutionsPacket(this.pokemon))
        }
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