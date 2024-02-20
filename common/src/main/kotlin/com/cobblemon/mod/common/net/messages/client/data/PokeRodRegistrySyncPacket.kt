package com.cobblemon.mod.common.net.messages.client.data

import com.cobblemon.mod.common.api.fishing.PokeRods
import com.cobblemon.mod.common.fishing.PokeRod
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

class PokeRodRegistrySyncPacket(rods: Collection<PokeRod>) : DataRegistrySyncPacket<PokeRod, PokeRodRegistrySyncPacket>(rods) {

    companion object {
        val ID = cobblemonResource("pokerod_sync")

        fun decode(buffer: PacketByteBuf) = PokeRodRegistrySyncPacket(emptyList()).apply { decodeBuffer(buffer) }
    }

    override val id = ID

    override fun encodeEntry(buffer: PacketByteBuf, entry: PokeRod) {
        entry.encode(buffer)
    }

    override fun decodeEntry(buffer: PacketByteBuf) = PokeRod.decode(buffer)

    override fun synchronizeDecoded(entries: Collection<PokeRod>) {
        PokeRods.reload(entries.associateBy { it.name!! })
    }
}