package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.EarJoint
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.RangeOfMotion
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.QuadrupedWalkAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.SingleBoneLookAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.EaredFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.QuadrupedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.getChildOf
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.Z_AXIS
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.world.phys.Vec3d
class RattataModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, QuadrupedFrame, EaredFrame {
    override val rootPart = registerRelevantPart("rattata", root.getChild("rattata"))
    override val foreLeftLeg = registerRelevantPart("leftfrontleg", rootPart.getChildOf("body", "leftfrontleg"))
    override val hindLeftLeg = registerRelevantPart("leftbackleg", rootPart.getChildOf("body", "leftbackleg"))
    override val hindRightLeg = registerRelevantPart("rightbackleg", rootPart.getChildOf("body", "rightbackleg"))
    override val foreRightLeg = registerRelevantPart("rightfrontleg", rootPart.getChildOf("body", "rightfrontleg"))
    override val head = registerRelevantPart("head", rootPart.getChildOf("body", "head"))
    val eyes = registerRelevantPart("eyes", rootPart.getChildOf("body", "head", "eyes"))
    override val leftEarJoint: EarJoint = EarJoint(
        registerRelevantPart("leftear", rootPart.getChildOf("body", "head", "leftear")),
        Z_AXIS,
        RangeOfMotion(
            0F.toRadians(),
            -20F.toRadians()
        )
    )
    override val rightEarJoint: EarJoint = EarJoint(
        registerRelevantPart("rightear", rootPart.getChildOf("body", "head", "rightear")),
        Z_AXIS,
        RangeOfMotion(
            0F.toRadians(),
            20F.toRadians()
        )
    )

    val tail = registerRelevantPart("tail", rootPart.getChildOf("body", "tail"))
    val tail2 = registerRelevantPart("tail2", rootPart.getChildOf("body", "tail", "tail2"))
    override val portraitScale = 2.5F
    override val portraitTranslation = Vec3d-0.25, -2.03, 0.0)

    override val profileScale = 1.0F
    override val profileTranslation = Vec3d0.0, 0.0, 0.0)

    init {
        registerPoses()
    }

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
    }

    companion object {
        val LAYER_LOCATION = ModelLayerLocation(cobbledResource("rattata"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root
            val rattata =
                partdefinition.addOrReplaceChild(
                    "rattata",
                    CubeListBuilder.create(),
                    PartPose.offset(0.0f, 24.0f, 0.0f)
                )
            val body = rattata.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-2.5f, -2.0f, -4.0f, 5.0f, 4.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -4.0f, 0.0f)
            )
            val head = body.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 12)
                    .addBox(-2.5f, -3.25f, -3.75f, 5.0f, 5.0f, 4.0f, CubeDeformation(0.02f))
                    .texOffs(16, 23).addBox(-1.5f, -1.0f, -5.75f, 3.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(0, 12).addBox(-1.0f, 0.5f, -5.25f, 2.0f, 2.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offset(0.0f, -1.0f, -3.5f)
            )
            val cube_r1 = head.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create().texOffs(18, 7)
                    .addBox(-2.5f, -0.5f, 0.25f, 5.0f, 1.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(-5.0f, 0.25f, -1.75f, 0.0f, 0.2618f, 0.0873f)
            )
            val cube_r2 = head.addOrReplaceChild(
                "cube_r2",
                CubeListBuilder.create().texOffs(24, 0)
                    .addBox(-2.5f, -0.5f, 0.25f, 5.0f, 1.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(5.0f, 0.25f, -1.75f, 0.0f, -0.2618f, -0.0873f)
            )
            val eyes = head.addOrReplaceChild("eyes", CubeListBuilder.create(), PartPose.offset(0.0f, -1.25f, -2.5f))
            val cube_r3 = eyes.addOrReplaceChild(
                "cube_r3",
                CubeListBuilder.create().texOffs(0, 12)
                    .addBox(0.0f, -1.0f, -1.0f, 0.0f, 2.0f, 2.0f, CubeDeformation(0.02f))
                    .texOffs(0, 12).mirror().addBox(-5.05f, -1.0f, -1.0f, 0.0f, 2.0f, 2.0f, CubeDeformation(0.02f))
                    .mirror(false),
                PartPose.offsetAndRotation(2.525f, 0.0f, 0.0f, 0.0436f, 0.0f, 0.0f)
            )
            val leftear =
                head.addOrReplaceChild("leftear", CubeListBuilder.create(), PartPose.offset(2.25f, -2.25f, -1.0f))
            val cube_r4 = leftear.addOrReplaceChild(
                "cube_r4",
                CubeListBuilder.create().texOffs(24, 12)
                    .addBox(0.25f, -10.0f, -3.5f, 4.0f, 4.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(-2.0f, 7.25f, 3.5f, 0.1187f, -0.5168f, -0.1507f)
            )
            val rightear =
                head.addOrReplaceChild("rightear", CubeListBuilder.create(), PartPose.offset(-2.25f, -2.25f, -1.0f))
            val cube_r5 = rightear.addOrReplaceChild(
                "cube_r5",
                CubeListBuilder.create().texOffs(0, 23)
                    .addBox(-4.25f, -10.0f, -3.5f, 4.0f, 4.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(2.0f, 7.25f, 3.5f, 0.1189f, 0.5187f, 0.1509f)
            )
            val tail = body.addOrReplaceChild(
                "tail",
                CubeListBuilder.create().texOffs(0, 19)
                    .addBox(0.0f, 0.0f, 3.0f, 0.0f, 1.0f, 3.0f, CubeDeformation(0.02f))
                    .texOffs(0, 18).addBox(0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 3.0f, CubeDeformation(0.02f)),
                PartPose.offset(0.0f, -1.0f, 4.0f)
            )
            val tail2 = tail.addOrReplaceChild(
                "tail2",
                CubeListBuilder.create().texOffs(14, 7)
                    .addBox(0.0f, -3.0f, 1.0f, 0.0f, 4.0f, 5.0f, CubeDeformation(0.02f)),
                PartPose.offset(0.0f, 0.0f, 5.0f)
            )
            val leftbackleg = body.addOrReplaceChild(
                "leftbackleg",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.0f, -1.0f, -1.0f, 2.0f, 5.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(1.75f, 0.0f, 2.75f)
            )
            val leftfrontleg = body.addOrReplaceChild(
                "leftfrontleg",
                CubeListBuilder.create().texOffs(18, 16)
                    .addBox(-1.0f, -1.0f, -1.0f, 2.0f, 5.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(1.75f, 0.0f, -2.5f)
            )
            val rightbackleg = body.addOrReplaceChild(
                "rightbackleg",
                CubeListBuilder.create().texOffs(18, 0)
                    .addBox(-1.0f, -1.0f, -1.0f, 2.0f, 5.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(-1.75f, 0.0f, 2.75f)
            )
            val rightfrontleg = body.addOrReplaceChild(
                "rightfrontleg",
                CubeListBuilder.create().texOffs(8, 21)
                    .addBox(-1.0f, -1.0f, -1.0f, 2.0f, 5.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(-1.75f, 0.0f, -2.5f)
            )
            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }
}
