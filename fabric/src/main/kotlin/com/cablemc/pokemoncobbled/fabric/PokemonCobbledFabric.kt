/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.fabric

import com.cablemc.pokemoncobbled.common.*
import com.cablemc.pokemoncobbled.common.net.serverhandling.ServerPacketRegistrar
import com.cablemc.pokemoncobbled.fabric.net.CobbledFabricNetworkDelegate
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.loader.api.FabricLoader

object PokemonCobbledFabric : PokemonCobbledModImplementation {
    override fun isModInstalled(id: String) = FabricLoader.getInstance().isModLoaded(id)
    fun initialize() {
        CobbledNetwork.networkDelegate = CobbledFabricNetworkDelegate
        PokemonCobbled.preinitialize(this)

        CobbledConfiguredFeatures.register()
        CobbledPlacements.register()

        PokemonCobbled.initialize()
        ServerPacketRegistrar.registerHandlers()
        CobbledNetwork.register()
        if (FabricLoader.getInstance().getModContainer("luckperms").isPresent) {
//            PokemonCobbled.permissionValidator = LuckPermsPermissionValidator()
        }
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register { player, _ ->
            PokemonCobbled.dataProvider.sync(player)
        }
    }
}