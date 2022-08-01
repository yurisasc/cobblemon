package com.cablemc.pokemoncobbled.common.net.messages.client.ui

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.config.starter.RenderableStarterCategory
import com.cablemc.pokemoncobbled.common.config.starter.StarterCategory
import com.cablemc.pokemoncobbled.common.pokemon.RenderablePokemon
import net.minecraft.network.PacketByteBuf

class StarterUIPacket internal constructor() : NetworkPacket {

    constructor(categories: List<StarterCategory>) : this() {
        this.categories.addAll(categories.map { it.asRenderableStarterCategory() })
    }

    val categories = mutableListOf<RenderableStarterCategory>()

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeInt(categories.size)
        categories.forEach {
            buffer.writeString(it.name)
            buffer.writeString(it.displayName)
            buffer.writeInt(it.pokemon.size)
            it.pokemon.forEach { it.saveToBuffer(buffer) }
        }
    }

    override fun decode(buffer: PacketByteBuf) {
        val numCategories = buffer.readInt()
        for (i in 0 until numCategories) {
            val name = buffer.readString()
            val displayName = buffer.readString()
            val numProperties = buffer.readInt()
            val renderablePokemon = mutableListOf<RenderablePokemon>()
            repeat(times = numProperties) {
                renderablePokemon.add(RenderablePokemon.loadFromBuffer(buffer))
            }
            categories.add(
                RenderableStarterCategory(
                    name = name,
                    displayName = displayName,
                    pokemon = renderablePokemon
                )
            )
        }
    }
}