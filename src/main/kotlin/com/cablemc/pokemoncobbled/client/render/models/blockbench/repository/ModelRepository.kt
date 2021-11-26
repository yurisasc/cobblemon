package com.cablemc.pokemoncobbled.client.render.models.blockbench.repository

import com.cablemc.pokemoncobbled.client.render.models.blockbench.BlockBenchModelWrapper
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.world.entity.Entity

abstract class ModelRepository<T : Entity> {

    private val _models: MutableList<BlockBenchModelWrapper<T>> = mutableListOf()
    val models: List<BlockBenchModelWrapper<T>>
        get() = _models.toList()

    fun addModel(model: BlockBenchModelWrapper<T>) {
        _models.add(model)
    }

    fun initializeModelLayers() {
        _models.forEach { it.initializeModelLayers() }
    }

    fun initializeModels(context: EntityRendererProvider.Context) {
        _models.forEach { it.initializeModel(context) }
    }

}