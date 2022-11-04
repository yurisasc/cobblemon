/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common

import com.cablemc.pokemod.common.registry.CompletableRegistry
import com.cablemc.pokemod.common.world.feature.ApricornTreeFeature
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.util.registry.Registry
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.SingleStateFeatureConfig
import java.util.function.Supplier

object PokemodFeatures : CompletableRegistry<Feature<*>>(Registry.FEATURE_KEY) {
    private fun <T : Feature<*>> register(name: String, feature: Supplier<T>) : RegistrySupplier<T> {
        return queue(name, feature)
    }

    val APRICORN_TREE_FEATURE = register("apricorn_tree_feature") { ApricornTreeFeature(SingleStateFeatureConfig.CODEC) }
}