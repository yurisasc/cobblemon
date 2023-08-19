package com.cobblemon.mod.common.api.npc.partyproviders

import com.cobblemon.mod.common.api.npc.NPCPartyProvider
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.storage.party.PartyStore
import com.cobblemon.mod.common.entity.npc.NPCEntity
import com.cobblemon.mod.common.net.IntSize
import com.cobblemon.mod.common.util.DataKeys
import com.cobblemon.mod.common.util.readSizedInt
import com.cobblemon.mod.common.util.writeSizedInt
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity

class SimplePartyProvider : NPCPartyProvider {
    companion object {
        const val TYPE = "simple"
    }

    @Transient
    override val type = TYPE

    val pokemon = mutableListOf<PokemonProperties>()

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeSizedInt(IntSize.U_BYTE, pokemon.size)
        for (pokemon in this.pokemon) {
            buffer.writeString(pokemon.originalString)
        }
    }

    override fun decode(buffer: PacketByteBuf) {
        repeat(times = buffer.readSizedInt(IntSize.U_BYTE)) {
            pokemon.add(PokemonProperties.parse(buffer.readString()))
        }
    }

    override fun saveToNBT(nbt: NbtCompound) {
        for ((index, pokemon) in this.pokemon.withIndex()) {
            val pokemonNBT = pokemon.saveToNBT()
            nbt.put(DataKeys.POKEMON_PROPERTIES + index, pokemonNBT)
        }
    }

    override fun loadFromNBT(nbt: NbtCompound) {
        var index = 0
        while (nbt.contains(DataKeys.POKEMON_PROPERTIES + index)) {
            this.pokemon.add(PokemonProperties().loadFromNBT(nbt.getCompound(DataKeys.POKEMON_PROPERTIES + index)))
            index++
        }
    }

    override fun provide(npc: NPCEntity, challengers: List<ServerPlayerEntity>): PartyStore {
        return PartyStore(npc.uuid).apply {
            for (properties in pokemon) {
                add(properties.create())
            }
        }
    }
}