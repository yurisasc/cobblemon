package com.cablemc.pokemoncobbled.common.api.storage.player

import com.cablemc.pokemoncobbled.common.api.scheduling.ScheduledTask
import com.cablemc.pokemoncobbled.common.api.storage.player.adapter.JsonPlayerData
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.MinecraftServer

class PlayerDataStoreManager {

    private val jpd = JsonPlayerData()

    private fun registerSaveScheduler() = ScheduledTask.Builder().execute { jpd.saveCache() }.delay(30f).interval(120f).build()

    fun setup(server: MinecraftServer) {
        registerSaveScheduler()
        jpd.setup(server)
    }

    fun get(player: PlayerEntity) = jpd.load(player.uuid)

    fun saveAll() = jpd.saveCache()

    fun saveSingle(playerData: PlayerData) = jpd.save(playerData)
}