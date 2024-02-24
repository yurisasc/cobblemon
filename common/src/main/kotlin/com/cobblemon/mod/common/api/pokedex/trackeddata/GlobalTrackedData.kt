/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pokedex.trackeddata

import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent
import net.minecraft.network.PacketByteBuf

/**
 * Tracked data that isn't specific to a species or form
 *
 * @author Apion
 * @since February 24, 2024
 */
abstract class GlobalTrackedData {
    abstract val triggerEvents: Set<EventTriggerType>
    @Transient
    val syncToClient = false
    open fun onCatch(event: PokemonCapturedEvent): Boolean {
        return false
    }

    abstract fun clone(): GlobalTrackedData

    abstract fun encode(buf: PacketByteBuf)

}