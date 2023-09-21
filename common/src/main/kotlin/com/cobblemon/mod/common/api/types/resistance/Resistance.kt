/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.types.resistance

import com.cobblemon.mod.common.api.types.ElementalType
import com.mojang.serialization.Codec
import net.minecraft.util.StringIdentifiable

/**
 * Represents the resistance of an [ElementalType] or a Showdown effect.
 *
 * @property showdownValue The showdown numerical value used in its data format for type resistances.
 *
 * @see [ResistanceInformation]
 */
enum class Resistance(val showdownValue: Int) : StringIdentifiable {

    NEUTRAL(0),
    WEAK(1),
    RESIST(2),
    IMMUNE(3);

    override fun asString(): String = this.name

    companion object {

        val CODEC: Codec<Resistance> = StringIdentifiable.createCodec(Resistance::values)

    }

}