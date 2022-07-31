package com.cablemc.pokemoncobbled.common.net.messages.client.ui

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.config.starter.StarterCategory
import net.minecraft.network.PacketByteBuf

class StarterUIPacket internal constructor() : NetworkPacket {

    constructor(vararg categories: StarterCategory) : this() {
        this.categories.addAll(categories)
    }

    constructor(categories: List<StarterCategory>) : this() {
        this.categories.addAll(categories)
    }

    val categories = mutableListOf<StarterCategory>()

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeInt(categories.size)
        categories.forEach {
            buffer.writeString(it.name)
            buffer.writeText(it.displayName)
            buffer.writeInt(it.pokemon.size)
            it.pokemon.forEach { properties ->
                buffer.writeNbt(properties.saveToNBT())
            }
        }
    }

    override fun decode(buffer: PacketByteBuf) {
        val numCategories = buffer.readInt()
        for (i in 0 until numCategories) {
            val name = buffer.readString()
            val displayName = buffer.readText()
            val numProperties = buffer.readInt()
            val properties = mutableListOf<PokemonProperties>()
            for (j in 0 until numProperties) {
                buffer.readNbt()?.let {
                    properties.add(PokemonProperties().loadFromNBT(it))
                }
            }
            categories.add(StarterCategory(
                name = name, displayName = displayName,
                pokemon = properties
            ))
        }
    }
}