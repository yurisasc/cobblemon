/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.StatelessAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.bedrock.animation.BedrockAnimationRepository
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.bedrock.animation.BedrockStatelessAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.ModelFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.getChildOf
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.Y_AXIS
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.withRotation
import com.cablemc.pokemoncobbled.common.entity.PoseType
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class MagikarpModel(root: ModelPart) : PokemonPoseableModel() {
    override val rootPart: ModelPart = registerRelevantPart("magikarp", root.getChild("magikarp"))
    val body: ModelPart = registerRelevantPart("body", rootPart.getChildOf("body"))
    val leftMustache: ModelPart = registerRelevantPart("leftmustache", rootPart.getChildOf("body", "mustache_left"))
    val leftMustacheTip: ModelPart = registerRelevantPart("leftmustachetip", rootPart.getChildOf("body", "mustache_left", "mustache_left_tip"))
    val leftFlipper: ModelPart = registerRelevantPart("leftlfipper", rootPart.getChildOf("body", "flipper_left"))
    val rightMustache: ModelPart = registerRelevantPart("rightmustache", rootPart.getChildOf("body", "mustache_right"))
    val rightMustacheTip: ModelPart = registerRelevantPart("rightmustachetip", rootPart.getChildOf("body", "mustache_right", "mustache_right_tip"))
    val rightFlipper: ModelPart = registerRelevantPart("rightlfipper", rootPart.getChildOf("body", "flipper_right"))
    val tail: ModelPart = registerRelevantPart("tail", rootPart.getChildOf("body", "tail"))

    override val portraitScale = 1.65F
    override val portraitTranslation = Vec3d(0.12, -0.45, 0.0)
    override val profileScale = 1F
    override val profileTranslation = Vec3d(0.0, 0.0, 0.0)

    override fun registerPoses() {
        registerPose(
            poseName = "land",
            poseTypes = setOf(PoseType.NONE, PoseType.PROFILE, PoseType.STAND, PoseType.WALK),
            idleAnimations = arrayOf(BedrockStatelessAnimation(this, BedrockAnimationRepository.getAnimation("magikarp.animation.json", "animation.magikarp.flop")))
        )

        registerPose(
            poseName = "swimming",
            poseTypes = setOf(PoseType.FLOAT, PoseType.SWIM),
            idleAnimations = arrayOf(BedrockStatelessAnimation(this, BedrockAnimationRepository.getAnimation("magikarp.animation.json","animation.magikarp.fly")))
        )

        registerPose(
            poseName = "portrait",
            poseTypes = setOf(PoseType.PORTRAIT),
            idleAnimations = emptyArray<StatelessAnimation<PokemonEntity, out ModelFrame>>(),
            transformedParts = arrayOf(
                leftMustache.withRotation(Y_AXIS, (-75F).toRadians()),
                rightMustache.withRotation(Y_AXIS, 75F.toRadians())
            )
        )
    }
}