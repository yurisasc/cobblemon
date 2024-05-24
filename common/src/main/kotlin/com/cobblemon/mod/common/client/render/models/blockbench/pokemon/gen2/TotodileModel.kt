/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen2

import com.cobblemon.mod.common.client.render.models.blockbench.PosableModel
import com.cobblemon.mod.common.client.render.models.blockbench.PosableState
import com.cobblemon.mod.common.client.render.models.blockbench.createTransformation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BipedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider
import com.cobblemon.mod.common.client.render.models.blockbench.pose.ModelPartTransformation
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Pose
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class TotodileModel (root: ModelPart) : PosableModel(), HeadedFrame, BipedFrame {
    override val rootPart = root.registerChildWithAllChildren("totodile")
    override val head = getPart("head")

    override val leftLeg = getPart("leg_left")
    override val rightLeg = getPart("leg_right")

    override var portraitScale = 1.4F
    override var portraitTranslation = Vec3d(-0.15, 0.3, 0.0)

    override var profileScale = 0.65F
    override var profileTranslation = Vec3d(0.0, 0.71, 0.0)

    lateinit var standing: Pose
    lateinit var walk: Pose
    lateinit var floating: Pose
    lateinit var swimming: Pose
    lateinit var sleep: Pose
    lateinit var watersleep: Pose
    lateinit var battleidle: Pose
    lateinit var water_surface_idle: Pose
    lateinit var water_surface_swim: Pose
    lateinit var shoulderLeft: Pose
    lateinit var shoulderRight: Pose

    val wateroffset = -10
    val shoulderOffset = 5.5

    override val cryAnimation = CryProvider { bedrockStateful("totodile", "cry") }

    override fun registerPoses() {
        val blink = quirk { bedrockStateful("totodile", "blink")}
        val sleepQuirk = quirk(secondsBetweenOccurrences = 60F to 120F) { bedrockStateful("totodile", "sleep_quirk")}
        val biteyQuirk = quirk(secondsBetweenOccurrences = 60F to 120F) { bedrockStateful("totodile", "bitey_quirk")}

        sleep = registerPose(
            poseName = "sleeping",
            poseType = PoseType.SLEEP,
            quirks = arrayOf(sleepQuirk),
            condition = { it.entity?.isTouchingWater == false },
            idleAnimations = arrayOf(bedrock("totodile", "sleep"))
        )

        watersleep = registerPose(
            poseName = "water_sleeping",
            poseType = PoseType.SLEEP,
            quirks = arrayOf(sleepQuirk),
            condition = { it.entity?.isTouchingWater == true },
            idleAnimations = arrayOf(bedrock("totodile", "water_sleep"))
        )

        standing = registerPose(
            poseName = "standing",
            poseTypes = PoseType.UI_POSES + PoseType.STAND,
            transformTicks = 10,
            condition = { (it.entity as? PokemonEntity)?.isBattling == false && it.entity?.isTouchingWater == false && it.entity?.isSubmergedInWater == false },
            quirks = arrayOf(blink, biteyQuirk),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("totodile", "ground_idle")
            )
        )

        walk = registerPose(
            poseName = "walk",
            transformTicks = 10,
            poseType = PoseType.WALK,
            condition = { it.entity?.isTouchingWater == false && it.entity?.isSubmergedInWater == false},
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("totodile", "ground_walk")
            )
        )

        floating = registerPose(
            poseName = "floating",
            transformTicks = 10,
            poseType = PoseType.FLOAT,
            condition = { it.entity?.isSubmergedInWater == true },
            quirks = arrayOf(blink, biteyQuirk),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("totodile", "water_idle")
            )
        )

        swimming = registerPose(
            poseName = "swimming",
            transformTicks = 10,
            condition = { it.entity?.isSubmergedInWater == true },
            poseType = PoseType.SWIM,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("totodile", "water_swim"),
            )
        )

        battleidle = registerPose(
            poseName = "battle_idle",
            poseTypes = PoseType.STATIONARY_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink),
            condition = { (it.entity as? PokemonEntity)?.isBattling == true && it.entity?.isTouchingWater == false },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("totodile", "battle_idle")
            )
        )

        water_surface_idle = registerPose(
            poseName = "surface_idle",
            poseTypes = PoseType.STATIONARY_POSES,
            quirks = arrayOf(blink, biteyQuirk),
            condition = { it.entity?.isSubmergedInWater == false && it.entity?.isTouchingWater == true },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("totodile", "surfacewater_idle"),
            ),
            transformedParts = arrayOf(
                rootPart.createTransformation().addPosition(ModelPartTransformation.Y_AXIS, wateroffset)
            )
        )

        water_surface_swim = registerPose(
            poseName = "surface_swim",
            poseTypes = PoseType.MOVING_POSES,
            quirks = arrayOf(blink),
            condition = { it.entity?.isSubmergedInWater == false && it.entity?.isTouchingWater == true },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("totodile", "surfacewater_swim"),
            ),
            transformedParts = arrayOf(
                rootPart.createTransformation().addPosition(ModelPartTransformation.Y_AXIS, wateroffset)
            )
        )

        shoulderLeft = registerPose(
            poseType = PoseType.SHOULDER_LEFT,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("totodile", "shoulder_right")
            ),
            transformedParts = arrayOf(
                rootPart.createTransformation().addPosition(ModelPartTransformation.X_AXIS, shoulderOffset)
            )
        )

        shoulderRight = registerPose(
            poseType = PoseType.SHOULDER_RIGHT,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("totodile", "shoulder_left")
            ),
            transformedParts = arrayOf(
                rootPart.createTransformation().addPosition(ModelPartTransformation.X_AXIS, -shoulderOffset)
            )
        )
    }
    override fun getFaintAnimation(state: PosableState) = if (state.isPosedIn(standing, walk, battleidle, sleep)) bedrockStateful("totodile", "faint") else if (state.isPosedIn(water_surface_idle, water_surface_swim, watersleep )) bedrockStateful("totodile", "faint") else null
}