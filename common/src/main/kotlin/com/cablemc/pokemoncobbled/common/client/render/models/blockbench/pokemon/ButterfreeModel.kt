package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.SingleBoneLookAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.WingFlapIdleAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.BiWingedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.getChildOf
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.X_AXIS
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.Y_AXIS
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

class ButterfreeModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, BiWingedFrame {
    override val rootPart = registerRelevantPart("butterfree", root.getChild("butterfree"))
    override val head = registerRelevantPart("head", rootPart.getChildOf("body", "head"))

    override val leftWing = registerRelevantPart("leftwing", rootPart.getChildOf("body", "leftwing"))
    override val rightWing = registerRelevantPart("rightwing", rootPart.getChildOf("body", "rightwing"))

    val leftWingBack = registerRelevantPart("leftwingback", rootPart.getChildOf("body", "leftwingback"))
    val rightWingBack = registerRelevantPart("rightwingback", rootPart.getChildOf("body", "rightwingback"))

    override fun registerPoses() {
        registerPose(
            poseType = PoseType.NONE,
            condition = { !it.isMoving.get() },
            transformTicks = 3,
            idleAnimations = arrayOf(
                SingleBoneLookAnimation(this),
                WingFlapIdleAnimation(
                    frame = this,
                    timeVariable = { state, _, _ -> state?.animationSeconds ?: 0F },
                    axis = Y_AXIS,
                    flapFunction = sineFunction(
                        amplitude = (-30F).toRadians(),
                        period = 0.4F,
                        verticalShift = (-35F).toRadians()
                    )
                ),
                WingFlapIdleAnimation(
                    frame = object : BiWingedFrame {
                        override val leftWing = leftWingBack
                        override val rightWing = rightWingBack
                        override val rootPart = this@ButterfreeModel.rootPart
                    },
                    timeVariable = { state, _, _ -> state?.animationSeconds ?: 0F },
                    axis = Y_AXIS,
                    flapFunction = sineFunction(
                        amplitude = (-30F).toRadians(),
                        period = 0.4F,
                        phaseShift = 0.00F,
                        verticalShift = (-45F).toRadians()
                    )
                )
            ),
            transformedParts = arrayOf()
        )

        registerPose(
            poseType = PoseType.WALK,
            condition = { it.isMoving.get() },
            transformTicks = 3,
            idleAnimations = arrayOf(
                SingleBoneLookAnimation(this),
                WingFlapIdleAnimation(
                    frame = this,
                    timeVariable = { state, _, _ -> state?.animationSeconds ?: 0F },
                    axis = Y_AXIS,
                    flapFunction = sineFunction(
                        amplitude = (-30F).toRadians(),
                        period = 0.3F,
                        verticalShift = (-35F).toRadians()
                    )
                ),
                WingFlapIdleAnimation(
                    frame = object : BiWingedFrame {
                        override val leftWing = leftWingBack
                        override val rightWing = rightWingBack
                        override val rootPart = this@ButterfreeModel.rootPart
                    },
                    timeVariable = { state, _, _ -> state?.animationSeconds ?: 0F },
                    axis = Y_AXIS,
                    flapFunction = sineFunction(
                        amplitude = (-30F).toRadians(),
                        period = 0.3F,
                        phaseShift = 0.00F,
                        verticalShift = (-45F).toRadians()
                    )
                )
            ),
            transformedParts = arrayOf(rootPart.withRotation(X_AXIS, 20F.toRadians()))
        )
    }

    override val portraitScale = 1.25F
    override val portraitTranslation = Vec3(0.29, 0.65, 0.0)

    companion object {
        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(cobbledResource("butterfree"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val butterfree = partdefinition.addOrReplaceChild(
                "butterfree",
                CubeListBuilder.create(),
                PartPose.offset(0.0f, 23.0f, 0.0f)
            )

            val body = butterfree.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(38, 11)
                    .addBox(-3.0f, -3.0f, -2.0f, 6.0f, 6.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -9.0f, 0.0f)
            )

            val head = body.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(42, 33)
                    .addBox(-1.5f, -2.5f, -3.75f, 3.0f, 2.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(38, 0).addBox(-4.0f, -6.0f, -3.0f, 8.0f, 6.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -3.0f, 0.0f)
            )

            val leftattena = head.addOrReplaceChild(
                "leftattena",
                CubeListBuilder.create().texOffs(40, 0)
                    .addBox(-0.5f, -5.0f, 0.0f, 1.0f, 5.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offset(2.5f, -6.0f, -1.0f)
            )

            val leftattenatip = leftattena.addOrReplaceChild(
                "leftattenatip",
                CubeListBuilder.create().texOffs(38, 40)
                    .addBox(-1.5f, -7.0f, 0.0f, 2.0f, 7.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offset(0.0f, -5.0f, 0.0f)
            )

            val rightattena = head.addOrReplaceChild(
                "rightattena",
                CubeListBuilder.create().texOffs(38, 0)
                    .addBox(-0.5f, -5.0f, 0.0f, 1.0f, 5.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offset(-2.5f, -6.0f, -1.0f)
            )

            val rightattenatip = rightattena.addOrReplaceChild(
                "rightattenatip",
                CubeListBuilder.create().texOffs(38, 33)
                    .addBox(-0.5f, -7.0f, 0.0f, 2.0f, 7.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offset(0.0f, -5.0f, 0.0f)
            )

            val rightwingback = body.addOrReplaceChild(
                "rightwingback",
                CubeListBuilder.create().texOffs(96, 0)
                    .addBox(-16.0f, -4.0f, 0.0f, 16.0f, 11.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(0.0f, 1.0f, 2.0f, 0.0f, 0.1309f, 0.0f)
            )

            val leftwingback = body.addOrReplaceChild(
                "leftwingback",
                CubeListBuilder.create().texOffs(96, 0).mirror()
                    .addBox(0.0f, -4.0f, 0.0f, 16.0f, 11.0f, 0.0f, CubeDeformation(0.02f)).mirror(false),
                PartPose.offsetAndRotation(0.0f, 1.0f, 2.0f, 0.0f, -0.1309f, 0.0f)
            )

            val rightwing = body.addOrReplaceChild(
                "rightwing",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-19.0f, -14.0f, 0.0f, 19.0f, 19.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(0.0f, -3.0f, 2.0f, 0.0f, 0.0873f, 0.0f)
            )

            val leftwing = body.addOrReplaceChild(
                "leftwing",
                CubeListBuilder.create().texOffs(0, 0).mirror()
                    .addBox(0.0f, -14.0f, 0.0f, 19.0f, 19.0f, 0.0f, CubeDeformation(0.02f)).mirror(false),
                PartPose.offsetAndRotation(0.0f, -3.0f, 2.0f, 0.0f, -0.0873f, 0.0f)
            )

            val lefthand = body.addOrReplaceChild(
                "lefthand",
                CubeListBuilder.create().texOffs(42, 40)
                    .addBox(0.0f, -1.0f, -2.0f, 1.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.0f, -1.25f, -0.5f, 0.0f, 0.2618f, 0.0f)
            )

            val righthand = body.addOrReplaceChild(
                "righthand",
                CubeListBuilder.create().texOffs(42, 40).mirror()
                    .addBox(-1.0f, -1.0f, -2.0f, 1.0f, 2.0f, 2.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(-3.0f, -1.25f, -0.5f, 0.0f, -0.2618f, 0.0f)
            )

            val leftleg = body.addOrReplaceChild(
                "leftleg",
                CubeListBuilder.create().texOffs(38, 27)
                    .addBox(-1.0f, 0.0f, 0.0f, 3.0f, 6.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offset(1.75f, 3.0f, 0.0f)
            )

            val rightleg = body.addOrReplaceChild(
                "rightleg",
                CubeListBuilder.create().texOffs(38, 21)
                    .addBox(-2.0f, 0.0f, 0.0f, 3.0f, 6.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offset(-1.75f, 3.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}