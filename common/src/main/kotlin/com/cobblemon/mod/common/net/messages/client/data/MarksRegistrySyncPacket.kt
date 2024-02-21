package com.cobblemon.mod.common.net.messages.client.data

import com.cobblemon.mod.common.api.pokemon.marks.PokemonMark
import com.cobblemon.mod.common.api.pokemon.marks.PokemonMarks
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.PacketByteBuf

class MarksRegistrySyncPacket(marks: List<PokemonMark>): DataRegistrySyncPacket<PokemonMark, MarksRegistrySyncPacket>(marks) {

    companion object {
        val ID = cobblemonResource("marks")
        fun decode(buffer: PacketByteBuf) = MarksRegistrySyncPacket(emptyList()).apply { decodeBuffer(buffer) }
    }

    override val id = ID
    override fun encodeEntry(buffer: PacketByteBuf, entry: PokemonMark) {
        buffer.writeIdentifier(entry.identifier)
        buffer.writeString(entry.name)
        buffer.writeIdentifier(entry.icon)
        buffer.writeString(entry.title)
    }

    override fun decodeEntry(buffer: PacketByteBuf): PokemonMark {
        return PokemonMark(
            id = buffer.readIdentifier(),
            name = buffer.readString(),
            icon = buffer.readIdentifier(),
            title = buffer.readString()
        )
    }

    override fun synchronizeDecoded(entries: Collection<PokemonMark>) {
        PokemonMarks.reload(entries.associateBy { it.identifier })
    }

}