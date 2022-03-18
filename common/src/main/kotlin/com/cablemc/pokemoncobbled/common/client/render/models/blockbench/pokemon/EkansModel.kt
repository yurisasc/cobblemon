package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.SingleBoneLookAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.WaveAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.WaveSegment
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.X_AXIS
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.Y_AXIS
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.wavefunction.sineFunction
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.world.phys.Vec3

class EkansModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame {

    override val rootPart = registerRelevantPart("ekans", root.getChild("ekans"))
    private val body = registerRelevantPart("body", rootPart.getChild("body"))
    override val head = registerRelevantPart("head", body.getChild("head"))
    private val tail = registerRelevantPart("tail", body.getChild("tail"))
    private val tail2 = registerRelevantPart("tail2", tail.getChild("tail2"))
    private val tail3 = registerRelevantPart("tail3", tail2.getChild("tail3"))
    private val tail4 = registerRelevantPart("tail4", tail3.getChild("tail4"))
    private val tail5 = registerRelevantPart("tail5", tail4.getChild("tail5"))
    private val tail6 = registerRelevantPart("tail6", tail5.getChild("tail6"))

    val tailSegment = WaveSegment(modelPart = tail, length = 9F)
    val tail2Segment = WaveSegment(modelPart = tail2, length = 9F)
    val tail3Segment = WaveSegment(modelPart = tail3, length = 9F)
    val tail4Segment = WaveSegment(modelPart = tail4, length = 9F)
    val tail5Segment = WaveSegment(modelPart = tail5, length = 10F)
    val tail6Segment = WaveSegment(modelPart = tail6, length = 10F)

    override val portraitScale = 1.85F
    override val portraitTranslation = Vec3(-1.3, -0.75, 0.0)
    override val profileScale = 1.0F
    override val profileTranslation = Vec3(0.0, 0.0, 0.0)

    override fun registerPoses() {
        registerPose(
            poseType = PoseType.WALK,
            condition = { true },
            idleAnimations = arrayOf(
                SingleBoneLookAnimation(this),
                WaveAnimation(
                    frame = this,
                    waveFunction = sineFunction(
                        period = 8F,
                        amplitude = 0.8F
                    ),
                    basedOnLimbSwing = true,
                    oscillationsScalar = 5F,
                    head = head,
                    rotationAxis = Y_AXIS,
                    motionAxis = X_AXIS,
                    moveHead = false,
                    headLength = 16F,
                    segments = arrayOf(
                        tailSegment,
                        tail2Segment,
                        tail3Segment,
                        tail4Segment,
                        tail5Segment,
                        tail6Segment
                    )
                )
            ),
            transformedParts = arrayOf(

            )
        )
    }

    companion object {
        val LAYER_LOCATION = ModelLayerLocation(cobbledResource("ekans"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root
            val ekans =
                partdefinition.addOrReplaceChild("ekans", CubeListBuilder.create(), PartPose.offset(0.5f, 24.0f, 0.0f))
            val body = ekans.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-2.5f, -2.5f, -4.5f, 5.0f, 5.0f, 9.0f, CubeDeformation(0.0f)),
                PartPose.offset(-0.5f, -2.5f, -13.5f)
            )
            val head = body.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(36, 22)
                    .addBox(-3.0f, 1.9f, -7.0f, 6.0f, 1.0f, 4.0f, CubeDeformation(0.0f))
                    .texOffs(0, 3).addBox(-3.0f, -1.1f, -6.0f, 0.0f, 3.0f, 3.0f, CubeDeformation(0.02f))
                    .texOffs(36, 14).addBox(-3.0f, -4.1f, -8.0f, 6.0f, 3.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(0, 46).addBox(-3.0f, -4.1f, -3.0f, 6.0f, 7.0f, 3.0f, CubeDeformation(0.0f))
                    .texOffs(0, 0).addBox(3.0f, -1.1f, -6.0f, 0.0f, 3.0f, 3.0f, CubeDeformation(0.02f)),
                PartPose.offset(0.0f, -0.4f, -4.5f)
            )
            val eyes = head.addOrReplaceChild("eyes", CubeListBuilder.create(), PartPose.offset(0.0f, -2.6f, -3.5f))
            val cube_r1 = eyes.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create().texOffs(58, 32)
                    .addBox(0.0f, -1.0f, -1.5f, 0.0f, 2.0f, 3.0f, CubeDeformation(0.0f))
                    .texOffs(58, 32).mirror().addBox(6.1f, -1.0f, -1.5f, 0.0f, 2.0f, 3.0f, CubeDeformation(0.0f))
                    .mirror(false),
                PartPose.offsetAndRotation(-3.05f, 0.0f, 0.0f, 0.0873f, 0.0f, 0.0f)
            )
            val tail = body.addOrReplaceChild(
                "tail",
                CubeListBuilder.create().texOffs(28, 32)
                    .addBox(-2.5f, -2.5f, 0.0f, 5.0f, 5.0f, 9.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 4.5f)
            )
            val tail2 = tail.addOrReplaceChild(
                "tail2",
                CubeListBuilder.create().texOffs(0, 32)
                    .addBox(-2.5f, -2.5f, 0.0f, 5.0f, 5.0f, 9.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 9.0f)
            )
            val tail3 = tail2.addOrReplaceChild(
                "tail3",
                CubeListBuilder.create().texOffs(28, 0)
                    .addBox(-2.5f, -2.5f, 0.0f, 5.0f, 5.0f, 9.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 9.0f)
            )
            val tail4 = tail3.addOrReplaceChild(
                "tail4",
                CubeListBuilder.create().texOffs(0, 14)
                    .addBox(-2.5f, -2.5f, 0.0f, 5.0f, 5.0f, 9.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 9.0f)
            )
            val tail5 = tail4.addOrReplaceChild(
                "tail5",
                CubeListBuilder.create().texOffs(18, 18)
                    .addBox(-2.0f, -2.0f, 0.0f, 4.0f, 4.0f, 10.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.5f, 9.0f)
            )
            val tail6 = tail5.addOrReplaceChild(
                "tail6",
                CubeListBuilder.create().texOffs(19, 4)
                    .addBox(0.0f, -1.5f, 0.0f, 0.0f, 3.0f, 10.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 10.0f)
            )
            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }
}