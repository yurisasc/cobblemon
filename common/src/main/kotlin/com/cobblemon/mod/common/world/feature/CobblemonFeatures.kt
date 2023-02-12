/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.world.feature

import com.cobblemon.mod.common.registry.PlatformRegistry
import com.cobblemon.mod.common.world.feature.apricorn.ApricornTreeFeature
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.world.gen.feature.Feature

object CobblemonFeatures : PlatformRegistry<Registry<Feature<*>>, RegistryKey<Registry<Feature<*>>>, Feature<*>>() {

    override val registry: Registry<Feature<*>> = Registries.FEATURE
    override val registryKey: RegistryKey<Registry<Feature<*>>> = RegistryKeys.FEATURE

    val APRICORN_TREE_FEATURE = this.create("apricorn_tree", ApricornTreeFeature())

}