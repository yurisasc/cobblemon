/*
 *
 *  * Copyright (C) 2023 Cobblemon Contributors
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 */

package com.cobblemon.mod.common.pokemon.misc

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.pokemon.feature.ChoiceSpeciesFeatureProvider
import com.cobblemon.mod.common.api.pokemon.feature.SpeciesFeatures
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature
import com.cobblemon.mod.common.api.pokemon.stats.Stats.*
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.DataKeys

object SmeargleColorHandler {
    private val natureAndCharacteristicMapping = mapOf(
        Pair(Pair(ATTACK, ATTACK), "red"),
        Pair(Pair(ATTACK, HP), "red"),
        Pair(Pair(null, ATTACK), "red"),
        Pair(Pair(SPEED, DEFENCE), "red"),
        Pair(Pair(DEFENCE, SPEED), "red"),
        Pair(Pair(ATTACK, DEFENCE), "orange"),
        Pair(Pair(DEFENCE, ATTACK), "orange"),
        Pair(Pair(DEFENCE, DEFENCE), "yellow"),
        Pair(Pair(DEFENCE, HP), "yellow"),
        Pair(Pair(null, DEFENCE), "yellow"),
        Pair(Pair(ATTACK, SPECIAL_DEFENCE), "yellow"),
        Pair(Pair(SPECIAL_DEFENCE, ATTACK), "yellow"),
        Pair(Pair(DEFENCE, SPECIAL_DEFENCE), "lime"),
        Pair(Pair(SPECIAL_DEFENCE, DEFENCE), "lime"),
        Pair(Pair(SPECIAL_DEFENCE, SPECIAL_DEFENCE), "green"),
        Pair(Pair(SPECIAL_DEFENCE, HP), "green"),
        Pair(Pair(null, SPECIAL_DEFENCE), "green"),
        Pair(Pair(DEFENCE, SPECIAL_ATTACK), "green"),
        Pair(Pair(SPECIAL_ATTACK, DEFENCE), "green"),
        Pair(Pair(SPECIAL_DEFENCE, SPECIAL_ATTACK), "cyan"),
        Pair(Pair(SPECIAL_ATTACK, SPECIAL_DEFENCE), "cyan"),
        Pair(Pair(SPECIAL_ATTACK, SPECIAL_ATTACK), "blue"),
        Pair(Pair(SPECIAL_ATTACK, HP), "blue"),
        Pair(Pair(null, SPECIAL_ATTACK), "blue"),
        Pair(Pair(SPECIAL_DEFENCE, SPEED), "blue"),
        Pair(Pair(SPEED, SPECIAL_DEFENCE), "blue"),
        Pair(Pair(SPECIAL_ATTACK, SPEED), "purple"),
        Pair(Pair(SPEED, SPECIAL_ATTACK), "purple"),
        Pair(Pair(SPEED, SPEED), "magenta"),
        Pair(Pair(SPEED, HP), "magenta"),
        Pair(Pair(null, SPEED), "magenta"),
        Pair(Pair(SPECIAL_ATTACK, ATTACK), "magenta"),
        Pair(Pair(ATTACK, SPECIAL_ATTACK), "magenta"),
        Pair(Pair(SPEED, ATTACK), "pink"),
        Pair(Pair(ATTACK, SPEED), "pink"),
        Pair(Pair(null, HP), "light_blue"),
    )

    fun assignColor(pokemon: Pokemon) {
        if (pokemon.isClient || pokemon.species != PokemonSpecies.getByName("smeargle")) {
            return
        }
        val colorFeatureType = SpeciesFeatures.getFeaturesFor(pokemon.species)
            .find { it is ChoiceSpeciesFeatureProvider && DataKeys.CAN_BE_COLORED in it.keys }
        if (colorFeatureType == null) {
            return
        }

        val color = natureAndCharacteristicMapping[Pair(pokemon.nature.increasedStat, pokemon.characteristic.highStat)]

        val colorFeature = pokemon.getFeature<StringSpeciesFeature>(DataKeys.CAN_BE_COLORED)
        if (colorFeature != null) {
            colorFeature.value = color ?: "red"
            pokemon.markFeatureDirty(colorFeature)
        } else {
            val newColorFeature = StringSpeciesFeature(DataKeys.CAN_BE_COLORED, color ?: "red")
            pokemon.features.add(newColorFeature)
            pokemon.anyChangeObservable.emit(pokemon)
        }

        pokemon.updateAspects()
    }
}
