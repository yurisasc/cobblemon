/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common

import com.cobblemon.mod.common.block.entity.*
import com.cobblemon.mod.common.platform.PlatformRegistry
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys

object CobblemonBlockEntities : PlatformRegistry<Registry<BlockEntityType<*>>, RegistryKey<Registry<BlockEntityType<*>>>, BlockEntityType<*>>() {

    override val registry: Registry<BlockEntityType<*>> = Registries.BLOCK_ENTITY_TYPE
    override val registryKey: RegistryKey<Registry<BlockEntityType<*>>> = RegistryKeys.BLOCK_ENTITY_TYPE

    @JvmField
    val HEALING_MACHINE: BlockEntityType<HealingMachineBlockEntity> = this.create("healing_machine", BlockEntityType.Builder.create(::HealingMachineBlockEntity, CobblemonBlocks.HEALING_MACHINE).build(null))
    @JvmField
    val PC: BlockEntityType<PCBlockEntity> = this.create("pc", BlockEntityType.Builder.create(::PCBlockEntity, CobblemonBlocks.PC).build(null))
    val BERRY = this.create("berry", BlockEntityType.Builder.create(::BerryBlockEntity, *CobblemonBlocks.berries().values.toTypedArray()).build(null))
    @JvmField
    val PASTURE: BlockEntityType<PokemonPastureBlockEntity> = this.create("pasture", BlockEntityType.Builder.create(::PokemonPastureBlockEntity, CobblemonBlocks.PASTURE).build(null))
    @JvmField
    val SIGN: BlockEntityType<CobblemonSignBlockEntity> = this.create("sign", BlockEntityType.Builder.create(::CobblemonSignBlockEntity, CobblemonBlocks.APRICORN_SIGN, CobblemonBlocks.APRICORN_WALL_SIGN).build(null))
    @JvmField
    val HANGING_SIGN: BlockEntityType<CobblemonHangingSignBlockEntity> = this.create("hanging_sign", BlockEntityType.Builder.create(::CobblemonHangingSignBlockEntity, CobblemonBlocks.APRICORN_HANGING_SIGN, CobblemonBlocks.APRICORN_WALL_HANGING_SIGN).build(null))
    @JvmField
    val TM_BLOCK: BlockEntityType<TMBlockEntity> = this.create("tm_block", BlockEntityType.Builder.create(::TMBlockEntity, CobblemonBlocks.TM_MACHINE).build(null))

}