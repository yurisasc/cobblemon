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
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import net.minecraft.client.model.*
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d

class EeveeModel(root: ModelPart) : PokemonPoseableModel(), EaredFrame, HeadedFrame, QuadrupedFrame {
    override val rootPart = registerRelevantPart("eevee", root.getChild("eevee"))
    val body = registerRelevantPart("body", rootPart.getChild("body"))
    override val head = registerRelevantPart("head", body.getChild("head"))
    override val hindRightLeg = registerRelevantPart("rightbackleg", rootPart.getChild("body").getChild("rightbackleg"))
    override val hindLeftLeg = registerRelevantPart("leftbackleg", rootPart.getChild("body").getChild("leftbackleg"))
    override val foreRightLeg = registerRelevantPart("rightleg", rootPart.getChild("body").getChild("rightleg"))
    override val foreLeftLeg = registerRelevantPart("leftlag", rootPart.getChild("body").getChild("leftleg"))
    private val tail = registerRelevantPart("tail", rootPart.getChild("body").getChild("tail"))
    private val leftEar = registerRelevantPart("leftear", head.getChild("leftear"))
    private val rightEar = registerRelevantPart("rightear", head.getChild("rightear"))
    override val leftEarJoint = EarJoint(leftEar, Z_AXIS, RangeOfMotion(50F.toRadians(), 0F))
    override val rightEarJoint = EarJoint(rightEar, Z_AXIS, RangeOfMotion((-50F).toRadians(), 0F))

    override val portraitScale = 1.55F
    override val portraitTranslation = Vec3d(-0.15, 0.1, 0.0)

    override val profileScale = 1.0F
    override val profileTranslation = Vec3d(0.0, 0.0, 0.0)

    override fun registerPoses() {
        registerPose(
            poseType = PoseType.WALK,
            condition = { true },
            idleAnimations = arrayOf(
                QuadrupedWalkAnimation(this),
                SingleBoneLookAnimation(this)
            ),
            transformedParts = arrayOf()
        )

        registerShoulderPoses(
            condition = { true },
            idleAnimations = arrayOf(SingleBoneLookAnimation(this)),
            transformedParts = arrayOf()
        )
    }

    override fun setAngles(entity: PokemonEntity, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, pNetHeadYaw: Float, pHeadPitch: Float) {
        super.setAngles(entity, limbSwing, limbSwingAmount, ageInTicks, pNetHeadYaw, pHeadPitch)
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
        val LAYER_LOCATION = EntityModelLayer(cobbledResource("eevee"), "main")
        fun createBodyLayer(): TexturedModelData {
            val meshDefinition = ModelData()
            val partDefinition = meshDefinition.root
            val eevee = partDefinition.addChild("eevee", ModelPartBuilder.create(), ModelTransform.pivot(0.0f, 24.0f, 0.0f))
            val body = eevee.addChild(
                "body",
                ModelPartBuilder.create().uv(0, 0).cuboid(-3.5f, -2.625f, -3.875f, 7.0f, 6.0f, 12.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, -8.375f, -2.125f)
            )
            val body_r1 = body.addChild(
                "body_r1",
                ModelPartBuilder.create().uv(26, 49).cuboid(-5.5f, -3.0f, -4.5f, 10.0f, 6.0f, 9.0f, Dilation(0.0f)),
                ModelTransform.of(0.5f, -0.375f, -2.125f, 0.5236f, 0.0f, 0.0f)
            )
            val tail = body.addChild("tail", ModelPartBuilder.create(), ModelTransform.pivot(0.0f, -0.225f, 7.125f))
            val tail_r1 = tail.addChild(
                "tail_r1",
                ModelPartBuilder.create().uv(0, 18).cuboid(-4.5f, -4.0f, -5.0f, 8.0f, 7.0f, 10.0f, Dilation(0.0f)),
                ModelTransform.of(0.5f, -2.4f, 3.0f, 0.7418f, 0.0f, 0.0f)
            )
            val leftbackleg = body.addChild(
                "leftbackleg",
                ModelPartBuilder.create().uv(14, 37).cuboid(-1.5f, -1.5f, -2.25f, 3.0f, 4.0f, 4.0f, Dilation(0.0f)).uv(28, 41).cuboid(-1.5f, 2.5f, -1.25f, 3.0f, 4.0f, 3.0f, Dilation(0.0f)),
                ModelTransform.pivot(2.25f, 1.875f, 6.05f)
            )
            val rightbackleg = body.addChild(
                "rightbackleg",
                ModelPartBuilder.create().uv(14, 37).mirrored()
                    .cuboid(-1.5f, -1.5f, -2.25f, 3.0f, 4.0f, 4.0f, Dilation(0.0f)).mirrored(false)
                    .uv(28, 41).mirrored().cuboid(-1.5f, 2.5f, -1.25f, 3.0f, 4.0f, 3.0f, Dilation(0.0f))
                    .mirrored(false),
                ModelTransform.pivot(-2.25f, 1.875f, 6.05f)
            )
            val rightleg = body.addChild(
                "rightleg",
                ModelPartBuilder.create().uv(0, 0).cuboid(-1.5f, -1.5f, -1.5f, 3.0f, 8.0f, 3.0f, Dilation(0.0f)),
                ModelTransform.pivot(-2.25f, 1.875f, -2.625f)
            )
            val leftleg = body.addChild(
                "leftleg",
                ModelPartBuilder.create().uv(26, 0).cuboid(-1.5f, -1.5f, -1.5f, 3.0f, 8.0f, 3.0f, Dilation(0.0f)),
                ModelTransform.pivot(2.25f, 1.875f, -2.625f)
            )
            val head = body.addChild(
                "head",
                ModelPartBuilder.create().uv(31, 11)
                    .cuboid(-4.0f, -6.5f, -4.25f, 8.0f, 8.0f, 7.0f, Dilation(0.0f))
                    .uv(62, 5).mirrored().cuboid(-0.5f, -1.5f, -4.275f, 1.0f, 1.0f, 0.0f, Dilation(0.0f))
                    .mirrored(false),
                ModelTransform.pivot(0.0f, -2.125f, -2.875f)
            )
            val pog = head.addChild(
                "pog",
                ModelPartBuilder.create().uv(1, 60).mirrored().cuboid(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 0.0f, Dilation(0.0f)).mirrored(false),
                ModelTransform.pivot(0.0f, 0.25f, -3.525f)
            )
            val mouth = head.addChild(
                "mouth",
                ModelPartBuilder.create().uv(60, 3).mirrored().cuboid(-1.0f, -0.5f, 0.0f, 2.0f, 1.0f, 0.0f, Dilation(0.0f)).mirrored(false),
                ModelTransform.pivot(0.0f, 0.25f, -4.275f)
            )
            val eyes = head.addChild(
                "eyes",
                ModelPartBuilder.create().uv(55, 0).mirrored()
                    .cuboid(1.5f, -1.75f, 0.0f, 2.0f, 3.0f, 0.0f, Dilation(0.0f)).mirrored(false)
                    .uv(55, 0).cuboid(-3.5f, -1.75f, 0.0f, 2.0f, 3.0f, 0.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, -2.75f, -4.3f)
            )
            val eyesback = head.addChild(
                "eyesback",
                ModelPartBuilder.create().uv(54, 5).mirrored()
                    .cuboid(1.5f, -0.5f, 0.0f, 2.0f, 1.0f, 0.0f, Dilation(0.0f)).mirrored(false)
                    .uv(54, 5).cuboid(-3.5f, -0.5f, 0.0f, 2.0f, 1.0f, 0.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, -2.75f, -4.275f)
            )
            val rightear = head.addChild("rightear", ModelPartBuilder.create(), ModelTransform.pivot(-3.5f, -4.75f, -0.25f))
            val rightear_r1 = rightear.addChild(
                "rightear_r1",
                ModelPartBuilder.create().uv(40, 41).mirrored().cuboid(-1.5f, -4.0f, 0.0f, 4.0f, 8.0f, 0.0f, Dilation(0.02f)).mirrored(false),
                ModelTransform.of(-1.75f, -3.75f, 0.0f, 0.0f, 0.0f, -0.6109f)
            )
            val leftear = head.addChild("leftear", ModelPartBuilder.create(), ModelTransform.pivot(3.5f, -4.75f, -0.25f))
            val leftear_r1 = leftear.addChild(
                "leftear_r1",
                ModelPartBuilder.create().uv(40, 41).cuboid(-2.5f, -4.0f, 0.0f, 4.0f, 8.0f, 0.0f, Dilation(0.02f)),
                ModelTransform.of(1.75f, -3.75f, 0.0f, 0.0f, 0.0f, 0.6109f)
            )
            return TexturedModelData.of(meshDefinition, 64, 64)
        }
    }
}