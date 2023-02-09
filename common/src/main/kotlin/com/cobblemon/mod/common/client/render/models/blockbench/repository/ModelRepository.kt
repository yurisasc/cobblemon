/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.repository

import com.cobblemon.mod.common.client.render.models.blockbench.BlockBenchModelWrapper
import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityModel
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.entity.Entity
import net.minecraft.resource.ResourceManager

abstract class ModelRepository<T : Entity> {

    private val _models: MutableList<BlockBenchModelWrapper<T>> = mutableListOf()
    val models: List<BlockBenchModelWrapper<T>>
        get() = _models.toList()

    fun addModel(model: BlockBenchModelWrapper<T>) {
        _models.add(model)
    }

    open fun initializeModelLayers() {
        _models.forEach { it.initializeModelLayers() }
    }

    open fun initializeModels(context: EntityRendererFactory.Context) {
        _models.forEach { it.initializeModel(context) }
    }

    abstract fun registerAll()

    fun init() {
        registerAll()
        initializeModelLayers()
    }

    open fun reload(resourceManager: ResourceManager) {
        _models.filter { it.isModelInitialized }.forEach {
            val model = it.entityModel
            if (model is PoseableEntityModel<*>) {
                model.poses.clear()
                model.registerPoses()
            }
        }
    }
}