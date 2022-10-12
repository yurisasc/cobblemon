/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.spawning.preset

import com.cablemc.pokemod.common.Pokemod
import com.cablemc.pokemod.common.Pokemod.LOGGER
import com.cablemc.pokemod.common.api.spawning.BestSpawner
import com.cablemc.pokemod.common.api.spawning.SpawnBucket
import com.cablemc.pokemod.common.api.spawning.context.RegisteredSpawningContext
import com.cablemc.pokemod.common.util.adapters.RegisteredSpawningContextAdapter
import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.InputStreamReader

/**
 * The config class for everything related to the [BestSpawner]. This is loaded immediately after the
 * main mod config.
 *
 * @author Hiroku
 * @since July 8th, 2022
 */
class BestSpawnerConfig {
    val version = 0
    /** Whether or not an external config will be replaced by an internal one once [version] is higher on the internal. */
    val replaceWithNewVersion = true
    val contextWeights = mutableMapOf(
        "grounded" to 1F,
        "submerged" to 0.7F
    )
    val buckets = mutableListOf(
        SpawnBucket("common", 94.4F),
        SpawnBucket("uncommon", 5F),
        SpawnBucket("rare", 0.5F),
        SpawnBucket("ultra-rare", 0.1F)
    )

    companion object {
        val GSON = GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(RegisteredSpawningContext::class.java, RegisteredSpawningContextAdapter)
            .setLenient()
            .disableHtmlEscaping()
            .create()

        const val CONFIG_NAME = "best-spawner-config.json"

        fun load(): BestSpawnerConfig {
            val internal = loadInternal()
            if (Pokemod.config.exportSpawnConfigToConfig) {
                val external = loadExternal()
                return if (external == null) {
                    saveExternal()
                    internal
                } else {
                    if (external.replaceWithNewVersion && internal.version > external.version) {
                        saveExternal()
                        internal
                    } else {
                        external
                    }
                }
            } else {
                return internal
            }
        }

        private fun loadInternal(): BestSpawnerConfig {
            val reader = InputStreamReader(Pokemod::class.java.getResourceAsStream("/assets/${Pokemod.MODID}/spawning/$CONFIG_NAME")!!)
            val config = GSON.fromJson(reader, BestSpawnerConfig::class.java)
            reader.close()
            return config
        }

        private fun loadExternal(): BestSpawnerConfig? {
            val configFile = File("config/${Pokemod.MODID}/spawning/$CONFIG_NAME.json")
            configFile.parentFile.mkdirs()
            return if (configFile.exists()) {
                try {
                    val reader = FileReader(configFile)
                    val config = GSON.fromJson(reader, BestSpawnerConfig::class.java)
                    reader.close()
                    config
                } catch (e: Exception) {
                    LOGGER.error("Unable to load external Best Spawner configuration", e)
                    null
                }
            } else {
                null
            }
        }

        fun saveExternal() {
            val stream = Pokemod::class.java.getResourceAsStream("/assets/${Pokemod.MODID}/spawning/$CONFIG_NAME")!!
            val bytes = stream.readAllBytes()
            stream.close()
            val configFile = File("config/${Pokemod.MODID}/spawning/$CONFIG_NAME.json")
            configFile.parentFile.mkdirs()
            configFile.createNewFile()
            val outputStream = FileOutputStream(configFile)
            outputStream.write(bytes)
            outputStream.close()
        }
    }
}