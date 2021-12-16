package com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.client.render.models.blockbench.animation.WaveAnimation
import com.cablemc.pokemoncobbled.client.render.models.blockbench.animation.WaveSegment
import com.cablemc.pokemoncobbled.client.render.models.blockbench.getChildOf
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pose.TransformedModelPart.Companion.X_AXIS
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pose.TransformedModelPart.Companion.Y_AXIS
import com.cablemc.pokemoncobbled.client.render.models.blockbench.wavefunction.sineFunction
import com.cablemc.pokemoncobbled.client.render.models.blockbench.withPosition
import com.cablemc.pokemoncobbled.client.render.models.blockbench.withRotation
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.resources.ResourceLocation


class GyaradosModel(root: ModelPart) : PokemonPoseableModel() {
    override val rootPart = registerRelevantPart(root.getChild("gyarados"))
    val spine = registerRelevantPart(rootPart.getChild("tailjoint"))
    val spineFinal = registerRelevantPart(spine.getChild("spinefinal"))
    val spine3 = registerRelevantPart(spineFinal.getChild("spine3"))
    val spine2 = registerRelevantPart(spine3.getChild("spine2"))
    val spine1 = registerRelevantPart(spine2.getChild("spine"))
    val bodyJoint = registerRelevantPart(spine1.getChild("bodyjoint"))
    val body = registerRelevantPart(bodyJoint.getChild("body"))
    val tail = registerRelevantPart(body.getChild("tail"))
    val tail2 = registerRelevantPart(tail.getChild("tail2"))
    val tail3 = registerRelevantPart(tail2.getChild("tail3"))
    val tail4 = registerRelevantPart(tail3.getChild("tail4"))
    val tail5 = registerRelevantPart(tail4.getChild("tail5"))
    val tail6 = registerRelevantPart(tail5.getChild("tail6"))
    val tail7 = registerRelevantPart(tail6.getChild("tail7"))
    val head = registerRelevantPart(rootPart.getChildOf("head"))

    val spineFinalWaveSegment = WaveSegment(
        modelPart = spineFinal,
        length = 6F
    )

    val spine3WaveSegment = WaveSegment(
        modelPart = spine3,
        length = 6F
    )

    val spineWaveSegment = WaveSegment(
        modelPart = spine1,
        length = 8F
    )

    val spine2WaveSegment = WaveSegment(
        modelPart = spine2,
        length = 7F
    )

    val bodyWaveSegment = WaveSegment(
        modelPart = body,
        length = 9F
    )
    val tailWaveSegment = WaveSegment(
        modelPart = tail,
        length = 7F
    )
    val tail2WaveSegment = WaveSegment(
        modelPart = tail2,
        length = 7F
    )
    val tail3WaveSegment = WaveSegment(
        modelPart = tail3,
        length = 6F
    )
    val tail4WaveSegment = WaveSegment(
        modelPart = tail4,
        length = 4F
    )
    val tail5WaveSegment = WaveSegment(
        modelPart = tail5,
        length = 4F
    )
    val tail6WaveSegment = WaveSegment(
        modelPart = tail6,
        length = 4F
    )
    val tail7WaveSegment = WaveSegment(
        modelPart = tail7,
        length = 15F
    )

    override fun registerPoses() {
        registerPose(
            poseType = PoseType.WALK,
            { !it.isUnderWater },
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
            { it.isUnderWater },
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
            ),
            transformedParts = arrayOf()
        )
    }


    companion object {
        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(ResourceLocation(PokemonCobbled.MODID, "gyarados"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val gyarados = partdefinition.addOrReplaceChild(
                "gyarados",
                CubeListBuilder.create(),
                PartPose.offset(0.0f, 24f, 32f)
            )

            val tailjoint = gyarados.addOrReplaceChild(
                "tailjoint",
                CubeListBuilder.create().texOffs(65, 35)
                    .addBox(-3.0f, -3.5f, 0.0f, 6.0f, 7.0f, 4.0f, CubeDeformation(0.02f)),
                PartPose.offset(0.0f, -3.5f, -38.0f)
            )

            val spinefinal = tailjoint.addOrReplaceChild(
                "spinefinal",
                CubeListBuilder.create().texOffs(26, 52)
                    .addBox(-3.0f, -3.5f, 0.0f, 6.0f, 7.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 4.0f)
            )

            val spine3 = spinefinal.addOrReplaceChild(
                "spine3",
                CubeListBuilder.create().texOffs(0, 47)
                    .addBox(-3.5f, -3.5f, 0.0f, 7.0f, 7.0f, 6.0f, CubeDeformation(0.02f))
                    .texOffs(0, 0).addBox(0.0f, -10.5f, 0.0f, 0.0f, 7.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 6.0f)
            )

            val spine2 = spine3.addOrReplaceChild(
                "spine2",
                CubeListBuilder.create().texOffs(28, 38)
                    .addBox(-3.5f, -3.5f, 0.0f, 7.0f, 7.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 6.0f)
            )

            val spine = spine2.addOrReplaceChild(
                "spine",
                CubeListBuilder.create().texOffs(24, 23)
                    .addBox(-3.5f, -3.5f, 0.0f, 7.0f, 7.0f, 8.0f, CubeDeformation(0.02f)),
                PartPose.offset(0.0f, 0.0f, 7.0f)
            )

            val bodyjoint = spine.addOrReplaceChild(
                "bodyjoint",
                CubeListBuilder.create(),
                PartPose.offset(0.0f, 0.0f, 8.0f)
            )

            val body = bodyjoint.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(0, 15)
                    .addBox(-3.5f, -3.5f, 0.0f, 7.0f, 7.0f, 9.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val tail = body.addOrReplaceChild(
                "tail",
                CubeListBuilder.create().texOffs(37, 0)
                    .addBox(-3.5f, -3.5f, 0.0f, 7.0f, 7.0f, 7.0f, CubeDeformation(0.02f)),
                PartPose.offset(0.0f, 0.0f, 9.0f)
            )

            val tail2 = tail.addOrReplaceChild(
                "tail2",
                CubeListBuilder.create().texOffs(46, 14)
                    .addBox(-3.0f, -3.5f, 0.0f, 6.0f, 7.0f, 7.0f, CubeDeformation(0.0f))
                    .texOffs(0, 51).addBox(0.0f, -11.5f, -1.0f, 0.0f, 8.0f, 9.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 7.0f)
            )

            val tail3 = tail2.addOrReplaceChild(
                "tail3",
                CubeListBuilder.create().texOffs(65, 0)
                    .addBox(-2.5f, -3.0f, 0.0f, 5.0f, 6.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.5f, 7.0f)
            )

            val tail4 = tail3.addOrReplaceChild(
                "tail4",
                CubeListBuilder.create().texOffs(65, 12)
                    .addBox(-2.0f, -2.5f, 0.0f, 4.0f, 5.0f, 4.0f, CubeDeformation(0.0f))
                    .texOffs(50, 65).addBox(0.0f, -8.5f, -0.5f, 0.0f, 6.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.5f, 6.0f)
            )

            val tail5 = tail4.addOrReplaceChild(
                "tail5",
                CubeListBuilder.create().texOffs(34, 65)
                    .addBox(-2.0f, -2.0f, 0.0f, 4.0f, 4.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.5f, 4.0f)
            )

            val tail6 = tail5.addOrReplaceChild(
                "tail6",
                CubeListBuilder.create().texOffs(70, 24)
                    .addBox(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.5f, 4.0f)
            )

            val tail7 = tail6.addOrReplaceChild(
                "tail7",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-5.5f, 0.0f, 0.0f, 11.0f, 0.0f, 15.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 4.0f)
            )

            val head = gyarados.addOrReplaceChild(
                "head",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0f, -3.25f, -36.0f, -1.2654f, 0.0f, 0.0f)
            )

            val head_r1 = head.addOrReplaceChild(
                "head_r1",
                CubeListBuilder.create().texOffs(0, 15)
                    .addBox(-7.0f, -4.5f, 3.5f, 3.0f, 9.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(60, 70).addBox(3.0f, -4.5f, 3.5f, 3.0f, 9.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(0, 31).addBox(-4.0f, -4.5f, -0.5f, 7.0f, 9.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.5f, -0.5f, -4.5f, -0.3491f, 0.0f, 0.0f)
            )

            val head_r2 = head.addOrReplaceChild(
                "head_r2",
                CubeListBuilder.create().texOffs(18, 57)
                    .addBox(0.0f, -2.75f, -1.75f, 0.0f, 7.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -3.4918f, -9.7961f, -0.5236f, 0.0f, 0.0f)
            )

            val skull = head.addOrReplaceChild("skull", CubeListBuilder.create(), PartPose.offset(0.0f, 0.0f, 0.0f))

            val head_r3 = skull.addOrReplaceChild(
                "head_r3",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.5f, 0.25f, -3.0f, 3.0f, 0.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(32, 0).addBox(7.5f, 0.25f, -3.0f, 3.0f, 0.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-4.5f, 8.6356f, -3.4774f, -0.4363f, 0.0f, 0.0f)
            )

            val head_r4 = skull.addOrReplaceChild(
                "head_r4",
                CubeListBuilder.create().texOffs(44, 28)
                    .addBox(-2.25f, 0.25f, -1.25f, 4.0f, 0.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(18, 47).addBox(10.25f, 0.25f, -1.25f, 4.0f, 0.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-6.0f, 6.1691f, -6.6184f, -1.1345f, 0.0f, 0.0f)
            )

            val head_r5 = skull.addOrReplaceChild(
                "head_r5",
                CubeListBuilder.create().texOffs(50, 58)
                    .addBox(-2.5f, 4.5f, 1.5f, 6.0f, 6.0f, 6.0f, CubeDeformation(0.0f))
                    .texOffs(51, 47).addBox(-3.5f, 4.5f, -3.5f, 8.0f, 6.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(0, 7).addBox(-2.0f, 10.5f, 0.5f, 0.0f, 2.0f, 6.0f, CubeDeformation(0.02f))
                    .texOffs(54, 29).addBox(3.0f, 10.5f, 0.5f, 0.0f, 2.0f, 6.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(-0.5f, -0.5f, -4.5f, -0.3491f, 0.0f, 0.0f)
            )

            val head_r6 = skull.addOrReplaceChild(
                "head_r6",
                CubeListBuilder.create().texOffs(15, 15)
                    .addBox(-5.5f, -1.0f, -4.0f, 11.0f, 0.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 8.7674f, -12.6619f, -0.3927f, 0.0f, 0.0f)
            )

            val head_r7 = skull.addOrReplaceChild(
                "head_r7",
                CubeListBuilder.create().texOffs(57, 1)
                    .addBox(-2.5f, 1.25f, 0.75f, 5.0f, 0.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(68, 58).addBox(-2.5f, -1.75f, -1.25f, 5.0f, 3.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 10.9473f, -8.1344f, -0.5236f, 0.0f, 0.0f)
            )

            val head_r8 = skull.addOrReplaceChild(
                "head_r8",
                CubeListBuilder.create().texOffs(0, 68)
                    .addBox(-2.5f, -5.25f, 0.0f, 5.0f, 6.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(57, 0).addBox(-2.5f, 0.75f, -1.0f, 5.0f, 0.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 13.1508f, -2.8175f, -0.0436f, 0.0f, 0.0f)
            )

            val leftheadfinfront = skull.addOrReplaceChild(
                "leftheadfinfront",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(3.0f, 7.461f, -3.2999f, -0.0475f, -0.346f, -0.6898f)
            )

            val leftheadfinfront_r1 = leftheadfinfront.addOrReplaceChild(
                "leftheadfinfront_r1",
                CubeListBuilder.create().texOffs(47, 28)
                    .addBox(3.5f, 6.75f, 0.5f, 8.0f, 0.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-3.5f, -7.711f, -1.4501f, -0.3491f, 0.0f, 0.0f)
            )

            val rightheadfinfront = skull.addOrReplaceChild(
                "rightheadfinfront",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(-3.0f, 7.461f, -3.2999f, -0.0475f, 0.346f, 0.6898f)
            )

            val rightheadfinfront_r1 = rightheadfinfront.addOrReplaceChild(
                "rightheadfinfront_r1",
                CubeListBuilder.create().texOffs(42, 38)
                    .addBox(-11.5f, 6.75f, 0.5f, 8.0f, 0.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.5f, -7.711f, -1.4501f, -0.3491f, 0.0f, 0.0f)
            )

            val leftwhisker = skull.addOrReplaceChild(
                "leftwhisker",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(3.0f, 10.6372f, -2.0507f, 0.0873f, 0.0f, 0.0f)
            )

            val leftwhisker_r1 = leftwhisker.addOrReplaceChild(
                "leftwhisker_r1",
                CubeListBuilder.create().texOffs(19, 52)
                    .addBox(3.5f, 9.5f, 4.5f, 5.0f, 0.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-3.5f, -10.6372f, -1.4493f, -0.3491f, 0.0f, 0.0f)
            )

            val leftwhiskermid = leftwhisker.addOrReplaceChild(
                "leftwhiskermid",
                CubeListBuilder.create(),
                PartPose.offset(5.0f, 0.0f, 0.0f)
            )

            val leftwhiskermid_r1 = leftwhiskermid.addOrReplaceChild(
                "leftwhiskermid_r1",
                CubeListBuilder.create().texOffs(0, 5)
                    .addBox(8.5f, 9.5f, 4.5f, 5.0f, 0.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-8.5f, -10.6372f, -1.4493f, -0.3491f, 0.0f, 0.0f)
            )

            val leftwhiskertip = leftwhiskermid.addOrReplaceChild(
                "leftwhiskertip",
                CubeListBuilder.create(),
                PartPose.offset(5.0f, 0.033f, 0.0603f)
            )

            val leftwhiskertip_r1 = leftwhiskertip.addOrReplaceChild(
                "leftwhiskertip_r1",
                CubeListBuilder.create().texOffs(42, 14)
                    .addBox(13.5f, 9.5f, 2.5f, 4.0f, 0.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-13.5f, -10.6702f, -1.5096f, -0.3491f, 0.0f, 0.0f)
            )

            val rightwhisker = skull.addOrReplaceChild(
                "rightwhisker",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(-3.0f, 10.6372f, -2.0507f, 0.0873f, 0.0f, 0.0f)
            )

            val rightwhisker_r1 = rightwhisker.addOrReplaceChild(
                "rightwhisker_r1",
                CubeListBuilder.create().texOffs(55, 46)
                    .addBox(-8.5f, 9.5f, 4.5f, 5.0f, 0.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.5f, -10.6372f, -1.4493f, -0.3491f, 0.0f, 0.0f)
            )

            val rightwhiskermid = rightwhisker.addOrReplaceChild(
                "rightwhiskermid",
                CubeListBuilder.create(),
                PartPose.offset(-5.0f, 0.0f, 0.0f)
            )

            val rightwhiskermid_r1 = rightwhiskermid.addOrReplaceChild(
                "rightwhiskermid_r1",
                CubeListBuilder.create().texOffs(53, 37)
                    .addBox(-13.5f, 9.5f, 4.5f, 5.0f, 0.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(8.5f, -10.6372f, -1.4493f, -0.3491f, 0.0f, 0.0f)
            )

            val rightwhiskertip = rightwhiskermid.addOrReplaceChild(
                "rightwhiskertip",
                CubeListBuilder.create(),
                PartPose.offset(-5.0f, 0.033f, 0.0603f)
            )

            val rightwhiskertip_r1 = rightwhiskertip.addOrReplaceChild(
                "rightwhiskertip_r1",
                CubeListBuilder.create().texOffs(42, 17)
                    .addBox(-17.5f, 9.5f, 2.5f, 4.0f, 0.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(13.5f, -10.6702f, -1.5096f, -0.3491f, 0.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}