package com.cablemc.pokemod.common.api.data

import net.minecraft.network.PacketByteBuf

interface ClientDataSynchronizer<T> {

    fun shouldSynchronize(other: T): Boolean

    fun decode(buffer: PacketByteBuf)

    fun encode(buffer: PacketByteBuf)

}