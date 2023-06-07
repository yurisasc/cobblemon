/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.riding.properties.mounting

import net.minecraft.util.Identifier

object CobblemonMountingTypes {

    @JvmField
    val LAND : MountType = create(Identifier("cobblemon", "land"))

    @JvmField
    val WATER : MountType = create(Identifier("cobblemon", "water"))

    @JvmField
    val AIR : MountType = create(Identifier("cobblemon", "air"))

    private fun create(identifier: Identifier) : MountType {
        return MountType(identifier)
    }
}