/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.world.feature

import com.cobblemon.mod.common.registry.CompletableRegistry
import com.cobblemon.mod.common.world.feature.apricorn.ApricornTreeFeature
import dev.architectury.registry.registries.RegistrySupplier
import java.util.function.Supplier
import net.minecraft.registry.RegistryKeys
import net.minecraft.world.gen.feature.Feature

object CobblemonFeatures : CompletableRegistry<Feature<*>>(RegistryKeys.FEATURE) {

    val APRICORN_TREE_FEATURE = this.queue("apricorn_tree") { ApricornTreeFeature() }

}