package com.cobblemon.mod.common.client.entity

import com.cobblemon.mod.common.api.entity.EntitySideDelegate
import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.entity.generic.GenericBedrockEntity
import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity

class GenericBedrockClientDelegate : EntitySideDelegate<GenericBedrockEntity>, PoseableEntityState<GenericBedrockEntity>() {
    override fun initialize(entity: GenericBedrockEntity) {
        super.initialize(entity)
//        entity.poseType.subscribe {
//            this.setPose()
//        }
    }

    override fun tick(entity: GenericBedrockEntity) {
        super.tick(entity)
        updateLocatorPosition(entity.pos)
    }
}