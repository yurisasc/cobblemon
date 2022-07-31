package com.cablemc.pokemoncobbled.common.api.storage.player

import com.cablemc.pokemoncobbled.common.api.scheduling.ScheduledTask
import com.cablemc.pokemoncobbled.common.api.storage.player.adapter.JsonPlayerData
import net.minecraft.entity.player.PlayerEntity

class PlayerDataStoreManager {

    private val jpd = JsonPlayerData()

    init {
        registerSaveScheduler()
    }
    private fun registerSaveScheduler() = ScheduledTask.Builder().execute { jpd.saveCache() }.delay(30f).interval(120f).build()

    fun get(player: PlayerEntity) = jpd.load(player.uuid)

    fun saveAll() = jpd.saveCache()

    fun saveSingle(playerData: PlayerData) = jpd.save(playerData)
}