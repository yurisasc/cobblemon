package com.cobblemon.mod.common.api.npc.configuration

import net.minecraft.server.network.ServerPlayerEntity

/**
 * A type of interaction handler for when a player right clicks the NPC.
 *
 * @author Hiroku
 * @since August 19th, 2023
 */
interface NPCInteractConfiguration {
    val type: String
    fun interact(player: ServerPlayerEntity): Boolean
}