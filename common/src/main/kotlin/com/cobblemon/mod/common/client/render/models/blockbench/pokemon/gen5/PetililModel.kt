/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen5

import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.createTransformation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.client.render.models.blockbench.pose.ModelPartTransformation
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class PetililModel (root: ModelPart) : PokemonPoseableModel(), HeadedFrame {
    override val rootPart = root.registerChildWithAllChildren("petilil")
    override val head = getPart("head")

    override var portraitScale = 1.52F
    override var portraitTranslation = Vec3d(0.1, -0.25, 0.0)

    override var profileScale = 0.8F
    override var profileTranslation = Vec3d(0.0, 0.5, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose
    lateinit var sleep: PokemonPose
    lateinit var battleIdle: PokemonPose
    lateinit var shoulderLeft: PokemonPose
    lateinit var shoulderRight: PokemonPose

    val shoulderOffset = 3

    override val cryAnimation = CryProvider { _, _ -> bedrockStateful("petilil", "cry") }

    override fun registerPoses() {
        val blink = quirk { bedrockStateful("petilil", "blink") }
        val quirk = quirk { bedrockStateful("petilil", "idle_quirk") }

        sleep = registerPose(
            poseName = "sleep",
            poseType = PoseType.SLEEP,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("petilil", "sleep")
            )
        )

        standing = registerPose(
            poseName = "standing",
            poseTypes = PoseType.STATIONARY_POSES + PoseType.UI_POSES,
            condition = { !it.isBattling },
            quirks = arrayOf(blink, quirk),
            idleAnimations = arrayOf(
                singleBoneLook(pitchMultiplier = 0.9F, yawMultiplier = 0.9F),
                bedrock("petilil", "ground_idle")
            )
        )

        walk = registerPose(
            poseName = "walk",
            poseTypes = PoseType.MOVING_POSES,
            quirks = arrayOf(blink, quirk),
            idleAnimations = arrayOf(
                singleBoneLook(pitchMultiplier = 0.9F, yawMultiplier = 0.9F),
                bedrock("petilil", "ground_walk")
            )
        )

        battleIdle = registerPose(
            poseName = "battle_idle",
            poseTypes = PoseType.STATIONARY_POSES,
            quirks = arrayOf(blink, quirk),
            idleAnimations = arrayOf(
                singleBoneLook(pitchMultiplier = 0.9F, yawMultiplier = 0.9F),
                bedrock("petilil", "battle_idle")
            )
        )

        shoulderLeft = registerPose(
                poseType = PoseType.SHOULDER_LEFT,
                quirks = arrayOf(blink),
                idleAnimations = arrayOf(
                        singleBoneLook(),
                        bedrock("petilil", "ground_idle")
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
                        bedrock("petilil", "ground_idle")
                ),
                transformedParts = arrayOf(
                        rootPart.createTransformation().addPosition(ModelPartTransformation.X_AXIS, -shoulderOffset)
                )
        )
    }

    override fun getFaintAnimation(
            pokemonEntity: PokemonEntity,
            state: PoseableEntityState<PokemonEntity>
    ) = if (state.isPosedIn(standing, walk, battleIdle, sleep)) bedrockStateful("petilil", "faint") else null
}