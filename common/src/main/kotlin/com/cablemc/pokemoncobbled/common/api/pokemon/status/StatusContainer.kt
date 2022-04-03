package com.cablemc.pokemoncobbled.common.api.pokemon.status

import com.cablemc.pokemoncobbled.common.util.DataKeys
import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation

/**
 * Container that stores all status details
 *
 * @author Deltric
 */
class StatusContainer(
    val status: Status,
    var activeSeconds: Int = 0
) {
    fun saveToNBT(nbt: CompoundTag): CompoundTag {
        nbt.putString(DataKeys.POKEMON_STATUS_NAME, status.name.toString())
        nbt.putInt(DataKeys.POKEMON_STATUS_TIMER, activeSeconds)
        return nbt
    }

    fun saveToJSON(json: JsonObject): JsonObject {
        json.addProperty(DataKeys.POKEMON_STATUS_NAME, status.name.toString())
        json.addProperty(DataKeys.POKEMON_STATUS_TIMER, activeSeconds)
        return json
    }

    companion object {
        fun loadFromNBT(nbt: CompoundTag): StatusContainer? {
            val statusId = nbt.getString(DataKeys.POKEMON_STATUS_NAME)
            val activeSeconds = nbt.getInt(DataKeys.POKEMON_STATUS_TIMER)

            // Missing status id
            if(statusId.isEmpty()) {
                return null
            }

            // Return null if status doesn't exist, otherwise return a container with the status.
            val status = Statuses.getStatus(ResourceLocation(statusId)) ?: return null
            return StatusContainer(status, activeSeconds)
        }

        fun loadFromJSON(json: JsonObject): StatusContainer? {
            val statusId = json.get(DataKeys.POKEMON_STATUS_NAME).asString
            val activeSeconds = json.get(DataKeys.POKEMON_STATUS_TIMER).asInt

            // Missing status id
            if(statusId.isEmpty()) {
                return null
            }

            // Return null if status doesn't exist, otherwise return a container with the status.
            val status = Statuses.getStatus(ResourceLocation(statusId)) ?: return null
            return StatusContainer(status, activeSeconds)
        }
    }
}