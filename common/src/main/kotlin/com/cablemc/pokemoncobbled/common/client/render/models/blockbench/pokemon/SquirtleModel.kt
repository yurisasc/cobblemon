package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.*
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.BimanualFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.BipedFrame
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
import net.minecraft.world.phys.Vec3d

class SquirtleModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, BipedFrame, BimanualFrame {
    override val rootPart = registerRelevantPart("squirtle", root.getChild("squirtle"))
    val body = registerRelevantPart("body", rootPart.getChild("body"))
    override val head = registerRelevantPart("head", body.getChild("head"))
    override val rightLeg = registerRelevantPart("rightleg", body.getChild("rightleg"))
    override val leftLeg = registerRelevantPart("leftleg", body.getChild("leftleg"))
    override val rightArm = registerRelevantPart("rightarm", body.getChild("rightarm"))
    override val leftArm = registerRelevantPart("leftarm", body.getChild("leftarm"))
    private val tail = registerRelevantPart("tail", body.getChild("tail"))

    override val portraitScale = 1.6F
    override val portraitTranslation = Vec3d0.0, 0.10, 0.0)

    override val profileScale = 1.0F
    override val profileTranslation = Vec3d0.0, 0.0, 0.0)

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
                                        amplitude = 0.5F,
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

    companion object {
        val LAYER_LOCATION = ModelLayerLocation(cobbledResource("squirtle"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root
            val squirtle = partdefinition.addOrReplaceChild(
                "squirtle",
                CubeListBuilder.create(),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )
            val body = squirtle.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-4.0f, -5.0f, -3.5f, 8.0f, 10.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -8.0f, 0.0f)
            )
            val tail = body.addOrReplaceChild(
                "tail",
                CubeListBuilder.create().texOffs(0, 19)
                    .addBox(0.0f, -5.5f, 0.0f, 0.0f, 8.0f, 11.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 3.0f, 3.5f)
            )
            val head = body.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 17)
                    .addBox(-3.5f, -6.5f, -4.0f, 7.0f, 6.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -4.5f, -0.5f)
            )
            val eyes = head.addOrReplaceChild(
                "eyes",
                CubeListBuilder.create().texOffs(0, 17)
                    .addBox(1.5f, -1.5f, -1.0f, 2.0f, 3.0f, 2.0f, CubeDeformation(0.02f))
                    .texOffs(0, 17).mirror().addBox(-3.5f, -1.5f, -1.0f, 2.0f, 3.0f, 2.0f, CubeDeformation(0.02f))
                    .mirror(false),
                PartPose.offset(0.0f, -3.75f, -3.0f)
            )
            val leftarm = body.addOrReplaceChild(
                "leftarm",
                CubeListBuilder.create().texOffs(21, 17)
                    .addBox(-1.0f, -1.5f, -1.5f, 7.0f, 3.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(4.0f, -3.25f, -1.25f)
            )
            val rightarm = body.addOrReplaceChild(
                "rightarm",
                CubeListBuilder.create().texOffs(23, 0)
                    .addBox(-6.0f, -1.5f, -1.5f, 7.0f, 3.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(-4.0f, -3.25f, -1.25f)
            )
            val leftleg = body.addOrReplaceChild(
                "leftleg",
                CubeListBuilder.create().texOffs(25, 27)
                    .addBox(-1.5f, -1.0f, -1.5f, 3.0f, 6.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(3.0f, 3.0f, -0.5f)
            )
            val rightleg = body.addOrReplaceChild(
                "rightleg",
                CubeListBuilder.create().texOffs(30, 6)
                    .addBox(-1.5f, -1.0f, -1.5f, 3.0f, 6.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(-3.0f, 3.0f, -0.5f)
            )
            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }
}