/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.npc.configuration

import net.minecraft.server.level.ServerPlayer

/**
 * A type of interaction handler for when a player right clicks the NPC.
 *
 * @author Hiroku
 * @since August 19th, 2023
 */
interface NPCInteractConfiguration {
    val type: String
    fun interact(player: ServerPlayer): Boolean
}