package com.cobblemon.mod.common.client.render.models.blockbench.npc

import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatefulAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatelessAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.ModelFrame
import com.cobblemon.mod.common.entity.npc.NPCEntity



@FunctionalInterface
fun interface TrainerAnimationProvider {
    operator fun invoke(state: PoseableEntityState<NPCEntity>): StatefulAnimation<NPCEntity, ModelFrame>?
}

@FunctionalInterface
fun interface TrainerStatelessAnimationProvider {
    operator fun invoke(entity: NPCEntity?, state: PoseableEntityState<NPCEntity>?): StatelessAnimation<NPCEntity, ModelFrame>?
}