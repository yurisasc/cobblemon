package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.entity.PokemonClientDelegate
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.EarJoint
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.RangeOfMotion
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.QuadrupedWalkAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.SingleBoneLookAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.EaredFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.QuadrupedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.Z_AXIS
import com.cablemc.pokemoncobbled.common.client.render.pokemon.PokemonRenderer.Companion.DELTA_TICKS
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d

class EeveeModel(root: ModelPart) : PokemonPoseableModel(), EaredFrame, HeadedFrame, QuadrupedFrame {
    override val rootPart = root.registerChildWithAllChildren("eevee")
    val body = getPart("body")
    override val head = getPart("head")
    override val hindRightLeg = getPart("leg_back_right1")
    override val hindLeftLeg = getPart("leg_back_left1")
    override val foreRightLeg = getPart("leg_front_right1")
    override val foreLeftLeg = getPart("leg_front_left1")
    private val tail = getPart("tail")
    override val leftEarJoint = EarJoint(getPart("ear_left"), Z_AXIS, RangeOfMotion(50F.toRadians(), 0F))
    override val rightEarJoint = EarJoint(getPart("ear_right"), Z_AXIS, RangeOfMotion((-50F).toRadians(), 0F))

    override val portraitScale = 1.55F
    override val portraitTranslation = Vec3d(-0.15, 0.1, 0.0)

    override val profileScale = 1.0F
    override val profileTranslation = Vec3d(0.0, 0.0, 0.0)

    override fun registerPoses() {
        registerPose(
            poseType = PoseType.WALK,
            condition = { true },
            idleAnimations = arrayOf(
                SingleBoneLookAnimation(this),
                QuadrupedWalkAnimation(this)
            )
        )

        registerShoulderPoses(
            condition = { true },
            idleAnimations = arrayOf(SingleBoneLookAnimation(this))
        )
    }

    override fun setAngles(entity: PokemonEntity, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, headYaw: Float, headPitch: Float) {
        super.setAngles(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch)
        val clientDelegate = entity.delegate as PokemonClientDelegate
        if (entity.isMoving.get()) {
            clientDelegate.animTick += DELTA_TICKS * 4
            if (clientDelegate.animTick > TAIL_ANIMATION_TOTAL) {
                clientDelegate.animTick = 0F
            }
        } else {
            clientDelegate.animTick = 0F
        }
        tail.yaw = MathHelper.sin(clientDelegate.animTick * 6 * Math.PI.toFloat() / 180) * Math.PI.toFloat() / 7
    }

    companion object {
        private const val TAIL_ANIMATION_TOTAL = 60F
    }
}