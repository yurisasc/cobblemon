/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1

import com.cablemc.pokemod.common.client.render.models.blockbench.animation.WaveAnimation
import com.cablemc.pokemod.common.client.render.models.blockbench.animation.WaveSegment
import com.cablemc.pokemod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.X_AXIS
import com.cablemc.pokemod.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.Y_AXIS
import com.cablemc.pokemod.common.client.render.models.blockbench.wavefunction.sineFunction
import com.cablemc.pokemod.common.client.render.models.blockbench.withPosition
import com.cablemc.pokemod.common.client.render.models.blockbench.withRotation
import com.cablemc.pokemod.common.entity.PoseType.Companion.FLYING_POSES
import com.cablemc.pokemod.common.entity.PoseType.Companion.STANDING_POSES
import com.cablemc.pokemod.common.entity.PoseType.Companion.SWIMMING_POSES
import com.cablemc.pokemod.common.entity.PoseType.Companion.UI_POSES
import com.cablemc.pokemod.common.util.math.geometry.toRadians
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d
class GyaradosModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame {
    override val rootPart = root.registerChildWithAllChildren("gyarados")

    val seg1 = getPart("segment1")
    val seg2 = getPart("segment2")
    val seg3 = getPart("segment3")
    val seg4 = getPart("segment4")
    val seg5 = getPart("segment5")
    val seg6 = getPart("segment6")
    val seg7 = getPart("segment7")
    val seg8 = getPart("segment8")
    val seg9 = getPart("segment9")
    val seg10 = getPart("segment10")
    val seg11 = getPart("segment11")
    val seg12 = getPart("segment12")

    override val head = getPart("head")

    val wseg1 = WaveSegment(seg1, 7F)
    val wseg2 = WaveSegment(seg2, 5F)
    val wseg3 = WaveSegment(seg3, 6F)
    val wseg4 = WaveSegment(seg4, 6F)
    val wseg5 = WaveSegment(seg5, 6F)
    val wseg6 = WaveSegment(seg6, 6F)
    val wseg7 = WaveSegment(seg7, 6F)
    val wseg8 = WaveSegment(seg8, 6F)
    val wseg9 = WaveSegment(seg9, 6F)
    val wseg10 = WaveSegment(seg10, 5F)
    val wseg11 = WaveSegment(seg11, 5F)
    val wseg12 = WaveSegment(seg12, 4F)

    override val portraitScale = 1.6F
    override val portraitTranslation = Vec3d(-0.8, 0.6, 0.0)
    override val profileScale = 0.7F
    override val profileTranslation = Vec3d(-0.1, 0.7, 0.0)

    override fun registerPoses() {
        registerPose(
            poseName = "land",
            poseTypes = STANDING_POSES + UI_POSES,
            idleAnimations = arrayOf(
                singleBoneLook(),
                WaveAnimation(
                    frame = this,
                    waveFunction = sineFunction(
                        period = 8F,
                        amplitude = 0.4F
                    ),
                    basedOnLimbSwing = true,
                    oscillationsScalar = 8F,
                    head = seg5,
                    rotationAxis = Y_AXIS,
                    motionAxis = X_AXIS,
                    headLength = 0.1F,
                    segments = arrayOf(
                        wseg6,
                        wseg7,
                        wseg8,
                        wseg9,
                        wseg10,
                        wseg11,
                        wseg12
                    )
                )
            ),
            transformedParts = arrayOf(
                rootPart.withPosition(0F, 5F, 10F),
                seg1.withRotation(X_AXIS, (-60F).toRadians()),
                seg2.withRotation(X_AXIS, (-12.5F).toRadians()),
                seg3.withRotation(X_AXIS, (-10F).toRadians()),
                seg4.withRotation(X_AXIS, 7.5F.toRadians()),
                seg5.withRotation(X_AXIS, 75F.toRadians()).withPosition(Y_AXIS, 2F)
//                head.withRotation(X_AXIS, (-62.5F).toRadians())
            )
        )
        registerPose(
            poseName = "swim",
            poseTypes = SWIMMING_POSES + FLYING_POSES,
//            transformedParts = arrayOf(head.withRotation(X_AXIS, -70F.toRadians())),
            idleAnimations = arrayOf(
                singleBoneLook(),
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
                        wseg1,
                        wseg2,
                        wseg3,
                        wseg4,
                        wseg5,
                        wseg6,
                        wseg7,
                        wseg8,
                        wseg9,
                        wseg10,
                        wseg11,
                        wseg12
                    )
                )
            )
        )
    }
}