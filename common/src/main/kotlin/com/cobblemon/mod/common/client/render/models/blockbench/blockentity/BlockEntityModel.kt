package com.cobblemon.mod.common.client.render.models.blockbench.blockentity

import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityModel
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatelessAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.ModelFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Bone
import com.cobblemon.mod.common.entity.PoseType
import net.minecraft.entity.Entity

class BlockEntityModel(override val rootPart: Bone) : PoseableEntityModel<Entity>() {
    override val isForLivingEntityRenderer = false
    var animations: Array<StatelessAnimation<Entity, out ModelFrame>> = emptyArray()
    var maxScale = 1F
    var yTranslation = 0F
    override fun registerPoses() {
        registerPose(
            poseType = PoseType.NONE,
            idleAnimations = arrayOf(bedrock("gilded_chest", "closing"))
        )
        registerPose(
            poseType = PoseType.OPEN,
            idleAnimations = arrayOf(bedrock("gilded_chest", "opening"))
        )

    }

    override fun getState(entity: Entity) = throw NotImplementedError("This is not supported for the gilded chest")
}