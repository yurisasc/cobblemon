package com.cablemc.pokemoncobbled.common.api.storage.player.adapter

import com.cablemc.pokemoncobbled.common.api.storage.player.PlayerData
import java.util.UUID

interface PlayerDataFileStoreAdapter {
    fun load(uuid: UUID): PlayerData
    fun save(playerData: PlayerData)
}