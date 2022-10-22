/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.fabric

import com.cablemc.pokemod.common.Pokemod
import com.cablemc.pokemod.common.PokemodConfiguredFeatures
import com.cablemc.pokemod.common.PokemodImplementation
import com.cablemc.pokemod.common.PokemodNetwork
import com.cablemc.pokemod.common.PokemodPlacements
import com.cablemc.pokemod.common.net.serverhandling.ServerPacketRegistrar
import com.cablemc.pokemod.fabric.net.PokemodFabricNetworkDelegate
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.loader.api.FabricLoader

object PokemodFabric : PokemodImplementation {
    override fun isModInstalled(id: String) = FabricLoader.getInstance().isModLoaded(id)
    fun initialize() {
        PokemodNetwork.networkDelegate = PokemodFabricNetworkDelegate
        Pokemod.preinitialize(this)

        PokemodConfiguredFeatures.register()
        PokemodPlacements.register()

        Pokemod.initialize()
        ServerPacketRegistrar.registerHandlers()
        PokemodNetwork.register()
        if (FabricLoader.getInstance().getModContainer("luckperms").isPresent) {
//            PokemonCobbled.permissionValidator = LuckPermsPermissionValidator()
        }
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register { player, isLogin ->
            if (isLogin) {
                Pokemod.dataProvider.sync(player)
            }
        }
    }
}