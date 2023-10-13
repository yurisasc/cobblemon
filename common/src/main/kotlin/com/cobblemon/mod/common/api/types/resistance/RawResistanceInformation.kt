/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.types.resistance

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

/**
 * The raw representation of [ResistanceInformation].
 * This is used for (de)serialization purposes as certain implementations expect elements that depend on these to be loaded.
 *
 * @property value The raw string value used in the [ResistanceVariant.toImplementation].
 * @property resistance The [Resistance] value used in the [ResistanceVariant.toImplementation].
 * @property variant The [ResistanceVariant] that will decide the final [ResistanceInformation] implementation.
 */
data class RawResistanceInformation(
    val value: String,
    val resistance: Resistance,
    val variant: ResistanceVariant
) {

    /**
     * Converts this raw data to a [ResistanceInformation].
     *
     * @return The resulting [ResistanceInformation].
     *
     * @throws Exception An exception can be thrown by the [ResistanceVariant.toImplementation].
     */
    fun toImplementation(): ResistanceInformation = this.variant.toImplementation(this)

    companion object {

        val CODEC: Codec<RawResistanceInformation> = RecordCodecBuilder.create { builder ->
            builder.group(
                Codec.STRING.fieldOf("value").forGetter(RawResistanceInformation::value),
                Resistance.CODEC.fieldOf("resistance").forGetter(RawResistanceInformation::resistance),
                ResistanceVariant.CODEC.fieldOf("variant").forGetter(RawResistanceInformation::variant)
            ).apply(builder, ::RawResistanceInformation)
        }

    }

}
