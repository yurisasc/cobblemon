/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen8

import com.cobblemon.mod.common.client.render.models.blockbench.PosableModel
import com.cobblemon.mod.common.client.render.models.blockbench.PosableState
import com.cobblemon.mod.common.client.render.models.blockbench.createTransformation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.QuadrupedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Pose
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.util.DataKeys
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class DubwoolModel(root: ModelPart) : PosableModel(root), HeadedFrame, QuadrupedFrame {
    override val rootPart = root.registerChildWithAllChildren("dubwool")
    override val head = getPart("head")
    override val foreLeftLeg= getPart("leg_front_left")
    override val foreRightLeg = getPart("leg_front_right")
    override val hindLeftLeg = getPart("leg_back_left")
    override val hindRightLeg = getPart("leg_back_right")
    val wool = getPart("wool")
    val neckWool = getPart("neck_wool")

    override var portraitScale = 3.1F
    override var portraitTranslation = Vec3d(-1.2, -0.7, 0.0)
    override var profileScale = 0.9F
    override var profileTranslation = Vec3d(0.0, 0.4, 0.0)

    lateinit var sleep: Pose
    lateinit var standing: Pose
    lateinit var walk: Pose
    lateinit var shearedsleep: Pose
    lateinit var shearedstanding: Pose
    lateinit var shearedwalk: Pose

    override val cryAnimation = CryProvider { bedrockStateful("dubwool", "cry") }

    override fun registerPoses() {
        val blink = quirk { bedrockStateful("dubwool", "blink") }
        sleep = registerPose(
                poseType = PoseType.SLEEP,
                transformTicks = 10,
                condition = { !it.containsAspect(DataKeys.HAS_BEEN_SHEARED) },
                transformedParts = arrayOf(
                        wool.createTransformation().withVisibility(visibility = true),
                        neckWool.createTransformation().withVisibility(visibility = true)
                ),
                idleAnimations = arrayOf(bedrock("dubwool", "sleep"))
        )

        standing = registerPose(
                poseName = "standing",
                poseTypes = setOf(PoseType.NONE, PoseType.STAND, PoseType.PORTRAIT, PoseType.PROFILE),
                transformTicks = 10,
                quirks = arrayOf(blink),
                condition = { !it.containsAspect(DataKeys.HAS_BEEN_SHEARED) },
                transformedParts = arrayOf(
                        wool.createTransformation().withVisibility(visibility = true),
                        neckWool.createTransformation().withVisibility(visibility = true)
                ),
                idleAnimations = arrayOf(
                        singleBoneLook(),
                        bedrock("dubwool", "ground_idle")
                )
        )

        walk = registerPose(
                poseName = "walking",
                poseTypes = setOf(PoseType.SWIM, PoseType.WALK),
                transformTicks = 10,
                quirks = arrayOf(blink),
                condition = { !it.containsAspect(DataKeys.HAS_BEEN_SHEARED) },
                transformedParts = arrayOf(
                        wool.createTransformation().withVisibility(visibility = true),
                        neckWool.createTransformation().withVisibility(visibility = true)
                ),
                idleAnimations = arrayOf(
                        singleBoneLook(),
                        bedrock("dubwool", "ground_walk")
                )
        )

        shearedsleep = registerPose(
                poseName = "shearedsleep",
                poseType = PoseType.SLEEP,
                transformTicks = 10,
                condition = { it.containsAspect(DataKeys.HAS_BEEN_SHEARED) },
                transformedParts = arrayOf(
                        wool.createTransformation().withVisibility(visibility = false),
                        neckWool.createTransformation().withVisibility(visibility = false)
                ),
                idleAnimations = arrayOf(
                        bedrock("dubwool", "sleep")
                )
        )

        shearedstanding = registerPose(
                poseName = "shearedstanding",
                poseTypes = setOf(PoseType.NONE, PoseType.STAND, PoseType.PORTRAIT, PoseType.PROFILE),
                transformTicks = 10,
                quirks = arrayOf(blink),
                condition = { it.containsAspect(DataKeys.HAS_BEEN_SHEARED) },
                transformedParts = arrayOf(
                        wool.createTransformation().withVisibility(visibility = false),
                        neckWool.createTransformation().withVisibility(visibility = false)
                ),
                idleAnimations = arrayOf(
                        singleBoneLook(),
                        bedrock("dubwool", "ground_idle")
                )
        )
        shearedwalk = registerPose(
                poseName = "shearedwalking",
                poseTypes = setOf(PoseType.SWIM, PoseType.WALK),
                transformTicks = 10,
                quirks = arrayOf(blink),
                condition = { it.containsAspect(DataKeys.HAS_BEEN_SHEARED) },
                transformedParts = arrayOf(
                        wool.createTransformation().withVisibility(visibility = false),
                        neckWool.createTransformation().withVisibility(visibility = false)
                ),
                idleAnimations = arrayOf(
                        singleBoneLook(),
                        bedrock("dubwool", "ground_walk")
                )
        )
    }

    override fun getFaintAnimation(state: PosableState) = if (state.isPosedIn(standing, walk, shearedwalk, shearedstanding, sleep)) bedrockStateful("dubwool", "faint") else null
    override fun getEatAnimation(state: PosableState) = if (state.isNotPosedIn(sleep, shearedsleep)) bedrockStateful("dubwool", "eat") else null
}