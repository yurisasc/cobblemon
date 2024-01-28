/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen5

import com.cobblemon.mod.common.client.render.models.blockbench.createTransformation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BimanualFrame
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

class OshawottModel (root: ModelPart) : PosableModel(), HeadedFrame, BipedFrame, BimanualFrame {
    override val rootPart = root.registerChildWithAllChildren("oshawott")
    override val head = getPart("head")
    override val rightArm = getPart("arm_right")
    override val leftArm = getPart("arm_left")
    override val rightLeg = getPart("leg_right")
    override val leftLeg = getPart("leg_left")
    val scalchop = getPart("scalchop_hand")
    val scalchopbody = getPart("scalchop_torso")

    override val portraitScale = 2.0F
    override val portraitTranslation = Vec3d(-0.2, -0.15, 0.0)
    override val profileScale = 0.7F
    override val profileTranslation = Vec3d(0.0, 0.69, 0.0)

    lateinit var battleidle: Pose
    lateinit var sleep: Pose
    lateinit var standing: Pose
    lateinit var walk: Pose

    override val cryAnimation = CryProvider { bedrockStateful("oshawott", "cry") }

    override fun registerPoses() {
        val blink = quirk { bedrockStateful("oshawott", "blink") }
        sleep = registerPose(
            poseType = PoseType.SLEEP,
            transformTicks = 10,
            condition = { (it.entity as? PokemonEntity)?.isBattling == false },
            transformedParts = arrayOf(
                scalchop.createTransformation().withVisibility(visibility = false),
                scalchopbody.createTransformation().withVisibility(visibility = true)
            ),
            idleAnimations = arrayOf(bedrock("oshawott", "sleep"))
        )

        standing = registerPose(
            poseName = "standing",
            poseTypes = setOf(PoseType.NONE, PoseType.STAND, PoseType.PORTRAIT, PoseType.PROFILE),
            transformTicks = 10,
            quirks = arrayOf(blink),
            condition = { (it.entity as? PokemonEntity)?.isBattling == false },
            transformedParts = arrayOf(
                scalchop.createTransformation().withVisibility(visibility = false),
                scalchopbody.createTransformation().withVisibility(visibility = true)
            ),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("oshawott", "ground_idle")
            )
        )

        walk = registerPose(
            poseName = "walking",
            poseTypes = setOf(PoseType.SWIM, PoseType.WALK),
            transformTicks = 10,
            quirks = arrayOf(blink),
            condition = { (it.entity as? PokemonEntity)?.isBattling == false },
            transformedParts = arrayOf(
                scalchop.createTransformation().withVisibility(visibility = false),
                scalchopbody.createTransformation().withVisibility(visibility = true)
            ),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("oshawott", "ground_walk")
            )
        )

        battleidle = registerPose(
            poseName = "battle_idle",
            poseTypes = PoseType.STATIONARY_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink),
            condition = { (it.entity as? PokemonEntity)?.isBattling == true },
            transformedParts = arrayOf(
                scalchop.createTransformation().withVisibility(visibility = true),
                scalchopbody.createTransformation().withVisibility(visibility = false)
            ),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("oshawott", "ground_idle")
            )

        )
    }
}