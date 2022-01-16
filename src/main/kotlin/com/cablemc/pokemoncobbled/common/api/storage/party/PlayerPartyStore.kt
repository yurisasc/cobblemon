package com.cablemc.pokemoncobbled.common.api.storage.party

import java.util.UUID

/**
 * A [PartyStore] used for a single player. This uses the player's UUID as the store's UUID, and is declared as its own
 * class so that the purpose of this store is clear in practice. It also automatically adds the player's UUID as an
 * observer UUID as per [PartyStore.observerUUIDs]
 *
 * @author Hiroku
 * @since November 29th, 2021
 */
class PlayerPartyStore(
    /** The UUID of the player this store is for. */
    val playerUUID: UUID
) : PartyStore(playerUUID) {
    override fun initialize() {
        super.initialize()
        observerUUIDs.add(playerUUID)
    }
}