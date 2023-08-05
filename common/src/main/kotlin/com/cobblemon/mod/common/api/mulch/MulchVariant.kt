/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.mulch

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.item.MulchItem

/**
 * Represents the different types of Mulch implemented in the mod.
 *
 */
enum class MulchVariant {

    COARSE,
    GROWTH,
    HUMID,
    LOAMY,
    PEAT,
    RICH,
    SANDY,
    SURPRISE;

    /**
     * Resolves the item version of the Mulch.
     *
     * @return The [MulchItem] equivalent.
     */
    fun item(): MulchItem = when (this) {
        COARSE -> CobblemonItems.COARSE_MULCH
        GROWTH -> CobblemonItems.GROWTH_MULCH
        HUMID -> CobblemonItems.HUMID_MULCH
        LOAMY -> CobblemonItems.LOAMY_MULCH
        PEAT -> CobblemonItems.PEAT_MULCH
        RICH -> CobblemonItems.RICH_MULCH
        SANDY -> CobblemonItems.SANDY_MULCH
        SURPRISE -> CobblemonItems.SURPRISE_MULCH
    }

}