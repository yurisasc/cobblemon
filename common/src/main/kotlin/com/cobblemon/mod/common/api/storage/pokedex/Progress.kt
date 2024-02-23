
/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokedex

import com.cobblemon.mod.common.util.DataKeys.NUM_CAUGHT
import com.cobblemon.mod.common.util.DataKeys.NUM_ENCOUNTED_BATTLE
import com.cobblemon.mod.common.util.DataKeys.NUM_ENCOUNTED_WILD
import com.google.gson.JsonObject
import net.minecraft.util.StringIdentifiable
import kotlin.reflect.full.memberProperties

/**
 * Contains stats about a specific pokemon for putting in the pokedex
 *
 * @author JPAK, Apion
 * @since February 21, 2024
 */
data class DexStats(
    var numEncounteredWild: Byte = 0,
    var numEncounteredBattle: Byte = 0,
    var numCaught: Byte = 0,
) {
    //If this is being called from a new encounter, we need to subtract 1 from the number of mons encountered so far.
    //Alternatively, we can check this before we increment the num encountered, but good luck with that
    //(For some reason we don't actually start battles on the client until we are in ShowdownInterpreter
    fun getKnowledge(isNewEncounter: Boolean = false): Knowledge {
        if (numCaught > 0) {
            return Knowledge.CAUGHT
        }
        var numEncountered = numEncounteredBattle + numEncounteredWild
        if (isNewEncounter) {
            numEncountered--
        }
        if (numEncountered > 0) {
            return Knowledge.ENCOUNTERED
        }
        return Knowledge.NONE
    }

    enum class Knowledge : StringIdentifiable {
        NONE,
        ENCOUNTERED,
        CAUGHT;
        override fun asString(): String {
            return this.name
        }
    }

    companion object {
        fun incrementNumEncounteredWild(stats: DexStats){
            if(stats.numEncounteredWild < Byte.MAX_VALUE){
                stats.numEncounteredWild++
            }
        }

        fun incrementNumEncounteredBattle(stats: DexStats){
            if(stats.numEncounteredBattle < Byte.MAX_VALUE){
                stats.numEncounteredBattle++
            }
        }

        fun incrementNumCaught(stats: DexStats){
            if(stats.numCaught < Byte.MAX_VALUE){
                stats.numCaught++
            }
        }

    }

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
