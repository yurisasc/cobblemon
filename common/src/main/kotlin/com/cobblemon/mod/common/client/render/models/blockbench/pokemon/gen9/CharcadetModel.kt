package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen9

// import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BimanualFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BipedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
// import com.cobblemon.mod.common.entity.PoseType
// import com.cobblemon.mod.common.entity.PoseType.Companion.MOVING_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.STATIONARY_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.UI_POSES
// import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class CharcadetModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, BipedFrame, BimanualFrame {
    override val rootPart = root.registerChildWithAllChildren("charcadet")
    override val head = getPart("head")
    override val rightArm = getPart("arm_right")
    override val leftArm = getPart("arm_left")
    override val rightLeg = getPart("leg_right")
    override val leftLeg = getPart("leg_left")

    override val portraitScale = 1.5F
    override val portraitTranslation = Vec3d(0.05, 0.3, 0.0)

    override val profileScale = 0.9F
    override val profileTranslation = Vec3d(0.0, 0.35, 0.0)

    lateinit var standing: PokemonPose

    override fun registerPoses() {
        val blink = quirk("blink") { bedrockStateful("charcadet", "blink").setPreventsIdle(false) }
        standing = registerPose(
                poseName = "standing",
                poseTypes = STATIONARY_POSES + UI_POSES,
                transformTicks = 10,
                quirks = arrayOf(blink),
                idleAnimations = arrayOf(
                        singleBoneLook(),
                        bedrock("charcadet", "ground_idle")
                )
        )
    }
}