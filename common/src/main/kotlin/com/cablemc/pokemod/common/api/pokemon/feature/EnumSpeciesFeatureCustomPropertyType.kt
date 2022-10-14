/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.pokemon.feature

import com.cablemc.pokemod.common.api.properties.CustomPokemonPropertyType

/**
 * An implementation of [CustomPokemonPropertyType] that interprets values for some [EnumSpeciesFeature]. The key of the
 * property is the name of the enum species feature.
 *
 * @author Hiroku
 * @since October 13th, 2022
 */
open class EnumSpeciesFeatureCustomPropertyType<T : Enum<T>>(val name: String) : CustomPokemonPropertyType<EnumSpeciesFeature<T>> {
    override val keys = setOf(name)
    override val needsKey = true

    override fun fromString(value: String?): EnumSpeciesFeature<T>? {
        value ?: return null
        val feature = SpeciesFeature.get(name)?.invoke() as? EnumSpeciesFeature<T> ?: return null
        val enumValue = feature.values.find { it.name.equals(value, ignoreCase = true) } ?: return null
        feature.enumValue = enumValue
        return feature
    }
}