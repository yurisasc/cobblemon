package com.cablemc.pokemoncobbled.common.api.storage.player.adapter

import com.cablemc.pokemoncobbled.common.api.storage.player.PlayerData
import com.cablemc.pokemoncobbled.common.api.storage.player.PlayerDataExtension
import com.cablemc.pokemoncobbled.common.util.fromJson
import com.google.gson.GsonBuilder
import java.io.BufferedReader
import java.io.FileReader
import java.io.PrintWriter
import java.nio.file.Path
import java.util.UUID
import net.minecraft.server.MinecraftServer
import net.minecraft.util.WorldSavePath

class JsonPlayerData: PlayerDataFileStoreAdapter {

    companion object {
        val gson = GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeAdapter(PlayerDataExtension::class.java, PlayerDataExtensionAdapter)
            .create()
    }

    private val cache = mutableMapOf<UUID, PlayerData>()
    lateinit var savePath: Path
    var useNestedStructure = true

    fun setup(server: MinecraftServer) {
        savePath = server.getSavePath(WorldSavePath.PLAYERDATA).parent
    }

    fun getSubFile(uuid: UUID): String {
        return if (useNestedStructure) {
            "${uuid.toString().substring(0, 2)}/$uuid.json"
        } else {
            "$uuid.json"
        }
    }
    private fun file(uuid: UUID) = savePath.resolve("cobbledplayerdata/${getSubFile(uuid)}").toFile()

    override fun load(uuid: UUID): PlayerData {
        if (cache.contains(uuid)) {
            return cache[uuid]!!
        }

        val playerFile = file(uuid)
        playerFile.parentFile.mkdirs()
        return if (playerFile.exists()) {
            gson.fromJson<PlayerData>(BufferedReader(FileReader(playerFile))).also {
                cache[uuid] = it
            }
        } else {
            PlayerData.default(uuid).also(::save)
        }
    }

    fun saveCache() {
        cache.forEach { (_, pd) -> save(pd)}
        cache.clear()
    }

    override fun save(playerData: PlayerData) {
        val file = file(playerData.uuid)
        file.parentFile.mkdirs()
        val pw = PrintWriter(file(playerData.uuid))
        pw.write(gson.toJson(playerData))
        pw.flush()
        pw.close()
        cache[playerData.uuid] = playerData
    }
}