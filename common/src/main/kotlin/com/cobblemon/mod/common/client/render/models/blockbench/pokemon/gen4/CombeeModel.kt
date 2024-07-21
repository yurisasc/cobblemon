/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen4

import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.createTransformation
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.client.render.models.blockbench.pose.ModelPartTransformation
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class CombeeModel (root: ModelPart) : PokemonPoseableModel() {
    override val rootPart = root.registerChildWithAllChildren("combee")

    override var portraitScale = 1.8F
    override var portraitTranslation = Vec3d(-0.11, -0.77, 0.0)

    override var profileScale = 0.9F
    override var profileTranslation = Vec3d(0.0, 0.35, 0.0)

    lateinit var hover: PokemonPose
    lateinit var fly: PokemonPose
    lateinit var sleep: PokemonPose
    lateinit var shoulderLeft: PokemonPose
    lateinit var shoulderRight: PokemonPose

    val shoulderOffset = 4
    val shoulderZOffset = 8

    override fun registerPoses() {
        val blink1 = quirk { bedrockStateful("combee", "blink_right") }
        val blink2 = quirk { bedrockStateful("combee", "blink_gender") }
        val blink3 = quirk { bedrockStateful("combee", "blink_left") }

        sleep = registerPose(
            poseType = PoseType.SLEEP,
            idleAnimations = arrayOf(bedrock("combee", "air_sleep"))
        )

        hover = registerPose(
            poseName = "hover",
            poseTypes = PoseType.UI_POSES + PoseType.STATIONARY_POSES,
            quirks = arrayOf(blink1, blink2, blink3),
            idleAnimations = arrayOf(
                bedrock("combee", "air_idle")
            )
        )

        fly = registerPose(
            poseName = "fly",
            poseTypes = PoseType.MOVING_POSES,
            quirks = arrayOf(blink1, blink2, blink3),
            idleAnimations = arrayOf(
                bedrock("combee", "air_fly")
            )
        )

        shoulderLeft = registerPose(
                poseType = PoseType.SHOULDER_LEFT,
                quirks = arrayOf(blink1, blink2, blink3),
                idleAnimations = arrayOf(
                        bedrock("combee", "air_idle")
                ),
                transformedParts = arrayOf(
                        rootPart.createTransformation().addPosition(ModelPartTransformation.X_AXIS, shoulderOffset),
                        rootPart.createTransformation().addPosition(ModelPartTransformation.Z_AXIS, shoulderZOffset)
                )
        )

        shoulderRight = registerPose(
                poseType = PoseType.SHOULDER_RIGHT,
                quirks = arrayOf(blink1, blink2, blink3),
                idleAnimations = arrayOf(
                        bedrock("combee", "air_idle")
                ),
                transformedParts = arrayOf(
                        rootPart.createTransformation().addPosition(ModelPartTransformation.X_AXIS, -shoulderOffset),
                        rootPart.createTransformation().addPosition(ModelPartTransformation.Z_AXIS, shoulderZOffset)
                )
        )
    }

    override fun getFaintAnimation(
        pokemonEntity: PokemonEntity,
        state: PoseableEntityState<PokemonEntity>
    ) = if (state.isPosedIn(hover, fly, sleep)) bedrockStateful("combee", "faint") else null
}