/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.mulch

import com.cobblemon.mod.common.BakingOverride
import com.cobblemon.mod.common.client.CobblemonBakingOverrides
import net.minecraft.util.StringIdentifiable

/**
 * Represents the different types of Mulch implemented in the mod.
 *
 */
enum class MulchVariant(val model: BakingOverride?, val duration: Int = -1) : StringIdentifiable {
    COARSE(CobblemonBakingOverrides.COARSE_MULCH),
    GROWTH(CobblemonBakingOverrides.GROWTH_MULCH, 5),
    HUMID(CobblemonBakingOverrides.HUMID_MULCH),
    LOAMY(CobblemonBakingOverrides.LOAMY_MULCH),
    PEAT(CobblemonBakingOverrides.PEAT_MULCH),
    RICH(CobblemonBakingOverrides.RICH_MULCH, 5),
    SANDY(CobblemonBakingOverrides.SANDY_MULCH),
    SURPRISE(CobblemonBakingOverrides.SURPRISE_MULCH, 3),
    NONE(null);

    override fun asString(): String {
        return name.lowercase()
    }

    companion object {
        const val GROWTH_TIME_MULTIPLIER = 0.5
    }
}
