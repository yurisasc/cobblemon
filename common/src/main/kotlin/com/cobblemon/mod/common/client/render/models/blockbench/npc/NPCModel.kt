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
import com.cobblemon.mod.common.entity.npc.NPCEntity
import net.minecraft.client.model.ModelPart

abstract class NPCModel(override val rootPart: ModelPart) : PoseableEntityModel<NPCEntity>() {
    abstract val name: String
    override fun getState(entity: NPCEntity): PoseableEntityState<NPCEntity> {
        return entity.delegate as NPCClientDelegate
    }

    open fun getIdle(state: PoseableEntityState<NPCEntity>) = bedrockOrNull(name, "idle") ?: blankAnimation()
    open fun getIdleBattle() = bedrockOrNull(name, "idle_battle") ?: blankAnimation()
    open fun getBattleIntro() = bedrockStatefulOrNull(name, "battle_intro")
    open fun getLose() = bedrockStatefulOrNull(name, "lose")
    open fun getWin() = bedrockStatefulOrNull(name, "win")
    open fun getSendOut() = bedrockStatefulOrNull(name, "send_out")
    open fun getCommand() = bedrockStatefulOrNull(name, "command")
    open fun getBlink() = bedrockStatefulOrNull(name, "blink")
    open fun getMega() = bedrockStatefulOrNull(name, "mega")
}