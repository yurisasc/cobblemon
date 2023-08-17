/*
 *
 *  * Copyright (C) 2023 Cobblemon Contributors
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 */

package com.cobblemon.mod.common.world.structureprocessors

import com.cobblemon.mod.common.CobblemonBlocks
import com.cobblemon.mod.common.bridges.StructureProcessorListBridge
import com.cobblemon.mod.common.platform.PlatformRegistry
import net.minecraft.block.Blocks
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.MinecraftServer
import net.minecraft.structure.processor.StructureProcessorList
import net.minecraft.structure.processor.StructureProcessorLists
import net.minecraft.structure.processor.StructureProcessorRule
import net.minecraft.structure.rule.AlwaysTrueRuleTest
import net.minecraft.structure.rule.BlockMatchRuleTest

object CobblemonStructureProcessorListOverrides {
    val registryKey = RegistryKeys.PROCESSOR_LIST

    fun register(server: MinecraftServer) {
        val registry = server.registryManager.get(registryKey)
        registerFarmOverrides(registry)
    }

    fun registerFarmOverrides(registry: Registry<StructureProcessorList>) {
        val desertFarm = registry[StructureProcessorLists.FARM_DESERT] as StructureProcessorListBridge
        val plainsFarm = registry[StructureProcessorLists.FARM_PLAINS] as StructureProcessorListBridge
        val taigaFarm = registry[StructureProcessorLists.FARM_TAIGA] as StructureProcessorListBridge
        val savannahFarm = registry[StructureProcessorLists.FARM_SAVANNA] as StructureProcessorListBridge
        val snowyFarm = registry[StructureProcessorLists.FARM_SNOWY] as StructureProcessorListBridge

        val plantMatchRuleTest = BlockMatchRuleTest(Blocks.WHEAT)
        val plantMatchRule = StructureProcessorRule(plantMatchRuleTest, AlwaysTrueRuleTest.INSTANCE, CobblemonBlocks.VIVICHOKE_SEEDS.defaultState)

        val processor = ProbabilityProcessor(0.3f, listOf(plantMatchRule))
        desertFarm.append(processor)
        plainsFarm.append(processor)
        taigaFarm.append(processor)
        savannahFarm.append(processor)
        snowyFarm.append(processor)
    }

}
