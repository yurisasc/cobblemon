/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
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
    val spine = getPart("neck")
    val spineFinal = getPart("spine_final")
    val spine3 = getPart("spine3")
    val spine2 = getPart("spine2")
    val spine1 = getPart("spine")
//    val bodyJoint = registerRelevantPart("bodyJoint", spine1.getChild("bodyjoint"))
    val body = getPart("body")
    val tail = getPart("tail")
    val tail2 = getPart("tail2")
    val tail3 = getPart("tail3")
    val tail4 = getPart("tail4")
    val tail5 = getPart("tail5")
    val tail6 = getPart("tail6")
    val tail7 = getPart("tail_end")
    override val head = getPart("head")

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
            poseName = "land",
            poseTypes = STANDING_POSES + UI_POSES,
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
                body.withRotation(X_AXIS, 75F.toRadians()).withPosition(Y_AXIS, 2F),
                head.withRotation(X_AXIS, (-62.5F).toRadians())
            )
        )
        registerPose(
            poseName = "swim",
            poseTypes = SWIMMING_POSES + FLYING_POSES,
            transformedParts = arrayOf(
                head.withRotation(X_AXIS, -70F.toRadians())
            ),
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
                )
            )
        )
    }
}