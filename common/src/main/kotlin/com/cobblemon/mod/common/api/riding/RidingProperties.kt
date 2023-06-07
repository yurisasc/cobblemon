/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.riding

import com.cobblemon.mod.common.api.riding.conditions.RidingCondition
import com.cobblemon.mod.common.api.riding.properties.mounting.MountProperties
import com.cobblemon.mod.common.api.riding.properties.mounting.MountType
import com.cobblemon.mod.common.api.riding.seats.properties.SeatProperties

interface RidingProperties {

    fun supported(): Boolean

    fun seats(): List<SeatProperties>

    fun conditions(): List<RidingCondition>

    fun properties(type: MountType): MountProperties?

}
