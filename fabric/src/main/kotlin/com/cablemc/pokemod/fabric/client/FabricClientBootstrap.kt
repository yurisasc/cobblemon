/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.fabric.client

import com.cablemc.pokemod.common.PokemodClientImplementation
import com.cablemc.pokemod.common.PokemodEntities
import com.cablemc.pokemod.common.PokemodNetwork
import com.cablemc.pokemod.common.client.PokemodClient
import com.cablemc.pokemod.common.client.PokemodClient.reloadCodedAssets
import com.cablemc.pokemod.common.util.pokemodResource
import dev.architectury.init.fabric.ArchitecturyClient
import java.util.function.Supplier
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.client.model.TexturedModelData
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceType

class FabricClientBootstrap: ClientModInitializer, PokemodClientImplementation {
    override fun onInitializeClient() {
        ArchitecturyClient.init()

        PokemodClient.initialize(this)
        PokemodNetwork.register()

        EntityRendererRegistry.register(PokemodEntities.POKEMON_TYPE) { PokemodClient.registerPokemonRenderer(it) }
        EntityRendererRegistry.register(PokemodEntities.EMPTY_POKEBALL_TYPE) { PokemodClient.registerPokeBallRenderer(it) }

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(object : SimpleSynchronousResourceReloadListener {
            override fun getFabricId() = pokemodResource("resources")
            override fun reload(resourceManager: ResourceManager) { reloadCodedAssets(resourceManager) }
        })
    }

    override fun registerLayer(modelLayer: EntityModelLayer, supplier: Supplier<TexturedModelData>) {
        EntityModelLayerRegistry.registerModelLayer(modelLayer) { supplier.get() }
    }
}