package com.cablemc.pokemoncobbled.common.net.messages.client.starter

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.api.storage.player.PlayerData
import java.util.UUID
import net.minecraft.network.PacketByteBuf

/**
 * Packet to update the general player data on the client (which is just starter information).
 *
 * @author Hiroku
 * @since August 1st, 2022
 */
class SetClientPlayerDataPacket internal constructor() : NetworkPacket {
    var promptStarter = true
    var starterLocked = false
    var starterSelected = false
    var starterUUID: UUID? = null

    constructor(playerData: PlayerData): this() {
        promptStarter = !playerData.starterPrompted || !PokemonCobbled.starterConfig.promptStarterOnceOnly
        starterLocked = playerData.starterLocked
        starterSelected = playerData.starterSelected
        starterUUID = playerData.starterUUID
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeBoolean(promptStarter)
        buffer.writeBoolean(starterLocked)
        buffer.writeBoolean(starterSelected)
        val starterUUID = starterUUID
        buffer.writeBoolean(starterUUID != null)
        if (starterUUID != null) {
            buffer.writeUuid(starterUUID)
        }
    }

    override fun decode(buffer: PacketByteBuf) {
        promptStarter = buffer.readBoolean()
        starterLocked = buffer.readBoolean()
        starterSelected = buffer.readBoolean()
        if (buffer.readBoolean()) {
            starterUUID = buffer.readUuid()
        }
    }
}