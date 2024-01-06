/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.npc

import com.cobblemon.mod.common.client.render.models.blockbench.frame.BimanualFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BipedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.npc.NPCEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class StandardNPCModel(part: ModelPart) : NPCModel(part), BipedFrame, BimanualFrame, HeadedFrame {
    override val rootPart = part.registerChildWithAllChildren("model")

    override val leftArm = getPart("arm_left")
    override val rightArm = getPart("arm_right")
    override val leftLeg = getPart("leg_left")
    override val rightLeg = getPart("leg_right")
    override val head = getPart("head")

    override val name = "trainer_generic"

    override val isForLivingEntityRenderer = true

    override val portraitScale: Float = 2.3F
    override val portraitTranslation: Vec3d = Vec3d(0.0, 1.1, 0.0)

    override fun registerPoses() {
        val blink = quirk("blink") { blinkAnimation(it) ?: blankAnimationStateful() }
        registerPose(
            poseTypes = PoseType.ALL_POSES,
            poseName = "standard",
            condition = { npc -> !npc.isInBattle() },
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                idleAnimation.invoke(null, null) ?: blankAnimation()
            )
        )

        registerPose(
            poseTypes = PoseType.ALL_POSES,
            poseName = "battle",
            condition = NPCEntity::isInBattle,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                getIdleBattle(),
//                BipedWalkAnimation(this),
//                BimanualSwingAnimation(this)
            )
        )
    }
}