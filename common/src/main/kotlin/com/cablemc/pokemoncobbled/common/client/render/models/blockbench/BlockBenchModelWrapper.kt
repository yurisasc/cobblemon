package com.cablemc.pokemoncobbled.common.client.render.models.blockbench

import com.cablemc.pokemoncobbled.common.client.render.CobbledLayerDefinitions
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.world.entity.Entity

/**
 * Wraps around a model generated by BlockBench for easy registration in repositories.
 */
class BlockBenchModelWrapper<T : Entity>(
    val layerLocation: ModelLayerLocation,
    val layerDefinitionSupplier: () -> LayerDefinition,
    val modelFactory: (ModelPart) -> EntityModel<T>
) {

    lateinit var entityModel: EntityModel<T>
    var isModelInitialized = false
        private set
    var isLayerInitialized = false
        private set

    fun initializeModel(context: EntityRendererProvider.Context) {
        entityModel = modelFactory(context.bakeLayer(layerLocation)).also {
            if (it is PoseableEntityModel<T>) {
                it.registerPoses()
            }
        }
        isModelInitialized = true
    }

    fun initializeModelLayers() {
        if (!isLayerInitialized) {
            CobbledLayerDefinitions.registerLayerDefinition(layerLocation, layerDefinitionSupplier)
            isLayerInitialized = true
        }
    }
}