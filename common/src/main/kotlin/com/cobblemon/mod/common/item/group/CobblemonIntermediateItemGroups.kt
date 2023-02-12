/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item.group

/**
 * An intermediate to allow us to make a less boilerplate solution to assigning items to item groups.
 * This should not be exposed to 3rd party to avoid confusion.
 *
 * @author Licious
 * @since February 11th, 2023
 */
internal enum class CobblemonIntermediateItemGroups {

    POKE_BALL,
    EVOLUTION_ITEM,
    MEDICINE_ITEM,
    HELD_ITEM,
    PLANTS

}