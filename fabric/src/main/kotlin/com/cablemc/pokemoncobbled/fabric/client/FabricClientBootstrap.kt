package com.cablemc.pokemoncobbled.fabric.client

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.PokemonCobbledClientImplementation
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.render.CobbledTexturedModelDatas.layerDefinitions
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import dev.architectury.init.fabric.ArchitecturyClient
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.client.model.TexturedModelData
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceType
import java.util.function.Supplier

class FabricClientBootstrap: ClientModInitializer, PokemonCobbledClientImplementation {
    override fun onInitializeClient() {
        ArchitecturyClient.init()

        PokemonCobbledClient.initialize(this)
        CobbledNetwork.register()
        layerDefinitions.forEach { (location, definition) ->
            EntityModelLayerRegistry.registerModelLayer(location) {
                definition.get()
            }
        }

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(object : SimpleSynchronousResourceReloadListener {
            override fun getFabricId() = cobbledResource("resources")
            override fun reload(resourceManager: ResourceManager) { PokemonCobbledClient.reloadCodedAssets() }
        })
    }

    override fun registerLayer(modelLayer: EntityModelLayer, supplier: Supplier<TexturedModelData>) {
        EntityModelLayerRegistry.registerModelLayer(modelLayer) { supplier.get() }
    }
}