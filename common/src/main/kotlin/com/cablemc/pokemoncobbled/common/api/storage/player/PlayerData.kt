package com.cablemc.pokemoncobbled.common.api.storage.player

import com.cablemc.pokemoncobbled.common.CobbledNetwork.sendPacket
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.net.messages.client.starter.SetClientPlayerDataPacket
import java.util.UUID
import net.minecraft.server.network.ServerPlayerEntity

data class PlayerData(
    val uuid: UUID,
    var starterPrompted: Boolean,
    var starterLocked: Boolean,
    var starterSelected: Boolean,
    var starterUUID: UUID?,
    val extraData: MutableMap<String, PlayerDataExtension>
) {
    fun sendToPlayer(player: ServerPlayerEntity) {
        player.sendPacket(SetClientPlayerDataPacket(this))
    }

    companion object {
        fun default(forPlayer: UUID) = PlayerData(
            uuid = forPlayer,
            starterPrompted = false,
            starterLocked = !PokemonCobbled.starterConfig.allowStarterOnJoin,
            starterSelected =  false,
            starterUUID =  null,
            extraData = mutableMapOf()
        )
    }
}
