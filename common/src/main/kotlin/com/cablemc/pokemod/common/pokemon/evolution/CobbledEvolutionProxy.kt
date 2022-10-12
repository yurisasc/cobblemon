/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.pokemon.evolution

import com.cablemc.pokemod.common.api.pokemon.evolution.Evolution
import com.cablemc.pokemod.common.api.pokemon.evolution.EvolutionController
import com.cablemc.pokemod.common.api.pokemon.evolution.EvolutionDisplay
import com.cablemc.pokemod.common.api.pokemon.evolution.EvolutionLike
import com.cablemc.pokemod.common.api.pokemon.evolution.EvolutionProxy
import com.cablemc.pokemod.common.pokemon.Pokemon
import com.cablemc.pokemod.common.pokemon.evolution.controller.ClientEvolutionController
import com.cablemc.pokemod.common.pokemon.evolution.controller.ServerEvolutionController
import com.cablemc.pokemod.common.util.DataKeys
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.network.PacketByteBuf

class CobbledEvolutionProxy(private val pokemon: Pokemon, private val clientSide: Boolean) : EvolutionProxy<EvolutionDisplay, Evolution> {

    private val controller = if (this.clientSide) ClientEvolutionController(this.pokemon) else ServerEvolutionController(this.pokemon)

    override fun isClient(): Boolean = this.clientSide

    override fun current(): EvolutionController<out EvolutionLike> = this.controller

    override fun client(): EvolutionController<EvolutionDisplay> {
        return this.controller as? EvolutionController<EvolutionDisplay> ?: throw ClassCastException("Cannot use the client implementation from the server side")
    }

    override fun server(): EvolutionController<Evolution> {
        return this.controller as? EvolutionController<Evolution> ?: throw ClassCastException("Cannot use the server implementation from the client side")
    }

    override fun saveToNBT(): NbtElement {
        val nbt = NbtCompound()
        nbt.put(DataKeys.POKEMON_PENDING_EVOLUTIONS, this.current().saveToNBT())
        return nbt
    }

    override fun loadFromNBT(nbt: NbtElement) {
        val compound = nbt as? NbtCompound ?: return
        this.current().loadFromNBT(compound.get(DataKeys.POKEMON_PENDING_EVOLUTIONS) ?: return)
    }

    override fun saveToJson(): JsonElement {
        val json = JsonObject()
        json.add(DataKeys.POKEMON_PENDING_EVOLUTIONS, this.current().saveToJson())
        return json
    }

    override fun loadFromJson(json: JsonElement) {
        val jObject = json as? JsonObject ?: return
        this.current().loadFromJson(jObject.get(DataKeys.POKEMON_PENDING_EVOLUTIONS))
    }

    override fun saveToBuffer(buffer: PacketByteBuf, toClient: Boolean) {
        this.current().saveToBuffer(buffer, toClient)
    }

    override fun loadFromBuffer(buffer: PacketByteBuf) {
        this.current().loadFromBuffer(buffer)
    }
}