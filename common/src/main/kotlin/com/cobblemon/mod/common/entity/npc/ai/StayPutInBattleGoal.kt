/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.entity.npc.ai

import com.cobblemon.mod.common.entity.npc.NPCEntity
import java.util.EnumSet
import net.minecraft.entity.ai.goal.Goal

/**
 * AI goal that stops movement tasks while the NPC is in battle.
 *
 * @author Hiroku
 * @since August 25th, 2023
 */
class StayPutInBattleGoal(val npc: NPCEntity) : Goal() {
    override fun canStart() = npc.isInBattle()
    override fun getControls() = EnumSet.of(Control.MOVE)
}