package com.cablemc.pokemoncobbled.common.api.storage.pc.link

import com.cablemc.pokemoncobbled.common.api.storage.pc.PCStore
import net.minecraft.server.network.ServerPlayerEntity
import java.util.UUID

/**
 * A registered connection from a UUID to a specific PC. The purpose of this interface
 * is to be added to the [PCLinkManager] when a player is being told to open a PC GUI,
 * which grants that player permission to edit that store.
 *
 * @author Hiroku
 * @since June 19th, 2022
 */
open class PCLink(
    /** The PC it links to. */
    open val pc: PCStore,
    /** The player it's for. */
    open val playerID: UUID
) {
    /**
     * Returns whether the given player is able to use this link. This can be used
     * to check if the link is still valid, and you may choose to remove the link
     * using [PCLinkManager.removeLink] if you find that it's no longer valid.
     */
    open fun isPermitted(player: ServerPlayerEntity) = true
}