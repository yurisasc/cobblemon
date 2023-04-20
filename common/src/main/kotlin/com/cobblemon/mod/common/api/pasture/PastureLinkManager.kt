/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pasture

import java.util.UUID
import net.minecraft.server.network.ServerPlayerEntity

// TODO PASTURE Need to use this in the interact with pasture block then use links when handling server-side packets
// to authenticate that they were able to interact.
object PastureLinkManager {
    // Maps player UUID
    val links = mutableMapOf<UUID, PastureLink>()

    fun getLinkByPlayerId(playerId: UUID) = links[playerId]
    fun createLink(playerId: UUID, link: PastureLink) {
        links[playerId] = link
    }

    fun getLinkByPlayerAndPastureId(player: ServerPlayerEntity, pastureId: UUID): PastureLink? {
        val link = getLinkByPlayerId(player.uuid)
        if (link != null) {
            if (link.dimension != player.world.dimensionKey.value || !link.pos.isWithinDistance(player.pos, 10.0)) {
                links.remove(player.uuid)
                return null
            }
        }

        return link
    }
}