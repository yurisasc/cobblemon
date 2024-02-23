/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokedex

import net.minecraft.util.StringIdentifiable

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
}
