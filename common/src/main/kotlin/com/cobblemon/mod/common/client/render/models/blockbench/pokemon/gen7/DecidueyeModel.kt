/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen7

import com.cobblemon.mod.common.client.render.models.blockbench.animation.BipedWalkAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.WingFlapIdleAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.createTransformation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BiWingedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BipedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.client.render.models.blockbench.pose.ModelPartTransformation
import com.cobblemon.mod.common.client.render.models.blockbench.wavefunction.sineFunction
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.PoseType.Companion.MOVING_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.STATIONARY_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.UI_POSES
import com.cobblemon.mod.common.util.math.geometry.toRadians
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class DecidueyeModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, BipedFrame, BiWingedFrame {
    override val rootPart = root.registerChildWithAllChildren("decidueye")
    override val head = getPart("head")

    override val leftWing = getPart("wing_left1")
    override val rightWing = getPart("wing_right1")
    override val leftLeg = getPart("leg_left1")
    override val rightLeg = getPart("leg_right1")

    val folded_wings = getPart("folded_wings")
    val arrow = getPart("arrow")

    override val portraitScale = 2.1F
    override val portraitTranslation = Vec3d(-0.2, 0.65, 0.0)

    override val profileScale = 0.8F
    override val profileTranslation = Vec3d(0.0, 0.5, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose
    lateinit var hover: PokemonPose
    lateinit var fly: PokemonPose

    override val cryAnimation = CryProvider { _, _ -> bedrockStateful("decidueye", "cry").setPreventsIdle(false) }

    override fun registerPoses() {
        val blink = quirk("blink") { bedrockStateful("decidueye", "blink").setPreventsIdle(false) }
        standing = registerPose(
            poseName = "standing",
            poseTypes = STATIONARY_POSES - PoseType.HOVER + UI_POSES,
            transformedParts = arrayOf(
                folded_wings.createTransformation().withVisibility(visibility = true),
                leftWing.createTransformation().withVisibility(visibility = false),
                rightWing.createTransformation().withVisibility(visibility = false),
                arrow.createTransformation().withVisibility(visibility = false)
            ),
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("decidueye", "ground_idle")
            )
        )

        walk = registerPose(
            poseName = "walk",
            poseTypes = MOVING_POSES - PoseType.FLY,
            transformedParts = arrayOf(
                folded_wings.createTransformation().withVisibility(visibility = true),
                leftWing.createTransformation().withVisibility(visibility = false),
                rightWing.createTransformation().withVisibility(visibility = false),
                arrow.createTransformation().withVisibility(visibility = false)
            ),
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                BipedWalkAnimation(this),
                bedrock("decidueye", "ground_idle")
            )
        )

        hover = registerPose(
            poseName = "hover",
            poseType = PoseType.HOVER,
            transformedParts = arrayOf(
                folded_wings.createTransformation().withVisibility(visibility = false),
                leftWing.createTransformation().withVisibility(visibility = true),
                rightWing.createTransformation().withVisibility(visibility = true),
                arrow.createTransformation().withVisibility(visibility = false)
            ),
            transformTicks = 10,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("decidueye", "air_idle"),
                WingFlapIdleAnimation(this,
                    flapFunction = sineFunction(verticalShift = -10F.toRadians(), period = 0.9F, amplitude = 0.6F),
                    timeVariable = { state, _, _ -> state?.animationSeconds ?: 0F },
                    axis = ModelPartTransformation.Y_AXIS
                )
            )
        )

        fly = registerPose(
            poseName = "fly",
            poseType = PoseType.FLY,
            transformedParts = arrayOf(
                folded_wings.createTransformation().withVisibility(visibility = false),
                leftWing.createTransformation().withVisibility(visibility = true),
                rightWing.createTransformation().withVisibility(visibility = true),
                arrow.createTransformation().withVisibility(visibility = false)
            ),
            transformTicks = 10,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("decidueye", "air_fly"),
                WingFlapIdleAnimation(this,
                    flapFunction = sineFunction(verticalShift = -14F.toRadians(), period = 0.9F, amplitude = 0.9F),
                    timeVariable = { state, _, _ -> state?.animationSeconds ?: 0F },
                    axis = ModelPartTransformation.Y_AXIS
                )
            )
        )
    }

//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PoseableEntityState<PokemonEntity>
//    ) = if (state.isPosedIn(standing, walk)) bedrockStateful("decidueye", "faint") else null
}