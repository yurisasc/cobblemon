package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.EarJoint
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.RangeOfMotion
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.*
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.BimanualFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.BipedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.EaredFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.wavefunction.sineFunction
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.withRotation
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.world.phys.Vec3


class WartortleModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, BipedFrame, BimanualFrame, EaredFrame {
    override val rootPart = registerRelevantPart("wartortle", root.getChild("wartortle"))
    val body = registerRelevantPart("body", rootPart.getChild("body"))
    override val head = registerRelevantPart("head", body.getChild("head"))
    override val rightLeg = registerRelevantPart("rightleg", body.getChild("rightleg"))
    override val leftLeg = registerRelevantPart("leftleg", body.getChild("leftleg"))
    override val rightArm = registerRelevantPart("rightarm", body.getChild("rightarm"))
    override val leftArm = registerRelevantPart("leftarm", body.getChild("leftarm"))
    private val rightEar = registerRelevantPart("rightear", head.getChild("rightear"))
    private val leftEar = registerRelevantPart("leftear", head.getChild("leftear"))
    override val leftEarJoint = EarJoint(leftEar, TransformedModelPart.Z_AXIS, RangeOfMotion(50F.toRadians(), 0F))
    override val rightEarJoint = EarJoint(rightEar, TransformedModelPart.Z_AXIS, RangeOfMotion((-50F).toRadians(), 0F))
    private val tail = registerRelevantPart("tail", body.getChild("tail"))

    override fun registerPoses() {
        registerPose(
                poseType = PoseType.WALK,
                condition = { true },
                idleAnimations = arrayOf(
                        BipedWalkAnimation(this),
                        BimanualSwingAnimation(this),
                        SingleBoneLookAnimation(this),
                        tail.rotation(
                                function = sineFunction(
                                        amplitude = 0.4F,
                                        period = 5F
                                ),
                                axis = TransformedModelPart.Y_AXIS,
                                timeVariable = { _, _, ageInTicks -> ageInTicks / 20 },
                        )
                ),
                transformedParts = arrayOf(
                        leftArm.withRotation(2, 70f.toRadians()),
                        rightArm.withRotation(2, (-70f).toRadians()),
                )
        )
    }

    override val portraitScale = 1.6F
    override val portraitTranslation = Vec3(-0.05, 0.40, 0.0)

    companion object {
        val LAYER_LOCATION = ModelLayerLocation(cobbledResource("wartortle"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root
            val wartortle = partdefinition.addOrReplaceChild(
                "wartortle",
                CubeListBuilder.create(),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )
            val body = wartortle.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-5.0f, -6.0f, -4.0f, 10.0f, 12.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -11.0f, 0.0f)
            )
            val tail = body.addOrReplaceChild(
                "tail",
                CubeListBuilder.create().texOffs(0, 7)
                    .addBox(0.0f, -13.0f, 0.0f, 0.0f, 16.0f, 13.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 3.0f, 4.0f)
            )
            val leftarm = body.addOrReplaceChild(
                "leftarm",
                CubeListBuilder.create().texOffs(28, 0)
                    .addBox(-1.0f, -2.0f, -2.0f, 8.0f, 4.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(5.0f, -3.5f, -1.5f, 0.0057f, 0.0001f, -0.0001f)
            )
            val rightarm = body.addOrReplaceChild(
                "rightarm",
                CubeListBuilder.create().texOffs(22, 35)
                    .addBox(-7.0f, -2.0f, -2.0f, 8.0f, 4.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(-5.0f, -3.5f, -1.5f)
            )
            val leftleg = body.addOrReplaceChild(
                "leftleg",
                CubeListBuilder.create().texOffs(36, 8)
                    .addBox(-2.0f, -1.0f, -2.0f, 4.0f, 7.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(3.5f, 5.0f, -1.0f)
            )
            val rightleg = body.addOrReplaceChild(
                "rightleg",
                CubeListBuilder.create().texOffs(0, 36)
                    .addBox(-2.0f, -1.0f, -2.0f, 4.0f, 7.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(-3.5f, 5.0f, -1.0f)
            )
            val head = body.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(26, 20)
                    .addBox(-4.5f, -7.0f, -4.0f, 9.0f, 7.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -6.0f, -1.5f, 0.0003f, 0.0003f, -0.0076f)
            )
            val head_r1 = head.addOrReplaceChild(
                "head_r1",
                CubeListBuilder.create().texOffs(26, 20).mirror()
                    .addBox(-1.5f, -2.0f, 0.0f, 3.0f, 4.0f, 0.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(2.75f, -4.0f, -4.05f, 0.0f, 0.0f, -0.0873f)
            )
            val head_r2 = head.addOrReplaceChild(
                "head_r2",
                CubeListBuilder.create().texOffs(26, 20)
                    .addBox(-1.5f, -2.0f, 0.0f, 3.0f, 4.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-2.75f, -4.0f, -4.05f, 0.0f, 0.0f, 0.0873f)
            )
            val leftear = head.addOrReplaceChild(
                "leftear",
                CubeListBuilder.create().texOffs(16, 38)
                    .addBox(0.0493f, -8.9483f, -0.1887f, 0.0f, 9.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(4.475f, -4.925f, -1.975f, -0.2224f, 0.0756f, 0.0693f)
            )
            val rightear = head.addOrReplaceChild(
                "rightear",
                CubeListBuilder.create().texOffs(26, 38)
                    .addBox(-0.0493f, -8.9483f, -0.1887f, 0.0f, 9.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-4.475f, -4.925f, -1.975f, -0.2224f, -0.0756f, -0.0693f)
            )
            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }
}