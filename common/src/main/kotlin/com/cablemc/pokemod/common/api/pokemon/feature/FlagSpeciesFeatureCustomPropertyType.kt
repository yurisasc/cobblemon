/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.pokemon.feature

import com.cablemc.pokemod.common.api.properties.CustomPokemonPropertyType

/**
 * An implementation of [CustomPokemonPropertyType] that is a simple true/false value for a specific
 * [FlagSpeciesFeature]. The key of the property is the name of the flag.
 *
 * @author Hiroku
 * @since May 13th, 2022
 */
open class FlagSpeciesFeatureCustomPropertyType(val name: String) : CustomPokemonPropertyType<FlagSpeciesFeature> {
    override val keys = setOf(name)
    override val needsKey = true

    override fun fromString(value: String?): FlagSpeciesFeature? {
        var enabled = true
        if (value != null) {
            try {
                enabled = value.toBoolean()
            } catch (_: Exception) {}
        }
        val feature = SpeciesFeature.get(name)?.invoke() as? FlagSpeciesFeature ?: return null
        feature.enabled = enabled
        return feature
    }

    override fun examples() = setOf("true", "false")
}