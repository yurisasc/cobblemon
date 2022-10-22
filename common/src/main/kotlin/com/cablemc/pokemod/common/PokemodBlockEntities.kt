/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common

import com.cablemc.pokemod.common.registry.CompletableRegistry
import com.cablemc.pokemod.common.world.block.entity.HealingMachineBlockEntity
import com.cablemc.pokemod.common.world.block.entity.PCBlockEntity
import dev.architectury.registry.registries.RegistrySupplier
import java.util.function.Supplier
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.registry.Registry

object PokemodBlockEntities : CompletableRegistry<BlockEntityType<*>>(Registry.BLOCK_ENTITY_TYPE_KEY) {
    private fun <T : BlockEntityType<*>> register(name: String, blockEntityType: Supplier<T>) : RegistrySupplier<T> {
        return queue(name, blockEntityType)
    }

    val HEALING_MACHINE = com.cablemc.pokemod.common.PokemodBlockEntities.register("healing_machine") {
        BlockEntityType.Builder.create(
            ::HealingMachineBlockEntity,
            PokemodBlocks.HEALING_MACHINE.get()
        ).build(null)
    }
    val PC = com.cablemc.pokemod.common.PokemodBlockEntities.register("pc") {
        BlockEntityType.Builder.create(
            ::PCBlockEntity,
            PokemodBlocks.PC.get()
        ).build(null)
    }
}