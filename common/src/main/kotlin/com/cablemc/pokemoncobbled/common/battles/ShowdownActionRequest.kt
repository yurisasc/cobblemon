package com.cablemc.pokemoncobbled.common.battles

import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.util.readSizedInt
import com.cablemc.pokemoncobbled.common.util.writeSizedInt
import net.minecraft.network.PacketByteBuf
import java.util.UUID

class ShowdownActionRequest(
    var wait: Boolean = false,
    var active: List<ShowdownMoveset>? = null,
    var forceSwitch: List<Boolean> = emptyList(),
    var noCancel: Boolean = false,
    var side: ShowdownSide? = null
) {
    fun saveToBuffer(buffer: PacketByteBuf) {
        buffer.writeSizedInt(IntSize.U_BYTE, active?.size ?: 0)
        active?.forEach {

        }
    }

    fun loadFromBuffer(buffer: PacketByteBuf) {

    }
}

class ShowdownMoveset {
    lateinit var moves: List<InBattleMove>
    fun saveToBuffer(buffer: PacketByteBuf) {
        buffer.writeSizedInt(IntSize.U_BYTE, moves.size)
        moves.forEach { }// TODO }
    }

    fun loadFromBuffer(buffer: PacketByteBuf): ShowdownMoveset {

        return this
    }
}

class ShowdownSide {
    lateinit var name: UUID
    lateinit var id: String
    lateinit var pokemon: List<ShowdownPokemon>
    fun saveToBuffer(buffer: PacketByteBuf) {
        buffer.writeUuid(name)
        buffer.writeString(id)
        buffer.writeSizedInt(IntSize.U_BYTE, pokemon.size)
        pokemon.forEach { it.saveToBuffer(buffer) }
    }
    fun loadFromBuffer(buffer: PacketByteBuf): ShowdownSide {
        name = buffer.readUuid()
        id = buffer.readString()
        val pokemon = mutableListOf<ShowdownPokemon>()
        repeat(times = buffer.readSizedInt(IntSize.U_BYTE)) {
            pokemon.add(ShowdownPokemon().loadFromBuffer(buffer))
        }
        this.pokemon = pokemon
        return this
    }
}

class ShowdownPokemon {
    lateinit var ident: String
    lateinit var details: String
    lateinit var condition: String
    var active: Boolean = false
    val moves = mutableListOf<String>()
    lateinit var baseAbility: String
    lateinit var pokeball: String
    lateinit var ability: String
    fun saveToBuffer(buffer: PacketByteBuf) {
        buffer.writeString(ident)
        buffer.writeString(details)
        buffer.writeString(condition)
        buffer.writeBoolean(active)
        buffer.writeSizedInt(IntSize.U_BYTE, moves.size)
        moves.forEach(buffer::writeString)
        buffer.writeString(baseAbility)
        buffer.writeString(pokeball)
        buffer.writeString(ability)

    }
    fun loadFromBuffer(buffer: PacketByteBuf): ShowdownPokemon {
        ident = buffer.readString()
        details = buffer.readString()
        condition = buffer.readString()
        active = buffer.readBoolean()
        repeat(times = buffer.readSizedInt(IntSize.U_BYTE)) {
            moves.add(buffer.readString())
        }
        baseAbility = buffer.readString()
        pokeball = buffer.readString()
        ability = buffer.readString()
        return this
    }
}