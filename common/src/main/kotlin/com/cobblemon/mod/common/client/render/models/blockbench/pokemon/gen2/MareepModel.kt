/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen2

import com.cobblemon.mod.common.client.render.models.blockbench.PosableModel
import com.cobblemon.mod.common.client.render.models.blockbench.createTransformation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Pose
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.DataKeys
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class MareepModel (root: ModelPart) : PosableModel(), HeadedFrame {
    override val rootPart = root.registerChildWithAllChildren("mareep")
    override val head = getPart("head")

    val wool = getPart("wool")

    override var portraitScale = 2.3F
    override var portraitTranslation = Vec3d(-0.5, -1.2, 0.0)

    override var profileScale = 0.9F
    override var profileTranslation = Vec3d(0.0, 0.4, 0.0)

    lateinit var standing: Pose
    lateinit var walk: Pose
    lateinit var shearedstanding: Pose
    lateinit var shearedwalk: Pose
    lateinit var sleep: Pose
    lateinit var battleidle: Pose
    lateinit var shearedsleep: Pose
    lateinit var shearedbattleidle: Pose

    override val cryAnimation = CryProvider { bedrockStateful("mareep", "cry") }

    override fun registerPoses() {
        val blink = quirk { bedrockStateful("mareep", "blink") }
        sleep = registerPose(
            poseName = "unsheared_sleep",
            poseType = PoseType.SLEEP,
            condition = { !it.containsAspect(DataKeys.HAS_BEEN_SHEARED) },
            transformedParts = arrayOf(
                wool.createTransformation().withVisibility(visibility = true)
            ),
            idleAnimations = arrayOf(bedrock("mareep", "sleep"))
        )

        shearedsleep = registerPose(
            poseName = "sheared_sleep",
            poseType = PoseType.SLEEP,
            condition = { it.containsAspect(DataKeys.HAS_BEEN_SHEARED) },
            transformedParts = arrayOf(
                wool.createTransformation().withVisibility(visibility = false)
            ),
            idleAnimations = arrayOf(bedrock("mareep", "sleep"))
        )

        standing = registerPose(
            poseName = "standing",
            poseTypes = PoseType.STATIONARY_POSES + PoseType.UI_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink),
            condition = { !it.containsAspect(DataKeys.HAS_BEEN_SHEARED) && (it.entity as? PokemonEntity)?.isBattling == false },
            transformedParts = arrayOf(
                wool.createTransformation().withVisibility(visibility = true)
            ),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("mareep", "ground_idle")
            )
        )

        walk = registerPose(
            poseName = "walk",
            poseTypes = PoseType.MOVING_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink),
            condition = { !it.containsAspect(DataKeys.HAS_BEEN_SHEARED) },
            transformedParts = arrayOf(
                wool.createTransformation().withVisibility(visibility = true)
            ),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("mareep", "ground_walk")
            )
        )

        shearedstanding = registerPose(
            poseName = "shearedstanding",
            poseTypes = PoseType.STATIONARY_POSES,
            transformTicks = 0,
            quirks = arrayOf(blink),
            condition = { it.containsAspect(DataKeys.HAS_BEEN_SHEARED) && (it.entity as? PokemonEntity)?.isBattling == false },
            transformedParts = arrayOf(
                wool.createTransformation().withVisibility(visibility = false)
            ),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("mareep", "ground_idle")
            )
        )
        shearedwalk = registerPose(
            poseName = "shearedwalking",
            poseTypes = PoseType.MOVING_POSES,
            transformTicks = 0,
            quirks = arrayOf(blink),
            condition = { it.containsAspect(DataKeys.HAS_BEEN_SHEARED) },
            transformedParts = arrayOf(
                wool.createTransformation().withVisibility(visibility = false)
            ),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("mareep", "ground_walk")
            )
        )

        battleidle = registerPose(
            poseName = "battle_idle",
            poseTypes = PoseType.STATIONARY_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink),
            condition = { (it.entity as? PokemonEntity)?.isBattling == true },
            transformedParts = arrayOf(
                wool.createTransformation().withVisibility(visibility = true)
            ),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("mareep", "battle_idle")
            )
        )

        shearedbattleidle = registerPose(
            poseName = "battle_idle_sheared",
            poseTypes = PoseType.STATIONARY_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink),
            condition = { it.containsAspect(DataKeys.HAS_BEEN_SHEARED) && (it.entity as? PokemonEntity)?.isBattling == true },
            transformedParts = arrayOf(
                wool.createTransformation().withVisibility(visibility = false)
            ),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("mareep", "battle_idle")
            )
        )
    }

//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PosableState<PokemonEntity>
//    ) = if (state.isPosedIn(standing, walk)) bedrockStateful("mareep", "faint") else null
}