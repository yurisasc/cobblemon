/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.fabric

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonBlocks
import com.cobblemon.mod.common.CobblemonImplementation
import com.cobblemon.mod.common.CobblemonNetwork
import com.cobblemon.mod.common.item.CobblemonItems
import com.cobblemon.mod.common.item.group.CobblemonItemGroups
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.fabric.net.CobblemonFabricNetworkDelegate
import com.cobblemon.mod.fabric.permission.FabricPermissionValidator
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registry
import net.minecraft.text.Text

object CobblemonFabric : CobblemonImplementation {

    fun initialize() {
        this.registerPermissionValidator()
        this.registerBlocks()
        this.registerItems()
        CobblemonNetwork.networkDelegate = CobblemonFabricNetworkDelegate
        Cobblemon.preinitialize(this)

        Cobblemon.initialize()
        /*
        if (FabricLoader.getInstance().getModContainer("luckperms").isPresent) {
            Cobblemon.permissionValidator = LuckPermsPermissionValidator()
        }
         */
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
    }
}