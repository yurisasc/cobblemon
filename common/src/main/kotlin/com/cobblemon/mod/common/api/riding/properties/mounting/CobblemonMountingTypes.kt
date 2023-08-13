/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.riding.properties.mounting

import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.util.Identifier

object CobblemonMountingTypes {

    @JvmField
    val LAND : MountType = create(cobblemonResource("land"))

    @JvmField
    val WATER : MountType = create(cobblemonResource("water"))

    @JvmField
    val AIR : MountType = create(cobblemonResource("air"))

    private fun create(identifier: Identifier) : MountType {
        return MountType(identifier)
    }
}