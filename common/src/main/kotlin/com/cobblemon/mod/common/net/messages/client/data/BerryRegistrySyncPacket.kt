package com.cobblemon.mod.common.net.messages.client.data

import com.cobblemon.mod.common.api.berry.Berries
import com.cobblemon.mod.common.api.berry.Berry
import net.minecraft.network.PacketByteBuf

class BerryRegistrySyncPacket(registryEntries: Collection<Berry>) : DataRegistrySyncPacket<Berry>(registryEntries) {

    constructor() : this(emptyList())

    override fun encodeEntry(buffer: PacketByteBuf, entry: Berry) {
        entry.encode(buffer)
    }

    override fun decodeEntry(buffer: PacketByteBuf) = Berry.decode(buffer)

    override fun synchronizeDecoded(entries: Collection<Berry>) {
        Berries.reload(entries.associateBy { it.identifier })
    }

}