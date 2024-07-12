/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.fabric.data

import com.cobblemon.mod.common.data.elemental.ElementalTypeAssetProvider
import com.cobblemon.mod.common.data.elemental.ElementalTypeProvider
import com.cobblemon.mod.common.data.elemental.ElementalTypeTagProvider
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator

class CobblemonFabricDataGeneratorEntrypoint : DataGeneratorEntrypoint {

    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        this.commonDataGen(fabricDataGenerator.createPack())
    }

    private fun commonDataGen(pack: FabricDataGenerator.Pack) {
        pack.addProvider(::ElementalTypeProvider)
        pack.addProvider(::ElementalTypeTagProvider)
        pack.addProvider(::ElementalTypeAssetProvider)
    }

}