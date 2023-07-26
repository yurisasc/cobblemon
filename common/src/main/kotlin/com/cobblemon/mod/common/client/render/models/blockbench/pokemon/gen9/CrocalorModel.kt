/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen9

import com.cobblemon.mod.common.client.render.models.blockbench.frame.BimanualFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BipedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.entity.PoseType
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class CrocalorModel (root: ModelPart) : PokemonPoseableModel(), HeadedFrame{
    override val rootPart = root.registerChildWithAllChildren("crocalor")
    override val head = getPart("head")

    override val portraitScale = 1.5F
    override val portraitTranslation = Vec3d(-0.5, 0.2, 0.0)

    override val profileScale = 0.7F
    override val profileTranslation = Vec3d(0.0, 0.65, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walking: PokemonPose
    lateinit var sleep: PokemonPose
    lateinit var battleidle: PokemonPose

    override val cryAnimation = CryProvider { _, _ -> bedrockStateful("crocalor", "cry").setPreventsIdle(false) }

    override fun registerPoses() {
        val blink = quirk("blink") { bedrockStateful("crocalor", "blink").setPreventsIdle(false) }
        sleep = registerPose(
            poseType = PoseType.SLEEP,
            idleAnimations = arrayOf(bedrock("crocalor", "sleep"))
        )

        standing = registerPose(
            poseName = "standing",
            poseTypes = PoseType.STATIONARY_POSES + PoseType.UI_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(disableY = true),
                bedrock("crocalor", "ground_idle")
            )
        )

        walking = registerPose(
            poseName = "walking",
            poseTypes = PoseType.MOVING_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(disableY = true),
                bedrock("crocalor", "ground_walk")
            )
        )

        battleidle = registerPose(
            poseName = "battle_idle",
            poseTypes = PoseType.STATIONARY_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink),
            condition = { it.isBattling },
            idleAnimations = arrayOf(
                singleBoneLook(disableY = true),
                bedrock("crocalor", "battle_idle")
            )
        )
    }
//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PoseableEntityState<PokemonEntity>
//    ) = if (state.isPosedIn(standing, walking, battleidle, sleep)) bedrockStateful("crocalor", "faint") else null
}