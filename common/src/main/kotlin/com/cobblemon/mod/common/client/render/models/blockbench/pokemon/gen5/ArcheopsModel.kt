package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen5

import com.cobblemon.mod.common.client.render.models.blockbench.animation.BimanualSwingAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.BipedWalkAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.WingFlapIdleAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BiWingedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BimanualFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BipedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.client.render.models.blockbench.pose.ModelPartTransformation
import com.cobblemon.mod.common.client.render.models.blockbench.wavefunction.sineFunction
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.util.math.geometry.toRadians
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class ArcheopsModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, BipedFrame, BimanualFrame, BiWingedFrame {
    override val rootPart = root.registerChildWithAllChildren("archeops")
    override val head = getPart("neck")

    override val leftLeg = getPart("leg_left")
    override val rightLeg = getPart("leg_right")

    override val leftArm = getPart("arm_left")
    override val rightArm = getPart("arm_right")

    override val leftWing = getPart("arm_left")
    override val rightWing = getPart("arm_right")

    override val portraitScale = 1.8F
    override val portraitTranslation = Vec3d(0.0, 0.2, 0.0)

    override val profileScale = 0.85F
    override val profileTranslation = Vec3d(0.0, 0.7, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walking: PokemonPose
    lateinit var hovering: PokemonPose
    lateinit var flying: PokemonPose

    override fun registerPoses() {

        standing = registerPose(
            poseName = "standing",
            poseTypes = PoseType.STATIONARY_POSES + PoseType.UI_POSES - PoseType.HOVER,
            transformTicks = 10,
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("archeops", "ground_idle")
            )
        )

        walking = registerPose(
            poseName = "walking",
            poseTypes = PoseType.MOVING_POSES - PoseType.FLY,
            transformTicks = 10,
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("archeops", "ground_idle"),
                BipedWalkAnimation(this, 0.5F, 0.5F),
                BimanualSwingAnimation(this, 0.5F, 0.5F)
            )
        )

        hovering = registerPose(
            poseName = "hovering",
            poseType = PoseType.HOVER,
            transformTicks = 10,
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("archeops", "ground_idle"),
                WingFlapIdleAnimation(this,
                    flapFunction = sineFunction(verticalShift = (-14F).toRadians(), period = 0.9F, amplitude = 0.7F),
                    timeVariable = { state, _, _ -> state?.animationSeconds ?: 0F },
                    axis = ModelPartTransformation.X_AXIS
                )
            )
        )

        flying = registerPose(
            poseName = "flying",
            poseType = PoseType.FLY,
            transformTicks = 10,
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("archeops", "ground_idle"),
                WingFlapIdleAnimation(this,
                    flapFunction = sineFunction(verticalShift = (-18F).toRadians(), period = 0.9F, amplitude = 0.8F),
                    timeVariable = { state, _, _ -> state?.animationSeconds ?: 0F },
                    axis = ModelPartTransformation.X_AXIS
                )
            )
        )
    }
//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PoseableEntityState<PokemonEntity>
//    ) = if (state.isPosedIn(standing, walking)) bedrockStateful("archeops", "faint") else null
}