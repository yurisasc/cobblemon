package com.cablemc.pokemoncobbled.common.net.messages.client.settings

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import net.minecraft.network.PacketByteBuf

/**
 * A packet that will sync simple config settings to the client that shouldn't require to be data pack powered.
 *
 * @author Licious
 * @since September 25th, 2022
 */
class ServerSettingsPacket internal constructor() : NetworkPacket {

    var preventCompletePartyDeposit = false
        private set

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeBoolean(PokemonCobbled.config.preventCompletePartyDeposit)
    }

    override fun decode(buffer: PacketByteBuf) {
        this.preventCompletePartyDeposit = buffer.readBoolean()
    }

}