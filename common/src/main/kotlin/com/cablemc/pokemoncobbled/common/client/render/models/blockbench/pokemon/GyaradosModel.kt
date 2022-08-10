package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.WaveAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.WaveSegment
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.getChildOf
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.X_AXIS
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.Y_AXIS
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.wavefunction.sineFunction
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.withPosition
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.withRotation
import com.cablemc.pokemoncobbled.common.entity.PoseType
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import net.minecraft.client.model.Dilation
import net.minecraft.client.model.ModelData
import net.minecraft.client.model.ModelPart
import net.minecraft.client.model.ModelPartBuilder
import net.minecraft.client.model.ModelTransform
import net.minecraft.client.model.TexturedModelData
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.util.math.Vec3d

class GyaradosModel(root: ModelPart) : PokemonPoseableModel() {
    override val rootPart = registerRelevantPart("gyarados", root.getChild("gyarados"))
    val spine = registerRelevantPart("tailjoint", rootPart.getChild("tailjoint"))
    val spineFinal = registerRelevantPart("spinefinal", spine.getChild("spinefinal"))
    val spine3 = registerRelevantPart("spine3", spineFinal.getChild("spine3"))
    val spine2 = registerRelevantPart("spine2", spine3.getChild("spine2"))
    val spine1 = registerRelevantPart("spine", spine2.getChild("spine"))
    val bodyJoint = registerRelevantPart("bodyJoint", spine1.getChild("bodyjoint"))
    val body = registerRelevantPart("body", bodyJoint.getChild("body"))
    val tail = registerRelevantPart("tail", body.getChild("tail"))
    val tail2 = registerRelevantPart("tail2", tail.getChild("tail2"))
    val tail3 = registerRelevantPart("tail3", tail2.getChild("tail3"))
    val tail4 = registerRelevantPart("tail4", tail3.getChild("tail4"))
    val tail5 = registerRelevantPart("tail5", tail4.getChild("tail5"))
    val tail6 = registerRelevantPart("tail6", tail5.getChild("tail6"))
    val tail7 = registerRelevantPart("tail7", tail6.getChild("tail7"))
    val head = registerRelevantPart("head", rootPart.getChildOf("head"))

    val spineFinalWaveSegment = WaveSegment(modelPart = spineFinal, length = 6F)
    val spine3WaveSegment = WaveSegment(modelPart = spine3, length = 6F)
    val spineWaveSegment = WaveSegment(modelPart = spine1, length = 8F)
    val spine2WaveSegment = WaveSegment(modelPart = spine2, length = 7F)
    val bodyWaveSegment = WaveSegment(modelPart = body, length = 9F)
    val tailWaveSegment = WaveSegment(modelPart = tail, length = 7F)
    val tail2WaveSegment = WaveSegment(modelPart = tail2, length = 7F)
    val tail3WaveSegment = WaveSegment(modelPart = tail3, length = 6F)
    val tail4WaveSegment = WaveSegment(modelPart = tail4, length = 4F)
    val tail5WaveSegment = WaveSegment(modelPart = tail5, length = 4F)
    val tail6WaveSegment = WaveSegment(modelPart = tail6, length = 4F)
    val tail7WaveSegment = WaveSegment(modelPart = tail7, length = 15F)

    override val portraitScale = 1.9F
    override val portraitTranslation = Vec3d(-1.8, 1.4, 0.0)
    override val profileScale = 0.4F
    override val profileTranslation = Vec3d(0.0, 0.5, 0.0)

    override fun registerPoses() {
        registerPose(
            poseType = PoseType.WALK,
            { !it.isSubmergedInWater },
            idleAnimations = arrayOf(
                WaveAnimation(
                    frame = this,
                    waveFunction = sineFunction(
                        period = 8F,
                        amplitude = 0.4F
                    ),
                    basedOnLimbSwing = true,
                    oscillationsScalar = 8F,
                    head = spine,
                    rotationAxis = Y_AXIS,
                    motionAxis = X_AXIS,
                    headLength = 0.1F,
                    segments = arrayOf(
                        bodyWaveSegment,
                        tailWaveSegment,
                        tail2WaveSegment,
                        tail3WaveSegment,
                        tail4WaveSegment,
                        tail5WaveSegment,
                        tail6WaveSegment,
                        tail7WaveSegment
                    )
                )
            ),
            transformedParts = arrayOf(
                rootPart.withPosition(0F, -2F, 16F),
                spineFinal.withRotation(X_AXIS, (-60F).toRadians()),
                spine3.withRotation(X_AXIS, (-12.5F).toRadians()),
                spine2.withRotation(X_AXIS, (-10F).toRadians()),
                spine.withRotation(X_AXIS, 7.5F.toRadians()),
                bodyJoint.withRotation(X_AXIS, 75F.toRadians()).withPosition(Y_AXIS, 2F),
                head.withRotation(X_AXIS, (-62.5F).toRadians())
            )
        )
        registerPose(
            poseType = PoseType.SWIM,
            { it.isSubmergedInWater },
            idleAnimations = arrayOf(
                WaveAnimation(
                    frame = this,
                    waveFunction = sineFunction(
                        period = 3F,
                        amplitude = 0.4F
                    ),
                    oscillationsScalar = 24F,
                    head = rootPart,
                    rotationAxis = X_AXIS,
                    motionAxis = Y_AXIS,
                    headLength = 4F,
                    moveHead = true,
                    segments = arrayOf(
                        spineFinalWaveSegment,
                        spine3WaveSegment,
                        spine2WaveSegment,
                        spineWaveSegment,
                        bodyWaveSegment,
                        tailWaveSegment,
                        tail2WaveSegment,
                        tail3WaveSegment,
                        tail4WaveSegment,
                        tail5WaveSegment,
                        tail6WaveSegment,
                        tail7WaveSegment
                    )
                ),
            )
        )
    }

    companion object {
        val LAYER_LOCATION: EntityModelLayer = EntityModelLayer(cobbledResource("gyarados"), "main")
        fun createBodyLayer(): TexturedModelData {
            val meshdefinition = ModelData()
            val partdefinition = meshdefinition.root

            val gyarados = partdefinition.addChild(
                "gyarados",
                ModelPartBuilder.create(),
                ModelTransform.pivot(0.0f, 24f, 32f)
            )

            val tailjoint = gyarados.addChild(
                "tailjoint",
                ModelPartBuilder.create().uv(65, 35)
                    .cuboid(-3.0f, -3.5f, 0.0f, 6.0f, 7.0f, 4.0f, Dilation(0.02f)),
                ModelTransform.pivot(0.0f, -3.5f, -38.0f)
            )

            val spinefinal = tailjoint.addChild(
                "spinefinal",
                ModelPartBuilder.create().uv(26, 52)
                    .cuboid(-3.0f, -3.5f, 0.0f, 6.0f, 7.0f, 6.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 0.0f, 4.0f)
            )

            val spine3 = spinefinal.addChild(
                "spine3",
                ModelPartBuilder.create().uv(0, 47)
                    .cuboid(-3.5f, -3.5f, 0.0f, 7.0f, 7.0f, 6.0f, Dilation(0.02f))
                    .uv(0, 0).cuboid(0.0f, -10.5f, 0.0f, 0.0f, 7.0f, 6.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 0.0f, 6.0f)
            )

            val spine2 = spine3.addChild(
                "spine2",
                ModelPartBuilder.create().uv(28, 38)
                    .cuboid(-3.5f, -3.5f, 0.0f, 7.0f, 7.0f, 7.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 0.0f, 6.0f)
            )

            val spine = spine2.addChild(
                "spine",
                ModelPartBuilder.create().uv(24, 23)
                    .cuboid(-3.5f, -3.5f, 0.0f, 7.0f, 7.0f, 8.0f, Dilation(0.02f)),
                ModelTransform.pivot(0.0f, 0.0f, 7.0f)
            )

            val bodyjoint = spine.addChild(
                "bodyjoint",
                ModelPartBuilder.create(),
                ModelTransform.pivot(0.0f, 0.0f, 8.0f)
            )

            val body = bodyjoint.addChild(
                "body",
                ModelPartBuilder.create().uv(0, 15)
                    .cuboid(-3.5f, -3.5f, 0.0f, 7.0f, 7.0f, 9.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 0.0f, 0.0f)
            )

            val tail = body.addChild(
                "tail",
                ModelPartBuilder.create().uv(37, 0)
                    .cuboid(-3.5f, -3.5f, 0.0f, 7.0f, 7.0f, 7.0f, Dilation(0.02f)),
                ModelTransform.pivot(0.0f, 0.0f, 9.0f)
            )

            val tail2 = tail.addChild(
                "tail2",
                ModelPartBuilder.create().uv(46, 14)
                    .cuboid(-3.0f, -3.5f, 0.0f, 6.0f, 7.0f, 7.0f, Dilation(0.0f))
                    .uv(0, 51).cuboid(0.0f, -11.5f, -1.0f, 0.0f, 8.0f, 9.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 0.0f, 7.0f)
            )

            val tail3 = tail2.addChild(
                "tail3",
                ModelPartBuilder.create().uv(65, 0)
                    .cuboid(-2.5f, -3.0f, 0.0f, 5.0f, 6.0f, 6.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 0.5f, 7.0f)
            )

            val tail4 = tail3.addChild(
                "tail4",
                ModelPartBuilder.create().uv(65, 12)
                    .cuboid(-2.0f, -2.5f, 0.0f, 4.0f, 5.0f, 4.0f, Dilation(0.0f))
                    .uv(50, 65).cuboid(0.0f, -8.5f, -0.5f, 0.0f, 6.0f, 5.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 0.5f, 6.0f)
            )

            val tail5 = tail4.addChild(
                "tail5",
                ModelPartBuilder.create().uv(34, 65)
                    .cuboid(-2.0f, -2.0f, 0.0f, 4.0f, 4.0f, 4.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 0.5f, 4.0f)
            )

            val tail6 = tail5.addChild(
                "tail6",
                ModelPartBuilder.create().uv(70, 24)
                    .cuboid(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 4.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 0.5f, 4.0f)
            )

            val tail7 = tail6.addChild(
                "tail7",
                ModelPartBuilder.create().uv(0, 0)
                    .cuboid(-5.5f, 0.0f, 0.0f, 11.0f, 0.0f, 15.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 0.0f, 4.0f)
            )

            val head = gyarados.addChild(
                "head",
                ModelPartBuilder.create(),
                ModelTransform.of(0.0f, -3.25f, -36.0f, -1.2654f, 0.0f, 0.0f)
            )

            val head_r1 = head.addChild(
                "head_r1",
                ModelPartBuilder.create().uv(0, 15)
                    .cuboid(-7.0f, -4.5f, 3.5f, 3.0f, 9.0f, 0.0f, Dilation(0.0f))
                    .uv(60, 70).cuboid(3.0f, -4.5f, 3.5f, 3.0f, 9.0f, 0.0f, Dilation(0.0f))
                    .uv(0, 31).cuboid(-4.0f, -4.5f, -0.5f, 7.0f, 9.0f, 7.0f, Dilation(0.0f)),
                ModelTransform.of(0.5f, -0.5f, -4.5f, -0.3491f, 0.0f, 0.0f)
            )

            val head_r2 = head.addChild(
                "head_r2",
                ModelPartBuilder.create().uv(18, 57)
                    .cuboid(0.0f, -2.75f, -1.75f, 0.0f, 7.0f, 8.0f, Dilation(0.0f)),
                ModelTransform.of(0.0f, -3.4918f, -9.7961f, -0.5236f, 0.0f, 0.0f)
            )

            val skull = head.addChild("skull", ModelPartBuilder.create(), ModelTransform.pivot(0.0f, 0.0f, 0.0f))

            val head_r3 = skull.addChild(
                "head_r3",
                ModelPartBuilder.create().uv(0, 0)
                    .cuboid(-1.5f, 0.25f, -3.0f, 3.0f, 0.0f, 5.0f, Dilation(0.0f))
                    .uv(32, 0).cuboid(7.5f, 0.25f, -3.0f, 3.0f, 0.0f, 5.0f, Dilation(0.0f)),
                ModelTransform.of(-4.5f, 8.6356f, -3.4774f, -0.4363f, 0.0f, 0.0f)
            )

            val head_r4 = skull.addChild(
                "head_r4",
                ModelPartBuilder.create().uv(44, 28)
                    .cuboid(-2.25f, 0.25f, -1.25f, 4.0f, 0.0f, 2.0f, Dilation(0.0f))
                    .uv(18, 47).cuboid(10.25f, 0.25f, -1.25f, 4.0f, 0.0f, 2.0f, Dilation(0.0f)),
                ModelTransform.of(-6.0f, 6.1691f, -6.6184f, -1.1345f, 0.0f, 0.0f)
            )

            val head_r5 = skull.addChild(
                "head_r5",
                ModelPartBuilder.create().uv(50, 58)
                    .cuboid(-2.5f, 4.5f, 1.5f, 6.0f, 6.0f, 6.0f, Dilation(0.0f))
                    .uv(51, 47).cuboid(-3.5f, 4.5f, -3.5f, 8.0f, 6.0f, 5.0f, Dilation(0.0f))
                    .uv(0, 7).cuboid(-2.0f, 10.5f, 0.5f, 0.0f, 2.0f, 6.0f, Dilation(0.02f))
                    .uv(54, 29).cuboid(3.0f, 10.5f, 0.5f, 0.0f, 2.0f, 6.0f, Dilation(0.02f)),
                ModelTransform.of(-0.5f, -0.5f, -4.5f, -0.3491f, 0.0f, 0.0f)
            )

            val head_r6 = skull.addChild(
                "head_r6",
                ModelPartBuilder.create().uv(15, 15)
                    .cuboid(-5.5f, -1.0f, -4.0f, 11.0f, 0.0f, 8.0f, Dilation(0.0f)),
                ModelTransform.of(0.0f, 8.7674f, -12.6619f, -0.3927f, 0.0f, 0.0f)
            )

            val head_r7 = skull.addChild(
                "head_r7",
                ModelPartBuilder.create().uv(57, 1)
                    .cuboid(-2.5f, 1.25f, 0.75f, 5.0f, 0.0f, 1.0f, Dilation(0.0f))
                    .uv(68, 58).cuboid(-2.5f, -1.75f, -1.25f, 5.0f, 3.0f, 2.0f, Dilation(0.0f)),
                ModelTransform.of(0.0f, 10.9473f, -8.1344f, -0.5236f, 0.0f, 0.0f)
            )

            val head_r8 = skull.addChild(
                "head_r8",
                ModelPartBuilder.create().uv(0, 68)
                    .cuboid(-2.5f, -5.25f, 0.0f, 5.0f, 6.0f, 2.0f, Dilation(0.0f))
                    .uv(57, 0).cuboid(-2.5f, 0.75f, -1.0f, 5.0f, 0.0f, 1.0f, Dilation(0.0f)),
                ModelTransform.of(0.0f, 13.1508f, -2.8175f, -0.0436f, 0.0f, 0.0f)
            )

            val leftheadfinfront = skull.addChild(
                "leftheadfinfront",
                ModelPartBuilder.create(),
                ModelTransform.of(3.0f, 7.461f, -3.2999f, -0.0475f, -0.346f, -0.6898f)
            )

            val leftheadfinfront_r1 = leftheadfinfront.addChild(
                "leftheadfinfront_r1",
                ModelPartBuilder.create().uv(47, 28)
                    .cuboid(3.5f, 6.75f, 0.5f, 8.0f, 0.0f, 7.0f, Dilation(0.0f)),
                ModelTransform.of(-3.5f, -7.711f, -1.4501f, -0.3491f, 0.0f, 0.0f)
            )

            val rightheadfinfront = skull.addChild(
                "rightheadfinfront",
                ModelPartBuilder.create(),
                ModelTransform.of(-3.0f, 7.461f, -3.2999f, -0.0475f, 0.346f, 0.6898f)
            )

            val rightheadfinfront_r1 = rightheadfinfront.addChild(
                "rightheadfinfront_r1",
                ModelPartBuilder.create().uv(42, 38)
                    .cuboid(-11.5f, 6.75f, 0.5f, 8.0f, 0.0f, 7.0f, Dilation(0.0f)),
                ModelTransform.of(3.5f, -7.711f, -1.4501f, -0.3491f, 0.0f, 0.0f)
            )

            val leftwhisker = skull.addChild(
                "leftwhisker",
                ModelPartBuilder.create(),
                ModelTransform.of(3.0f, 10.6372f, -2.0507f, 0.0873f, 0.0f, 0.0f)
            )

            val leftwhisker_r1 = leftwhisker.addChild(
                "leftwhisker_r1",
                ModelPartBuilder.create().uv(19, 52)
                    .cuboid(3.5f, 9.5f, 4.5f, 5.0f, 0.0f, 1.0f, Dilation(0.0f)),
                ModelTransform.of(-3.5f, -10.6372f, -1.4493f, -0.3491f, 0.0f, 0.0f)
            )

            val leftwhiskermid = leftwhisker.addChild(
                "leftwhiskermid",
                ModelPartBuilder.create(),
                ModelTransform.pivot(5.0f, 0.0f, 0.0f)
            )

            val leftwhiskermid_r1 = leftwhiskermid.addChild(
                "leftwhiskermid_r1",
                ModelPartBuilder.create().uv(0, 5)
                    .cuboid(8.5f, 9.5f, 4.5f, 5.0f, 0.0f, 1.0f, Dilation(0.0f)),
                ModelTransform.of(-8.5f, -10.6372f, -1.4493f, -0.3491f, 0.0f, 0.0f)
            )

            val leftwhiskertip = leftwhiskermid.addChild(
                "leftwhiskertip",
                ModelPartBuilder.create(),
                ModelTransform.pivot(5.0f, 0.033f, 0.0603f)
            )

            val leftwhiskertip_r1 = leftwhiskertip.addChild(
                "leftwhiskertip_r1",
                ModelPartBuilder.create().uv(42, 14)
                    .cuboid(13.5f, 9.5f, 2.5f, 4.0f, 0.0f, 3.0f, Dilation(0.0f)),
                ModelTransform.of(-13.5f, -10.6702f, -1.5096f, -0.3491f, 0.0f, 0.0f)
            )

            val rightwhisker = skull.addChild(
                "rightwhisker",
                ModelPartBuilder.create(),
                ModelTransform.of(-3.0f, 10.6372f, -2.0507f, 0.0873f, 0.0f, 0.0f)
            )

            val rightwhisker_r1 = rightwhisker.addChild(
                "rightwhisker_r1",
                ModelPartBuilder.create().uv(55, 46)
                    .cuboid(-8.5f, 9.5f, 4.5f, 5.0f, 0.0f, 1.0f, Dilation(0.0f)),
                ModelTransform.of(3.5f, -10.6372f, -1.4493f, -0.3491f, 0.0f, 0.0f)
            )

            val rightwhiskermid = rightwhisker.addChild(
                "rightwhiskermid",
                ModelPartBuilder.create(),
                ModelTransform.pivot(-5.0f, 0.0f, 0.0f)
            )

            val rightwhiskermid_r1 = rightwhiskermid.addChild(
                "rightwhiskermid_r1",
                ModelPartBuilder.create().uv(53, 37)
                    .cuboid(-13.5f, 9.5f, 4.5f, 5.0f, 0.0f, 1.0f, Dilation(0.0f)),
                ModelTransform.of(8.5f, -10.6372f, -1.4493f, -0.3491f, 0.0f, 0.0f)
            )

            val rightwhiskertip = rightwhiskermid.addChild(
                "rightwhiskertip",
                ModelPartBuilder.create(),
                ModelTransform.pivot(-5.0f, 0.033f, 0.0603f)
            )

            val rightwhiskertip_r1 = rightwhiskertip.addChild(
                "rightwhiskertip_r1",
                ModelPartBuilder.create().uv(42, 17)
                    .cuboid(-17.5f, 9.5f, 2.5f, 4.0f, 0.0f, 3.0f, Dilation(0.0f)),
                ModelTransform.of(13.5f, -10.6702f, -1.5096f, -0.3491f, 0.0f, 0.0f)
            )

            return TexturedModelData.of(meshdefinition, 128, 128)
        }
    }
}