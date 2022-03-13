package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.BimanualSwingAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.BipedWalkAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.CascadeAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.SingleBoneLookAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.cosineFunction
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.gradualFunction
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.BimanualFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.BipedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
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

    override val portraitScale = 1.5F
    override val portraitTranslation = Vec3(0.0, 0.3, 0.0)

    companion object {
        val LAYER_LOCATION = ModelLayerLocation(cobbledResource("charmander"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root
            val charmander = partdefinition.addOrReplaceChild(
                "charmander",
                CubeListBuilder.create(),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )
            val body = charmander.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(23, 20)
                    .addBox(-4.0f, -5.5f, -2.5f, 8.0f, 11.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -8.5f, 0.0f)
            )
            val head = body.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-4.0f, -7.0f, -3.0f, 8.0f, 7.0f, 6.0f, CubeDeformation(0.0f))
                    .texOffs(18, 36).addBox(-3.5f, -3.0f, -5.0f, 7.0f, 3.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -5.5f, 0.0f)
            )

            val tail = body.addOrReplaceChild(
                "tail",
                CubeListBuilder.create().texOffs(0, 14)
                    .addBox(-2.0F, -2.0F, -1.0F, 4.0F, 4.0F, 7.0F, CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 3.25F, 2.5F)
            )

            val tail2 = tail.addOrReplaceChild("tail2",
                CubeListBuilder.create().texOffs(42, 53).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 7.0F, CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.5F, 5.0F)
            )

            val fire = tail2.addOrReplaceChild("fire",
                CubeListBuilder.create().texOffs(54, 0)
                    .addBox(0.0F, -8.0F, -2.5F, 0.0F, 8.0F, 5.0F, CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -1.5F, 6.5F)
            )

            val leftarm = body.addOrReplaceChild(
                "leftarm",
                CubeListBuilder.create().texOffs(22, 0)
                    .addBox(-1.0f, -1.5f, -1.5f, 7.0f, 3.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(4.0f, -3.5f, 0.0f)
            )
            val rightarm = body.addOrReplaceChild(
                "rightarm",
                CubeListBuilder.create().texOffs(22, 0).mirror()
                    .addBox(-6.0f, -1.5f, -1.5f, 7.0f, 3.0f, 3.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offset(-4.0f, -3.5f, 0.0f)
            )
            val leftleg = body.addOrReplaceChild(
                "leftleg",
                CubeListBuilder.create().texOffs(28, 6)
                    .addBox(-1.5f, -1.0f, -2.0f, 3.0f, 6.0f, 4.0f, CubeDeformation(0.0f))
                    .texOffs(55, 0).addBox(-1.25f, 4.0f, -3.0f, 0.0f, 1.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(55, 0).addBox(0.0f, 4.0f, -3.0f, 0.0f, 1.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(55, 0).addBox(1.25f, 4.0f, -3.0f, 0.0f, 1.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(3.0f, 3.5f, 0.0f)
            )
            val rightleg = body.addOrReplaceChild(
                "rightleg",
                CubeListBuilder.create().texOffs(0, 25)
                    .addBox(-1.5f, -1.0f, -2.0f, 3.0f, 6.0f, 4.0f, CubeDeformation(0.0f))
                    .texOffs(55, 0).addBox(0.0f, 4.0f, -3.0f, 0.0f, 1.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(55, 0).addBox(1.25f, 4.0f, -3.0f, 0.0f, 1.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(55, 0).addBox(-1.25f, 4.0f, -3.0f, 0.0f, 1.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(-3.0f, 3.5f, 0.0f)
            )
            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }
}