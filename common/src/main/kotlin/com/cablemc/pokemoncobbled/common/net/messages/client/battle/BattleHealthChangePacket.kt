package com.cablemc.pokemoncobbled.common.net.messages.client.battle

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import net.minecraft.network.PacketByteBuf

/**
 * Informs the client that a Pokémon's health has changed. Executes a tile animation.
 *
 * Handled by [com.cablemc.pokemoncobbled.common.client.net.battle.BattleHealthChangeHandler].
 *
 * @author Hiroku
 * @since June 5th, 2022
 */
class BattleHealthChangePacket() : NetworkPacket {
    lateinit var pnx: String
    var newHealthRatio = 0F

    constructor(pnx: String, newHealthRatio: Float): this() {
        this.pnx = pnx
        this.newHealthRatio = newHealthRatio
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeString(pnx)
        buffer.writeFloat(newHealthRatio)
    }

    override fun decode(buffer: PacketByteBuf) {
        pnx = buffer.readString()
        newHealthRatio = buffer.readFloat()
    }
}