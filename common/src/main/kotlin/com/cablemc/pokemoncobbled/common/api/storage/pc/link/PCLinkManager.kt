package com.cablemc.pokemoncobbled.common.api.storage.pc.link

import com.cablemc.pokemoncobbled.common.api.storage.pc.PCStore
import net.minecraft.server.network.ServerPlayerEntity
import java.util.UUID

/**
 * Manages the [PCLink]s for the server. This is a memory of IDs that map to a PC which also
 * dictates whether a player is able to modify that PC.
 *
 * @author Hiroku
 * @since June 19th, 2022
 */
object PCLinkManager {
    private val links = mutableMapOf<UUID, PCLink>()

    fun getLink(playerID: UUID) = links[playerID]

    fun addLink(pcLink: PCLink) {
        links[pcLink.playerID] = pcLink
    }

    fun addLink(playerID: UUID, pcStore: PCStore, condition: (ServerPlayerEntity) -> Boolean = { true }) {
        links[playerID] = object : PCLink(playerID = playerID, pc = pcStore) {
            override fun isPermitted(player: ServerPlayerEntity) = condition(player)
        }
    }

    fun removeLink(playerID: UUID) {
        links.remove(playerID)
    }

    fun getPC(player: ServerPlayerEntity) = getLink(player.uuid)?.takeIf { it.isPermitted(player) }?.pc
}