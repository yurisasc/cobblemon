package com.cablemc.pokemoncobbled.common.api.storage.party

import com.cablemc.pokemoncobbled.common.util.getServer
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

/**
 * A [PartyStore] used for a single player. This uses the player's UUID as the store's UUID, and is declared as its own
 * class so that the purpose of this store is clear in practice.
 *
 * @author Hiroku
 * @since November 29th, 2021
 */
class PlayerPartyStore(
    /** The UUID of the player this store is for. */
    val playerUUID: UUID
) : PartyStore(playerUUID) {
    override fun getObservingPlayers(): Iterable<ServerPlayer> {
        // Additional observer list TODO
        return getServer().playerList.getPlayer(playerUUID)?.let { listOf(it) } ?: emptyList()
    }
}