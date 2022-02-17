package com.cablemc.pokemoncobbled.common.client.render

import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.builders.LayerDefinition
import java.util.function.Supplier

object CobbledLayerDefinitions {

    val layerDefinitions = mutableMapOf<ModelLayerLocation, Supplier<LayerDefinition>>()

    fun registerLayerDefinition(layerLocation: ModelLayerLocation, supplier: Supplier<LayerDefinition>) {
        layerDefinitions[layerLocation] = supplier
    }
}