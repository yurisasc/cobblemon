/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen4

import com.cobblemon.mod.common.client.render.models.blockbench.PosableModel
import com.cobblemon.mod.common.client.render.models.blockbench.animation.BimanualSwingAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.BipedWalkAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BimanualFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BipedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider
import com.cobblemon.mod.common.entity.PoseType
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class FloatzelModel (root: ModelPart) : PosableModel(), HeadedFrame, BipedFrame, BimanualFrame {
    override val rootPart = root.registerChildWithAllChildren("floatzel")
    override val head = getPart("head")
    override val rightArm = getPart("arm_right")
    override val leftArm = getPart("arm_left")
    override val rightLeg = getPart("leg_right")
    override val leftLeg = getPart("leg_left")

    override var portraitScale = 1.9F
    override var portraitTranslation = Vec3d(-0.3, 0.65, 0.0)

    override var profileScale = 0.8F
    override var profileTranslation = Vec3d(0.0, 0.55, 0.0)

    lateinit var standing: CobblemonPose
    lateinit var walking: CobblemonPose
//    lateinit var sleep: CobblemonPose

    override val cryAnimation = CryProvider { bedrockStateful("floatzel", "cry") }

    override fun registerPoses() {
        val blink = quirk { bedrockStateful("floatzel", "blink") }
//        sleep = registerPose(
//            poseType = PoseType.SLEEP,
//            idleAnimations = arrayOf(bedrock("floatzel", "sleep"))
//        )

        standing = registerPose(
            poseName = "standing",
            poseTypes = PoseType.STATIONARY_POSES + PoseType.UI_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("floatzel", "ground_idle")
            )
        )

        walking = registerPose(
            poseName = "walking",
            poseTypes = PoseType.MOVING_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
//                bedrock("floatzel", "ground_walk"),
                bedrock("floatzel", "ground_idle"),
                BipedWalkAnimation(this),
                BimanualSwingAnimation(this)
            )
        )
    }
//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PoseableEntityState<PokemonEntity>
//    ) = if (state.isPosedIn(standing, walking, sleep)) bedrockStateful("floatzel", "faint") else null
}