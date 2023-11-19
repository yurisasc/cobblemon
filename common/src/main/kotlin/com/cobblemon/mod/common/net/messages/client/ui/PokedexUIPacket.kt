package com.cobblemon.mod.common.net.messages.client.ui

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.config.starter.RenderableStarterCategory
import com.cobblemon.mod.common.pokedex.PokedexEntry
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import net.minecraft.network.PacketByteBuf
import org.spongepowered.asm.mixin.Mutable
import java.util.*

class PokedexUIPacket internal constructor() : NetworkPacket {

    var pokedex: MutableList<PokedexEntry> = mutableListOf<PokedexEntry>()

    constructor(pokedex: MutableList<PokedexEntry>) : this(){
        this.pokedex = pokedex
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeInt(pokedex.size)
        pokedex.forEach {
            it.encode(buffer)
        }
    }

    override fun decode(buffer: PacketByteBuf) {
        val pokedexSize = buffer.readInt()
        for(i in 0 until pokedexSize){
            pokedex.add(PokedexEntry().decode(buffer))
        }
    }

}