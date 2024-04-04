/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen8

import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.entity.PoseType
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class ArctozoltModel (root: ModelPart) : PokemonPoseableModel(), HeadedFrame {
    override val rootPart = root.registerChildWithAllChildren("arctozolt")
    override val head = getPart("head")

    override var portraitTranslation = Vec3d(-0.45, 2.15, 0.0)
    override var portraitScale = 0.85F

    override var profileTranslation = Vec3d(0.02, 0.95, 0.0)
    override var profileScale = 0.33F

    lateinit var sleep: PokemonPose
    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose
    lateinit var float: PokemonPose
    lateinit var swim: PokemonPose
    lateinit var surfacefloat: PokemonPose
    lateinit var surfaceswim: PokemonPose
    lateinit var battleidle: PokemonPose

    override val cryAnimation = CryProvider { _, _ -> bedrockStateful("arctozolt", "cry") }

    override fun registerPoses() {
        val blink = quirk { bedrockStateful("arctozolt", "blink") }
        val quirk = quirk (secondsBetweenOccurrences = 30F to 60f) { bedrockStateful("arctozolt", "quirk") }

        standing = registerPose(
            poseName = "standing",
            poseTypes = PoseType.STATIONARY_POSES + PoseType.UI_POSES - PoseType.FLOAT,
            condition = { !it.isBattling },
            transformTicks = 10,
            quirks = arrayOf(blink, quirk),
            idleAnimations = arrayOf(
                bedrock("arctozolt", "ground_idle")
            )
        )

        walk = registerPose(
            poseName = "walk",
            poseTypes = PoseType.MOVING_POSES - PoseType.SWIM,
            transformTicks = 10,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("arctozolt", "ground_walk")
            )
        )

        float = registerPose(
            poseName = "float",
            poseType = PoseType.FLOAT,
            condition = { it.isSubmergedInWater },
            transformTicks = 10,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("arctozolt", "ground_idle")
            )
        )

        swim = registerPose(
            poseName = "swim",
            poseType = PoseType.SWIM,
            condition = { it.isSubmergedInWater },
            transformTicks = 10,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("arctozolt", "ground_idle")
            )
        )

        surfacefloat = registerPose(
            poseName = "surfacefloat",
            poseType = PoseType.FLOAT,
            condition = { !it.isSubmergedInWater && it.isTouchingWater },
            transformTicks = 10,
            quirks = arrayOf(blink, quirk),
            idleAnimations = arrayOf(
                bedrock("arctozolt", "ground_idle")
            )
        )

        surfaceswim = registerPose(
            poseName = "surfaceswim",
            poseType = PoseType.SWIM,
            condition = { !it.isSubmergedInWater && it.isTouchingWater },
            transformTicks = 10,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("arctozolt", "ground_idle")
            )
        )

        battleidle = registerPose(
            poseName = "battleidle",
            poseTypes = PoseType.STATIONARY_POSES,
            condition = { it.isBattling },
            transformTicks = 10,
            quirks = arrayOf(blink, quirk),
            idleAnimations = arrayOf(
                bedrock("arctozolt", "battle_idle")
            )
        )
    }

//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PoseableEntityState<PokemonEntity>
//    ) = if (state.isPosedIn(standing, walk)) bedrockStateful("arctozolt", "faint") else null
}