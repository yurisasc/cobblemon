/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.fabric

import com.cobblemon.mod.common.*
import com.cobblemon.mod.common.CobblemonBlocks
import com.cobblemon.mod.common.CobblemonEntities
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.item.group.CobblemonItemGroups
import com.cobblemon.mod.fabric.net.CobblemonFabricNetworkDelegate
import com.cobblemon.mod.fabric.permission.FabricPermissionValidator
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.registry.Registry

object CobblemonFabric : CobblemonImplementation {

    fun initialize() {
        this.registerPermissionValidator()
        this.registerSoundEvents()
        this.registerBlocks()
        this.registerItems()
        this.registerEntityTypes()
        this.registerEntityAttributes()
        this.registerBlockEntityTypes()
        CobblemonNetwork.networkDelegate = CobblemonFabricNetworkDelegate
        Cobblemon.preinitialize(this)

        Cobblemon.initialize()
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register { player, isLogin ->
            if (isLogin) {
                Cobblemon.dataProvider.sync(player)
            }
        }
    }

    override fun isModInstalled(id: String) = FabricLoader.getInstance().isModLoaded(id)

    override fun registerPermissionValidator() {
        if (this.isModInstalled("fabric-permissions-api-v0")) {
            Cobblemon.permissionValidator = FabricPermissionValidator()
        }
    }

    override fun registerSoundEvents() {
        CobblemonSounds.register { identifier, sound -> Registry.register(CobblemonSounds.registry, identifier, sound) }
    }

    override fun registerItems() {
        CobblemonItems.register { identifier, item -> Registry.register(CobblemonItems.registry, identifier, item) }
        CobblemonItemGroups.register { provider ->
            FabricItemGroup.builder(provider.identifier)
                .displayName(provider.displayName)
                .icon(provider.icon)
                .build()
        }
        CobblemonItems.registerToItemGroups { group, item -> ItemGroupEvents.modifyEntriesEvent(group).register { entries -> entries.add(item) } }
    }

    override fun registerBlocks() {
        CobblemonBlocks.register { identifier, item -> Registry.register(CobblemonBlocks.registry, identifier, item) }
        CobblemonBlocks.strippedBlocks().forEach { (input, output) -> StrippableBlockRegistry.register(input, output) }
    }

    override fun registerEntityTypes() {
        CobblemonEntities.register { identifier, type -> Registry.register(CobblemonEntities.registry, identifier, type) }
    }

    override fun registerEntityAttributes() {
        FabricDefaultAttributeRegistry.register(CobblemonEntities.POKEMON, PokemonEntity.createAttributes())
    }

    override fun registerBlockEntityTypes() {
        CobblemonBlockEntities.register { identifier, type -> Registry.register(CobblemonBlockEntities.registry, identifier, type) }
    }
}