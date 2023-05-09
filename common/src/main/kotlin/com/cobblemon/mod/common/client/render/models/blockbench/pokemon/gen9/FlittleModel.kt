/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen9

import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.asTransformed
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.client.render.models.blockbench.pose.TransformedModelPart
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.PoseType.Companion.MOVING_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.STATIONARY_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.UI_POSES
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class FlittleModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame {
    override val rootPart = root.registerChildWithAllChildren("flittle")
    override val head = getPart("body")

    override val portraitScale = 1.0F
    override val portraitTranslation = Vec3d(0.0, 0.0, 0.0)

    override val profileScale = 1.0F
    override val profileTranslation = Vec3d(0.0, 0.2, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose
    lateinit var floating: PokemonPose
    lateinit var flying: PokemonPose
    lateinit var shoulderLeft: PokemonPose
    lateinit var shoulderRight: PokemonPose
    lateinit var sleep: PokemonPose

    val shoulderOffsetX = 6
    val shoulderOffsetY = 6
    override fun registerPoses() {
        val blink = quirk("blink") { bedrockStateful("flittle", "blink").setPreventsIdle(false) }
        sleep = registerPose(
                poseType = PoseType.SLEEP,
                idleAnimations = arrayOf(bedrock("flittle", "sleep"))
        )

        standing = registerPose(
                poseName = "standing",
            quirks = arrayOf(blink),
                poseTypes = UI_POSES + PoseType.STAND,
                transformTicks = 10,
                idleAnimations = arrayOf(
                    singleBoneLook(),
                        bedrock("flittle", "ground_idle")
                )
        )

        walk = registerPose(
                poseName = "walk",
            quirks = arrayOf(blink),
                poseTypes = MOVING_POSES,
                transformTicks = 10,
                idleAnimations = arrayOf(
                    singleBoneLook(),
                        bedrock("flittle", "ground_walk")
                )
        )

        floating = registerPose(
            poseName = "floating",
            quirks = arrayOf(blink),
            poseType = PoseType.FLOAT,
            transformTicks = 10,
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("flittle", "air_idle")
            )
        )

        flying = registerPose(
            poseName = "flying",
            quirks = arrayOf(blink),
            poseType = PoseType.FLY,
            transformTicks = 10,
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("flittle", "air_fly")
            )
        )

        shoulderLeft = registerPose(
            poseType = PoseType.SHOULDER_LEFT,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("flittle", "ground_idle")
            ),
            transformedParts = arrayOf(
                rootPart.asTransformed().addPosition(shoulderOffsetX, shoulderOffsetY, 0.0)
            )
        )

        shoulderRight = registerPose(
            poseType = PoseType.SHOULDER_RIGHT,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("flittle", "ground_idle")
            ),
            transformedParts = arrayOf(
                rootPart.asTransformed().addPosition(-shoulderOffsetX, shoulderOffsetY, 0.0)
            )
        )

    }
    override fun getFaintAnimation(
        pokemonEntity: PokemonEntity,
        state: PoseableEntityState<PokemonEntity>
    ) = if (state.isNotPosedIn(sleep)) bedrockStateful("flittle", "faint") else null
}