/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.registry.CompletableRegistry
import com.cablemc.pokemoncobbled.common.world.level.block.entity.HealingMachineBlockEntity
import com.cablemc.pokemoncobbled.common.world.level.block.entity.PCBlockEntity
import dev.architectury.registry.registries.RegistrySupplier
import java.util.function.Supplier
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.registry.Registry

object CobbledBlockEntities : CompletableRegistry<BlockEntityType<*>>(Registry.BLOCK_ENTITY_TYPE_KEY) {
    private fun <T : BlockEntityType<*>> register(name: String, blockEntityType: Supplier<T>) : RegistrySupplier<T> {
        return queue(name, blockEntityType)
    }

    val HEALING_MACHINE = register("healing_machine") { BlockEntityType.Builder.create(::HealingMachineBlockEntity, CobbledBlocks.HEALING_MACHINE.get()).build(null) }
    val PC = register("pc") { BlockEntityType.Builder.create(::PCBlockEntity, CobbledBlocks.PC.get()).build(null) }
}