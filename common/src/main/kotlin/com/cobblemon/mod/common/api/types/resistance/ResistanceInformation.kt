/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.types.resistance

import com.cobblemon.mod.common.api.data.ShowdownIdentifiable
import com.cobblemon.mod.common.api.types.ElementalType

/**
 * Represents a resistance of an [ElementalType].
 * This can be to another [ElementalType] or some Showdown effect.
 * @see [ElementalTypeResistanceInformation]
 * @see [ShowdownEffectResistanceInformation]
 */
sealed interface ResistanceInformation : ShowdownIdentifiable {

    /**
     * The [Resistance] value.
     */
    val resistance: Resistance

    /**
     * The raw form of this instance.
     * This is used for (de)serialization as certain elements like elemental types might not be fully loaded when utilizing this.
     *
     * @return The resulting [RawResistanceInformation].
     */
    fun toRaw(): RawResistanceInformation

}
