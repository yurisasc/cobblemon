package com.cobblemon.mod.common.api.storage.player.adapter

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.pokedex.Pokedex
import com.cobblemon.mod.common.api.storage.player.InstancedPlayerData
import com.cobblemon.mod.common.api.storage.player.PlayerInstancedDataStoreType
import com.mojang.serialization.Codec
import net.minecraft.nbt.NbtIo
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.visitor.StringNbtWriter
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileReader
import java.io.FileWriter
import java.io.PrintWriter
import java.util.UUID
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties

abstract class NbtBackedPlayerData<T : InstancedPlayerData>(
    subfolder: String,
    type: PlayerInstancedDataStoreType
) : FileBasedPlayerDataStoreBackend<T>(subfolder, type) {
    abstract val codec: Codec<T>
    override fun save(playerData: T) {
        val file = filePath(playerData.uuid)
        file.parentFile.mkdirs()
        val os = DataOutputStream(FileOutputStream(filePath(playerData.uuid)))
        val encodeResult = NbtOps.INSTANCE.withEncoder(codec).apply(playerData)
        encodeResult.get().ifLeft {
            NbtIo.write(it, os)
        }.ifRight {
            Cobblemon.LOGGER.error("Error encoding pokedex for player uuid ${playerData.uuid}")
            Cobblemon.LOGGER.error(it.message())
        }
        os.flush()
        os.close()
    }

    override fun load(uuid: UUID): T {
        val playerFile = filePath(uuid)
        playerFile.parentFile.mkdirs()
        return if (playerFile.exists()) {
            val input = NbtIo.read(playerFile)
            val decodeResult = NbtOps.INSTANCE.withDecoder(codec).apply(input)
            decodeResult.get().ifRight {
                Cobblemon.LOGGER.error("Error decoding pokedex for player uuid ${uuid}")
                Cobblemon.LOGGER.error(it.message())
                throw UnsupportedOperationException()
            }.left().get().first
        } else {
            defaultData.invoke(uuid).also(::save)
        }
    }
}