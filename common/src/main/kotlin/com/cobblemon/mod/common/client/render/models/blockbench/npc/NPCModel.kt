/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.npc

import com.cobblemon.mod.common.client.entity.NPCClientDelegate
import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityModel
import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatefulAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.ModelFrame
import com.cobblemon.mod.common.entity.npc.NPCEntity
import net.minecraft.client.model.ModelPart

abstract class NPCModel(override val rootPart: ModelPart) : PoseableEntityModel<NPCEntity>() {
    abstract val name: String
    override fun getState(entity: NPCEntity): PoseableEntityState<NPCEntity> {
        return entity.delegate as NPCClientDelegate
    }

    val blinkAnimation = TrainerAnimationProvider { state -> bedrockStatefulOrNull(name, "blink") }
    val idleAnimation = TrainerStatelessAnimationProvider { entity, state -> bedrockOrNull(name, "idle") ?: blankAnimation() }
    open fun getIdleBattle() = bedrockOrNull(name, "idle_battle") ?: blankAnimation()
    open fun getBattleIntro() = bedrockStatefulOrNull(name, "battle_intro")
    open fun getLose() = bedrockStatefulOrNull(name, "lose")
    open fun getWin() = bedrockStatefulOrNull(name, "win")
    open fun getSendOut() = bedrockStatefulOrNull(name, "send_out")
    open fun getRecall() = getSendOut()
    open fun getCommand() = bedrockStatefulOrNull(name, "command")
    open fun getBlink() = bedrockStatefulOrNull(name, "blink")
    open fun getMega() = bedrockStatefulOrNull(name, "mega")

    open fun getAnimation(animationType: String): StatefulAnimation<NPCEntity, ModelFrame> {
        return when (animationType) {
            NPCEntity.RECALL_ANIMATION -> getRecall() ?: blankAnimationStateful()
            NPCEntity.SEND_OUT_ANIMATION -> getSendOut() ?: blankAnimationStateful()
            NPCEntity.WIN_ANIMATION -> getWin() ?: blankAnimationStateful()
            NPCEntity.LOSE_ANIMATION -> getLose() ?: blankAnimationStateful()
            else -> blankAnimationStateful() // Maybe try to parse bedrock(..., ...) stuff
        }
    }
}