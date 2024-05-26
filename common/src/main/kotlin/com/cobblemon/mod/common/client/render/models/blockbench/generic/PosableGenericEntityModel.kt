package com.cobblemon.mod.common.client.render.models.blockbench.generic

import com.cobblemon.mod.common.client.render.models.blockbench.PosableEntityModel
import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext
import com.cobblemon.mod.common.entity.generic.GenericBedrockEntity
import net.minecraft.entity.Entity

class PosableGenericEntityModel : PosableEntityModel<GenericBedrockEntity>() {
    override fun setupEntityTypeContext(entity: Entity?) {
        super.setupEntityTypeContext(entity)
        val entity = entity as? GenericBedrockEntity ?: return
        context.put(RenderContext.ASPECTS, entity.aspects)
    }
}