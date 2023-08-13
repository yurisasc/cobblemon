/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.npc

import com.cobblemon.mod.common.client.render.models.blockbench.animation.BimanualSwingAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.BipedWalkAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BimanualFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BipedFrame
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.npc.NPCEntity
import net.minecraft.client.model.ModelPart

class StandardNPCModel(part: ModelPart) : NPCModel(part), BipedFrame, BimanualFrame {
    override val rootPart = part.registerChildWithAllChildren("model")

    override val leftArm = getPart("arm_left")
    override val rightArm = getPart("arm_right")
    override val leftLeg = getPart("leg_left")
    override val rightLeg = getPart("leg_right")

    override val name = "trainer_generic"

    override fun registerPoses() {
        val blink = quirk("blink") { blinkAnimation(it) ?: blankAnimationStateful() }
        registerPose(
            poseTypes = PoseType.ALL_POSES,
            poseName = "standard",
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                idleAnimation.invoke(null, null) ?: blankAnimation(),
                BipedWalkAnimation(this),
                BimanualSwingAnimation(this)
            )
        )

        registerPose(
            poseTypes = PoseType.ALL_POSES,
            poseName = "battle",
            condition = NPCEntity::isInBattle,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                getIdleBattle(),
                BipedWalkAnimation(this),
                BimanualSwingAnimation(this)
            )
        )
    }
}