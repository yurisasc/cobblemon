/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.moves.animations.keyframes

import com.cobblemon.mod.common.CobblemonNetwork.sendPacket
import com.cobblemon.mod.common.api.moves.animations.ActionEffectContext
import com.cobblemon.mod.common.api.moves.animations.TargetsProvider
import com.cobblemon.mod.common.api.moves.animations.UsersProvider
import com.cobblemon.mod.common.api.scheduling.delayedFuture
import com.cobblemon.mod.common.entity.Poseable
import com.cobblemon.mod.common.net.messages.client.effect.RunPosableMoLangPacket
import com.cobblemon.mod.common.util.asExpressionLike
import java.util.concurrent.CompletableFuture
import net.minecraft.entity.Entity
import net.minecraft.server.world.ServerWorld

class MoLangActionEffectKeyframe : ActionEffectKeyframe {
    var delay = "1".asExpressionLike()
    val expressions = mutableSetOf<String>()
    val visibilityRange = 200
    val applyToTarget = false

    override fun play(context: ActionEffectContext): CompletableFuture<Unit> {
        val entities = if (applyToTarget) {
            context.providers.filterIsInstance<TargetsProvider>().flatMap { it.targets }
        } else {
            context.providers.filterIsInstance<UsersProvider>().flatMap { it.users }
        }.filterIsInstance<Poseable>()

        for (entity in entities) {
            val world = (entity as Entity).world as ServerWorld
            val players = world.getPlayers { it.distanceTo(entity) <= visibilityRange }
            val pkt = RunPosableMoLangPacket(entityId = entity.id, expressions = expressions)
            players.forEach { it.sendPacket(pkt) }
        }

        return delayedFuture(seconds = delay.resolveFloat(context.runtime), serverThread = true)
    }
}
