package com.cobblemon.mod.common.client.render.block

import com.cobblemon.mod.common.block.entity.BerryBlockEntity
import com.cobblemon.mod.common.client.render.layer.CobblemonRenderLayers
import com.cobblemon.mod.common.client.render.models.blockbench.repository.BerryModelRepository
import com.jozufozu.flywheel.api.MaterialManager
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance
import com.jozufozu.flywheel.core.Materials
import com.jozufozu.flywheel.core.materials.model.ModelData

class BerryEntityInstance(val materialManager: MaterialManager, val entity: BerryBlockEntity) : BlockEntityInstance<BerryBlockEntity>(materialManager, entity) {
    val models: MutableList<ModelData> = mutableListOf()
    override fun init() {
        super.init()
        entity.berryAndGrowthPoint().forEach {
            val newModel = BerryModelRepository.modelOf(it.first.fruitModelIdentifier)
            if (newModel != null) {
                val instancedModel = materialManager
                    .solid(CobblemonRenderLayers.BERRY_LAYER)
                    .material(Materials.TRANSFORMED)
                    .model(newModel.name()) { newModel }
                    .createInstance()
                models.add(instancedModel)
                val identity = instancedModel.loadIdentity()
                identity.translate(instancePosition)
                identity.translate(it.second.position.multiply(1.0/16.0))
                identity.rotateX(it.second.rotation.x)
                identity.rotateY(it.second.rotation.y)
                identity.rotateZ(it.second.rotation.z)
            }

        }
    }

    override fun updateLight() {
        relight(worldPosition, models.stream())
    }
    override fun remove() {
        models.forEach {
            it.delete()
        }
    }

}