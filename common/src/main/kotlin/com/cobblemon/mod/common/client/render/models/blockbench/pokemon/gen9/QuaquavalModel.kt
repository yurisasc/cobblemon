/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen9

import com.cobblemon.mod.common.client.render.models.blockbench.createTransformation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BipedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Pose
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.client.render.models.blockbench.PosableModel
import com.cobblemon.mod.common.client.render.models.blockbench.PosableState
import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class QuaquavalModel (root: ModelPart) : PosableModel(), HeadedFrame, BipedFrame {
    override val rootPart = root.registerChildWithAllChildren("quaquaval")
    override val head = getPart("head")

    override val leftLeg = getPart("leg_left")
    override val rightLeg = getPart("leg_right")

    val water_feathers = getPart("water_feathers")

    override val portraitScale = 2.3F
    override val portraitTranslation = Vec3d(-0.42, 3.7, 0.0)

    override val profileScale = 0.4F
    override val profileTranslation = Vec3d(0.0, 1.1, 0.0)

    lateinit var standing: Pose
    lateinit var standing2: Pose
    lateinit var walking: Pose
    lateinit var sleep: Pose
    lateinit var battleidle: Pose

    override val cryAnimation = CryProvider { bedrockStateful("quaquaval", "cry") }

    override fun registerPoses() {
        val blink = quirk { bedrockStateful("quaquaval", "blink") }
        sleep = registerPose(
            poseType = PoseType.SLEEP,
            idleAnimations = arrayOf(bedrock("quaquaval", "sleep"))
        )

        standing = registerPose(
            poseName = "standing",
            poseTypes = PoseType.STATIONARY_POSES + PoseType.UI_POSES,
            transformTicks = 10,
            condition = { (it.entity as? PokemonEntity)?.let { !it.isBattling && !it.isTouchingWaterOrRain } == true },
            transformedParts = arrayOf(
                water_feathers.createTransformation().withVisibility(visibility = false)
            ),
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("quaquaval", "ground_idle")
            )
        )

        standing2 = registerPose(
            poseName = "standing2",
            poseTypes = PoseType.STATIONARY_POSES,
            transformTicks = 10,
            condition = { (it.entity as? PokemonEntity)?.let { !it.isBattling && it.isTouchingWaterOrRain } == true },
            transformedParts = arrayOf(
                water_feathers.createTransformation().withVisibility(visibility = false)
            ),
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("quaquaval", "ground_idle")
            )
        )

        walking = registerPose(
            poseName = "walking",
            poseTypes = PoseType.MOVING_POSES,
            transformTicks = 10,
            transformedParts = arrayOf(
                water_feathers.createTransformation().withVisibility(visibility = false)
            ),
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("quaquaval", "ground_walk")
            )
        )

        battleidle = registerPose(
            poseName = "battle_idle",
            poseTypes = PoseType.STATIONARY_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink),
            transformedParts = arrayOf(
                water_feathers.createTransformation().withVisibility(visibility = true)
            ),
            condition = { (it.entity as? PokemonEntity)?.isBattling == true },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("quaquaval", "battle_idle")
            )
        )
    }
//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PosableState<PokemonEntity>
//    ) = if (state.isPosedIn(standing, walking, battleidle, sleep)) bedrockStateful("quaquaval", "faint") else null
}