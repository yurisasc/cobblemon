package com.cobblemon.mod.common.client.render.block

import com.cobblemon.mod.common.CobblemonBlocks
import com.cobblemon.mod.common.block.entity.BerryBlockEntity
import com.cobblemon.mod.common.client.render.models.blockbench.repository.BerryModelRepository
import com.jozufozu.flywheel.api.Instancer
import com.jozufozu.flywheel.api.MaterialManager
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance
import com.jozufozu.flywheel.core.Materials
import com.jozufozu.flywheel.core.materials.model.ModelData
import com.jozufozu.flywheel.core.model.BlockModel
import com.jozufozu.flywheel.core.model.Model
import net.minecraft.block.Blocks

class BerryEntityInstance(val materialManager: MaterialManager, val entity: BerryBlockEntity) : BlockEntityInstance<BerryBlockEntity>(materialManager, entity) {
    var baseModel: ModelData = materialManager.defaultSolid()
        .material(Materials.TRANSFORMED)
        .getModel(CobblemonBlocks.APRICORN_PLANKS.defaultState)
        .createInstance()

    var modelTwo: ModelData = materialManager.defaultSolid()
        .material(Materials.TRANSFORMED)
        .getModel(CobblemonBlocks.APRICORN_PLANKS.defaultState)
        .createInstance()


    var modelThree: ModelData = materialManager.defaultSolid()
        .material(Materials.TRANSFORMED)
        .getModel(CobblemonBlocks.APRICORN_PLANKS.defaultState)
        .createInstance()

    var modelFour: ModelData = materialManager.defaultSolid()
        .material(Materials.TRANSFORMED)
        .getModel(CobblemonBlocks.APRICORN_PLANKS.defaultState)
        .createInstance()

    var modelFive: ModelData = materialManager.defaultSolid()
        .material(Materials.TRANSFORMED)
        .getModel(CobblemonBlocks.APRICORN_PLANKS.defaultState)
        .createInstance()
    init {
        baseModel.loadIdentity().translate(instancePosition).scale(0.5f)
        modelTwo.loadIdentity().translate(instancePosition).translate(0.0, 1.0, 0.0)
        modelThree.loadIdentity().translate(instancePosition).translate(0.5, 1.0, 0.0)
        modelFour.loadIdentity().translate(instancePosition).translate(0.5, 0.0, 0.0)
        modelFive.loadIdentity().translate(instancePosition).translate(0.5, 1.0, 0.5)
    }

    override fun updateLight() {
        relight(worldPosition, baseModel, modelTwo, modelThree, modelFour, modelFive)
    }
    override fun remove() {
        baseModel.delete()
        modelTwo.delete()
        modelThree.delete()
        modelFour.delete()
        modelFive.delete()
    }

}