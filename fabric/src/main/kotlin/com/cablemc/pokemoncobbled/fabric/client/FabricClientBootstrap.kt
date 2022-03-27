package com.cablemc.pokemoncobbled.fabric.client

import com.cablemc.pokemoncobbled.common.CobbledEntities
import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.PokemonCobbledClientImplementation
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.render.CobbledLayerDefinitions.layerDefinitions
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import dev.architectury.init.fabric.ArchitecturyClient
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.ResourceManager
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

        EntityRendererRegistry.register(CobbledEntities.POKEMON_TYPE) { PokemonCobbledClient.registerPokemonRenderer(it) }
        EntityRendererRegistry.register(CobbledEntities.EMPTY_POKEBALL_TYPE) { PokemonCobbledClient.registerPokeBallRenderer(it) }

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(object : SimpleSynchronousResourceReloadListener {
            override fun getFabricId() = cobbledResource("resources")
            override fun onResourceManagerReload(resourceManager: ResourceManager) { PokemonCobbledClient.reloadCodedAssets() }
        })
    }

    override fun registerLayer(layerLocation: ModelLayerLocation, supplier: Supplier<LayerDefinition>) {
        EntityModelLayerRegistry.registerModelLayer(layerLocation) { supplier.get() }
    }
}