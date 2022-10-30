/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1

import com.cablemc.pokemod.common.client.render.models.blockbench.PoseableEntityState
import com.cablemc.pokemod.common.client.render.models.blockbench.animation.StatefulAnimation
import com.cablemc.pokemod.common.client.render.models.blockbench.animation.WaveAnimation
import com.cablemc.pokemod.common.client.render.models.blockbench.animation.WaveSegment
import com.cablemc.pokemod.common.client.render.models.blockbench.childNamed
import com.cablemc.pokemod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemod.common.client.render.models.blockbench.frame.ModelFrame
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.X_AXIS
import com.cablemc.pokemod.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.Y_AXIS
import com.cablemc.pokemod.common.client.render.models.blockbench.wavefunction.sineFunction
import com.cablemc.pokemod.common.client.render.models.blockbench.withRotationDegrees
import com.cablemc.pokemod.common.entity.PoseType
import com.cablemc.pokemod.common.entity.PoseType.Companion.MOVING_POSES
import com.cablemc.pokemod.common.entity.PoseType.Companion.STATIONARY_POSES
import com.cablemc.pokemod.common.entity.PoseType.Companion.UI_POSES
import com.cablemc.pokemod.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemod.common.util.pokemodResource
import net.minecraft.client.model.Dilation
import net.minecraft.client.model.ModelData
import net.minecraft.client.model.ModelPart
import net.minecraft.client.model.ModelPartBuilder
import net.minecraft.client.model.ModelTransform
import net.minecraft.client.model.TexturedModelData
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.util.math.Vec3d
class EkansModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame {

    override val rootPart = root.registerChildWithAllChildren("ekans")
    private val body = getPart("body")
    override val head = getPart("head")
    private val tail = getPart("tail")
    private val tail2 = getPart("tail2")
    private val tail3 = getPart("tail3")
    private val tail4 = getPart("tail4")
    private val tail5 = getPart("tail5")
    private val tail6 = getPart("tail6")

    val tailSegment = WaveSegment(modelPart = tail, length = 9F)
    val tail2Segment = WaveSegment(modelPart = tail2, length = 9F)
    val tail3Segment = WaveSegment(modelPart = tail3, length = 9F)
    val tail4Segment = WaveSegment(modelPart = tail4, length = 9F)
    val tail5Segment = WaveSegment(modelPart = tail5, length = 10F)
    val tail6Segment = WaveSegment(modelPart = tail6, length = 10F)

    override val portraitScale = 1.7F
    override val portraitTranslation = Vec3d(-0.3, -0.45, 0.0)

    override val profileScale = 1.0F
    override val profileTranslation = Vec3d(0.75, -0.5, 0.0)

    override fun registerPoses() {
        registerPose(
            poseName = "normal",
            poseTypes = STATIONARY_POSES + MOVING_POSES,
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("0023_ekans/ekans", "ground_idle"),
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

        registerPose(
            poseName = "portrait",
            poseTypes = UI_POSES,
            idleAnimations = arrayOf(bedrock("0023_ekans/ekans", "summary_idle"))
        )
    }


    override fun getFaintAnimation(
        pokemonEntity: PokemonEntity,
        state: PoseableEntityState<PokemonEntity>
    ) = bedrockStateful("0023_ekans/ekans", "faint")
}