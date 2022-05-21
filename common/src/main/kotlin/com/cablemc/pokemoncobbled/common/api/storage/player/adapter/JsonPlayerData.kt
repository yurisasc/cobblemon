package com.cablemc.pokemoncobbled.common.api.storage.player.adapter

import com.cablemc.pokemoncobbled.common.api.storage.player.PlayerData
import com.cablemc.pokemoncobbled.common.api.storage.player.PlayerDataExtension
import com.cablemc.pokemoncobbled.common.util.fromJson
import com.google.gson.GsonBuilder
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.PrintWriter
import java.util.UUID

class JsonPlayerData: PlayerDataFileStoreAdapter {

    companion object {
        val gson = GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeAdapter(PlayerDataExtension::class.java, PlayerDataExtensionAdapter)
            .create()
        private val cache = mutableMapOf<UUID, PlayerData>()
    }

    override val rootFolder: String
        get() = "pokemoncobbled/playerdata"
    override val useNestedFolders: Boolean
        get() = true
    override val fileExtension: String
        get() = ".json"

    private fun folder(uuid: UUID) = File("$rootFolder/${uuid.toString().substring(0, 2)}/").also { it.mkdirs() }
    private fun file(uuid: UUID) = File(folder(uuid), "$uuid$fileExtension")

    override fun load(uuid: UUID): PlayerData {
        if (cache.contains(uuid)) {
            return cache[uuid]!!
        }

        val playerFile = file(uuid)
        return if (playerFile.exists()) {
            gson.fromJson<PlayerData>(BufferedReader(FileReader(playerFile))).also {
                cache[uuid] = it
            }
        } else {
            PlayerData.default(uuid).also {
                save(it)
            }
        }
    }

    override fun save(playerData: PlayerData) {
        val pw = PrintWriter(file(playerData.uuid))
        pw.write(gson.toJson(playerData))
        pw.flush()
        pw.close()
        cache[playerData.uuid] = playerData
    }
}