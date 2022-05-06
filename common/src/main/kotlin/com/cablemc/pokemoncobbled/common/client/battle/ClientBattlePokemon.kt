package com.cablemc.pokemoncobbled.common.client.battle

import net.minecraft.network.PacketByteBuf
import net.minecraft.text.MutableText

abstract class ClientBattlePokemon {
    abstract fun getDisplayName(): MutableText
    abstract fun getHealthRatio(): Float
    abstract fun loadFromBuffer(buffer: PacketByteBuf)
}