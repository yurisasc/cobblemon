/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common

import com.cobblemon.mod.common.registry.CompletableRegistry
import com.cobblemon.mod.common.world.feature.ApricornTreeFeature
import dev.architectury.registry.registries.RegistrySupplier
import java.util.function.Supplier
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKeys
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.SingleStateFeatureConfig

object CobblemonFeatures : CompletableRegistry<Feature<*>>(RegistryKeys.FEATURE) {
    private fun <T : Feature<*>> register(name: String, feature: Supplier<T>) : RegistrySupplier<T> {
        return queue(name, feature)
    }

    val APRICORN_TREE_FEATURE = register("apricorn_tree_feature") { ApricornTreeFeature(SingleStateFeatureConfig.CODEC) }
}