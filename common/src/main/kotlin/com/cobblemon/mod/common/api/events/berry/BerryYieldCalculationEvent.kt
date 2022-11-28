/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.events.berry

import com.cobblemon.mod.common.api.berry.Berry
import net.minecraft.server.network.ServerPlayerEntity

/**
 * The event fired when [Berry.calculateYield] is invoked with a non-null player argument.
 *
 * @property player The [ServerPlayerEntity] triggering the calculation.
 * @property yield The current yield of berries.
 * @property passedTemperatureCheck If [Berry.temperatureRange] was valid for the berry harvest location.
 * @property passedDownfallCheck If [Berry.downfallRange] was valid for the berry harvest location.
 */
data class BerryYieldCalculationEvent(
    override val berry: Berry,
    val player: ServerPlayerEntity,
    var yield: Int,
    val passedTemperatureCheck: Boolean,
    val passedDownfallCheck: Boolean
) : BerryEvent