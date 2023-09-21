/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.types.resistance

import com.cobblemon.mod.common.api.registry.CobblemonRegistries
import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import com.mojang.serialization.Codec
import net.minecraft.util.StringIdentifiable

/**
 * Utility type to help with (de)serializing [ResistanceInformation].
 *
 * @see [RawResistanceInformation]
 * @see [RawResistanceInformation.CODEC]
 */
enum class ResistanceVariant : StringIdentifiable {

    ELEMENTAL_TYPE,
    EFFECT_ID;

    override fun asString(): String  = this.name

    /**
     * Converts a [RawResistanceInformation] to a [ResistanceInformation].
     *
     * @param raw The base [RawResistanceInformation].
     * @return The resulting [ResistanceInformation].
     *
     * @throws NullPointerException If this is a [ELEMENTAL_TYPE] and the [RawResistanceInformation.value] is not a possible [ElementalType].
     */
    fun toImplementation(raw: RawResistanceInformation): ResistanceInformation = when (this) {
        ELEMENTAL_TYPE -> ElementalTypeResistanceInformation(CobblemonRegistries.ELEMENTAL_TYPE.get(raw.value.asIdentifierDefaultingNamespace())!!, raw.resistance)
        EFFECT_ID -> ShowdownEffectResistanceInformation(raw.value, raw.resistance)
    }

    companion object {

        val CODEC: Codec<ResistanceVariant> = StringIdentifiable.createCodec(ResistanceVariant::values)

    }

}