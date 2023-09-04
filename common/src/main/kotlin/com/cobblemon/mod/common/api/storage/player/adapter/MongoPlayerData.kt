/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.storage.player.adapter

import com.cobblemon.mod.common.api.storage.player.PlayerAdvancementData
import com.cobblemon.mod.common.api.storage.player.PlayerData
import com.cobblemon.mod.common.api.storage.player.PlayerDataExtension
import com.cobblemon.mod.common.util.adapters.IdentifierAdapter
import com.google.gson.GsonBuilder
import com.mongodb.client.MongoClient
import com.mongodb.client.model.ReplaceOptions
import net.minecraft.util.Identifier
import org.bson.Document
import java.util.UUID

class MongoPlayerDataAdapter(
    mongoClient: MongoClient,
    databaseName: String
) : PlayerDataStoreAdapter {

    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .registerTypeAdapter(PlayerDataExtension::class.java, PlayerDataExtensionAdapter)
        .registerTypeAdapter(Identifier::class.java, IdentifierAdapter)
        .create()

    private val collection = mongoClient.getDatabase(databaseName).getCollection("PlayerDataCollection")

    override fun load(uuid: UUID): PlayerData {
        val filter = Document("uuid", uuid.toString())
        val document = collection.find(filter).first()

        return if (document != null) {
            val jsonStr = document.toJson()
            gson.fromJson(jsonStr, PlayerData::class.java).also {
                // Something Hiroku did on the other one, perhaps necessary? - Pebbles
                if (it.advancementData == null) {
                    it.advancementData = PlayerAdvancementData()
                }
            }
        } else {
            PlayerData.defaultData(uuid).also(::save)
        }
    }

    override fun save(playerData: PlayerData) {
        val jsonStr = gson.toJson(playerData)
        val document = Document.parse(jsonStr)
        document.put("uuid", playerData.uuid.toString())
        val filter = Document("uuid", playerData.uuid.toString())

        collection.replaceOne(filter, document, ReplaceOptions().upsert(true))
    }
}
