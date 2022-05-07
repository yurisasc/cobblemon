package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.*
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.BimanualFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.BipedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.withRotation
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import net.minecraft.client.model.*
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.util.math.Vec3d

class CharmanderModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, BipedFrame, BimanualFrame {
    override val rootPart = registerRelevantPart("charmander", root.getChild("charmander"))
    val body = registerRelevantPart("body", rootPart.getChild("body"))
    override val head = registerRelevantPart("head", body.getChild("head"))
    override val rightLeg = registerRelevantPart("rightleg", body.getChild("rightleg"))
    override val leftLeg = registerRelevantPart("leftleg", body.getChild("leftleg"))
    override val rightArm = registerRelevantPart("rightarm", body.getChild("rightarm"))
    override val leftArm = registerRelevantPart("leftarm", body.getChild("leftarm"))

    private val tail = registerRelevantPart("tail", body.getChild("tail"))
    private val tailTip = registerRelevantPart("tail2", tail.getChild("tail2"))
    private val tailFlame = registerRelevantPart("fire", tailTip.getChild("fire"))

    override val portraitScale = 1.5F
    override val portraitTranslation = Vec3d(0.05, 0.3, 0.0)

    override val profileScale = 1.0F
    override val profileTranslation = Vec3d(0.0, 0.0, 0.0)

    override fun registerPoses() {
        registerPose(
            poseType = PoseType.WALK,
            condition = { true },
            idleAnimations = arrayOf(
                BipedWalkAnimation(this),
                BimanualSwingAnimation(this),
                SingleBoneLookAnimation(this),
                CascadeAnimation(
                    frame = this,
                    rootFunction = cosineFunction(
                        period = 0.09f
                    ),
                    amplitudeFunction = gradualFunction(
                        base = 0.1f,
                        step = 0.1f
                    ),
                    segments = arrayOf(
                        tail,
                        tailTip
                    )
                )
            ),
            transformedParts = arrayOf(
                leftArm.withRotation(2, 70f.toRadians()),
                rightArm.withRotation(2, (-70f).toRadians()),
                tailTip.withRotation(0, 35f.toRadians()),
                tailFlame.withRotation(0, (-35f).toRadians())
            )
        )
    }

    companion object {
        val LAYER_LOCATION = EntityModelLayer(cobbledResource("charmander"), "main")
        fun createBodyLayer(): TexturedModelData {
            val meshdefinition = ModelData()
            val partdefinition = meshdefinition.root
            val charmander = partdefinition.addChild(
                "charmander",
                ModelPartBuilder.create(),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f)
            )
            val body = charmander.addChild(
                "body",
                ModelPartBuilder.create().uv(23, 20)
                    .cuboid(-4.0f, -5.5f, -2.5f, 8.0f, 11.0f, 5.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, -8.5f, 0.0f)
            )
            val head = body.addChild(
                "head",
                ModelPartBuilder.create().uv(0, 0)
                    .cuboid(-4.0f, -7.0f, -3.0f, 8.0f, 7.0f, 6.0f, Dilation(0.0f))
                    .uv(18, 36).cuboid(-3.5f, -3.0f, -5.0f, 7.0f, 3.0f, 2.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, -5.5f, 0.0f)
            )

            val tail = body.addChild(
                "tail",
                ModelPartBuilder.create().uv(0, 14)
                    .cuboid(-2.0F, -2.0F, -1.0F, 4.0F, 4.0F, 7.0F, Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 3.25F, 2.5F)
            )

            val tail2 = tail.addChild("tail2",
                ModelPartBuilder.create().uv(42, 53).cuboid(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 7.0F, Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.5F, 5.0F)
            )

            val fire = tail2.addChild("fire",
                ModelPartBuilder.create().uv(54, 0)
                    .cuboid(0.0F, -8.0F, -2.5F, 0.0F, 8.0F, 5.0F, Dilation(0.0F)),
                ModelTransform.pivot(0.0F, -1.5F, 6.5F)
            )

            val leftarm = body.addChild(
                "leftarm",
                ModelPartBuilder.create().uv(22, 0)
                    .cuboid(-1.0f, -1.5f, -1.5f, 7.0f, 3.0f, 3.0f, Dilation(0.0f)),
                ModelTransform.pivot(4.0f, -3.5f, 0.0f)
            )
            val rightarm = body.addChild(
                "rightarm",
                ModelPartBuilder.create().uv(22, 0).mirrored()
                    .cuboid(-6.0f, -1.5f, -1.5f, 7.0f, 3.0f, 3.0f, Dilation(0.0f)).mirrored(false),
                ModelTransform.pivot(-4.0f, -3.5f, 0.0f)
            )
            val leftleg = body.addChild(
                "leftleg",
                ModelPartBuilder.create().uv(28, 6)
                    .cuboid(-1.5f, -1.0f, -2.0f, 3.0f, 6.0f, 4.0f, Dilation(0.0f))
                    .uv(55, 0).cuboid(-1.25f, 4.0f, -3.0f, 0.0f, 1.0f, 1.0f, Dilation(0.0f))
                    .uv(55, 0).cuboid(0.0f, 4.0f, -3.0f, 0.0f, 1.0f, 1.0f, Dilation(0.0f))
                    .uv(55, 0).cuboid(1.25f, 4.0f, -3.0f, 0.0f, 1.0f, 1.0f, Dilation(0.0f)),
                ModelTransform.pivot(3.0f, 3.5f, 0.0f)
            )
            val rightleg = body.addChild(
                "rightleg",
                ModelPartBuilder.create().uv(0, 25)
                    .cuboid(-1.5f, -1.0f, -2.0f, 3.0f, 6.0f, 4.0f, Dilation(0.0f))
                    .uv(55, 0).cuboid(0.0f, 4.0f, -3.0f, 0.0f, 1.0f, 1.0f, Dilation(0.0f))
                    .uv(55, 0).cuboid(1.25f, 4.0f, -3.0f, 0.0f, 1.0f, 1.0f, Dilation(0.0f))
                    .uv(55, 0).cuboid(-1.25f, 4.0f, -3.0f, 0.0f, 1.0f, 1.0f, Dilation(0.0f)),
                ModelTransform.pivot(-3.0f, 3.5f, 0.0f)
            )
            return TexturedModelData.of(meshdefinition, 64, 64)
        }
    }
}