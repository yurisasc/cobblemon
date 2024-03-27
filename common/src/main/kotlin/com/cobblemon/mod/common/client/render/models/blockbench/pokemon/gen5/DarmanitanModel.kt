/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen5

import com.cobblemon.mod.common.client.render.models.blockbench.animation.BimanualSwingAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.BipedWalkAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BimanualFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BipedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.entity.PoseType
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class DarmanitanModel(root: ModelPart) : PokemonPoseableModel(), BipedFrame, BimanualFrame {
    override val rootPart = root.registerChildWithAllChildren("darmanitan")

    override val leftArm = getPart("arm_left")
    override val rightArm = getPart("arm_right")
    override val leftLeg = getPart("left_upper_leg")
    override val rightLeg = getPart("right_upper_leg")

    override var portraitScale = 0.96F
    override var portraitTranslation = Vec3d(-0.35, 0.71, 0.0)

    override var profileScale = 0.57F
    override var profileTranslation = Vec3d(-0.11, 0.73, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose
    lateinit var sleep: PokemonPose
    lateinit var battleidle: PokemonPose

    //    override val cryAnimation = CryProvider { _, _ -> bedrockStateful("darmanitan", "cry") }
    override fun registerPoses() {
        val blink = quirk { bedrockStateful("darmanitan", "blink") }
        val quirk = quirk { bedrockStateful("darmanitan", "quirk") }

        sleep = registerPose(
                poseType = PoseType.SLEEP,
                transformTicks = 10,
                idleAnimations = arrayOf(bedrock("darmanitan", "sleep"))
        )

        standing = registerPose(
                poseName = "standing",
                poseTypes = PoseType.STATIONARY_POSES + PoseType.UI_POSES,
                quirks = arrayOf(blink, quirk),
                condition = { !it.isBattling },
                idleAnimations = arrayOf(
                        bedrock("darmanitan", "ground_idle")
                )
        )

        battleidle = registerPose(
                poseTypes = setOf(PoseType.STAND),
                poseName = "battle_standing",
                quirks = arrayOf(blink, quirk),
                condition = { it.isBattling },
                idleAnimations = arrayOf(
                        bedrock("darmanitan", "battle_idle")
                )
        )

        walk = registerPose(
                poseName = "walk",
                poseTypes = PoseType.MOVING_POSES,
                quirks = arrayOf(blink, quirk),
                idleAnimations = arrayOf(
                        bedrock("darmanitan", "ground_walk")
                )
        )
    }

//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PoseableEntityState<PokemonEntity>
//    ) = if (state.isPosedIn(standing, walk)) bedrockStateful("darmanitan", "faint") else null
}