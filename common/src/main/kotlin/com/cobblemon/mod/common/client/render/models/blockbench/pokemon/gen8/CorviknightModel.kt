/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen8

import com.cobblemon.mod.common.client.render.models.blockbench.animation.BipedWalkAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.WingFlapIdleAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.asTransformed
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BiWingedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BipedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.client.render.models.blockbench.pose.TransformedModelPart
import com.cobblemon.mod.common.client.render.models.blockbench.wavefunction.parabolaFunction
import com.cobblemon.mod.common.client.render.models.blockbench.wavefunction.sineFunction
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.util.math.geometry.toRadians
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class CorviknightModel (root: ModelPart) : PokemonPoseableModel(), HeadedFrame, BipedFrame, BiWingedFrame {
    override val rootPart = root.registerChildWithAllChildren("corviknight")
    override val leftWing = getPart("wing_left")
    override val rightWing = getPart("wing_right")
    override val leftLeg = getPart("leg_left")
    override val rightLeg = getPart("leg_right")
    override val head = getPart("head")

    val openWingLeft = getPart("wing_open_left")
    val openWingRight = getPart("wing_open_right")
    val closedWingLeft = getPart("wing_closed_left")
    val closedWingRight = getPart("wing_closed_right")

    override val portraitScale = 1.0F
    override val portraitTranslation = Vec3d(-0.45, 3.0, 0.0)

    override val profileScale = 0.36F
    override val profileTranslation = Vec3d(0.0, 1.2, 0.0)


    lateinit var stand: PokemonPose
    lateinit var walk: PokemonPose
    lateinit var hover: PokemonPose
    lateinit var fly: PokemonPose
    lateinit var sleep: PokemonPose

    override val cryAnimation = CryProvider { _, _ -> bedrockStateful("corviknight", "cry").setPreventsIdle(false) }

    override fun registerPoses() {
        val blink = quirk("blink") { bedrockStateful("corviknight", "blink").setPreventsIdle(false) }
        sleep = registerPose(
            poseType = PoseType.SLEEP,
            idleAnimations = arrayOf(bedrock("corviknight", "sleep"))
        )

        stand = registerPose(
            poseName = "standing",
            poseTypes = PoseType.UI_POSES + PoseType.STATIONARY_POSES - PoseType.HOVER,
            transformTicks = 10,
            transformedParts = arrayOf(
                openWingLeft.asTransformed().withVisibility(visibility = false),
                openWingRight.asTransformed().withVisibility(visibility = false),
                closedWingLeft.asTransformed().withVisibility(visibility = true),
                closedWingRight.asTransformed().withVisibility(visibility = true)
            ),
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("corviknight", "ground_idle")
            )
        )

        hover = registerPose(
            poseName = "hover",
            poseType = PoseType.HOVER,
            transformTicks = 10,
            transformedParts = arrayOf(
                openWingLeft.asTransformed().withVisibility(visibility = true),
                openWingRight.asTransformed().withVisibility(visibility = true),
                closedWingLeft.asTransformed().withVisibility(visibility = false),
                closedWingRight.asTransformed().withVisibility(visibility = false)
            ),
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("corviknight", "air_idle")
            )
        )

        fly = registerPose(
            poseName = "fly",
            poseType = PoseType.FLY,
            transformTicks = 10,
            transformedParts = arrayOf(
                openWingLeft.asTransformed().withVisibility(visibility = true),
                openWingRight.asTransformed().withVisibility(visibility = true),
                closedWingLeft.asTransformed().withVisibility(visibility = false),
                closedWingRight.asTransformed().withVisibility(visibility = false)
            ),
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("corviknight", "air_fly")
            )
        )

        walk = registerPose(
            poseName = "walking",
            poseTypes = PoseType.MOVING_POSES - PoseType.FLY,
            transformTicks = 10,
            transformedParts = arrayOf(
                openWingLeft.asTransformed().withVisibility(visibility = false),
                openWingRight.asTransformed().withVisibility(visibility = false),
                closedWingLeft.asTransformed().withVisibility(visibility = true),
                closedWingRight.asTransformed().withVisibility(visibility = true)
            ),
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("corviknight", "ground_walk")
            )
        )
    }
}