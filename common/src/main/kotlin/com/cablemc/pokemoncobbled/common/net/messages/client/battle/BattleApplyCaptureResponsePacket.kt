package com.cablemc.pokemoncobbled.common.net.messages.client.battle

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import net.minecraft.network.PacketByteBuf

/**
 * Tells a specific player that they should choose a battle capture response for the next Pokémon request in their queue.
 *
 * Handled by [com.cablemc.pokemoncobbled.common.client.net.battle.BattleApplyCaptureResponseHandler].
 *
 * @author Hiroku
 * @since July 3rd, 2022
 */
class BattleApplyCaptureResponsePacket() : NetworkPacket {
    override fun encode(buffer: PacketByteBuf) {}
    override fun decode(buffer: PacketByteBuf) {}
}