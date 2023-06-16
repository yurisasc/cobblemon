/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen1

import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.animation.BipedWalkAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.SingleBoneLookAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BipedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.PoseType.Companion.MOVING_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.STATIONARY_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.UI_POSES
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d
class ExeggutorModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, BipedFrame {
    override val rootPart = root.registerChildWithAllChildren("exeggutor")
    override val head = getPart("head")

    override val leftLeg = getPart("leg_left")
    override val rightLeg = getPart("leg_right")

    val head2 = object : HeadedFrame {
        override val rootPart = this@ExeggutorModel.rootPart
        override val head: ModelPart = getPart("head2")
    }
    val head3 = object : HeadedFrame {
        override val rootPart = this@ExeggutorModel.rootPart
        override val head: ModelPart = getPart("head3")
    }

    override val portraitScale = 2.1F
    override val portraitTranslation = Vec3d(-0.5, 0.55, 0.0)

    override val profileScale = 0.75F
    override val profileTranslation = Vec3d(0.0, 0.65, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose
    lateinit var sleep: PokemonPose
    lateinit var battleidle: PokemonPose

    override fun registerPoses() {
        val blink1 = quirk("blink") { bedrockStateful("exeggutor", "blink").setPreventsIdle(false) }
        val blink2 = quirk("blink") { bedrockStateful("exeggutor", "blink2").setPreventsIdle(false) }
        val blink3 = quirk("blink") { bedrockStateful("exeggutor", "blink3").setPreventsIdle(false) }
        sleep = registerPose(
            poseType = PoseType.SLEEP,
            idleAnimations = arrayOf(bedrock("exeggutor", "sleep"))
        )

        standing = registerPose(
            poseName = "standing",
            poseTypes = STATIONARY_POSES + UI_POSES,
            quirks = arrayOf(blink1, blink2, blink3),
            condition = { !it.isBattling },
            idleAnimations = arrayOf(
                singleBoneLook(),
                SingleBoneLookAnimation(head2, false, false),
                SingleBoneLookAnimation(head3, false, false),
                bedrock("exeggutor", "ground_idle")
            )
        )

        walk = registerPose(
            poseName = "walk",
            poseTypes = MOVING_POSES,
            quirks = arrayOf(blink1, blink2, blink3),
            condition = { !it.isBattling },
            idleAnimations = arrayOf(
                BipedWalkAnimation(this, periodMultiplier = 0.7F, amplitudeMultiplier = 1f),
                singleBoneLook(),
                SingleBoneLookAnimation(head2, false, false),
                SingleBoneLookAnimation(head3, false, false),
                bedrock("exeggutor", "ground_walk")
            )
        )

        battleidle = registerPose(
            poseName = "battle_idle",
            poseTypes = PoseType.STATIONARY_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink1, blink2, blink3),
            condition = { it.isBattling },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("exeggutor", "battle_idle")
            )

        )
    }

    override fun getFaintAnimation(
        pokemonEntity: PokemonEntity,
        state: PoseableEntityState<PokemonEntity>
    ) = if (state.isPosedIn(standing, walk, battleidle)) bedrockStateful("exeggutor", "faint") else null
}