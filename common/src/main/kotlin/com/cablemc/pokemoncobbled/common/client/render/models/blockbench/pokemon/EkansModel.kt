/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.WaveAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.WaveSegment
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.childNamed
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.X_AXIS
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.Y_AXIS
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.wavefunction.sineFunction
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.withRotationDegrees
import com.cablemc.pokemoncobbled.common.entity.PoseType
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.client.model.Dilation
import net.minecraft.client.model.ModelData
import net.minecraft.client.model.ModelPart
import net.minecraft.client.model.ModelPartBuilder
import net.minecraft.client.model.ModelTransform
import net.minecraft.client.model.TexturedModelData
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.util.math.Vec3d

class EkansModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame {

    override val rootPart = registerRelevantPart("ekans", root.getChild("ekans"))
    private val body = registerRelevantPart("body", rootPart.getChild("body"))
    override val head = registerRelevantPart("head", body.getChild("head"))
    val jaw = registerRelevantPart(head.childNamed("jaw"))
    private val tail = registerRelevantPart("tail", body.getChild("tail"))
    private val tail2 = registerRelevantPart("tail2", tail.getChild("tail2"))
    private val tail3 = registerRelevantPart("tail3", tail2.getChild("tail3"))
    private val tail4 = registerRelevantPart("tail4", tail3.getChild("tail4"))
    private val tail5 = registerRelevantPart("tail5", tail4.getChild("tail5"))
    private val rattle = registerRelevantPart(tail5.childNamed("rattle"))
    val rattle2 = registerRelevantPart(rattle.childNamed("rattle2"))
    val rattle3 = registerRelevantPart(rattle2.childNamed("rattle3"))
    val rattle4 = registerRelevantPart(rattle3.childNamed("rattle4"))
    val rattle5 = registerRelevantPart(rattle4.childNamed("rattle5"))

    val tailSegment = WaveSegment(modelPart = tail, length = 9F)
    val tail2Segment = WaveSegment(modelPart = tail2, length = 9F)
    val tail3Segment = WaveSegment(modelPart = tail3, length = 9F)
    val tail4Segment = WaveSegment(modelPart = tail4, length = 9F)
    val tail5Segment = WaveSegment(modelPart = tail5, length = 10F)
    val tail6Segment = WaveSegment(modelPart = rattle, length = 10F)

    override val portraitScale = 1.8F
    override val portraitTranslation = Vec3d(-1.0, -1.6, 0.0)
    override val profileScale = 1.0F
    override val profileTranslation = Vec3d(0.75, -0.5, 0.0)

    override fun registerPoses() {
        registerPose(
            poseType = PoseType.WALK,
            condition = { true },
            idleAnimations = arrayOf(
                singleBoneLook(),
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
            )
        )

        registerPose<PokemonPoseableModel>(
            poseType = PoseType.PROFILE,
            transformedParts = arrayOf(
                body.withRotationDegrees(-35F, 0F, 0F),
                head.withRotationDegrees(32.5F, 0.75F, 0F),
                jaw.withRotationDegrees(15F, 0F, 0F),
                tail.withRotationDegrees(51.25F, -53.5F, -73F),
                tail2.withRotationDegrees(45F, -60F, -41.75F),
                tail3.withRotationDegrees(1.25F, -60F, -4.25F),
                tail4.withRotationDegrees(-2.5F, -72.5F, 0F),
                tail5.withRotationDegrees(12.5F, -45.25F, -1.75F),
                rattle.withRotationDegrees(40.75F, -9F, -8.75F),
                rattle2.withRotationDegrees(10F, -5F, -1F),
                rattle3.withRotationDegrees(15F, -2.5F, -0.75F),
                rattle4.withRotationDegrees(10F, -2.5F, -0.5F),
                rattle5.withRotationDegrees(5F, 0F, 0F)
            )
        )

        registerPose<PokemonPoseableModel>(
            poseType = PoseType.PORTRAIT,
            transformedParts = arrayOf(
                body.withRotationDegrees(-35F, 0F, 0F),
                head.withRotationDegrees(32.5F, 0.75F, 0F),
                jaw.withRotationDegrees(15F, 0F, 0F),
                tail.withRotationDegrees(51.25F, -53.5F, -73F),
                tail2.withRotationDegrees(45F, -60F, -41.75F),
                tail3.withRotationDegrees(1.25F, -60F, -4.25F),
                tail4.withRotationDegrees(-2.5F, -72.5F, 0F),
                tail5.withRotationDegrees(12.5F, -45.25F, -1.75F),
                rattle.withRotationDegrees(40.75F, -9F, -8.75F),
                rattle2.withRotationDegrees(10F, -5F, -1F),
                rattle3.withRotationDegrees(15F, -2.5F, -0.75F),
                rattle4.withRotationDegrees(10F, -2.5F, -0.5F),
                rattle5.withRotationDegrees(5F, 0F, 0F)
            )
        )
    }

    companion object {
        val LAYER_LOCATION = EntityModelLayer(cobbledResource("ekans"), "main")
        fun createBodyLayer(): TexturedModelData {
            val meshdefinition = ModelData()
            val partdefinition = meshdefinition.root

            val ekans =
                partdefinition.addChild("ekans", ModelPartBuilder.create(), ModelTransform.pivot(0.5f, 24.0f, 0.0f))

            val body = ekans.addChild(
                "body",
                ModelPartBuilder.create().uv(0, 0)
                    .cuboid(-2.5f, -2.5f, -4.5f, 5.0f, 5.0f, 9.0f, Dilation(0.0f)),
                ModelTransform.pivot(-0.5f, -2.5f, -13.5f)
            )

            val head = body.addChild(
                "head",
                ModelPartBuilder.create().uv(19, 0)
                    .cuboid(-3.0f, -3.0f, -3.0f, 6.0f, 6.0f, 3.0f, Dilation(0.02f))
                    .uv(36, 14).cuboid(-3.0f, -3.0f, -8.0f, 6.0f, 3.0f, 5.0f, Dilation(0.02f))
                    .uv(0, -1).cuboid(-3.0f, 0.0f, -6.0f, 0.0f, 2.0f, 3.0f, Dilation(0.02f))
                    .uv(0, -3).cuboid(3.0f, 0.0f, -6.0f, 0.0f, 2.0f, 3.0f, Dilation(0.02f)),
                ModelTransform.pivot(0.0f, -0.5f, -4.5f)
            )

            val jaw = head.addChild(
                "jaw",
                ModelPartBuilder.create().uv(36, 22)
                    .cuboid(-3.0f, 0.0f, -4.5f, 6.0f, 1.0f, 5.0f, Dilation(0.0f))
                    .uv(0, 4).cuboid(-2.975f, -2.0f, -2.0f, 0.0f, 2.0f, 2.0f, Dilation(0.02f))
                    .uv(0, 2).cuboid(2.975f, -2.0f, -2.0f, 0.0f, 2.0f, 2.0f, Dilation(0.02f)),
                ModelTransform.pivot(0.0f, 2.0f, -3.0f)
            )

            val eyes =
                head.addChild("eyes", ModelPartBuilder.create(), ModelTransform.pivot(0.0f, -2.5f, -3.5f))

            val eyelid_left_r1 = eyes.addChild(
                "eyelid_left_r1",
                ModelPartBuilder.create().uv(19, 36).mirrored()
                    .cuboid(-1.15f, -1.0f, -1.5f, 1.0f, 2.0f, 3.0f, Dilation(0.06f)).mirrored(false)
                    .uv(19, 36).cuboid(-5.95f, -1.0f, -1.5f, 1.0f, 2.0f, 3.0f, Dilation(0.06f))
                    .uv(58, 32).mirrored().cuboid(-0.05f, -1.0f, -1.5f, 0.0f, 2.0f, 3.0f, Dilation(0.04f))
                    .mirrored(false)
                    .uv(58, 32).cuboid(-6.05f, -1.0f, -1.5f, 0.0f, 2.0f, 3.0f, Dilation(0.04f)),
                ModelTransform.of(3.05f, 1.0f, 0.0f, 0.0873f, 0.0f, 0.0f)
            )

            val tail = body.addChild(
                "tail",
                ModelPartBuilder.create().uv(28, 32)
                    .cuboid(-2.5f, -2.5f, 0.0f, 5.0f, 5.0f, 9.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 0.0f, 4.5f)
            )

            val tail2 = tail.addChild(
                "tail2",
                ModelPartBuilder.create().uv(0, 32)
                    .cuboid(-2.5f, -2.5f, 0.0f, 5.0f, 5.0f, 9.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 0.0f, 9.0f)
            )

            val tail3 = tail2.addChild(
                "tail3",
                ModelPartBuilder.create().uv(28, 0)
                    .cuboid(-2.5f, -2.5f, 0.0f, 5.0f, 5.0f, 9.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 0.0f, 9.0f)
            )

            val tail4 = tail3.addChild(
                "tail4",
                ModelPartBuilder.create().uv(0, 14)
                    .cuboid(-2.5f, -2.5f, 0.0f, 5.0f, 5.0f, 9.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 0.0f, 9.0f)
            )

            val tail5 = tail4.addChild(
                "tail5",
                ModelPartBuilder.create().uv(18, 18)
                    .cuboid(-2.0f, -2.0f, 0.0f, 4.0f, 4.0f, 10.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 0.5f, 9.0f)
            )

            val rattle = tail5.addChild(
                "rattle",
                ModelPartBuilder.create().uv(0, 46)
                    .cuboid(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 2.0f, Dilation(0.02f)),
                ModelTransform.pivot(0.0f, 0.0f, 10.0f)
            )

            val rattle2 = rattle.addChild(
                "rattle2",
                ModelPartBuilder.create().uv(0, 51)
                    .cuboid(-1.0f, -1.0f, -0.25f, 2.0f, 2.0f, 1.0f, Dilation(0.02f))
                    .uv(10, 46).cuboid(-1.5f, -1.5f, 0.5f, 3.0f, 3.0f, 2.0f, Dilation(0.02f)),
                ModelTransform.pivot(0.0f, 0.0f, 2.0f)
            )

            val rattle3 = rattle2.addChild(
                "rattle3",
                ModelPartBuilder.create().uv(6, 51)
                    .cuboid(-1.0f, -1.0f, -0.25f, 2.0f, 2.0f, 1.0f, Dilation(0.02f))
                    .uv(20, 46).cuboid(-1.5f, -1.5f, 0.5f, 3.0f, 3.0f, 2.0f, Dilation(0.02f)),
                ModelTransform.pivot(0.0f, 0.0f, 2.5f)
            )

            val rattle4 = rattle3.addChild(
                "rattle4",
                ModelPartBuilder.create().uv(12, 51)
                    .cuboid(-0.5f, -0.5f, -0.25f, 1.0f, 1.0f, 1.0f, Dilation(0.02f))
                    .uv(30, 46).cuboid(-1.0f, -1.0f, 0.5f, 2.0f, 2.0f, 2.0f, Dilation(0.02f)),
                ModelTransform.pivot(0.0f, 0.0f, 2.5f)
            )

            val rattle5 = rattle4.addChild(
                "rattle5",
                ModelPartBuilder.create().uv(16, 51)
                    .cuboid(-0.5f, -0.5f, -0.25f, 1.0f, 1.0f, 1.0f, Dilation(0.02f))
                    .uv(38, 46).cuboid(-1.0f, -1.0f, 0.5f, 2.0f, 2.0f, 2.0f, Dilation(0.02f)),
                ModelTransform.pivot(0.0f, 0.0f, 2.5f)
            )

            return TexturedModelData.of(meshdefinition, 64, 64)
        }
    }
}