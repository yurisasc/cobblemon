/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pokedex

import com.cobblemon.mod.common.api.pokedex.adapter.GlobalTrackedDataAdapter
import com.cobblemon.mod.common.api.pokedex.trackeddata.GlobalTrackedData
import com.cobblemon.mod.common.api.storage.player.PlayerInstancedDataStoreType
import com.cobblemon.mod.common.api.storage.player.client.ClientInstancedPlayerData
import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.net.messages.client.SetClientPlayerDataPacket
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

/**
 * Clientside representation of the Pokedex
 *
 * @author Apion
 * @since February 24, 2024
 */
class ClientPokedex(
    val speciesEntries: Map<Identifier, SpeciesPokedexEntry>,
    val globalTrackedData: Set<GlobalTrackedData>,
) : ClientInstancedPlayerData(false) {
    override fun encode(buf: PacketByteBuf) {
        buf.writeInt(speciesEntries.size)
        speciesEntries.forEach {
            buf.writeIdentifier(it.key)
            encodeSpeciesEntry(buf, it.value)
        }
        buf.writeInt(globalTrackedData.size)
        globalTrackedData.forEach {
            GlobalTrackedDataAdapter.bufSerialize(buf, it)
        }
    }

    companion object {
        fun encodeSpeciesEntry(buf: PacketByteBuf, speciesEntry: SpeciesPokedexEntry) {
            buf.writeInt(speciesEntry.formEntries.size)
            speciesEntry.formEntries.forEach {
                buf.writeString(it.key)
                encodeFormEntry(buf, it.value)
            }
        }

        fun encodeFormEntry(buf: PacketByteBuf, formEntry: FormPokedexEntry) {
            buf.writeEnumConstant(formEntry.knowledge)
        }

        fun decodeSpeciesEntry(buf: PacketByteBuf): SpeciesPokedexEntry {
            val formEntries = mutableMapOf<String, FormPokedexEntry>()
            val numForms = buf.readInt()
            for (i in 1..numForms) {
                val formStr = buf.readString()
                val formEntry = decodeFormEntry(buf)
                formEntries[formStr] = formEntry
            }
            val result = SpeciesPokedexEntry()
            result.formEntries = formEntries
            return result
        }

        fun decodeFormEntry(buf: PacketByteBuf): FormPokedexEntry {
            val knowledge = buf.readEnumConstant(PokedexProgress::class.java)
            val result = FormPokedexEntry()
            result.knowledge = knowledge
            return FormPokedexEntry()
        }

        fun decode(buf: PacketByteBuf): SetClientPlayerDataPacket {
            val speciesMap = mutableMapOf<Identifier, SpeciesPokedexEntry>()
            val statSet = mutableSetOf<GlobalTrackedData>()
            val numSpecies = buf.readInt()
            for (i in 1..numSpecies) {
                val speciesId = buf.readIdentifier()
                val speciesEntry = decodeSpeciesEntry(buf)
                speciesMap[speciesId] = speciesEntry
            }
            val numStats = buf.readInt()
            for (i in 1..numStats) {
                statSet.add(GlobalTrackedDataAdapter.bufDeserialize(buf))
            }
            val clientPokedex = ClientPokedex(speciesMap, statSet)
            return SetClientPlayerDataPacket(PlayerInstancedDataStoreType.POKEDEX, clientPokedex)
        }

        fun afterDecodeAction(data: ClientInstancedPlayerData) {
            if (data !is ClientPokedex) return
            CobblemonClient.clientPokedexData = data
        }
    }

}