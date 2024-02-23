/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.storage.pokedex

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.util.DataKeys.NUM_ENCOUNTED_WILD
import com.cobblemon.mod.common.util.DataKeys.NUM_ENCOUNTED_BATTLE
import com.cobblemon.mod.common.util.DataKeys.NUM_CAUGHT
import com.google.gson.JsonObject
import net.minecraft.network.PacketByteBuf


/**
 * Contains stats about a specific pokemon for putting in the pokedex
 *
 * @author JPAK, Apion
 * @since February 21, 2024
 */
data class DexStats(
    var numEncounteredWild: Byte = 0,
    var numEncounteredBattle: Byte = 0,
    var numCaught: Byte = 0
) {
    fun saveToJson(jsonObject: JsonObject): JsonObject {
        jsonObject.addProperty(NUM_ENCOUNTED_WILD, numEncounteredWild)
        jsonObject.addProperty(NUM_ENCOUNTED_BATTLE, numEncounteredBattle)
        jsonObject.addProperty(NUM_CAUGHT, numCaught)

        return jsonObject
    }

    fun loadFromJson(jsonObject: JsonObject): DexStats {
        numEncounteredWild = jsonObject.get(NUM_ENCOUNTED_WILD).asByte
        numEncounteredBattle = jsonObject.get(NUM_ENCOUNTED_BATTLE).asByte
        numCaught = jsonObject.get(NUM_CAUGHT).asByte

        return this
    }
}
