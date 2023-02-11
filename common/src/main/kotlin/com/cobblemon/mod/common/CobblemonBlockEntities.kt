/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common

import com.cobblemon.mod.common.registry.CompletableRegistry
import com.cobblemon.mod.common.block.entity.HealingMachineBlockEntity
import com.cobblemon.mod.common.block.entity.PCBlockEntity
import dev.architectury.registry.registries.RegistrySupplier
import java.util.function.Supplier
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.registry.RegistryKeys
import net.minecraft.world.gen.feature.ConfiguredFeatures

object CobblemonBlockEntities : CompletableRegistry<BlockEntityType<*>>(RegistryKeys.BLOCK_ENTITY_TYPE) {
    private fun <T : BlockEntityType<*>> register(name: String, blockEntityType: Supplier<T>) : RegistrySupplier<T> {
        return queue(name, blockEntityType)
    }

    val HEALING_MACHINE = register("healing_machine") {
        BlockEntityType.Builder.create(::HealingMachineBlockEntity, CobblemonBlocks.HEALING_MACHINE).build(null)
    }
    val PC = register("pc") {
        BlockEntityType.Builder.create(::PCBlockEntity, CobblemonBlocks.PC).build(null)
    }
}