package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.QuadrupedWalkAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.asTransformed
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.*
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.Y_AXIS
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonBehaviourFlag
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class CaterpieModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame {
    override val rootPart = root.registerChildWithSpecificChildren("caterpie", listOf("body","body_segment","body_segment2","tail","tail2","tail3","head","tail4"))
    override val head = getPart("head")

    override val portraitScale = 1.95F
    override val portraitTranslation = Vec3d(-0.3, -1.3, 0.0)
    override val profileScale = 1.0F
    override val profileTranslation = Vec3d(0.0, 0.0, 0.0)
    override fun registerPoses() {
        registerPose(
            poseName = "standing",
            poseTypes = setOf(PoseType.NONE, PoseType.PROFILE),
            transformTicks = 10,
            condition = { !it.isMoving.get() },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("caterpie", "ground_idle")
            ),
            transformedParts = arrayOf()
        )

        registerPose(
            poseType = PoseType.WALK,
            transformTicks = 10,
            condition = { it.isMoving.get() },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("caterpie", "ground_idle"),
                bedrock("caterpie", "ground_walk")
            ),
            transformedParts = emptyArray()
        )
    }
}