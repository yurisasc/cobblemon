package com.cablemc.pokemoncobbled.common.api.storage.player

import com.google.gson.JsonObject

/**
 * An extension for the [PlayerData] allowing to save custom data to it.
 * Needs to have an empty constructor for GSON and write the
 *
 * @author Qu
 * @since 2022-05-20
 */
interface PlayerDataExtension {

    companion object {
        val NAME_KEY = "name"
    }

    fun name(): String
    fun serialize(): JsonObject
    fun deserialize(json: JsonObject): PlayerDataExtension
}