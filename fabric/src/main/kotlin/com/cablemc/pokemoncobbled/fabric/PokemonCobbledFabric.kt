package com.cablemc.pokemoncobbled.fabric

import com.cablemc.pokemoncobbled.common.*
import com.cablemc.pokemoncobbled.common.net.serverhandling.ServerPacketRegistrar
import com.cablemc.pokemoncobbled.common.permission.LuckPermsPermissionValidator
import com.cablemc.pokemoncobbled.fabric.net.CobbledFabricNetworkDelegate
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
            PokemonCobbled.permissionValidator = LuckPermsPermissionValidator()
        }
    }
}