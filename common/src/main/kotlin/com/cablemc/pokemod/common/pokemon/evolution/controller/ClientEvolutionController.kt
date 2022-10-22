/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.pokemon.evolution.controller

import com.cablemc.pokemod.common.PokemodNetwork.sendToServer
import com.cablemc.pokemod.common.api.pokemon.evolution.EvolutionController
import com.cablemc.pokemod.common.api.pokemon.evolution.EvolutionDisplay
import com.cablemc.pokemod.common.net.messages.client.pokemon.update.evolution.EvolutionUpdatePacket
import com.cablemc.pokemod.common.net.messages.server.pokemon.update.evolution.AcceptEvolutionPacket
import com.cablemc.pokemod.common.pokemon.Pokemon
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.network.PacketByteBuf

class ClientEvolutionController(override val pokemon: Pokemon) : EvolutionController<EvolutionDisplay> {

    private val evolutions = hashSetOf<EvolutionDisplay>()

    override val size: Int
        get() = this.evolutions.size

    override fun start(evolution: EvolutionDisplay) {
        sendToServer(AcceptEvolutionPacket(this.pokemon, evolution))
    }

    override fun saveToNBT(): NbtElement {
        return NbtCompound()
    }

    override fun loadFromNBT(nbt: NbtElement) {
        // Nothing is done on the client
    }

    override fun saveToJson(): JsonElement {
        return JsonArray()
    }

    override fun loadFromJson(json: JsonElement) {
        // Nothing is done on the client
    }

    override fun saveToBuffer(buffer: PacketByteBuf, toClient: Boolean) {
        // Nothing is done on the client
    }

    override fun loadFromBuffer(buffer: PacketByteBuf) {
       repeat(buffer.readInt()) {
            val display = EvolutionUpdatePacket.decodeSending(buffer)
            this.add(display)
       }
    }

    override fun add(element: EvolutionDisplay) = this.evolutions.add(element)

    override fun addAll(elements: Collection<EvolutionDisplay>) = this.evolutions.addAll(elements)

    override fun clear() {
        this.evolutions.clear()
    }

    override fun iterator() = this.evolutions.iterator()

    override fun remove(element: EvolutionDisplay) = this.evolutions.remove(element)

    override fun removeAll(elements: Collection<EvolutionDisplay>) = this.evolutions.removeAll(elements.toSet())

    override fun retainAll(elements: Collection<EvolutionDisplay>) = this.evolutions.retainAll(elements.toSet())

    override fun contains(element: EvolutionDisplay) = this.evolutions.contains(element)

    override fun containsAll(elements: Collection<EvolutionDisplay>) = this.evolutions.containsAll(elements)

    override fun isEmpty() = this.evolutions.isEmpty()

}