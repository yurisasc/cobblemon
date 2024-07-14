/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.spawning.detail

import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import com.cobblemon.mod.common.entity.npc.NPCEntity
import com.cobblemon.mod.common.util.party
import net.minecraft.server.level.ServerPlayer

/**
 * A [SpawnAction] for creating [NPCEntity]s.
 *
 * @author Hiroku
 * @since October 8th, 2023
 */
class NPCSpawnAction(ctx: SpawningContext, override val detail: NPCSpawnDetail) : SpawnAction<EntitySpawnResult>(ctx, detail) {
    override fun run(): EntitySpawnResult {
        val npc = NPCEntity(ctx.world)
        npc.npc = detail.npcClass
        npc.appliedAspects.addAll(detail.aspects)
        val seedLevel = (ctx.cause.entity as? ServerPlayer)?.let { it.party().maxOfOrNull { it.level } } ?: 10
        npc.initializeParty(seedLevel)
        return EntitySpawnResult(listOf(npc))
    }
}