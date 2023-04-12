/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.pasture

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.net.IntSize
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.readSizedInt
import com.cobblemon.mod.common.util.writeSizedInt
import java.util.UUID
import net.minecraft.network.PacketByteBuf
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos

/**
 * Opens a pasture GUI using the provided data.
 *
 * @author Hiroku
 * @since April 9th, 2023
 */
class OpenPasturePacket(val pcId: UUID, val pasturePos: BlockPos, val totalTethered: Int, val tetheredPokemon: List<TetherDataDTO>) : NetworkPacket<OpenPasturePacket> {
    class TetherDataDTO(
        val pokemonId: UUID,
        val name: Text,
        val species: Identifier,
        val aspects: Set<String>,
        val pcId: UUID,
        val entityKnown: Boolean
    )
    companion object {
        val ID = cobblemonResource("open_pasture")

        fun decode(buffer: PacketByteBuf): OpenPasturePacket {
            val pcId = buffer.readUuid()
            val pasturePos = BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt())
            val totalTethered = buffer.readSizedInt(IntSize.U_BYTE)
            val dtos = mutableListOf<TetherDataDTO>()
            repeat(times = buffer.readUnsignedByte().toInt()) {
                val pokemonId = buffer.readUuid()
                val name = buffer.readText()
                val species = buffer.readIdentifier()
                val aspects = buffer.readList { it.readString() }.toSet()
                val pcId = buffer.readUuid()
                val entityKnown = buffer.readBoolean()

                dtos.add(
                    TetherDataDTO(
                        pokemonId = pokemonId,
                        name = name,
                        species = species,
                        aspects = aspects,
                        pcId = pcId,
                        entityKnown = entityKnown
                    )
                )
            }
            return OpenPasturePacket(pcId, pasturePos, totalTethered, dtos)
        }
    }

    override val id = ID

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeUuid(pcId)
        buffer.writeInt(pasturePos.x)
        buffer.writeInt(pasturePos.y)
        buffer.writeInt(pasturePos.z)
        buffer.writeSizedInt(IntSize.U_BYTE, totalTethered)
        buffer.writeSizedInt(IntSize.U_BYTE, tetheredPokemon.size)
        for (tethered in tetheredPokemon) {
            buffer.writeUuid(tethered.pokemonId)
            buffer.writeText(tethered.name)
            buffer.writeIdentifier(tethered.species)
            buffer.writeCollection(tethered.aspects) { _, v -> buffer.writeString(v) }
            buffer.writeUuid(tethered.pcId)
            buffer.writeBoolean(tethered.entityKnown)
        }
    }
}