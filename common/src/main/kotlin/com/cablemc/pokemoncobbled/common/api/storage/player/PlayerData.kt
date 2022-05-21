package com.cablemc.pokemoncobbled.common.api.storage.player

import java.util.UUID

data class PlayerData(
    val uuid: UUID,
    var starterSelected: Boolean,
    var starterUUID: UUID?,
    val extraData: MutableMap<String, PlayerDataExtension>
) {
    companion object {
        fun default(forPlayer: UUID) = PlayerData(forPlayer, false, null, mutableMapOf())
    }
}
